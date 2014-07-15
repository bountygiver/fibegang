package BackEnd;

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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        r.run();
        isAsking = true;
        this.setChanged();
        notifyObservers();
        return currentAsk;
    }

    public Room(String[] path, Connector connector, int sessionId) {
        this.Path = path;
        this.connector = connector;
        TagsAvailable = new String[] {"Demo tag", "Question", "Assignment", "Others"};
        this.sessionId = sessionId;
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
