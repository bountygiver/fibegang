package BackEnd;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import Api.Connector;
import Api.Payload;
import Api.ResponsePayload;

/**
 * Created by bounty on 2014-07-14.
 */
public class ClientEngine extends Observable {

    private Connector connector;
    private int sessionId;

    public List<RoomItem> getRoomAvailable() {
        return roomAvailable;
    }

    private List<RoomItem> roomAvailable;

    String currentError;

    public ClientEngine() {
        roomAvailable = new ArrayList<RoomItem>();
    }

    public boolean ConnectTo(String s) {
        FlushConnection();
        try {
            connector = new Connector(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            currentError = "Invalid destination";
            return false;
        } catch (SocketException e) {
            e.printStackTrace();
            currentError = "Unable to connect";
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean login(String username, String password)
    {
        Payload p = Payload.makePayload();
        p.request = "login";
        p.addPayload("username", username);
        p.addPayload("password", password);
        p.addPayload("sessionkey", "");
        ResponsePayload r = null;
        try {
            r = connector.SendAndReceive(p);
            if (r.status.equals("success"))
            {
                connector.sessionId = sessionId = ((Double) r.payload.get("sessionid")).intValue() ;
                return true;
            }
            else
            {
                currentError = r.message;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            currentError = "Lost connection to server";
            connector.FlushConnection();
            return false;
        }
    }

    public boolean register(String username, String password)
    {
        Payload p = Payload.makePayload();
        p.request = "regist";
        p.addPayload("username", username);
        p.addPayload("password", password);
        ResponsePayload r = null;
        try {
            r = connector.SendAndReceive(p);
            if (r == null) {
                currentError = "Request timeout!";
                return false;
            }
            if (r.status.equals("success"))
            {
                return true;
            }
            else
            {
                currentError = r.message;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            currentError = "Lost connection to server";
            connector.FlushConnection();
            return false;
        }
    }

    private boolean isRoomExist(String path) {
        for (RoomItem r : roomAvailable) {
            if (r.getPath().equals(path)) return true;
        }
        return false;
    }

    public boolean list()
    {
        Payload p = Payload.makePayload();
        p.sessionid = sessionId;
        p.request = "ls";
        ResponsePayload r = null;
        try {
            r = connector.SendAndReceive(p);
            if (r == null) {
                currentError = "Request timeout!";
                return false;
            }
            if (r.status.equals("success"))
            {
                Map<String, String> k = (Map<String, String>) r.payload.get("list");
                for (String key : k.keySet()) {
                    if (!isRoomExist(key))
                        roomAvailable.add(new RoomItem(key, k.get(key)));
                }
                this.setChanged();
                notifyObservers();
                return true;
            }
            else
            {
                currentError = r.message;
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            currentError = "Lost connection to server";
            connector.FlushConnection();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            currentError = "Invalid response";
            return false;
        }
    }

    public Room JoinRoom(RoomItem r) {
        String[] path = r.getPath().split(",");
        return new Room(path, connector, sessionId);
    }

    private void FlushConnection() {
        if (connector != null) connector.FlushConnection();
        connector = null;
        this.setChanged();
        this.notifyObservers();
    }

    public String getError() {
        String cE = currentError;
        currentError = "";
        if (cE != null && cE.length() > 0) return cE;
        return "An unknown error has occurred!";
    }
}
