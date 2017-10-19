package invite.hfad.com.inviter;

/**
 * Created by Daryl on 10/2/2017.
 */

public class UserEvents {

    private String last_modified;
    private long read_messages;
    private String eventID;
    private int type;
    /*
    0 = Event
    1 = Chat
     */

    public UserEvents(){};

    public UserEvents(
            String last_modified,
            long read_messages,
            String eventID,
            int type)
    {
        this.last_modified = last_modified;
        this.read_messages = read_messages;
        this.eventID = eventID;
        this.type = type;
    }
    public String getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(String last_modified) {
        this.last_modified = last_modified;
    }

    public long getRead_messages() {
        return read_messages;
    }

    public void setRead_messages(long read_messages) {
        this.read_messages = read_messages;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
