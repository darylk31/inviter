package invite.hfad.com.inviter;

/**
 * Created by Daryl on 9/22/2016.
 */
public class UserDatabase {
    public int userId;
    public Event[] events;

    public UserDatabase(int userId, Event[] events){
        this.userId = userId;
        this.events = new Event[]{
                new Event("Nov 11", "10:00pm", "Badminton", "Badminton Vancouver"),
                new Event("Sept 12", "12:00pm", "Basketball", "Tbird")
        };
    }

    public void addEvent(Event event){
        events[events.length + 1] = event;
    }

    public int getUserId(){
        return this.userId;
    }

    public Event[] getEvents(){
        return this.events;
    }
}
