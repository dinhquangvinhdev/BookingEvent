package com.vdev.bookingevent.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdev.bookingevent.callback.CallbackAddDetailParticipant;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.model.Department;
import com.vdev.bookingevent.model.Detail_participant;
import com.vdev.bookingevent.model.Email;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;
import com.vdev.bookingevent.presenter.LoginContract;
import com.vdev.bookingevent.view.MainActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public boolean addEvent(Event event){
        //TODO check internet when call this function

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
    public void getEventWithRoomId(String title, int roomId, String startDate, String endDate) {
        //TODO check internet when call this function
        //convert time
        long startDateMili = -1 , endDateMili = -1;
        if(!startDate.equals("")){
            startDateMili = mConvertTime.convertStringToMili(startDate);
        }
        if(!endDate.equals("")){
            endDateMili = mConvertTime.convertStringToMili(endDate);
        }

        //query from database
        long finalStartDateMili = startDateMili;
        long finalEndDateMili = endDateMili;
        mDatabase.child("Event")
                .orderByChild("room_id")
                .equalTo(roomId)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for(DataSnapshot dataSnapshot : result.getChildren()){
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                //check filter
                               if(!title.equals("")){
                                    if(!tempEvent.getTitle().contains(title)){
                                        continue;
                                    }
                               }
                               if(finalStartDateMili >= 0){
                                    if(tempEvent.getDateStart() < finalStartDateMili){
                                        continue;
                                    }
                               }
                               if(finalEndDateMili >= 0){
                                   if(tempEvent.getDateEnd() > finalEndDateMili){
                                       continue;
                                   }
                               }
                               //add event
                                arrEventResult.add(tempEvent);
                            }
                            //call back
                            callbackUpdateEventDisplay.updateEvent(arrEventResult);
                        } else {
                            Log.d("bibibla", "onComplete: " + task.getException());
                        }
                    }
                });
    }
    public void getEventWithTitle(String title) {
        //TODO check internet when call this function
        mDatabase.child("Event")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                if(tempEvent.getTitle().contains(title)){
                                    arrEventResult.add(tempEvent);
                                }
                            }
                            //call back
                            callbackUpdateEventDisplay.updateEvent(arrEventResult);
                        } else {
                            Log.d("bibibla", "onComplete: " + task.getException());
                        }
                    }
                });
    }
    public void getEventWithStartDate(String title, String startDate, String endDate) {
        //TODO check internet when call this function
        long startDateMili = -1 , endDateMili = -1;
        if(!startDate.equals("")){
            startDateMili = mConvertTime.convertStringToMili(startDate);
        }
        if(!endDate.equals("")){
            endDateMili = mConvertTime.convertStringToMili(endDate);
        }

        //query from database
        long finalStartDateMili = startDateMili;
        long finalEndDateMili = endDateMili;
        mDatabase.child("Event")
                .orderByChild("dateStart")
                .startAt(startDateMili)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                if(!title.equals("")){
                                    if(!tempEvent.getTitle().contains(title)){
                                        continue;
                                    }
                                }
                                if(finalEndDateMili >= 0){
                                    if(tempEvent.getDateEnd() > finalEndDateMili){
                                        continue;
                                    }
                                }
                                //add event
                                arrEventResult.add(tempEvent);
                            }
                            //call back
                            callbackUpdateEventDisplay.updateEvent(arrEventResult);
                        } else {
                            Log.d("bibibla", "onComplete: " + task.getException());
                        }
                    }
                });
    }
    public void getEventWithEndDate(String title, String endDate) {
        //TODO check internet when call this function
        long startDateMili = -1 , endDateMili = -1;
        if(!endDate.equals("")){
            endDateMili = mConvertTime.convertStringToMili(endDate);
        }
        //query
        mDatabase.child("Event")
                .orderByChild("dateEnd")
                .endAt(endDateMili)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                if(!title.equals("")){
                                    if(!tempEvent.getTitle().contains(title)){
                                        continue;
                                    }
                                }
                                //add event
                                arrEventResult.add(tempEvent);
                            }
                            //call back
                            callbackUpdateEventDisplay.updateEvent(arrEventResult);
                        } else {
                            Log.d("bibibla", "onComplete: " + task.getException());
                        }
                    }
                });
    }
    public void checkAddNewEvent(Event tempEvent) {
        //get all event of the day want to add
        LocalDate tempLC = mConvertTime.convertMiliToLocalDate(tempEvent.getDateStart());
        long timeStart = mConvertTime.getMiliStartDayFromLocalDate(tempLC);
        long timeEnd = mConvertTime.getMiliLastDayFromLocalDate(tempLC);
        mDatabase.child("Event")
                .orderByChild("dateStart")
                .startAt(timeStart)
                .endAt(timeEnd)
                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot result = task.getResult();
                            boolean canAdd = true;
                            //check duplicate event
                            for(DataSnapshot dataSnapshot : result.getChildren()){
                                Event event1 = dataSnapshot.getValue(Event.class);
                                if(event1.getRoom_id() == tempEvent.getRoom_id()){
                                    if((tempEvent.getDateStart() < event1.getDateStart() && tempEvent.getDateEnd() < event1.getDateStart())
                                        || (tempEvent.getDateStart() > event1.getDateEnd() && tempEvent.getDateEnd() > event1.getDateEnd() )){
                                        //can add the event
                                        canAdd = true;
                                        break;
                                    } else {
                                        canAdd = false;
                                        Log.d("bibibla", "onComplete: " + "can not add because already event there");
                                    }
                                } else {
                                    Log.d("bibibla", "onComplete: " + "different room");
                                    canAdd = false;
                                }
                            }
                            //check can add
                            if(canAdd){
                                callbackAddDetailParticipant.callbackCanAddNewEvent(tempEvent);
                            } else {
                                callbackAddDetailParticipant.callbackCanAddNewEvent(null);
                            }
                        } else {
                            Log.d("bibibla", "onComplete: " + task.getException());
                        }
                    }
                });

    }


    public void checkUserAccount(LoginContract.View view, Context context, MDialog mDialog, String uid, String email) {

        mDatabase.child("Email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    boolean check = false;
                    // check account
                    for(DataSnapshot dataSnapshot : result.getChildren()){
                        Email email1 = dataSnapshot.getValue(Email.class);
                        if(email1.getId().equals(uid)){
                            //start main activity
                            check = true;
                            break;
                        }
                    }
                    // notification account not active
                    if (!check){
                        mDialog.showFillData(context , "The account is not activated");
                        //logout account
                        logoutAccount(context);
                        view.turnOffProgressBar();
                    } else {
                        view.startActivity(MainActivity.class);
                    }
                } else {
                    Log.d("bibibla", "onComplete: " + task.getException());
                    view.turnOffProgressBar();
                }
            }
        });
    }

    private void logoutAccount(Context context) {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gsio = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        GoogleSignInClient gsic = GoogleSignIn.getClient(context, gsio);
        gsic.signOut();
    }
}
