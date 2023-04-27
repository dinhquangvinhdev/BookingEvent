package com.vdev.bookingevent.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TimePicker;

import com.google.android.material.chip.Chip;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.GuestAdapter;
import com.vdev.bookingevent.callback.CallbackEditEvent;
import com.vdev.bookingevent.callback.OnItemGuestClickListener;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.ActivityEditEventBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;
import com.vdev.bookingevent.presenter.EditEventContract;
import com.vdev.bookingevent.presenter.EditEventPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditEventActivity extends AppCompatActivity implements EditEventContract.View, CallbackEditEvent, OnItemGuestClickListener {

    private final String KEY_GUESTS_EDIT_ACTIVITY = "KEY_GUESTS_EDIT_ACTIVITY";
    private final String KEY_EVENT_EDIT_ACTIVITY = "KEY_EVENT_EDIT_ACTIVITY";
    private ActivityEditEventBinding binding;
    private MConvertTime mConvertTime;
    private FirebaseController fc;
    private EditEventPresenter presenter;
    private Event event;
    int index_room_choice = 0;
    private TimePickerDialog tpd_start;
    private TimePickerDialog tpd_end;
    private DatePickerDialog dpd;
    private MDialog mDialog;
    private Dialog dialogEditSuccess;
    private List<User> guestListOld;
    private List<User> guestListNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get data from month fragment
        Bundle bundle = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            event = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY , Event.class);
            guestListOld = bundle.getParcelable(KEY_GUESTS_EDIT_ACTIVITY);
            guestListNew = new ArrayList<>(guestListOld);
        } else {
            event = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY);
            guestListOld = bundle.getParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY);
            guestListNew = new ArrayList<>(guestListOld);
        }
        initMConvertTime();
        initMDialog();
        initFirebaseController();
        initPresenter();
        //init TimePicker and DatePicker
        initTimeAndDatePicker();
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tpd_start.isShowing()){
            tpd_start.dismiss();
        }
        if(tpd_end.isShowing()){
            tpd_end.dismiss();
        }
        if(dpd.isShowing()){
            dpd.dismiss();
        }
        if(dialogEditSuccess != null && dialogEditSuccess.isShowing()){
            dialogEditSuccess.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(tpd_start.isShowing()){
            tpd_start.dismiss();
        }
        if(tpd_end.isShowing()){
            tpd_end.dismiss();
        }
        if(dpd.isShowing()){
            dpd.dismiss();
        }

        if(binding.rvGuest.getVisibility() == View.VISIBLE){
            binding.svGuest.clearFocus();
            binding.rvGuest.setVisibility(View.INVISIBLE);
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    private void initMDialog() {
        if(mDialog == null){
            mDialog = new MDialog();
        }
    }

    private void initTimeAndDatePicker() {
        //get time of event
        int year_event, month_event, day_event, hStart , mStart , hEnd , mEnd;
        Calendar calendarStart = mConvertTime.convertMiliToCalendar(event.getDateStart());
        Calendar calendarEnd = mConvertTime.convertMiliToCalendar(event.getDateEnd());
        year_event = calendarStart.get(Calendar.YEAR);
        month_event = calendarStart.get(Calendar.MONTH);
        day_event = calendarStart.get(Calendar.DAY_OF_MONTH);
        hStart = calendarStart.get(Calendar.HOUR_OF_DAY);
        mStart = calendarStart.get(Calendar.MINUTE);
        hEnd = calendarEnd.get(Calendar.HOUR_OF_DAY);
        mEnd = calendarEnd.get(Calendar.MINUTE);

        //set Time for view
        binding.tvStartTime.setText(String.format(MConst.FORMAT_TIME, hStart , mStart));
        binding.tvEndTime.setText(String.format(MConst.FORMAT_TIME, hEnd , mEnd));
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(year_event, month_event, day_event);
        binding.tvDate.setText(mConvertTime.convertDateToString3(mCalendar.getTime()));


        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvStartTime.setText(start_time);
            }
        },hStart,mStart,true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvEndTime.setText(end_time);
            }
        },hEnd,mEnd,true);
        //DatePickerDialog datePickerDialog
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = mConvertTime.convertDateToString3(mCalendar.getTime());
                binding.tvDate.setText(date);
            }
        }, year_event, month_event, day_event);
    }

    private void initView() {
        binding.imgIconBack.setOnClickListener(it -> {setResult(Activity.RESULT_CANCELED); finish();});
        binding.edtTitle.setText(event.getTitle());
        binding.edtSummary.setText(event.getSummery());
        // guests
        List<User> mListGuest = new ArrayList<>(MData.arrUser);
        mListGuest.remove(MData.userLogin);
        GuestAdapter adapterGuest = new GuestAdapter(mListGuest , this);
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
        binding.rvGuest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL , false));
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
        //add chip for guest already add
        for(int i = 0; i< guestListOld.size() ; i++){
            User user = guestListOld.get(i);
            //create chip
            Chip chip = new Chip(this);
            chip.setText(user.getFullName());
            chip.setCloseIconVisible(true);
            chip.setTextAppearance(R.style.ChipTextAppearance);
            chip.setOnCloseIconClickListener(it -> {
                guestListNew.remove(user); binding.cgGuests.removeView(chip);});
            //add chip to group
            binding.cgGuests.addView(chip);
        }
        //room
        //get array list for room
        List<String> tempRooms = new ArrayList<>();
        tempRooms.add("None");
        for(int i = 0; i< MData.arrRoom.size() ; i++){
            tempRooms.add(MData.arrRoom.get(i).getNickName());
        }
        ArrayAdapter<String> aaRoom = new ArrayAdapter<>(this , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , tempRooms);
        binding.actvRoom.setAdapter(aaRoom);
        binding.actvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index_room_choice = i;
            }
        });
        index_room_choice = presenter.findIndexRoomOfEvent(event.getRoom_id()) + 1; // because in the select room index 0 is None
        binding.actvRoom.setText(aaRoom.getItem(index_room_choice), false);
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
        binding.btnSaveEditEvent.setOnClickListener(it -> {
            if (mDialog.checkConnection(this)){
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
                if (binding.tvStartTime.getText().toString().isEmpty()){
                    check = true;
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                }
                else {
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if (binding.tvEndTime.getText().toString().isEmpty()){
                    check = true;
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                }
                else {
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if (binding.tvDate.getText().toString().isEmpty()){
                    check = true;
                    binding.tvDate.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                }
                else {
                    binding.tvDate.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if(check){
                    mDialog.showFillData(this, null);
                } else {
                    if(presenter.compareTimeDateStartAndDateEnd(binding.tvStartTime.getText().toString() , binding.tvEndTime.getText().toString())){
                        // get data to add into firebase
                        String title = binding.edtTitle.getText().toString();
                        String summary = binding.edtSummary.getText().toString();
                        int numberParticipant = guestListNew.size() + 1; //because host is a participant
                        int room_id = MData.arrRoom.get(index_room_choice - 1).getId();
                        Date dateStart = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvStartTime.getText().toString() + " " + binding.tvDate.getText()));
                        Date dateEnd = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvEndTime.getText().toString() + " " + binding.tvDate.getText()));
                        //create event
                        Event tempEvent = new Event();
                        tempEvent.setId(event.getId());
                        tempEvent.setTitle(title);
                        tempEvent.setSummery(summary);
                        tempEvent.setDateCreated(event.getDateCreated());
                        tempEvent.setDateUpdated(System.currentTimeMillis());
                        tempEvent.setDateStart(mConvertTime.convertDateToMili(dateStart));
                        tempEvent.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
                        tempEvent.setRoom_id(room_id);
                        tempEvent.setNumberParticipant(numberParticipant);
                        tempEvent.setStatus(0);
                        fc.checkEditEvent(tempEvent);
                    } else {
                        mDialog.showTimeError(this);
                    }
                }
            }
        });
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

    private void initFirebaseController() {
        if(fc == null){
            fc = new FirebaseController(null , null, this, null);
        }
    }

    private void initMConvertTime() {
        if(mConvertTime == null){
            mConvertTime = new MConvertTime();
        }
    }

    private void initPresenter() {
        if(presenter == null){
            presenter = new EditEventPresenter(this);
        }
    }


    @Override
    public void callbackEditEvent(Event event) {
        if(event != null) {
            fc.editEvent(event);
        } else {
            //notification if duplicate event
            mDialog.showFillData(this, "The Event schedule overlap");
        }
    }

    @Override
    public void editEventSuccess(Event event) {
        this.event = event; // set the event return to activity is the event new update
        // check to update detail participant
        List<User> keepGuest = new ArrayList<>(guestListOld);
        keepGuest.retainAll(guestListNew);
        List<User> addGuest = new ArrayList<>(guestListNew);
        addGuest.removeAll(keepGuest);
        List<User> removeGuest = new ArrayList<>(guestListOld);
        removeGuest.removeAll(keepGuest);
        if(addGuest.equals(removeGuest)){
            callbackAddDetailParticipant(true);
        } else {
            fc.editEventDetailParticipant(event.getId() , addGuest, removeGuest);
        }
    }

    @Override
    public void callbackAddDetailParticipant(boolean b) {
        if(b) {
            //show notification success add and update UI to the main home
            dialogEditSuccess = mDialog.showDialogSuccess(this, "Edit Success", "Edit Event Success");
            dialogEditSuccess.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go back month fragment
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_EVENT_EDIT_ACTIVITY , event);
                    bundle.putParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY, (ArrayList<? extends Parcelable>) guestListNew);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    dialogEditSuccess.dismiss();
                    finish();
                }
            });
            dialogEditSuccess.show();
        } else {
            mDialog.showFillData(this, "Some error when edit event");
        }
    }

    @Override
    public void OnItemGuestCLickListener(User user) {
        //check user was added
        if(!guestListNew.contains(user)){
            guestListNew.add(user);
            //create chip
            Chip chip = new Chip(this);
            chip.setText(user.getFullName());
            chip.setCloseIconVisible(true);
            chip.setTextAppearance(R.style.ChipTextAppearance);
            chip.setOnCloseIconClickListener(it -> {
                guestListNew.remove(user); binding.cgGuests.removeView(chip);});
            //add chip to group
            binding.cgGuests.addView(chip);
        }
    }
}