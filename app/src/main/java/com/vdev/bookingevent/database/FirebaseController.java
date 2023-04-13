package com.vdev.bookingevent.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class FirebaseController {
    private DatabaseReference mDatabase;

    public FirebaseController() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.child("Event").getChildren()){
                    Log.d("bibibla", "onDataChange: " + dataSnapshot.getValue(Event.class).toString());
                }
                for(DataSnapshot dataSnapshot : snapshot.child("Room").getChildren()){
                    Log.d("bibibla", "onDataChange: " + dataSnapshot.getValue(Room.class).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        };

        mDatabase.addValueEventListener(eventListener);
    }

    public boolean addEvent(String title, String summery, Date dateCreated, Date dateUpdated, Date dateStart, Date dateEnd, int room_id, int numberParticipant, int status){
        Event event = new Event();
        event.setId(0);
        event.setTitle(title);
        event.setSummery(summery);
        event.setDateCreated(convertDateToMilliseconds(dateCreated));
        event.setDateUpdated(convertDateToMilliseconds(dateUpdated));
        event.setDateStart(convertDateToMilliseconds(dateStart));
        event.setDateEnd(convertDateToMilliseconds(dateEnd));
        event.setRoom_id(0);
        event.setNumberParticipant(1);
        event.setStatus(status);

        mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event);

        return true;
    }

    private long convertDateToMilliseconds(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
