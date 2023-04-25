package com.vdev.bookingevent.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.vdev.bookingevent.common.MData;

import java.io.Serializable;
import java.util.Date;

public class Event implements Parcelable {
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
    @Exclude
    String roomColor;

    public Event() {
    }

    protected Event(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.summery = in.readString();
        this.dateCreated = in.readLong();
        this.dateUpdated = in.readLong();
        this.dateStart = in.readLong();
        this.dateEnd = in.readLong();
        this.room_id = in.readInt();
        this.numberParticipant = in.readInt();
        this.status = in.readInt();
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
        if(roomColor == null){
            for(int i=0 ; i<MData.arrRoom.size() ; i++){
                Room room = MData.arrRoom.get(i);
                if(room.getId() == room_id){
                    return room.getColor();
                }
            }
            // this is for the room was deleted but the event not yet
            return "#123456";
        }
        return roomColor;
    }

    @Exclude
    @Override
    public boolean equals(@Nullable Object obj) {
        Event tempEvent = (Event) obj;
        return this.getId() == tempEvent.getId();
    }

    @Exclude
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Exclude
    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Exclude
    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(summery);
        parcel.writeLong(dateCreated);
        parcel.writeLong(dateUpdated);
        parcel.writeLong(dateStart);
        parcel.writeLong(dateEnd);
        parcel.writeInt(room_id);
        parcel.writeInt(numberParticipant);
        parcel.writeInt(status);
    }

}
