package com.vdev.bookingevent.view.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.EventsOverlapAdapter;
import com.vdev.bookingevent.adapter.UserAdapter;
import com.vdev.bookingevent.callback.CallbackAddEvent;
import com.vdev.bookingevent.callback.CallbackEditEventOverlap;
import com.vdev.bookingevent.callback.CallbackFragmentManager;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.callback.OnItemEventOverlap;
import com.vdev.bookingevent.callback.OnItemUserClickListener;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.FragmentAddEventBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEventFragment extends Fragment implements CallbackAddEvent , CallbackUpdateEventDisplay, OnItemUserClickListener, CallbackEditEventOverlap {

    private final String KEY_ADD_HOST = "KEY_ADD_HOST";
    private final String KEY_ADD_TITLE = "KEY_ADD_TITLE";
    private final String KEY_ADD_SUMMARY = "KEY_ADD_SUMMARY";
    private final String KEY_ADD_LIST_GUEST = "KEY_ADD_LIST_GUEST";
    private final String KEY_ADD_INDEX_ROOM_CHOICE = "KEY_ADD_INDEX_ROOM";
    private final String KEY_ADD_START_TIME = "KEY_ADD_START_TIME";
    private final String KEY_ADD_END_TIME = "KEY_ADD_END_TIME";
    private final String KEY_ADD_DATE= "KEY_ADD_DATE";

    private FragmentAddEventBinding binding;
    final int year_now = Calendar.getInstance().get(Calendar.YEAR);
    final int month_now = Calendar.getInstance().get(Calendar.MONTH);
    final int day_now = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int index_room_choice = 0;
    int index_department_choice = 0;
    private TimePickerDialog tpd_start;
    private TimePickerDialog tpd_end;
    private DatePickerDialog dpd;
    private MDialog mDialog;
    private FirebaseController fc;
    private MConvertTime mConvertTime;
    private CallbackFragmentManager callbackFragmentManager;
    private Dialog dialogConfirmDeleteEvent;
    private Dialog dialogDeleteEvent;
    private Dialog dialogEditEvent;
    private Dialog dialogEventOverlap;
    private Dialog dialogAddSuccess;
    private Dialog dialogErrorEdit;
    private Event eventWantToAdd = new Event();
    private List<User> guests = new ArrayList<>();
    private User host;
    private UserAdapter adapterHost;

    public AddEventFragment(CallbackFragmentManager callbackFragmentManager) {
        // Required public constructor
        this.callbackFragmentManager = callbackFragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddEventBinding.inflate(inflater , container , false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mConvertTime = new MConvertTime();
        //init dialog
        initMDialog();
        //init FirebaseController
        initFC();
        //init view
        initView();
        //init for time and date picker
        initTimePickerStart(0,0);
        initTimePickerEnd(0,0);
        initDatePicker(day_now, month_now , year_now);

        Bundle bundle = getArguments();
        if(bundle != null){
            String title = bundle.getString(KEY_ADD_TITLE);
            String summary = bundle.getString(KEY_ADD_SUMMARY);
            guests = bundle.getParcelableArrayList(KEY_ADD_LIST_GUEST);
            host = bundle.getParcelable(KEY_ADD_HOST);
            index_room_choice = bundle.getInt(KEY_ADD_INDEX_ROOM_CHOICE);
            String startTime = bundle.getString(KEY_ADD_START_TIME);
            String endTime = bundle.getString(KEY_ADD_END_TIME);
            String date = bundle.getString(KEY_ADD_DATE);

            //update data in UI
            //title
            binding.edtTitle.setText(title);
            //summary
            binding.edtSummary.setText(summary);
            if(fc.userLoginIsAdmin() && host != null){
                //host
                Chip chipHost = new Chip(getContext());
                chipHost.setText(host.getFullName());
                chipHost.setCloseIconVisible(true);
                chipHost.setTextAppearance(R.style.ChipTextAppearance);
                chipHost.setOnCloseIconClickListener(it -> {host = null; binding.cgHost.removeView(chipHost);});
                //add chip to group
                binding.cgHost.addView(chipHost);
            }
            // guest
            for(User user : guests){
                //create chip
                Chip chip = new Chip(getContext());
                chip.setText(user.getFullName());
                chip.setCloseIconVisible(true);
                chip.setTextAppearance(R.style.ChipTextAppearance);
                chip.setOnCloseIconClickListener(it -> {guests.remove(user); binding.cgGuests.removeView(chip);});
                //add chip to group
                binding.cgGuests.addView(chip);
            }
            // room
            ArrayAdapter<String> tempAdapter = (ArrayAdapter<String>) binding.actvRoom.getAdapter();
            binding.actvRoom.setText(tempAdapter.getItem(index_room_choice), false);
            // startTime + endTime + date
            if(!startTime.isEmpty()){
                binding.tvStartTime.setText(startTime);
                Calendar calendarStart = mConvertTime.convertMiliToCalendar(mConvertTime.convertString1ToMili(startTime));
                initTimePickerStart(calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE));
            }
            if(!endTime.isEmpty()){
                binding.tvEndTime.setText(endTime);
                Calendar calendarEnd = mConvertTime.convertMiliToCalendar(mConvertTime.convertString1ToMili(endTime));
                initTimePickerEnd(calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE));
            }
            if(!date.isEmpty()){
                binding.tvDate.setText(date);
                Calendar calendarDate = mConvertTime.convertMiliToCalendar(mConvertTime.convertString4ToMili(date));
                initDatePicker(calendarDate.get(Calendar.DAY_OF_MONTH), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.YEAR));
            }
        }
    }

    private void initMDialog() {
        if(mDialog == null){
            mDialog = new MDialog();
            dialogConfirmDeleteEvent = mDialog.confirmDialog(getContext(), "Confirm Delete Event", "Are you sure want to delete event ?");
        }
    }

    private void initFC(){
        if(fc == null){
            fc = new FirebaseController(this, this, null,null, this);
        }
    }

    private void initTimePickerStart(int hour, int minute){
        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvStartTime.setText(start_time);
            }
        },hour,minute,true);
        tpd_start.setTitle("Time Start Event");
    }

    private void initTimePickerEnd(int hour, int minute) {
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvEndTime.setText(end_time);
            }
        },hour,minute,true);
        tpd_end.setTitle("Time End Event");
    }

    private void initDatePicker(int day , int month, int year){
        //DatePickerDialog datePickerDialog
        dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = mConvertTime.convertDateToString3(mCalendar.getTime());
                binding.tvDate.setText(date);
            }
        }, year, month, day);
    }

    private ArrayAdapter<String> getNameUserAdapter(Context context) {
        String[] userNames = new String[MData.arrUser.size()];
        for (int i = 0; i < MData.arrUser.size(); i++) {
            userNames[i] = MData.arrUser.get(i).getFullName();
        }
        return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, userNames);
    }
    private void initView() {
        //host
        if(fc.userLoginIsAdmin()){
            binding.tvTitleHost.setVisibility(View.VISIBLE);
            binding.svHost.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.tvTitleGuest.getLayoutParams();
            layoutParams.topToBottom = binding.svHost.getId();
            binding.clFormAddEvent.updateViewLayout(binding.tvTitleGuest, layoutParams);

            // host
            List<User> mListHost = new ArrayList<>(MData.arrUser);
            adapterHost = new UserAdapter(mListHost , this, MConst.USER_ADAPTER_TYPE_HOST);
            binding.svHost.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    binding.svHost.clearFocus();
                    return false;
                }
            });
            binding.svHost.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapterHost.getFilter().filter(s);
                    return false;
                }
            });
            binding.rvHost.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL , false));
            binding.rvHost.setHasFixedSize(true);
            binding.rvHost.setAdapter(adapterHost);
            binding.svHost.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        binding.rvHost.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvHost.setVisibility(View.INVISIBLE);
                    }
                }
            });
            binding.imgStartHost.setVisibility(View.VISIBLE);

            // guests
            List<User> mListGuest = new ArrayList<>(MData.arrUser);
            UserAdapter adapterGuest = new UserAdapter(mListGuest , this, MConst.USER_ADAPTER_TYPE_GUEST);
            binding.svGuest.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    binding.svGuest.clearFocus();
                    return false;
                }
            });
            binding.svGuest.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapterGuest.getFilter().filter(s);
                    return false;
                }
            });
            binding.rvGuest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL , false));
            binding.rvGuest.setHasFixedSize(true);
            binding.rvGuest.setAdapter(adapterGuest);
            binding.svGuest.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        binding.rvGuest.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvGuest.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            // guests
            List<User> mListGuest = new ArrayList<>(MData.arrUser);
            mListGuest.remove(MData.userLogin);
            UserAdapter adapterGuest = new UserAdapter(mListGuest , this, MConst.USER_ADAPTER_TYPE_GUEST);
            binding.svGuest.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    binding.svGuest.clearFocus();
                    return false;
                }
            });
            binding.svGuest.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapterGuest.getFilter().filter(s);
                    return false;
                }
            });
            binding.rvGuest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL , false));
            binding.rvGuest.setHasFixedSize(true);
            binding.rvGuest.setAdapter(adapterGuest);
            binding.svGuest.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b){
                        binding.rvGuest.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvGuest.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        //room
            //set data room
        List<String> tempRooms = new ArrayList<>();
        tempRooms.add("None");
        for(int i=0 ; i<MData.arrRoom.size() ; i++){
            tempRooms.add(MData.arrRoom.get(i).getNickName());
        }
            //set array for room
        ArrayAdapter<String> aaRoom = new ArrayAdapter<>(getContext() , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , tempRooms);
        binding.actvRoom.setAdapter(aaRoom);
        binding.actvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index_room_choice = i;
            }
        });
        binding.actvRoom.setText(aaRoom.getItem(0), false);
        //start time
        binding.tvStartTime.setOnClickListener(it -> {initTimeChoiceStart();});
        binding.tvStartTime.setInputType(InputType.TYPE_NULL);
        //end time
        binding.tvEndTime.setOnClickListener(it -> {initTimeChoiceEnd();});
        binding.tvEndTime.setInputType(InputType.TYPE_NULL);
        //date
        binding.tvDate.setOnClickListener(it -> {initDateChoice();});
        binding.tvDate.setInputType(InputType.TYPE_NULL);
        //button add event
        binding.btnAddEvent.setOnClickListener(it -> {
            if (mDialog.checkConnection(getContext())){
                boolean check = false;
                //check fill all data
                if (binding.actvRoom.getText().toString().equals("None")){
                    check = true;
                    if(!binding.tilRoom.isErrorEnabled()) {
                        binding.tilRoom.setErrorEnabled(true);
                    }
                    binding.tilRoom.setError("This field is required not None");
                } else {
                    if(binding.tilRoom.isErrorEnabled()) {
                        binding.tilRoom.setErrorEnabled(false);
                    }
                }
                if(binding.edtTitle.getText().toString().isEmpty()){
                    check = true;
                    binding.edtTitle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                } else {
                    binding.edtTitle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if (binding.edtSummary.getText().toString().isEmpty()){
                    check = true;
                    binding.edtSummary.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                } else {
                    binding.edtSummary.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if (binding.tvStartTime.getText().toString().isEmpty()){
                    check = true;
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                } else {
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if (binding.tvEndTime.getText().toString().isEmpty()){
                    check = true;
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                } else {
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if (binding.tvDate.getText().toString().isEmpty()){
                    check = true;
                    binding.tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                } else {
                    binding.tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if(fc.userLoginIsAdmin()){
                    if(host == null){
                        check = true;
                        mDialog.showErrorDialog(getContext(), "Please choice host");
                    }
                }
                if(check){
                    mDialog.showFillData(getContext(), null);
                } else {
                    if(compareTimeDateStartAndDateEnd(binding.tvStartTime.getText().toString() , binding.tvEndTime.getText().toString())){
                        // get data to add into firebase
                        String title = binding.edtTitle.getText().toString();
                        String summary = binding.edtSummary.getText().toString();
                        // remove host of guest <must fix it>
                        if(fc.userLoginIsAdmin() && host != null){
                            if(guests.contains(host)){
                                guests.remove(host);
                            }
                        }
                        int numberParticipant = guests.size() + 1; //because host is a participant
                        int room_id = MData.arrRoom.get(index_room_choice - 1).getId();
                        Date dateStart = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvStartTime.getText().toString() + " " + binding.tvDate.getText()));
                        Date dateEnd = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvEndTime.getText().toString() + " " + binding.tvDate.getText()));
                        Date dateCreated = mConvertTime.convertMiliToDate(System.currentTimeMillis());
                        Date dateUpdated = dateCreated;
                        //check the number participant if it smaller or equal than the max value of room's participant
                        if(MData.arrRoom.get(index_room_choice - 1).getMaxNum() >= numberParticipant){
                            //create event
                            Event tempEvent = new Event();
                            tempEvent.setTitle(title);
                            tempEvent.setSummery(summary);
                            tempEvent.setDateCreated(mConvertTime.convertDateToMili(dateCreated));
                            tempEvent.setDateUpdated(mConvertTime.convertDateToMili(dateUpdated));
                            tempEvent.setDateStart(mConvertTime.convertDateToMili(dateStart));
                            tempEvent.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
                            tempEvent.setRoom_id(room_id);
                            tempEvent.setNumberParticipant(numberParticipant);
                            tempEvent.setStatus(0);
                            //set event to local variable to create a loop when show event overlap
                            eventWantToAdd = tempEvent;
                            fc.checkAddNewEvent(getContext(), eventWantToAdd);
                        } else {
                            mDialog.showErrorDialog(getContext(), "The number of participants exceeds the maximum number of people\n the meeting room can accommodate");
                        }
                    } else {
                        mDialog.showTimeError(getContext());
                    }
                }
            }
        });
    }

    private boolean compareTimeDateStartAndDateEnd(String StartTime , String EndTime) {
        LocalTime start = LocalTime.parse(StartTime);
        LocalTime end = LocalTime.parse(EndTime);
        Duration duration = Duration.between(start , end);
        if(duration.isNegative() || duration.isZero()){
            return false;
        }
        return true;
    }

    private void initTimeChoiceEnd() {
        if(tpd_end != null && !tpd_end.isShowing() ){
            tpd_end.show();
        }
    }

    private void initTimeChoiceStart() {
        if (tpd_start != null && !tpd_start.isShowing()){
            tpd_start.show();
        }
    }

    private void initDateChoice() {
        if(dpd != null && !dpd.isShowing()){
            dpd.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        if(dialogDeleteEvent != null && dialogDeleteEvent.isShowing()){
            dialogDeleteEvent.dismiss();
        }
        if(dialogAddSuccess != null && dialogAddSuccess.isShowing()){
            dialogAddSuccess.dismiss();
        }
        if(dialogConfirmDeleteEvent != null && dialogConfirmDeleteEvent.isShowing()){
            dialogConfirmDeleteEvent.dismiss();
        }
        if(dialogEventOverlap != null && dialogEventOverlap.isShowing()){
            dialogEventOverlap.dismiss();
        }
        if(dialogErrorEdit != null && dialogErrorEdit.isShowing()){dialogErrorEdit.dismiss();}
    }

    @Override
    public void callbackAddDetailParticipant(boolean b) {
        if(b){
            //show notification success add and update UI to the main home
            dialogAddSuccess = mDialog.showDialogSuccess(getContext(), "Add Success" , "Add Event Success");
            dialogAddSuccess.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //clearDataForm();
                    dialogAddSuccess.dismiss();
                    callbackFragmentManager.goToFragmentDashboard();
                }
            });
            dialogAddSuccess.show();
        } else {
            mDialog.showFillData(getContext(), "Some error when add new Event");
        }

    }

    @Override
    public void callbackCanAddNewEvent(Event event , List<Event> eventsDuplicate) {
        if(event != null) {
            fc.addEvent(getContext(), event);
        } else {
            fc.getArrHostOfArrEvent(getContext(),eventsDuplicate);
        }
    }

    @Override
    public void callbackGetHostEventOverlap(List<Event> eventsOverlap, List<User> hosts) {
        dialogEventOverlap = mDialog.showEventsDuplicate(getContext());
        //set title time
        TextView tv = dialogEventOverlap.findViewById(R.id.tv_date);
        Date date = mConvertTime.convertMiliToDate(eventsOverlap.get(0).getDateStart());
        tv.setText(mConvertTime.convertDateToString3(date));
        //set recycle view
        RecyclerView rv = dialogEventOverlap.findViewById(R.id.rv_event_overlap);

        EventsOverlapAdapter adapter = new EventsOverlapAdapter(getContext(), eventsOverlap, hosts, new OnItemEventOverlap() {
            @Override
            public void OnItemDeleteCLickListener(int position) {
                showDialogDeleteEvent(eventsOverlap.get(position));
            }

            @Override
            public void OnItemEditCLickListener(Event editEvent) {
                fc.checkEditEventOverlap(getContext(), editEvent);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL , false));
        rv.setAdapter(adapter);
        //show dialog
        dialogEventOverlap.show();
    }

    @Override
    public void callbackAddEventSuccess(boolean b) {
        if(b){
            //add detail participant
            fc.addEventDetailParticipant(getContext(), MData.id_event, guests, host);
        } else {
            //notification can not add event
            mDialog.showFillData(getContext(), "Some error when add new Event");
        }
    }

    private void showDialogDeleteEvent(Event event) {
        dialogConfirmDeleteEvent.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fc.deleteEvent(getContext(), event);
            }
        });
        dialogConfirmDeleteEvent.show();
    }

    @Override
    public void updateEvent(List<Event> events) {
        //not do any thing here because we do not need to do here and reach here
    }

    @Override
    public void deleteEventSuccess(Event event) {
        //dismiss all dialog when delete success and show delete success
        //dismiss dialog
        if(dialogConfirmDeleteEvent != null && dialogConfirmDeleteEvent.isShowing()){
            dialogConfirmDeleteEvent.dismiss();
        }
        if(dialogEventOverlap.isShowing()){
            dialogEventOverlap.dismiss();
        }
        //show delete success
        dialogDeleteEvent = mDialog.dialogDeleteSuccess(getContext() , event);
        dialogDeleteEvent.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update adapter of dialog event overlap by a loop
                fc.checkAddNewEvent(getContext(), eventWantToAdd);
                dialogDeleteEvent.dismiss();
            }
        });
        dialogDeleteEvent.show();
    }

    @Override
    public void OnItemUserCLickListener(User user, int type) {
        if(type == MConst.USER_ADAPTER_TYPE_GUEST){
            //check user was added
            if(!guests.contains(user)){
                guests.add(user);
                //create chip
                Chip chip = new Chip(getContext());
                chip.setText(user.getFullName());
                chip.setCloseIconVisible(true);
                chip.setTextAppearance(R.style.ChipTextAppearance);
                chip.setOnCloseIconClickListener(it -> {guests.remove(user); binding.cgGuests.removeView(chip);});
                //add chip to group
                binding.cgGuests.addView(chip);
            }
        } else if(type == MConst.USER_ADAPTER_TYPE_HOST){
            host = user;
            if(binding.cgHost.getChildCount() == 0){
                //create chip
                Chip chip = new Chip(getContext());
                chip.setText(user.getFullName());
                chip.setCloseIconVisible(true);
                chip.setTextAppearance(R.style.ChipTextAppearance);
                chip.setOnCloseIconClickListener(it -> {host = null; binding.cgHost.removeView(chip);});
                //add chip to group
                binding.cgHost.addView(chip);
            } else if(binding.cgHost.getChildCount() == 1){
                for(int i=0 ; i<binding.cgHost.getChildCount() ; i++){
                    Chip chip = (Chip) binding.cgHost.getChildAt(i);
                    chip.setText(user.getFullName());
                }
            }
            binding.svHost.setQuery(user.getFullName(), false);
            binding.svHost.clearFocus();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_ADD_TITLE, binding.edtTitle.getText().toString());
        outState.putString(KEY_ADD_SUMMARY, binding.edtSummary.getText().toString());
        outState.putParcelableArrayList(KEY_ADD_LIST_GUEST, (ArrayList<? extends Parcelable>) guests);
        outState.putParcelable(KEY_ADD_HOST, host);
        outState.putInt(KEY_ADD_INDEX_ROOM_CHOICE, index_room_choice);
        outState.putString(KEY_ADD_START_TIME, binding.tvStartTime.getText().toString());
        outState.putString(KEY_ADD_END_TIME, binding.tvEndTime.getText().toString());
        outState.putString(KEY_ADD_DATE, binding.tvDate.getText().toString());
    }

    @Override
    public void callbackEditEventOverlap(Event event, List<Event> eventsOverlap) {
        if(eventsOverlap.isEmpty()){
            fc.editEventOverlap(getContext(),event);
        } else {
            dialogErrorEdit = mDialog.dialogError(getContext(), "ERROR", "The time is overlap");
            dialogErrorEdit.setOnDismissListener(it -> {
                if(dialogEventOverlap != null && dialogEventOverlap.isShowing()){
                    dialogEventOverlap.dismiss();
                    fc.checkAddNewEvent(getContext(), eventWantToAdd);
                }});
            dialogErrorEdit.show();
        }
    }

    @Override
    public void editEventSuccessOverlap(Event event) {
        //dismiss dialog
        if(dialogEventOverlap.isShowing()){
            dialogEventOverlap.dismiss();
        }
        //TODO DOING
        //show edit event success
        dialogEditEvent = mDialog.dialogEditSuccess(getContext() , event);
        dialogEditEvent.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update adapter of dialog event overlap by a loop
                fc.checkAddNewEvent(getContext(), eventWantToAdd);
                dialogEditEvent.dismiss();
            }
        });
        dialogEditEvent.show();
    }

    public void closeRVGuest(){
        if(binding.rvGuest.getVisibility() == View.VISIBLE){
            binding.svGuest.clearFocus();
            binding.rvGuest.setVisibility(View.INVISIBLE);
        }
    }

    public void closeRVHost() {
        if(binding.rvHost.getVisibility() == View.VISIBLE){
            binding.svHost.clearFocus();
            binding.rvHost.setVisibility(View.INVISIBLE);
        }
    }
}