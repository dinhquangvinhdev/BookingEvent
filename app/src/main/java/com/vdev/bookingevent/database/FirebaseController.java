package com.vdev.bookingevent.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdev.bookingevent.callback.CallbackAddEvent;
import com.vdev.bookingevent.callback.CallbackDetailEvent;
import com.vdev.bookingevent.callback.CallbackEditEvent;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.model.Department;
import com.vdev.bookingevent.model.Detail_participant;
import com.vdev.bookingevent.model.Email;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Role;
import com.vdev.bookingevent.model.Room;
import com.vdev.bookingevent.model.User;
import com.vdev.bookingevent.presenter.LoginContract;
import com.vdev.bookingevent.view.MainActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FirebaseController {
    private DatabaseReference mDatabase;
    private MConvertTime mConvertTime;
    private CallbackUpdateEventDisplay callbackUpdateEventDisplay;
    private CallbackAddEvent callbackAddEvent;
    private CallbackEditEvent callbackEditEvent;
    private CallbackDetailEvent callbackDetailEvent;

    public FirebaseController(CallbackUpdateEventDisplay callbackUpdateEventDisplay, CallbackAddEvent callbackAddEvent,
                              CallbackEditEvent callbackEditEvent, CallbackDetailEvent callbackDetailEvent) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        mConvertTime = new MConvertTime();
        this.callbackUpdateEventDisplay = callbackUpdateEventDisplay;
        this.callbackAddEvent = callbackAddEvent;
        this.callbackEditEvent = callbackEditEvent;
        this.callbackDetailEvent = callbackDetailEvent;
    }

    public void addEvent(Event event) {
        //TODO check internet when call this function
        //get the last item and the id event newest
        mDatabase.child("Event").orderByKey().limitToLast(1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Event event1 = snapshot.getValue(Event.class);
                        if(event1 == null){
                            MData.id_event = 0;
                        } else {
                            MData.id_event = event1.getId();
                        }
                        //add event
                        if (MData.id_event != -1) {
                            MData.id_event += 1;
                            event.setId(MData.id_event);
                            mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        callbackAddEvent.callbackAddEventSuccess(true);
                                    } else {
                                        callbackAddEvent.callbackAddEventSuccess(false);
                                    }
                                }
                            });
                        } else {
                            callbackAddEvent.callbackAddEventSuccess(false);
                        }
                        break;
                    }
                } else {
                    callbackAddEvent.callbackAddEventSuccess(false);
                }
            }
        });
    }

    public void addEventDetailParticipant(int event_id, List<User> guests) {
        //TODO check internet when call this function
        Detail_participant detailParticipantHost = new Detail_participant();
        detailParticipantHost.setEvent_id(event_id);
        detailParticipantHost.setUser_id(MData.userLogin.getId());
        detailParticipantHost.setRole(MConst.ROLE_HOST);

        List<Detail_participant> dpGuest = new ArrayList<>();
        for (int i = 0; i < guests.size(); i++) {
            Detail_participant dp = new Detail_participant();
            dp.setEvent_id(event_id);
            dp.setUser_id(guests.get(i).getId());
            dp.setRole(MConst.ROLE_GUEST);
            dpGuest.add(dp);
        }

        //add guest
        for(int i=0 ; i<dpGuest.size() ; i++){
            mDatabase.child("Detail_participant").push().setValue(dpGuest.get(i));
        }
        //add detail participant host
        mDatabase.child("Detail_participant").push().setValue(detailParticipantHost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callbackAddEvent.callbackAddDetailParticipant(true);
                } else {
                    callbackAddEvent.callbackAddDetailParticipant(false);
                }
            }
        });
    }

    public void editEventDetailParticipant(int event_id , List<User> addGuests, List<User> removeGuests){
        //TODO check internet when call this function
        //create list add guest
        List<Detail_participant> dpAddGuest = new ArrayList<>();
        for (int i = 0; i < addGuests.size(); i++) {
            Detail_participant dp = new Detail_participant();
            dp.setEvent_id(event_id);
            dp.setUser_id(addGuests.get(i).getId());
            dp.setRole(MConst.ROLE_GUEST);
            dpAddGuest.add(dp);
        }

        if(!addGuests.isEmpty() && removeGuests.isEmpty()){                     //case add guest not empty but removeGuest is empty
            //add guest
            for(int i=0 ; i<dpAddGuest.size() ; i++) {
                if (i == dpAddGuest.size() - 1) {
                    mDatabase.child("Detail_participant").push().setValue(dpAddGuest.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                callbackEditEvent.callbackAddDetailParticipant(true);
                            } else {
                                callbackEditEvent.callbackAddDetailParticipant(false);
                            }
                        }
                    });
                } else {
                    mDatabase.child("Detail_participant").push().setValue(dpAddGuest.get(i));
                }
            }

        } else if(addGuests.isEmpty() && !removeGuests.isEmpty()){              //case add guest is empty but removeGuest is not empty
            mDatabase.child("Detail_participant").orderByChild("event_id").equalTo(event_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            Detail_participant detailParticipant = dataSnapshot.getValue(Detail_participant.class);
                            for(int i=0 ; i<removeGuests.size() ; i++){
                                if(detailParticipant.getUser_id() == removeGuests.get(i).getId()){
                                    mDatabase.child("Detail_participant").child(dataSnapshot.getKey()).removeValue();
                                    break;
                                }
                            }
                        }
                        callbackEditEvent.callbackAddDetailParticipant(true);
                    } else {
                        callbackEditEvent.callbackAddDetailParticipant(false);
                    }
                }
            });

        } else {                                                                //case both add guest and removeGuest are not empty
            //remove guest
            //remove guest
            mDatabase.child("Detail_participant").orderByChild("event_id").equalTo(event_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            Detail_participant detailParticipant = dataSnapshot.getValue(Detail_participant.class);
                            for(int i=0 ; i<removeGuests.size() ; i++){
                                if(detailParticipant.getUser_id() == removeGuests.get(i).getId()){
                                    mDatabase.child("Detail_participant").child(dataSnapshot.getKey()).removeValue();
                                    break;
                                }
                            }
                        }
                    }
                }
            });
            //add guest
            for(int i=0 ; i<dpAddGuest.size() ; i++) {
                if (i == dpAddGuest.size() - 1) {
                    mDatabase.child("Detail_participant").push().setValue(dpAddGuest.get(i)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                callbackEditEvent.callbackAddDetailParticipant(true);
                            } else {
                                callbackEditEvent.callbackAddDetailParticipant(false);
                            }
                        }
                    });
                } else {
                    mDatabase.child("Detail_participant").push().setValue(dpAddGuest.get(i));
                }
            }
        }
    }

    public void getRoom() {
        //TODO check internet when call this function
        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get room
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Room rom = dataSnapshot.getValue(Room.class);
                    if (rom != null && !MData.arrRoom.contains(rom)) {
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

    public void getDepartment() {
        //TODO check internet when call this function

        ValueEventListener departmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MData.arrDepartment.clear();
                //get department
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Department department = dataSnapshot.getValue(Department.class);
                    if (department != null && !MData.arrDepartment.contains(department)) {
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

    public void getUser() {
        mDatabase.child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null && !MData.arrUser.contains(user)) {
                    MData.arrUser.add(user);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                for (int i = 0; i < MData.arrUser.size(); i++) {
                    if (MData.arrUser.get(i).getId() == user.getId()) {
                        MData.arrUser.set(i, user);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                for (int i = 0; i < MData.arrUser.size(); i++) {
                    if (MData.arrUser.get(i).getId() == user.getId()) {
                        MData.arrUser.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("bibibla", "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("bibibla", "onChildCancelled: ");
            }
        });
    }

    public void getEmail() {
        mDatabase.child("Email").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Email email = snapshot.getValue(Email.class);
                if (email != null && !MData.arrEvent.contains(email)) {
                    MData.arrEmail.add(email);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Email email = snapshot.getValue(Email.class);
                for (int i = 0; i < MData.arrEmail.size(); i++) {
                    if (MData.arrEmail.get(i).getId() == email.getId()) {
                        MData.arrEmail.set(i, email);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Email email = snapshot.getValue(Email.class);
                for (int i = 0; i < MData.arrEmail.size(); i++) {
                    if (MData.arrEmail.get(i).getId() == email.getId()) {
                        MData.arrEmail.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("bibibla", "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("bibibla", "onChildCancelled: ");
            }
        });
    }

    public void getRole() {
        mDatabase.child("Role").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Role role = snapshot.getValue(Role.class);
                if (role != null && !MData.arrRole.contains(role)) {
                    MData.arrRole.add(role);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Role role = snapshot.getValue(Role.class);
                for (int i = 0; i < MData.arrRole.size(); i++) {
                    if (MData.arrRole.get(i).getId() == role.getId()) {
                        MData.arrRole.set(i, role);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Role role = snapshot.getValue(Role.class);
                for (int i = 0; i < MData.arrRole.size(); i++) {
                    if (MData.arrRole.get(i).getId() == role.getId()) {
                        MData.arrRole.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("bibibla", "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("bibibla", "onChildCancelled: ");
            }
        });
    }

    public void getAllEvent() {
        mDatabase.child("Event").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //must check != null because may be user in the add activity
                if (callbackUpdateEventDisplay != null) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null && !MData.arrEvent.contains(event) && event.getStatus() == 0) {
                        MData.arrEvent.add(event);
                        callbackUpdateEventDisplay.updateEvent(MData.arrEvent);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //must check because may be user in the add activity
                if (callbackUpdateEventDisplay != null) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        //update status event
                        for (int i = 0; i < MData.arrEvent.size(); i++) {
                            Event tempEvent = MData.arrEvent.get(i);
                            if (tempEvent.getId() == event.getId()) {
                                MData.arrEvent.set(i, event);
                                break;
                            }
                        }
                        callbackUpdateEventDisplay.updateEvent(MData.arrEvent);
                        Log.d("bibibla", "onChildChanged: " + event.getId());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //must check because may be user in the add activity
                if (callbackUpdateEventDisplay != null) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        MData.arrEvent.remove(event);
                        callbackUpdateEventDisplay.updateEvent(MData.arrEvent);
                        Log.d("bibibla", "onChildReMoved: " + event.getTitle());
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (callbackUpdateEventDisplay != null) {
                    callbackUpdateEventDisplay.updateEvent(MData.arrEvent);
                    Log.d("bibibla", "onChildMoved: " + previousChildName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
            }
        });
    }

    public void getEventInRange2(long startTime, long endTime) {
        //TODO check internet when call this function

        mDatabase.child("Event")
                .orderByChild("dateStart")
                .startAt(startTime, "dateStart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Event tempEvent = dataSnapshot.getValue(Event.class);
                            if (tempEvent.getDateEnd() <= endTime) {
                                if (!MData.arrEvent.contains(tempEvent)) {
                                    //add event if it not exist
                                    MData.arrEvent.add(tempEvent);
                                } else {
                                    //update event if it exist
                                    for (int i = 0; i < MData.arrEvent.size(); i++) {
                                        Event event = MData.arrEvent.get(i);
                                        if (event.getId() == tempEvent.getId()) {
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
        long startDateMili = -1, endDateMili = -1;
        if (!startDate.equals("")) {
            startDateMili = mConvertTime.convertStringToMili(startDate);
        }
        if (!endDate.equals("")) {
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
                        if (task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                //check event is deleted
                                if (tempEvent == null || tempEvent.getStatus() == -1) {
                                    continue;
                                }
                                //check filter
                                if (!title.equals("")) {
                                    if (!tempEvent.getTitle().contains(title)) {
                                        continue;
                                    }
                                }
                                if (finalStartDateMili >= 0) {
                                    if (tempEvent.getDateStart() < finalStartDateMili) {
                                        continue;
                                    }
                                }
                                if (finalEndDateMili >= 0) {
                                    if (tempEvent.getDateEnd() > finalEndDateMili) {
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
                        if (task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                //check event is deleted
                                if (tempEvent == null || tempEvent.getStatus() == -1) {
                                    continue;
                                }
                                //filter
                                if (tempEvent.getTitle().contains(title)) {
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
        long startDateMili = -1, endDateMili = -1;
        if (!startDate.equals("")) {
            startDateMili = mConvertTime.convertStringToMili(startDate);
        }
        if (!endDate.equals("")) {
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
                        if (task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                //check event is deleted
                                if (tempEvent == null || tempEvent.getStatus() == -1) {
                                    continue;
                                }
                                //filter
                                if (!title.equals("")) {
                                    if (!tempEvent.getTitle().contains(title)) {
                                        continue;
                                    }
                                }
                                if (finalEndDateMili >= 0) {
                                    if (tempEvent.getDateEnd() > finalEndDateMili) {
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
        long startDateMili = -1, endDateMili = -1;
        if (!endDate.equals("")) {
            endDateMili = mConvertTime.convertStringToMili(endDate);
        }
        //query
        mDatabase.child("Event")
                .orderByChild("dateEnd")
                .endAt(endDateMili)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Event> arrEventResult = new ArrayList<>();
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event tempEvent = dataSnapshot.getValue(Event.class);
                                //check event is deleted
                                if (tempEvent == null || tempEvent.getStatus() == -1) {
                                    continue;
                                }
                                //filter
                                if (!title.equals("")) {
                                    if (!tempEvent.getTitle().contains(title)) {
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
                        if (task.isSuccessful()) {
                            DataSnapshot result = task.getResult();
                            List<Event> eventsOverlap = new ArrayList<>();
                            //check duplicate event
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event event1 = dataSnapshot.getValue(Event.class);
                                if (event1.getStatus() == 0 && event1.getRoom_id() == tempEvent.getRoom_id()) {
                                    if ((tempEvent.getDateStart() < event1.getDateStart() && tempEvent.getDateEnd() <= event1.getDateStart())
                                            || (tempEvent.getDateStart() >= event1.getDateEnd() && tempEvent.getDateEnd() > event1.getDateEnd())) {
                                        //can add the event
                                        continue;
                                    } else {
                                        eventsOverlap.add(event1);
                                    }
                                } else {
                                    //can add the event
                                    continue;
                                }
                            }
                            //check can add
                            if (eventsOverlap.isEmpty()) {
                                callbackAddEvent.callbackCanAddNewEvent(tempEvent, eventsOverlap);
                            } else {
                                callbackAddEvent.callbackCanAddNewEvent(null, eventsOverlap);
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
                if (task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    boolean check = false;
                    // check account
                    for (DataSnapshot dataSnapshot : result.getChildren()) {
                        Email email1 = dataSnapshot.getValue(Email.class);
                        if (email1.getId().equals(uid)) {
                            //start main activity
                            check = true;
                            break;
                        }
                    }
                    // notification account not active
                    if (!check) {
                        mDialog.showFillData(context, "The account is not activated");
                        //logout account
                        logoutAccount(context);
                        view.turnOffProgressBar();
                    } else {
                        getUserLogin(uid);
                        view.startActivity(MainActivity.class);
                    }
                } else {
                    Log.d("bibibla", "onComplete: " + task.getException());
                    view.turnOffProgressBar();
                }
            }
        });
    }

    private void getUserLogin(String email_id) {
        mDatabase.child("User").orderByChild("email_id").equalTo(email_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MData.userLogin = dataSnapshot.getValue(User.class);
                    Log.d("bibibla", "user name : " + MData.userLogin.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("bibibla", "loadEvent:onCancelled", error.toException());
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

    public void deleteEvent(Event event) {
        //set event status to -1 to delete it
        event.setStatus(-1);
        mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //notification if delete success
                    callbackUpdateEventDisplay.deleteEventSuccess(event);
                } else {
                    Log.d("bibibla", "onComplete: " + task.getException());
                }

            }
        });
    }

    public void checkEditEvent(Event tempEvent) {
        //get all event of the day want to update
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
                        if (task.isSuccessful()) {
                            DataSnapshot result = task.getResult();
                            List<Event> eventsOverlap = new ArrayList<>();
                            //check overlap event
                            for (DataSnapshot dataSnapshot : result.getChildren()) {
                                Event event1 = dataSnapshot.getValue(Event.class);
                                if (event1.getStatus() == 0 && event1.getId() != tempEvent.getId() && event1.getRoom_id() == tempEvent.getRoom_id()) {
                                    if ((tempEvent.getDateStart() < event1.getDateStart() && tempEvent.getDateEnd() <= event1.getDateStart())
                                            || (tempEvent.getDateStart() >= event1.getDateEnd() && tempEvent.getDateEnd() > event1.getDateEnd())) {
                                        //can edit the event
                                        continue;
                                    } else {
                                        eventsOverlap.add(event1);
                                    }
                                } else {
                                    //can edit the event
                                    continue;
                                }
                            }
                            //check can edit
                            if (eventsOverlap.isEmpty()) {
                                callbackEditEvent.callbackEditEvent(tempEvent, eventsOverlap);
                            } else {
                                callbackEditEvent.callbackEditEvent(null , eventsOverlap);
                            }
                        } else {
                            Log.d("bibibla", "onComplete: " + task.getException());
                        }
                    }
                });
    }

    public void editEvent(Event event) {
        mDatabase.child("Event").child(String.valueOf(event.getId())).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callbackEditEvent.editEventSuccess(event);
                } else {
                    Log.d("bibibla", "onComplete: " + task.getException());
                }

            }
        });
    }

    public void getParticipantOfEvent(int eventId) {

        mDatabase.child("Detail_participant").orderByChild("event_id").equalTo(eventId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    User host = new User();
                    List<User> guests = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        Detail_participant detailParticipant = dataSnapshot.getValue(Detail_participant.class);
                        if (detailParticipant.getRole().compareTo(MConst.ROLE_HOST) == 0) {
                            for (int i = 0; i < MData.arrUser.size(); i++) {
                                User user = MData.arrUser.get(i);
                                if (user.getId() == detailParticipant.getUser_id()) {
                                    host = user;
                                }
                            }
                        } else {
                            for (int i = 0; i < MData.arrUser.size(); i++) {
                                User user = MData.arrUser.get(i);
                                if (user.getId() == detailParticipant.getUser_id()) {
                                    guests.add(user);
                                }
                            }
                        }
                    }

                    callbackDetailEvent.callbackShowSlidingPanel(host, guests, eventId);
                } else {
                    Log.d("bibibla", "onComplete: " + task.getException());
                }
            }
        });
    }

    public void getArrHostOfArrEvent(List<Event> events) {
        List<User> arrHost = Arrays.asList(new User[events.size()]);

        mDatabase.child("Detail_participant").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    for (DataSnapshot dataSnapshot : result.getChildren()) {
                        Detail_participant detailParticipant = dataSnapshot.getValue(Detail_participant.class);
                        if (detailParticipant.getRole().compareTo(MConst.ROLE_HOST) == 0) {
                            for (int i = 0; i < events.size(); i++) {
                                if (events.get(i).getId() == detailParticipant.getEvent_id()) {
                                    for (User user : MData.arrUser) {
                                        if (user.getId() == detailParticipant.getUser_id()) {
                                            //set with the index of event
                                            arrHost.set(i, user);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(callbackAddEvent != null) {
                        callbackAddEvent.callbackGetHostEventOverlap(events, arrHost);
                    } else if(callbackEditEvent != null){
                        callbackEditEvent.callbackGetHostEventOverlap(events, arrHost);
                    }

                } else {
                    Log.d("bibibla", "onComplete: " + task.getException());
                }

            }
        });
    }

    /**
     * This method will return 3 case
     * -1 : something bad happen when compare
     * 0 : the login have higher than the host
     * 1 : the host have higher or equal than the login
     *
     * @param userId
     * @return
     */
    public int comparePriorityUser(int userId) {

        //compare id if the login is the host so they can edit any thing to their event
        if (userId == MData.userLogin.getId()) {
            return 0;
        }

        int role_id_user_login = -1, role_id_host = -1;
        String email_id_login = null, email_id_host = null;
        int priority_login = -1, priority_host = -1;

        for (int i = 0; i < MData.arrUser.size(); i++) {
            User user = MData.arrUser.get(i);
            if (user.getId() == userId) {
                email_id_host = user.getEmail_id();
            } else if (user.getId() == MData.userLogin.getId()) {
                email_id_login = user.getEmail_id();
            }

            //save time to find
            if (email_id_host != null && email_id_login != null) {
                break;
            }
        }

        //check if not found email return bad
        if (email_id_host == null || email_id_login == null) {
            return -1;
        }

        for (int j = 0; j < MData.arrEmail.size(); j++) {
            Email email = MData.arrEmail.get(j);
            if (email.getId().compareTo(email_id_login) == 0) {
                role_id_user_login = email.getRole_id();
            } else if (email.getId().compareTo(email_id_host) == 0) {
                role_id_host = email.getRole_id();
            }

            //save time find
            if (role_id_host != -1 && role_id_user_login != -1) {
                break;
            }
        }

        //check if not found email return bad
        if (role_id_host == -1 || role_id_user_login == -1) {
            return -1;
        }

        for (int i = 0; i < MData.arrRole.size(); i++) {
            Role role = MData.arrRole.get(i);
            if (role.getId() == role_id_user_login) {
                priority_login = role.getPriority();
            } else if (role.getId() == role_id_host) {
                priority_host = role.getPriority();
            }

            //save time find
            if (priority_host != -1 && priority_login != -1) {
                break;
            }
        }

        //check if not found email return bad
        if (priority_host == -1 || priority_login == -1) {
            return -1;
        }

        //compare priority
        if (priority_login > priority_host) {
            return 0;
        } else {
            return 1;
        }
    }
}
