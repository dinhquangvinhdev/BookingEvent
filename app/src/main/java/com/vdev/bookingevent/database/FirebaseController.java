package com.vdev.bookingevent.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseController {
    private DatabaseReference mDatabase;
    private MConvertTime mConvertTime;
    private List<Room> roomList = new ArrayList<>();
    private List<Event> eventList = new ArrayList<>();

    public FirebaseController() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        mConvertTime = new MConvertTime();

        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get room
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Room rom = dataSnapshot.getValue(Room.class);
                    if(rom != null && !roomList.contains(rom)){
                        roomList.add(rom);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        };

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("bibibla", "onDataChange: hello " + snapshot.exists());
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("bibibla", "onDataChange: " + dataSnapshot.getValue(Event.class).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        };
    }

    public boolean addEvent(String title, String summery, Date dateCreated, Date dateUpdated, Date dateStart, Date dateEnd, int room_id, int numberParticipant, int status){
        Event event = new Event();
        event.setId(3);
        event.setTitle(title);
        event.setSummery(summery);
        event.setDateCreated(mConvertTime.convertDateToMili(dateCreated));
        event.setDateUpdated(mConvertTime.convertDateToMili(dateUpdated));
        event.setDateStart(mConvertTime.convertDateToMili(dateStart));
        event.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
        event.setRoom_id(0);
        event.setNumberParticipant(1);
        event.setStatus(status);

        mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event);

        return true;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }
}
