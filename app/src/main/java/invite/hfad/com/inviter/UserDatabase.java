package invite.hfad.com.inviter;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Daryl on 9/22/2016.
 */
public class UserDatabase {
    public int userId;
    public String userName;
    public LinkedList<Event> events;
    public LinkedList<Event> invites;

    public UserDatabase(){
        this.events = new LinkedList<>();
        this.userId = 1234;
        this.userName = "Selena Gomez";

        Event E1 = new Event("Jan 11", "7:00pm", "Badminton", "testing");
        Event E2 = new Event("Jan 12", "7:00pm", "Badminton", "testing");
        addEvent(E1);
        addEvent(E2);
        E1.update_eventId(123);
        E2.update_eventId(213);

        this.invites = new LinkedList<>();
        Event I1 = new Event("Jan 11", "7:00pm", "BJ", "testing");
        Event I2 = new Event("Jan 12", "7:00pm", "BJ", "testing");
        addInvite(I1);
        addInvite(I2);
        I1.update_eventId(520);
        I2.update_eventId(1314);
        I1.update_creator("Selena Gomez");
        I2.update_creator("Selena Gomez");
    }

    public void addEvent(Event event){
        this.events.add(event);
    }

    public void addInvite(Event event) { this.invites.add(event);}

    public int getUserId(){
        return this.userId;
    }

    public LinkedList<Event> getEvents(){
        return this.events;
    }

    public Event getEvent(int eventid) {
        //...
        return events.getFirst();
    }

    public LinkedList<Event> getInvites() {return this.invites; }

    public String getUserName(int userId) {return this.userName; }

}
