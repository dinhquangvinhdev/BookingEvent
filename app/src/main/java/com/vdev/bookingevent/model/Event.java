package com.vdev.bookingevent.model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Event {
    int id;
    String title;
    String summery;
    long dateCreated;
    long dateUpdated;
    long dateStart;
    long dateEnd;
    int room_id;
    int numberParticipant;
    int status;

    public Event() {
    }

    public Event(int id, String title, String summery, long dateCreated, long dateUpdated, long dateStart, long dateEnd, int room_id, int numberParticipant, int status) {
        this.id = id;
        this.title = title;
        this.summery = summery;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.room_id = room_id;
        this.numberParticipant = numberParticipant;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getNumberParticipant() {
        return numberParticipant;
    }

    public void setNumberParticipant(int numberParticipant) {
        this.numberParticipant = numberParticipant;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summery='" + summery + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                ", room_id=" + room_id +
                ", numberParticipant=" + numberParticipant +
                ", status=" + status +
                '}';
    }

    @Exclude
    public String getRoomColor(){
        //TODO must delete it when you can call xml from firebase
        return "#123456";
    }
}
