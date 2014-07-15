package Api;

import android.util.Xml;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

/**
 * Created by bounty on 2014-07-14.
 */
public class Connector extends Observable {

    public static final int CONNECTIONPORT = 56789;

    private DatagramSocket socket;

    public String getEndpoint() {
        return endpoint;
    }

    private String endpoint;

    private Map<Integer, ResponsePayload> pendingRes;
    private List<Integer> identifierPool;
    private Random rand;
    public int sessionId;

    protected void removeIdentity(int i) {
        Integer toRemove = null;
        for (Integer s : identifierPool) {
            if (s == i) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) identifierPool.remove(toRemove);
    }

    public Connector(String endpoint) throws SocketException, UnknownHostException {
        InetAddress ep = InetAddress.getByName(endpoint);
        socket = new DatagramSocket();
        socket.connect(ep, CONNECTIONPORT);
        this.endpoint = endpoint;

        pendingRes = new HashMap<Integer, ResponsePayload>();
        rand = new Random();
        identifierPool = new ArrayList<Integer>();

        Runnable s = new Runnable() {
            @Override
            public void run() {
                boolean connection = true;
                while (connection) {
                    try {
                        byte[] buf = new byte[4096];
                        DatagramPacket rp = new DatagramPacket(buf, buf.length);
                        socket.receive(rp);

                        if ((new String(buf, 0, 4)).equals("a000")) {
                            continue;
                        }

                        String s = new String(buf, 0, rp.getLength());
                        try {
                            ResponsePayload responsePayload = ResponsePayload.Deserialize(s);

                            int identity = responsePayload.identity;
                            removeIdentity(identity);

                            if (identity != 0 && pendingRes.containsKey(identity))
                            {
                                pendingRes.put(identity, responsePayload);
                            }
                            continue;
                        } catch (Exception e) {

                        }
                        try {
                            NotifyPayload notifyPayload = NotifyPayload.Deserialize(s);

                            if (notifyPayload.status.equals("notify")) {
                                setChanged();
                                notifyObservers(notifyPayload);
                            }

                        } catch (Exception e) {

                        }
                    } catch (SocketException e) {
                        connection = false;
                    } catch (IOException e) {
                        connection = false;
                    }

                }
            }
        };
        new Thread(s).start();
    }

    public boolean Send(Payload p) {
        try {
            int identity = rand.nextInt(Integer.MAX_VALUE);
            while (identifierPool.contains(identity)) identity = rand.nextInt(Integer.MAX_VALUE);
            p.identity = identity;
            identifierPool.add(identity);
            socket.send(p.getPacket(socket));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public ResponsePayload SendAndReceive(final Payload p) throws IOException {
        boolean sent = Send(p);
        if (sent) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                        ResponsePayload timeout = new ResponsePayload();
                        timeout.status = "failed";
                        timeout.message = "Connection timeout!";
                        pendingRes.put(p.identity, timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            pendingRes.put(p.identity, null);
            while (pendingRes.get(p.identity) == null && pendingRes.containsKey(p.identity)) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return pendingRes.remove(p.identity);
                }
            }
            return pendingRes.remove(p.identity);
        }
        return null;
    }

    public void ReleaseAwaiter(Payload p) {
        pendingRes.remove(p.identity);
    }

    public void FlushConnection() {
        try {
            socket.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
