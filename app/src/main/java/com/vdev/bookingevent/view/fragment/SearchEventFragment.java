package com.vdev.bookingevent.view.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.EventsDashMonthAdapter;
import com.vdev.bookingevent.adapter.GuestEventDetailAdapter;
import com.vdev.bookingevent.callback.CallbackDetailEvent;
import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.FragmentSearchEventBinding;
import com.vdev.bookingevent.databinding.LayoutDetailEventBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;
import com.vdev.bookingevent.model.User;
import com.vdev.bookingevent.presenter.SearchEventContract;
import com.vdev.bookingevent.presenter.SearchPresenter;
import com.vdev.bookingevent.view.EditEventActivity;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchEventFragment extends Fragment implements CallbackItemCalDashMonth, SearchEventContract.View,
        CallbackUpdateEventDisplay, CallbackDetailEvent {
    private SearchPresenter presenter;
    private final String KEY_EVENT_EDIT_ACTIVITY = "KEY_EVENT_EDIT_ACTIVITY";
    private final String KEY_GUESTS_EDIT_ACTIVITY = "KEY_GUESTS_EDIT_ACTIVITY";
    private final int REQUEST_CODE_EDIT_EVENT_ACTIVITY = 10;
    private FragmentSearchEventBinding binding;
    private LayoutDetailEventBinding bindingDetailEvent;
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
    private FirebaseController fc;
    private BottomSheetBehavior bsb;
    private Dialog dialogDelete;
    private Dialog confirmDeleteEvent;
    private final String KEY_SEARCH_TITLE = "KEY_SEARCH_TITLE";
    private final String KEY_SEARCH_DATE_START = "KEY_SEARCH_DATE_START";
    private final String KEY_SEARCH_DATE_END = "KEY_SEARCH_DATE_END";
    private final String KEY_SEARCH_ROOM = "KEY_SEARCH_ROOM";

    public SearchEventFragment() {
        super(R.layout.fragment_search_event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchEventBinding.inflate(inflater, container, false);
        bindingDetailEvent = binding.includeLayoutDetailEvent;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init TimePicker and DatePicker
        initTimeAndDatePicker(0, 0, day_now, month_now, year_now,
                0, 0, day_now, month_now, year_now);
        //init dialog
        initMDialog();
        initFirebaseController();
        //init other view
        initPresenter();
        initSlidingPanel();
        initView();
        initRecycleView();
        binding.btnFind.setOnClickListener(it -> {
            findEvent();
        });
        Bundle oldBundle = getArguments();
        if (oldBundle != null) {
            binding.edtTitle.setText(oldBundle.getString(KEY_SEARCH_TITLE));
            binding.tvDateStart.setText(oldBundle.getString(KEY_SEARCH_DATE_START));
            binding.tvDateEnd.setText(oldBundle.getString(KEY_SEARCH_DATE_END));
            index_room_choice = oldBundle.getInt(KEY_SEARCH_ROOM);
            //set time for date start
            Calendar calendarStart;
            if(!binding.tvDateStart.getText().toString().isEmpty()){
                calendarStart = mConvertTime.convertMiliToCalendar(mConvertTime.convertStringToMili(binding.tvDateStart.getText().toString()));
            } else {
                calendarStart = null;
            }
            Calendar calendarEnd;
            if(!binding.tvDateEnd.getText().toString().isEmpty()){
                calendarEnd = mConvertTime.convertMiliToCalendar(mConvertTime.convertStringToMili(binding.tvDateEnd.getText().toString()));
            } else {
                calendarEnd = null;
            }
            if(calendarStart != null && calendarEnd != null) {
                initTimeAndDatePicker(calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.YEAR),
                        calendarEnd.get(Calendar.HOUR), calendarEnd.get(Calendar.MINUTE), calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.YEAR));
            } else if(calendarStart != null && calendarEnd == null){
                initTimeAndDatePicker(calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.YEAR),
                        0, 0, 0, 0, 0);
            } else if(calendarStart == null && calendarEnd != null){
                initTimeAndDatePicker(0, 0, 0, 0, 0,
                        calendarEnd.get(Calendar.HOUR), calendarEnd.get(Calendar.MINUTE), calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.YEAR));
            }
            //set room choice
            ArrayAdapter<String> tempAdapter = (ArrayAdapter<String>) binding.actvRoom.getAdapter();
            binding.actvRoom.setText(tempAdapter.getItem(index_room_choice), false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_SEARCH_TITLE, binding.edtTitle.getText().toString());
        outState.putString(KEY_SEARCH_DATE_START, binding.tvDateStart.getText().toString());
        outState.putString(KEY_SEARCH_DATE_END, binding.tvDateEnd.getText().toString());
        outState.putInt(KEY_SEARCH_ROOM, index_room_choice);
    }

    private void initMDialog() {
        if (mDialog == null) {
            mDialog = new MDialog();
            confirmDeleteEvent = mDialog.confirmDialog(getContext(), "Confirm Delete Event", "Are you sure want to delete event ?");
        }
    }

    private void initSlidingPanel() {
        bsb = BottomSheetBehavior.from(binding.flBottomSheet);
        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initFirebaseController() {
        if (fc == null) {
            fc = new FirebaseController(this, null, null, this,null);
        }
    }

    private void initPresenter() {
        if (presenter == null) {
            presenter = new SearchPresenter(this, this);
            mConvertTime = presenter.getmConvertTime();
        }
    }

    private void initTimeAndDatePicker(int hourStart, int minuteStart, int dayStart, int monthStart, int yearStart,
                                       int hourEnd, int minuteEnd, int dayEnd, int monthEnd, int yearEnd) {
        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(MConst.FORMAT_TIME, hour, minute);
                binding.tvDateStart.setText(start_time + " " + binding.tvDateStart.getText());
            }
        }, 0, 0, true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(MConst.FORMAT_TIME, hour, minute);
                binding.tvDateEnd.setText(end_time + " " + binding.tvDateEnd.getText());
            }
        }, 0, 0, true);
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
        ArrayAdapter<String> aaRoom = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, presenter.getTempRooms());
        binding.actvRoom.setAdapter(aaRoom);
        binding.actvRoom.setText(aaRoom.getItem(0), false);
        binding.actvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index_room_choice = i;
            }
        });
        //start date
        binding.tvDateStart.setOnClickListener(it -> {
            initDateChoiceStart();
        });
        binding.tvDateStart.setInputType(InputType.TYPE_NULL);
        //image clear date start
        binding.imgCloseDateStart.setOnClickListener(it -> {
            binding.tvDateStart.setText("");
        });
        //end date
        binding.tvDateEnd.setOnClickListener(it -> {
            initDateChoiceEnd();
        });
        binding.tvDateEnd.setInputType(InputType.TYPE_NULL);
        //image clear date end
        binding.imgCloseDateEnd.setOnClickListener(it -> {
            binding.tvDateEnd.setText("");
        });
    }

    private void initDateChoiceEnd() {
        if (dpd_end != null && !dpd_end.isShowing()) {
            dpd_end.show();
        }
    }

    private void initDateChoiceStart() {
        if (dpd_start != null && !dpd_start.isShowing()) {
            dpd_start.show();
        }
    }

    private void initTimeChoiceEnd() {
        if (tpd_end != null && !tpd_end.isShowing()) {
            tpd_end.show();
        }
    }

    private void initTimeChoiceStart() {
        if (tpd_start != null && !tpd_start.isShowing()) {
            tpd_start.show();
        }
    }

    private void initRecycleView() {
        adapter = new EventsDashMonthAdapter(this);
        List<Event> events = new ArrayList<>();
        adapter.setEvents(events);
        binding.rvResultEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rvResultEvents.setAdapter(adapter);
    }

    private void findEvent() {
        String title;
        String room;
        String startDate, endDate;
        if (mDialog.checkConnection(getContext())) {
            //get data input
            title = binding.edtTitle.getText().toString();
            room = binding.actvRoom.getText().toString();
            startDate = binding.tvDateStart.getText().toString();
            endDate = binding.tvDateEnd.getText().toString();
            //check data
            if (title.equals("") && room.equals("None") && startDate.equals("") && endDate.equals("")) {
                mDialog.showFillData(getContext(), "Please fill at least one field to search");
            } else {
                if ((!startDate.equals("") && !endDate.equals(""))) {
                    if (mConvertTime.convertStringToMili(binding.tvDateStart.getText().toString()) >= mConvertTime.convertStringToMili(binding.tvDateEnd.getText().toString())) {
                        mDialog.showFillData(getContext(), "Please set start Date smaller than end Date");
                    } else {
                        // then query to data
                        presenter.searchEvents(title, index_room_choice - 1, startDate, endDate);
                    }
                } else {
                    // then query to data
                    presenter.searchEvents(title, index_room_choice - 1, startDate, endDate);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (confirmDeleteEvent.isShowing()) {
            confirmDeleteEvent.dismiss();
        }
        if (dialogDelete != null && dialogDelete.isShowing()) {
            dialogDelete.dismiss();
        }
    }

    @Override
    public void openSlidingPanel(int idEvent, String roomColor) {
        fc.getParticipantOfEvent(getContext(), idEvent);
    }

    @Override
    public void updateEvent(List<Event> events) {
        adapter.setEvents(events);
        adapter.notifyDataSetChanged();
        //notification not found any events
        if(events.isEmpty()){
            Toast.makeText(getContext(), "Not found any Events", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteEventSuccess(Event event) {
        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
        dialogDelete = mDialog.dialogDeleteSuccess(getContext(), event);
        dialogDelete.show();
    }

    @Override
    public void callbackShowSlidingPanel(User host, List<User> guests, int idEvent) {
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
        // find the event in data
        Event event = presenter.findEventInData(idEvent);
        if (event != null) {
            Log.d("bibibla", "openSlidingPanel: " + "found event");
            bindingDetailEvent.tvEventDetailTitle.setText(event.getTitle());
            String textTime = presenter.convertTimeToStringDE(event.getDateStart(), event.getDateEnd());
            bindingDetailEvent.tvEventDetailTime.setText(textTime);
            String nameRoom = presenter.getNameRoom(event.getRoom_id());
            bindingDetailEvent.tvEventSummary.setText(event.getSummery());
            bindingDetailEvent.tvEventDetailNameRoom.setText(nameRoom);
            int checkPriority = fc.comparePriorityUser(host.getId());
            if (checkPriority != 0) {
                bindingDetailEvent.imgEditEvent.setVisibility(View.INVISIBLE);
                bindingDetailEvent.imgDeleteEvent.setVisibility(View.INVISIBLE);
            } else if (checkPriority == 0) {
                bindingDetailEvent.imgEditEvent.setVisibility(View.VISIBLE);
                bindingDetailEvent.imgDeleteEvent.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < MData.arrRoom.size(); i++) {
                Room room = MData.arrRoom.get(i);
                if (room.getId() == event.getRoom_id()) {
                    bindingDetailEvent.imgColorRoom.setBackgroundColor(Color.parseColor(room.getColor()));
                    break;
                }
            }
            bindingDetailEvent.tvEventDetailParticipant.setText((event.getNumberParticipant() - 1) + " Guest");
            bindingDetailEvent.imgDeleteEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDeleteEvent.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mDialog.checkConnection(getContext())) {
                                fc.deleteEvent(getContext(), event);
                                confirmDeleteEvent.dismiss();
                            } else {
                                confirmDeleteEvent.dismiss();
                            }
                        }
                    });
                    confirmDeleteEvent.show();
                }
            });
            bindingDetailEvent.imgEditEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), EditEventActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_EVENT_EDIT_ACTIVITY, event);
                    bundle.putParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY, (ArrayList<? extends Parcelable>) guests);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT_ACTIVITY);
                }
            });
            //create adapter
            GuestEventDetailAdapter adapterGuest = new GuestEventDetailAdapter(guests, host);
            bindingDetailEvent.rvGuest.setAdapter(adapterGuest);
            bindingDetailEvent.rvGuest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        } else {
            //TODO show notification or not do anything when not found event
            Log.d("bibibla", "openSlidingPanel: " + "not found event");
        }
    }

    private void updatedEventInSlidingPanel(Event updatedEvent, List<User> guests) {
        if (bsb != null && bsb.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (updatedEvent != null) {
                Log.d("bibibla", "openSlidingPanel: " + "found event");
                bindingDetailEvent.tvEventDetailTitle.setText(updatedEvent.getTitle());
                String textTime = presenter.convertTimeToStringDE(updatedEvent.getDateStart(), updatedEvent.getDateEnd());
                bindingDetailEvent.tvEventDetailTime.setText(textTime);
                String nameRoom = presenter.getNameRoom(updatedEvent.getRoom_id());
                bindingDetailEvent.tvEventSummary.setText(updatedEvent.getSummery());
                bindingDetailEvent.tvEventDetailNameRoom.setText(nameRoom);
                for (int i = 0; i < MData.arrRoom.size(); i++) {
                    Room room = MData.arrRoom.get(i);
                    if (room.getId() == updatedEvent.getRoom_id()) {
                        bindingDetailEvent.imgColorRoom.setBackgroundColor(Color.parseColor(room.getColor()));
                        break;
                    }
                }
                //need to refresh edit button because new event updated
                bindingDetailEvent.imgEditEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EditEventActivity.class);
                        intent.putExtra(KEY_EVENT_EDIT_ACTIVITY, updatedEvent);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(KEY_EVENT_EDIT_ACTIVITY, updatedEvent);
                        bundle.putParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY, (ArrayList<? extends Parcelable>) guests);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT_ACTIVITY);
                    }
                });
                bindingDetailEvent.tvEventDetailParticipant.setText((updatedEvent.getNumberParticipant() - 1) + " Guest");
                //update adapter
                GuestEventDetailAdapter adapterGuest = (GuestEventDetailAdapter) bindingDetailEvent.rvGuest.getAdapter();
                adapterGuest.updateDataGuest(guests);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_EVENT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Event updatedEvent;
                List<User> guests = new ArrayList<>();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    updatedEvent = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY, Event.class);
                } else {
                    updatedEvent = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY);
                    guests = bundle.getParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY);
                }
                updatedEventInSlidingPanel(updatedEvent, guests);
            }
        }
    }
}