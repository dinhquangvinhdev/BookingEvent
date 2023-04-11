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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.FragmentAddEventBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddEventFragment extends Fragment {

    private FragmentAddEventBinding binding;
    final int year_now = Calendar.getInstance().get(Calendar.YEAR);
    final int month_now = Calendar.getInstance().get(Calendar.MONTH);
    final int day_now = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    final SimpleDateFormat format_date = new SimpleDateFormat("dd-MM-yyyy");
    final String format_time = "%02d:%02d";
    private TimePickerDialog tpd_start;
    private TimePickerDialog tpd_end;
    private DatePickerDialog dpd;
    private MDialog mDialog;

    public AddEventFragment() {
        // Required public constructor
        //create dialog
        mDialog = new MDialog();
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

        //init TimePicker and DatePicker
        initTimeAndDatePicker();
        //init view
        initView();
    }

    private void initTimeAndDatePicker() {
        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(format_time, hour , minute);
                binding.tvStartTime.setText(start_time);
            }
        },0,0,true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(format_time, hour , minute);
                binding.tvEndTime.setText(end_time);
            }
        },0,0,true);
        //DatePickerDialog datePickerDialog
        dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = format_date.format(mCalendar.getTime());
                binding.tvDate.setText(date);
            }
        }, year_now, month_now, day_now);
    }

    private void initView() {
        //department
        //TODO temp data
        List<String> tempDepartments = new ArrayList<>();
        tempDepartments.add("None");
        for(int i=0 ; i<10 ; i++){
            tempDepartments.add("department " + (i+1));
        }
        //TODO end temp
        ArrayAdapter<String> aaDepartment = new ArrayAdapter<>(getContext() , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , tempDepartments);
        binding.actvDepartment.setAdapter(aaDepartment);
        binding.actvDepartment.setText(aaDepartment.getItem(0), false);
        //room
        //TODO temp data
        List<String> tempRooms = new ArrayList<>();
        tempRooms.add("None");
        for(int i=0 ; i<10 ; i++){
            tempRooms.add("room " + (i+1));
        }
        //TODO end temp
        ArrayAdapter<String> aaRoom = new ArrayAdapter<>(getContext() , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , tempRooms);
        binding.actvRoom.setAdapter(aaRoom);
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
                } else {
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
                    mDialog.showFillData(getContext());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}