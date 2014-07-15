package BackEnd;

import java.io.Serializable;

/**
 * Created by bounty on 2014-07-14.
 */
public class RoomItem implements Serializable {
    private String Path;

    public String getDescription() {
        return Description;
    }

    private String Description;

    public RoomItem(String path, String description) {
        Path = path;
        Description = description;
    }

    public String getPath() {
        return Path;
    }

    public String toString() {
        return Path + " - " + Description;
    }
}
