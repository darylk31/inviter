package invite.hfad.com.inviter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daryl on 9/14/2016.
 */

public class Event implements Parcelable{
    private String startDate;
    private String endDate;
    private String event_name;
    private String description;
    private String eventId;
    private String location;
    private String creator;

    private final int PARAMETERS = 6;

    public Event(){};

    public Event (String startDate,
                  String endDate,
                  String event_name,
                  String description,
                  String creator,
                  String location) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.event_name = event_name;
        this.description = description;
        this.creator = creator;
        this.location = location;
        //chat object
    }

    public Event(Parcel in){
        String[] data = new String[PARAMETERS];
        in.readStringArray(data);
        for(int i = 0 ; i < data.length ; i++)
            System.out.println(i + ":" + data[i]);
        this.startDate = data[0];
        this.endDate = data[1];
        this.event_name = data[2];
        this.description = data[3];
        this.creator = data[4];
        this.location = data[5];
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{startDate,endDate,event_name,description,creator,location});
    }


    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString(){
        String a = "startDate:" + startDate + "endDate: " + endDate + " event_name:" + event_name + " description:" + description + " creator:" + creator;
        return a;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
