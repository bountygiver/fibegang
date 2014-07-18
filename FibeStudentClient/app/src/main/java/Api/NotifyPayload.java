package Api;

import com.google.gson.Gson;

import java.util.Dictionary;
import java.util.Map;

/**
 * Created by bounty on 2014-07-14.
 */
public class NotifyPayload {
    public int identity;
    public String status;
    public String type;
    public Map<String, Object> payload;

    public static NotifyPayload Deserialize(String data) {
        Gson g = new Gson();
        return g.fromJson(data, NotifyPayload.class);
    }
}
