package BackEnd;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.util.Log;

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
    private int AUDIOBUFFERSIZE = 1200;

    private int packetIdentity, audioSessionId;

    public String[] getTags() {
        return tags;
    }

    public String getTagsString() {
        StringBuilder sb = new StringBuilder();
        boolean st = false;
        for (String s : tags) {
            if (st) sb.append(", ");
            sb.append(s);
            st = true;
        }
        return sb.toString();
    }

    public String getRoomName() {
        return room.getTitle();
    }

    private String[] tags;
    private Room room;
    private DatagramSocket audioSender;

    private Connector conn;

    public TalkSession(Room room, String[] tags, int sessionId, Connector conn) throws IOException {
        when = new Date();
        this.tags = tags;
        this.room = room;
        this.conn = conn;
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
        packetIdentity = p.identity;
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
        Log.d("AUDIO_DEBUGGING", "Observed!");
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
            Log.d("AUDIO_DEBUGGING", "Failed to create socket!");
            e.printStackTrace();
            setChanged();
            notifyObservers("ERROR");
            return;
        }
        InetAddress ep = null;
        try {
            ep = InetAddress.getByName(conn.getEndpoint());
        } catch (UnknownHostException e) {
            Log.d("AUDIO_DEBUGGING", "Failed to get InetAddress!");
            e.printStackTrace();
            setChanged();
            notifyObservers("ERROR");
            return;
        }
        audioSender.connect(ep, Connector.CONNECTIONPORT);
        audioSessionId = ((Double) n.payload.get("sessionid")).intValue();
        Log.d("AUDIO_DEBUGGING", "Connected!");

        StartTalking(conn.sessionId);

        setChanged();
        notifyObservers("START_TALKING");
    }

    private void StartTalking(final int sid) {
        Runnable r = new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void SuppressAudio(AudioRecord ar) {

                int id = ar.getAudioSessionId();
                NoiseSuppressor.create(id);
                AcousticEchoCanceler.create(id);
            }

            public volatile int ts = 0;

            @Override
            public void run() {
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC
                        , sampleRate, channelConfig, audioFormat, minBufSize);
                byte[] buffer = new byte[AUDIOBUFFERSIZE];
                DatagramPacket packet;
                SuppressAudio(recorder);
                recorder.startRecording();
                isTalking = true;

                while(isTalking) {
                    //reading data from MIC into buffer
                    recorder.read(buffer, 0, buffer.length);
                    final byte[] buf = buffer.clone();

                    ts = ts + 1;
                    Log.d("AUDIO_DEBUGGING", "Audio recorded!"+ts);
                    Runnable r = new Runnable() {

                        int cts = ts;
                        @Override
                        public void run() {

                            AudioPayload ap = new AudioPayload(buf, audioSessionId, cts);
                            try {
                                DatagramPacket dp = ap.getPacket(audioSender);
                                audioSender.send(dp);
                                Log.d("AUDIO_DEBUGGING", "Audio sent!" + dp.getLength()
                                        + "bytes! Timestamp: " + cts);
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("Skipped");
                            }
                        }
                    };
                    new Thread(r).start();

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

    public void CancelRequest() {
        StopTalking();
        conn.deleteObserver(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Payload p = Payload.makePayload("cancel");
                p.sessionid = conn.sessionId;
                p.path = new String[room.getPath().length + 1];
                int index = 0;
                for (String s : room.getPath()) {
                    p.path[index] = s;
                    index += 1;
                }
                p.path[index] = "audio";
                conn.Send(p);
            }
        }).start();
    }
}
