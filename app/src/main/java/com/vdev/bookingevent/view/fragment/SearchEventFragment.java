package com.vdev.bookingevent.view.fragment;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.kizitonwose.calendar.core.CalendarDay;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.EventsDashMonthAdapter;
import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.callback.CallbackItemDayCalMonth;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.FragmentSearchEventBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.presenter.SearchEventContract;
import com.vdev.bookingevent.presenter.SearchPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SearchEventFragment extends Fragment implements CallbackItemCalDashMonth , SearchEventContract.View , CallbackUpdateEventDisplay {
    private SearchPresenter presenter;
    private FragmentSearchEventBinding binding;
    private MDialog mDialog;
    private EventsDashMonthAdapter adapter;
    final int year_now = Calendar.getInstance().get(Calendar.YEAR);
    final int month_now = Calendar.getInstance().get(Calendar.MONTH);
    final int day_now = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private TimePickerDialog tpd_start;
    private TimePickerDialog tpd_end;
    private DatePickerDialog dpd_start;
    private DatePickerDialog dpd_end;
    private MConvertTime mConvertTime;
    private int index_room_choice = 0;

    public SearchEventFragment() {
        super(R.layout.fragment_search_event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchEventBinding.inflate(inflater , container , false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init TimePicker and DatePicker
        initTimeAndDatePicker();
        //init dialog
        mDialog = new MDialog();
        //init other view
        initPresenter();
        initView();
        initRecycleView();
        binding.btnFind.setOnClickListener(it -> {findEvent();});
    }

    private void initPresenter() {
        if(presenter == null){
            presenter = new SearchPresenter(this, this);
            mConvertTime = presenter.getmConvertTime();
        }
    }

    private void initTimeAndDatePicker() {
        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvDateStart.setText(start_time + " " + binding.tvDateStart.getText());
            }
        },0,0,true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                ,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(MConst.FORMAT_TIME, hour , minute);
                binding.tvDateEnd.setText(end_time + " " + binding.tvDateEnd.getText());
            }
        },0,0,true);
        //start date
        dpd_start = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = mConvertTime.convertDateToString3(mCalendar.getTime());
                binding.tvDateStart.setText(date);
                initTimeChoiceStart();
            }
        }, year_now, month_now, day_now);
        //end date
        dpd_end = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(year, month, day);
                String date = mConvertTime.convertDateToString3(mCalendar.getTime());
                binding.tvDateEnd.setText(date);
                initTimeChoiceEnd();
            }
        }, year_now, month_now, day_now);
    }

    private void initView() {
        //room
        ArrayAdapter<String> aaRoom = new ArrayAdapter<>(getContext() , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , presenter.getTempRooms());
        binding.actvRoom.setAdapter(aaRoom);
        binding.actvRoom.setText(aaRoom.getItem(0), false);
        binding.actvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index_room_choice = i;
            }
        });
        //start date
        binding.tvDateStart.setOnClickListener(it -> {initDateChoiceStart();});
        binding.tvDateStart.setInputType(InputType.TYPE_NULL);
        //image clear date start
        binding.imgCloseDateStart.setOnClickListener(it -> {binding.tvDateStart.setText("");});
        //end date
        binding.tvDateEnd.setOnClickListener(it -> {initDateChoiceEnd();});
        binding.tvDateEnd.setInputType(InputType.TYPE_NULL);
        //image clear date end
        binding.imgCloseDateEnd.setOnClickListener(it -> {binding.tvDateEnd.setText("");});
    }

    private void initDateChoiceEnd() {
        if(dpd_end != null && !dpd_end.isShowing()){
            dpd_end.show();
        }
    }

    private void initDateChoiceStart() {
        if(dpd_start != null && !dpd_start.isShowing()){
            dpd_start.show();
        }
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

    private void initRecycleView() {
        adapter = new EventsDashMonthAdapter(this);
        //TODO it is just a sample event in here
        List<Event> events = new ArrayList<>();
        for (int i=0 ; i<2 ; i++){
            Event event = new Event();
            event.setSummery("Test summery");
            event.setId(0);
            event.setDateStart(mConvertTime.convertDateToMili(GregorianCalendar.getInstance().getTime()));
            Calendar calEndTime = GregorianCalendar.getInstance();
            calEndTime.add(Calendar.HOUR_OF_DAY, 5);
            event.setDateEnd(mConvertTime.convertDateToMili(calEndTime.getTime()));
            events.add(event);
        }
        adapter.setEvents(events);
        binding.rvResultEvents.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));
        binding.rvResultEvents.setAdapter(adapter);
    }

    private void findEvent() {
        String title;
        String room;
        String startTime , endTime;
        String startDate, endDate;
        if(mDialog.checkConnection(getContext())){
            //get data input
            title = binding.edtTitle.getText().toString();
            room = binding.actvRoom.getText().toString();
            startDate = binding.tvDateStart.getText().toString();
            endDate = binding.tvDateEnd.getText().toString();
            //check data
            if(title.equals("") && room.equals("None") && startDate.equals("") && endDate.equals("")){
                mDialog.showFillData(getContext(), "Please fill at least one field to search");
            } else {
                if((!startDate.equals("") && !endDate.equals(""))){
                    if(mConvertTime.convertStringToMili(binding.tvDateStart.getText().toString()) >= mConvertTime.convertStringToMili(binding.tvDateEnd.getText().toString())){
                        mDialog.showFillData(getContext() , "Please set start Date smaller than end Date");
                    } else {
                        // then query to data
                        presenter.searchEvents(title , index_room_choice - 1, startDate , endDate);
                    }
                } else {
                    // then query to data
                    presenter.searchEvents(title , index_room_choice - 1, startDate , endDate);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void openSlidingPanel(int idEvent, String roomColor) {
        //TODO open event detail
    }

    @Override
    public void updateEvent(List<Event> events) {
        //TODO update adapter here
        adapter.setEvents(events);
        adapter.notifyDataSetChanged();
    }
}