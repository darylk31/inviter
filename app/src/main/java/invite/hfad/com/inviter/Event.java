package invite.hfad.com.inviter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daryl on 9/14/2016.
 */

public class Event implements Parcelable{
    private String day;
    private String time;
    private String end_day;
    private String end_time;
    private String event_name;
    private String description;
    private int eventId;
    private List invitedId;
    private String creator;

    public Event(){};

    public Event (String day,
                  String time,
                  String end_day,
                  String end_time,
                  String event_name,
                  String description,
                  String creator) {
        this.day = day;
        this.time = time;
        this.end_day = end_day;
        this.end_time = end_time;
        this.event_name = event_name;
        this.description = description;
        //location
        this.creator = creator;
        //chat object
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEnd_day() {
        return end_day;
    }

    public void setEnd_day(String end_day) {
        this.end_day = end_day;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List getInvitedId() {
        return invitedId;
    }

    public void setInvitedId(List invitedId) {
        this.invitedId = invitedId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ;
    }
}
