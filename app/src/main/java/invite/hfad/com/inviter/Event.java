package invite.hfad.com.inviter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daryl on 9/14/2016.
 */
//TESTING
public class Event {
    private String month;
    private String day;
    private String time;
    private String event_name;
    private String description;
    private int eventId;
    private List invitedId;

    private Event (String month, String day, String time, String event_name, String description) {
        this.month = month;
        this.day = day;
        this.time = time;
        this.event_name = event_name;
        this.description = description;
        this.invitedId = new LinkedList();
    }

    public String getMonth(){return month;}

    public String getTime(){return time;}

    public String getEvent_name(){return event_name;}

    public String getDescription(){return description;}

    public void update_eventId(int id){
        this.eventId = id;
    }

    public int getEventId(){return eventId;}

    public void add_invitedId(int userId){
        invitedId.add(userId);
    }

    public void remove_invitedId(int userId){
        invitedId.remove(userId);
    }
}
