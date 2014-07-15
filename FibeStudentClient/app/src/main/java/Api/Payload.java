package Api;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bounty on 2014-07-14.
 */
public class Payload {
    public String request;
    public int sessionid;
    public String[] path;
    public int identity;
    public Map<String, Object> payload;
    public String sessionkey;

    public void addPayload(String key, Object content)
    {
        payload.put(key, content);
    }

    public int payloadSize()
    {
        return payload.size();
    }

    public static Payload makePayload(String Action)
    {
        Payload p = makePayload();
        p.request = Action;
        return p;
    }

    public static Payload makePayload()
    {
        Payload p = new Payload();
        p.path = new String[0];
        p.payload = new HashMap<String, Object>();
        p.request = "";
        p.sessionid = 0;
        p.identity = 0;
        p.sessionkey = "";
        return p;
    }

    public DatagramPacket getPacket(DatagramSocket socket) {
        Gson g = new Gson();
        String sP = g.toJson(this);
        byte[] payload = sP.getBytes();
        return new DatagramPacket(payload, payload.length,
                socket.getInetAddress(), socket.getPort());
    }
}
