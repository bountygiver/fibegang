package BackEnd;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import Api.AudioPayload;
import Api.Connector;
import Api.NotifyPayload;
import Api.Payload;

/**
 * Created by bounty on 2014-07-14.
 */
public class TalkSession extends Observable implements Observer {
    private boolean isTalking;
    private boolean canceled;
    private double Volume;
    private Date when;
    private AudioRecord recorder;

    private int sampleRate = 16000;
    @SuppressWarnings("deprecation")
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

    private int packetIdentity, audioSessionId;
    private String[] tags;
    private Room room;
    private DatagramSocket audioSender;

    public TalkSession(Room room, String[] tags, int sessionId, Connector conn) throws IOException {
        when = new Date();
        this.tags = tags;
        this.room = room;
        isTalking = false;

        Payload p = Payload.makePayload("enqueue");
        p.addPayload("time", ((int) when.getTime()) + "");
        p.addPayload("tags", tags);
        p.path = new String[room.getPath().length + 1];
        int index = 0;
        for (String s : room.getPath()) {
            p.path[index] = s;
            index += 1;
        }
        p.path[index] = "audio";
        p.sessionid = sessionId;
        conn.Send(p);
        canceled = false;
        conn.addObserver(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean isStart = true;
        for (String s : tags) {
            if (!isStart) sb.append(", ");
            isStart = false;
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (canceled) {
            observable.deleteObserver(this);
            return;
        }
        NotifyPayload n = (NotifyPayload) o;
        Connector conn = (Connector) observable;
        if (n.identity != packetIdentity || !n.type.equals("permit")) return;
        observable.deleteObserver(this);

        try {
            audioSender = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            setChanged();
            notifyObservers();
            return;
        }
        InetAddress ep = null;
        try {
            ep = InetAddress.getByName(conn.getEndpoint());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            setChanged();
            notifyObservers();
            return;
        }
        audioSender.connect(ep, Connector.CONNECTIONPORT);
        audioSessionId = (Integer) n.payload.get("sessionid");

        StartTalking(conn.sessionId);

        setChanged();
        notifyObservers();
    }

    private void StartTalking(final int sid) {
        Runnable r = new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void SuppressAudio(AudioRecord ar) {

                int id = ar.getAudioSessionId();
                NoiseSuppressor.create(id);
                AcousticEchoCanceler.create(id);
            }

            @Override
            public void run() {
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC
                        , sampleRate, channelConfig, audioFormat, minBufSize);
                byte[] buffer = new byte[minBufSize];
                DatagramPacket packet;
                SuppressAudio(recorder);
                recorder.startRecording();

                while(isTalking) {
                    //reading data from MIC into buffer
                    minBufSize = recorder.read(buffer, 0, buffer.length);
                    final byte[] buf = buffer;
                    //putting buffer in the packet
                    packet = new DatagramPacket (buffer,buffer.length,audioSender.getInetAddress(),audioSender.getPort());

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            AudioPayload ap = new AudioPayload(buf, audioSessionId);
                            try {
                                audioSender.send(ap.getPacket(audioSender));
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("Skipped");
                            }
                        }
                    };
                    r.run();

                }
                recorder.stop();
                recorder.release();
                recorder = null;
                Payload p = new Payload();
                p.request = "cancel";
                p.sessionid = sid;
                p.path = new String[room.getPath().length + 1];
                int index = 0;
                for (String s : room.getPath()) {
                    p.path[index] = s;
                    index += 1;
                }
                p.path[index] = "audio";
                try {
                    audioSender.send(p.getPacket(audioSender));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                audioSender.disconnect();
                setChanged();
                notifyObservers();
            }
        };
        new Thread(r).start();
    }



    public void StopTalking() {
        isTalking = false;
        canceled = true;
    }
}
