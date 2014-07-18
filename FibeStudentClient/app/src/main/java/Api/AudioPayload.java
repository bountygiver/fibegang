package Api;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

/**
 * Created by bounty on 2014-07-14.
 */
public class AudioPayload {

    byte[] payload;

    public AudioPayload(byte[] audio, int session_id, int timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(audio.length+12);
        ByteBuffer flipper = ByteBuffer.allocate(4);

        String s = "a000";
        buf.put(s.getBytes());

        flipper.putInt(session_id);
        flipper.flip();
        buf.put(flipper.array());

        flipper.clear();
        flipper.putInt(timestamp);
        flipper.flip();
        buf.put(flipper.array());

        buf.put(audio);

        payload = buf.array();
    }

    public int getSize() {
        return payload.length;
    }

    public DatagramPacket getPacket(DatagramSocket socket) {
        return new DatagramPacket(payload, payload.length, socket.getInetAddress(),
                socket.getPort());
    }
}
