package com.vdev.bookingevent.view.fragment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.vdev.bookingevent.databinding.FragmentAddEventBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    public AddEventFragment() {
        // Required empty public constructor
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
                binding.edtStartTime.setText(start_time);
            }
        },0,0,true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(format_time, hour , minute);
                binding.edtEndTime.setText(end_time);
            }
        },0,0,true);
        //DatePickerDialog datePickerDialog
        dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = format_date.format(mCalendar.getTime());
                binding.edtDate.setText(date);
            }
        }, year_now, month_now, day_now);
    }

    private void initView() {
        //department
        //room
        //start time
        binding.edtStartTime.setOnClickListener(it -> {initTimeChoiceStart();});
        binding.edtStartTime.setInputType(InputType.TYPE_NULL);
        //end time
        binding.edtEndTime.setOnClickListener(it -> {initTimeChoiceEnd();});
        binding.edtEndTime.setInputType(InputType.TYPE_NULL);
        //date
        binding.edtDate.setOnClickListener(it -> {initDateChoice();});
        binding.edtDate.setInputType(InputType.TYPE_NULL);
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