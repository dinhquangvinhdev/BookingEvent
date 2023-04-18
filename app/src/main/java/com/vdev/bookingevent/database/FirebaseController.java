package com.vdev.bookingevent.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.util.Date;

public final class FirebaseController {
    private DatabaseReference mDatabase;
    private MConvertTime mConvertTime;
    private CallbackUpdateEventDisplay callback;

    public FirebaseController(CallbackUpdateEventDisplay callback) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        mConvertTime = new MConvertTime();
        this.callback = callback;
    }

    public boolean addEvent(String title, String summery, Date dateCreated, Date dateUpdated, Date dateStart, Date dateEnd, int room_id, int numberParticipant, int status){
        //TODO check internet when call this function

        Event event = new Event();
        event.setId(room_id);
        event.setTitle(title);
        event.setSummery(summery);
        event.setDateCreated(mConvertTime.convertDateToMili(dateCreated));
        event.setDateUpdated(mConvertTime.convertDateToMili(dateUpdated));
        event.setDateStart(mConvertTime.convertDateToMili(dateStart));
        event.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
        event.setRoom_id(room_id);
        event.setNumberParticipant(numberParticipant);
        event.setStatus(status);

        mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event);

        return true;
    }

    public void getRoom(){
        //TODO check internet when call this function

        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get room
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Room rom = dataSnapshot.getValue(Room.class);
                    if(rom != null && !MData.arrRoom.contains(rom)){
                        MData.arrRoom.add(rom);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        };

        mDatabase.child("Room").addValueEventListener(roomListener);
    }

    public void getEventInRange1(double startAtDS, double endAtDS, double startAtDE, double endAtDE){
        //TODO check internet when call this function
    }

    public void getEventInRange2(long startTime, long endTime){
        //TODO check internet when call this function

        mDatabase.child("Event")
                .orderByChild("dateStart")
                .startAt(startTime,"dateStart")
                //TODO need fix it because the value not update when data change
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Event tempEvent = dataSnapshot.getValue(Event.class);
                            if(tempEvent.getDateEnd() <= endTime){
                                if(!MData.arrEvent.contains(tempEvent)) {
                                    //add event if it not exist
                                    MData.arrEvent.add(tempEvent);
                                } else {
                                    //update event if it exist
                                    for(int i=0 ; i<MData.arrEvent.size() ; i++){
                                        Event event = MData.arrEvent.get(i);
                                        if(event.getId() == tempEvent.getId()){
                                            MData.arrEvent.set(i, tempEvent);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        callback.updateEvent(MData.arrEvent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("bibibla", "loadEvent:onCancelled", error.toException());
                    }
                });
    }
}
