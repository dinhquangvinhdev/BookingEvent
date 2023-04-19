package com.vdev.bookingevent.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdev.bookingevent.callback.CallbackAddDetailParticipant;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.model.Department;
import com.vdev.bookingevent.model.Detail_participant;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.util.Date;

public final class FirebaseController {
    private DatabaseReference mDatabase;
    private MConvertTime mConvertTime;
    private CallbackUpdateEventDisplay callbackUpdateEventDisplay;
    private CallbackAddDetailParticipant callbackAddDetailParticipant;

    public FirebaseController(CallbackUpdateEventDisplay callbackUpdateEventDisplay, CallbackAddDetailParticipant callbackAddDetailParticipant) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        mConvertTime = new MConvertTime();
        this.callbackUpdateEventDisplay = callbackUpdateEventDisplay;
        this.callbackAddDetailParticipant = callbackAddDetailParticipant;

        mDatabase.child("Event").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    MData.id_event = event.getId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        });
        mDatabase.child("Detail_participant").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MData.id_detail_participant = Integer.parseInt(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        });
    }

    public boolean addEvent(String title, String summery, Date dateCreated, Date dateUpdated, Date dateStart, Date dateEnd, int room_id, int numberParticipant, int status){
        //TODO check internet when call this function

        Event event = new Event();
        event.setTitle(title);
        event.setSummery(summery);
        event.setDateCreated(mConvertTime.convertDateToMili(dateCreated));
        event.setDateUpdated(mConvertTime.convertDateToMili(dateUpdated));
        event.setDateStart(mConvertTime.convertDateToMili(dateStart));
        event.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
        event.setRoom_id(room_id);
        event.setNumberParticipant(numberParticipant);
        event.setStatus(status);

        if(MData.id_event != -1){
            MData.id_event += 1;
            event.setId(MData.id_event);
            mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event);
            return true;
        } else {
            return false;
        }
    }

    public boolean addEventDetailParticipant(int event_id , int user_id , String role){
        //TODO check internet when call this function
        Detail_participant detailParticipant = new Detail_participant();
        detailParticipant.setEvent_id(event_id);
        detailParticipant.setUser_id(user_id);
        detailParticipant.setRole(role);

        Log.d("bibibla", "addEventDetailParticipant: calling here");

        //add event detail
        if(MData.id_detail_participant != -1){  //can not update while not update the last data from backend
            MData.id_detail_participant += 1;
            mDatabase.child("Detail_participant").child(String.valueOf(MData.id_detail_participant)).setValue(detailParticipant);
            mDatabase.child("Detail_participant").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Integer.parseInt(dataSnapshot.getKey()) == MData.id_detail_participant){
                            callbackAddDetailParticipant.callbackAddDetailParticipant();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return true;
        } else {
            return false;
        }
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

    public void getDepartment(){
        //TODO check internet when call this function

        ValueEventListener departmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MData.arrDepartment.clear();
                //get department
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Department department = dataSnapshot.getValue(Department.class);
                    if(department != null && !MData.arrDepartment.contains(department)){
                        MData.arrDepartment.add(department);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        };

        mDatabase.child("Department").addValueEventListener(departmentListener);
    }

    public void getEventInRange1(double startAtDS, double endAtDS, double startAtDE, double endAtDE){
        //TODO check internet when call this function
    }

    public void getEventInRange2(long startTime, long endTime){
        //TODO check internet when call this function

        mDatabase.child("Event")
                .orderByChild("dateStart")
                .startAt(startTime,"dateStart")
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
                        callbackUpdateEventDisplay.updateEvent(MData.arrEvent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("bibibla", "loadEvent:onCancelled", error.toException());
                    }
                });
    }
}
