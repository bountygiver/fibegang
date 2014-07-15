package Api;

import android.util.JsonWriter;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by bounty on 2014-07-14.
 */
public class Payload {
    public String request;
    public int sessionid;
    public List<String> path;
    public int identity;
    public Dictionary<String, Object> payload;
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
        p.path = new ArrayList<String>();
        p.payload = new Hashtable<String, Object>();
        p.request = "";
        p.sessionid = 0;
        p.identity = 0;
        p.sessionkey = "";
        return p;
    }

    public DatagramPacket getPacket() {
        return null;
    }
}
