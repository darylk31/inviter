package invite.hfad.com.inviter;

import java.util.LinkedList;

/**
 * Created by Daryl on 9/22/2016.
 */
public class UserDatabase {
    public int userId;
    public LinkedList<Event> events;

    public UserDatabase(){
        this.events = new LinkedList<>();

        Event E1 = new Event("Jan 11", "7:00pm", "Badminton", "testing");
        Event E2 = new Event("Jan 12", "7:00pm", "Badminton", "testing");
        addEvent(E1);
        addEvent(E2);
        E1.update_eventId(123);
        E2.update_eventId(213);
    }


    public void addEvent(Event event){
        this.events.add(event);
    }

    public int getUserId(){
        return this.userId;
    }

    public LinkedList<Event> getEvents(){
        return this.events;
    }
}
