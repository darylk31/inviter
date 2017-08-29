package invite.hfad.com.inviter.EventObjectModel;

import java.util.Date;

/**
 * Created by Jimmy on 3/18/2017.
 */

public class EventObject {
    private String eventTitle;
    private Date eventDate;
    private Date endDate;
    private String eventNote;
    private String userName;
    private EventPage eventPage;

    public EventObject(String userName,String eventTitle, String eventNote, Date eventDate){
        this.userName = userName;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventNote = eventNote;
    }

    public EventObject(){

    }


    public String getEventTitle(){
        return eventTitle;
    }

    public String getEventNote() {
        return eventNote;
    }

    public Date getEventDate(){
        return eventDate;
    }

    public void setEventTitle(String eventTitle){
        this.eventTitle = eventTitle;
    }

    public void setEventNote(String eventNote){
        this.eventNote = eventNote;
    }

    public void setEventDate(Date eventDate){
        this.eventDate = eventDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
