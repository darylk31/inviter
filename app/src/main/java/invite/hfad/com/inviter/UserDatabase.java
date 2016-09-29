package invite.hfad.com.inviter;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daryl on 9/22/2016.
 */
public class UserDatabase {
    public int userId;
    public LinkedList<Event> events;

    public UserDatabase(){
        this.events = new LinkedList<>();
        addEvent(new Event("Jan 11", "7:00pm", "Badminton", "testing"));
        addEvent(new Event("Jan 12", "7:00pm", "Badminton", "testing"));
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
