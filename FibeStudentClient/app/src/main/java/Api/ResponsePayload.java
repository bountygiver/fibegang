package Api;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by bounty on 2014-07-14.
 */
public class ResponsePayload {
    public int identity;
    public String status;
    public String message;
    public Map<String, Object> payload;

    public static ResponsePayload Deserialize(String data) {
        Gson g = new Gson();
        return g.fromJson(data, ResponsePayload.class);
    }
}
