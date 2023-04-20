package com.vdev.bookingevent.view.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.callback.CallbackAddDetailParticipant;
import com.vdev.bookingevent.callback.CallbackFragmentManager;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.FragmentAddEventBinding;
import com.vdev.bookingevent.model.Event;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEventFragment extends Fragment implements CallbackAddDetailParticipant {

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
        //init TimePicker and DatePicker
        initTimeAndDatePicker();
        //init view
        initView();
    }

    private void initMDialog() {
        if(mDialog == null){
            mDialog = new MDialog();
        }
    }

    private void initFC(){
        if(fc == null){
            fc = new FirebaseController(null, this);
        }
    }

    private void initTimeAndDatePicker() {
        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvStartTime.setText(start_time);
            }
        },0,0,true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvEndTime.setText(end_time);
            }
        },0,0,true);
        //DatePickerDialog datePickerDialog
        dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = mConvertTime.convertDateToString3(mCalendar.getTime());
                binding.tvDate.setText(date);
            }
        }, year_now, month_now, day_now);
    }

    private void initView() {
        //department
        //TODO temp data
        List<String> tempDepartments = new ArrayList<>();
        tempDepartments.add("None");
        for(int i = 0; i< MData.arrDepartment.size(); i++){
            tempDepartments.add(MData.arrDepartment.get(i).getNickName());
        }
        //TODO end temp
        ArrayAdapter<String> aaDepartment = new ArrayAdapter<>(getContext() , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , tempDepartments);
        binding.actvDepartment.setAdapter(aaDepartment);
        binding.actvDepartment.setText(aaDepartment.getItem(0), false);
        //room
        //TODO temp data
        List<String> tempRooms = new ArrayList<>();
        tempRooms.add("None");
        for(int i=0 ; i<MData.arrRoom.size() ; i++){
            tempRooms.add(MData.arrRoom.get(i).getNickName());
        }
        //TODO end temp
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
                if (binding.tvStartTime.getText().toString().isEmpty()){
                    check = true;
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                }
                else {
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if (binding.tvEndTime.getText().toString().isEmpty()){
                    check = true;
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                }
                else {
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if (binding.tvDate.getText().toString().isEmpty()){
                    check = true;
                    binding.tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_red));
                }
                else {
                    binding.tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_black));
                }
                if(check){
                    mDialog.showFillData(getContext(), null);
                } else {
                    if(compareTimeDateStartAndDateEnd(binding.tvStartTime.getText().toString() , binding.tvEndTime.getText().toString())){
                        // get data to add into firebase
                        String title = binding.edtTitle.getText().toString();
                        String summary = binding.edtSummary.getText().toString();
                        String guest = binding.edtGuest.getText().toString();
                        //TODO count number_participant
                        String department = binding.actvDepartment.getText().toString();
                        int room_id = MData.arrRoom.get(index_room_choice - 1).getId();
                        Date dateStart = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvStartTime.getText().toString() + " " + binding.tvDate.getText()));
                        Date dateEnd = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvEndTime.getText().toString() + " " + binding.tvDate.getText()));
                        Date dateCreated = mConvertTime.convertMiliToDate(System.currentTimeMillis());
                        Date dateUpdated = dateCreated;
                        //create event
                        Event tempEvent = new Event();
                        tempEvent.setTitle(title);
                        tempEvent.setSummery(summary);
                        tempEvent.setDateCreated(mConvertTime.convertDateToMili(dateCreated));
                        tempEvent.setDateUpdated(mConvertTime.convertDateToMili(dateUpdated));
                        tempEvent.setDateStart(mConvertTime.convertDateToMili(dateStart));
                        tempEvent.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
                        tempEvent.setRoom_id(room_id);
                        tempEvent.setNumberParticipant(1);  // TODO change number participant
                        tempEvent.setStatus(0);
                        fc.checkAddNewEvent(tempEvent);
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
    }

    @Override
    public void callbackAddDetailParticipant() {
        //show notification success add and update UI to the main home
        mDialog.showAddEventSuccess(getContext());
        callbackFragmentManager.goToFragmentDashboard();
    }

    @Override
    public void callbackCanAddNewEvent(Event event) {
        if(event != null) {
            if (fc.addEvent(event)) {
                if (!fc.addEventDetailParticipant(MData.id_event, MData.id_user, MConst.ROLE_HOST)) {
                    //notification can not add detail event
                    mDialog.showFillData(getContext(), "Some error when add new Event");
                }
            } else {
                //notification can not add event
                mDialog.showFillData(getContext(), "Some error when add new Event");
            }
        } else {
            //notification if duplicate event
            mDialog.showFillData(getContext(), "The Event schedule overlap");
        }
    }
}