package BackEnd;

import android.util.Log;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Observable;

import Api.Connector;

/**
 * Created by bounty on 2014-07-14.
 */
public class Room extends Observable {
    private String Title;

    public String[] getPath() {
        return Path;
    }

    private String[] Path;
    private int sessionId;

    public String getTitle() {
        return Title;
    }

    public String[] getTagsAvailable() {
        return TagsAvailable;
    }

    public boolean isAsking() {
        return isAsking;
    }

    public TalkSession getCurrentAsk() {
        return currentAsk;
    }

    private String[] TagsAvailable;
    private Connector connector;
    private boolean isAsking;
    private TalkSession currentAsk;

    public TalkSession addPing(final String[] tags)
    {
        final Room thisRoom = this;
        Runnable r = new Runnable() {
            @Override
            public void run() {

                try {
                    currentAsk = new TalkSession(thisRoom, tags, sessionId, connector);
                    thisRoom.setChanged();
                    thisRoom.notifyObservers("SESSION_CREATED");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
        isAsking = true;
        this.setChanged();
        notifyObservers("SUCCESS_ADD");
        return currentAsk;
    }

    public Room(String[] path, Connector connector, int sessionId) {
        this.Path = path;
        this.connector = connector;
        TagsAvailable = new String[] {"Demo tag", "Question", "Assignment", "Others"};
        this.sessionId = sessionId;
        Title = path[path.length - 1];
    }

    public boolean cancelRequest() {
        if (currentAsk == null) return false;
        currentAsk.StopTalking();
        isAsking = false;
        this.setChanged();
        notifyObservers();
        return true;
    }
}
