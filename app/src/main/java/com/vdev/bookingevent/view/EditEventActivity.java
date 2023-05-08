package com.vdev.bookingevent.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.View;
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
import com.vdev.bookingevent.callback.CallbackEditEvent;
import com.vdev.bookingevent.callback.CallbackEditEventOverlap;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.callback.OnItemEventOverlap;
import com.vdev.bookingevent.callback.OnItemUserClickListener;
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

public class EditEventActivity extends AppCompatActivity implements EditEventContract.View, CallbackEditEvent, OnItemUserClickListener,
        CallbackUpdateEventDisplay, CallbackEditEventOverlap {

    private final String KEY_GUESTS_EDIT_ACTIVITY = "KEY_GUESTS_EDIT_ACTIVITY";
    private final String KEY_HOST_EDIT_ACTIVITY = "KEY_HOST_EDIT_ACTIVITY";
    private final String KEY_EVENT_EDIT_ACTIVITY = "KEY_EVENT_EDIT_ACTIVITY";
    private ActivityEditEventBinding binding;
    private MConvertTime mConvertTime;
    private FirebaseController fc;
    private EditEventPresenter presenter;
    private Event eventWantToEdit;
    int index_room_choice = 0;
    private TimePickerDialog tpd_start;
    private TimePickerDialog tpd_end;
    private DatePickerDialog dpd;
    private MDialog mDialog;
    private Dialog dialogEditSuccess;
    private Dialog dialogEventOverlap;
    private Dialog dialogEditEventSuccessOverlap;
    private Dialog dialogConfirmDeleteEvent;
    private Dialog dialogDeleteEvent;
    private List<User> guestListOld;
    private List<User> guestListNew;
    private Dialog dialogErrorEdit;
    private User host;
    private UserAdapter adapterHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get data from month fragment
        Bundle bundle = getIntent().getExtras();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            eventWantToEdit = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY, Event.class);
            guestListOld = bundle.getParcelable(KEY_GUESTS_EDIT_ACTIVITY);
            host = bundle.getParcelable(KEY_HOST_EDIT_ACTIVITY, User.class);
            guestListNew = new ArrayList<>(guestListOld);
        } else {
            eventWantToEdit = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY);
            guestListOld = bundle.getParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY);
            host = bundle.getParcelable(KEY_HOST_EDIT_ACTIVITY);
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
        if (tpd_start.isShowing()) {
            tpd_start.dismiss();
        }
        if (tpd_end.isShowing()) {
            tpd_end.dismiss();
        }
        if (dpd.isShowing()) {
            dpd.dismiss();
        }
        if (dialogEditSuccess != null && dialogEditSuccess.isShowing()) {
            dialogEditSuccess.dismiss();
        }
        if (dialogEventOverlap != null && dialogEventOverlap.isShowing()) {
            dialogEventOverlap.dismiss();
        }
        if (dialogConfirmDeleteEvent != null && dialogConfirmDeleteEvent.isShowing()) {
            dialogConfirmDeleteEvent.dismiss();
        }
        if (dialogDeleteEvent != null && dialogDeleteEvent.isShowing()) {
            dialogDeleteEvent.dismiss();
        }
        if (dialogErrorEdit != null && dialogErrorEdit.isShowing()) {
            dialogErrorEdit.dismiss();
        }
        if (dialogEditEventSuccessOverlap != null && dialogEditEventSuccessOverlap.isShowing()) {
            dialogEditEventSuccessOverlap.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (tpd_start.isShowing()) {
            tpd_start.dismiss();
        }
        if (tpd_end.isShowing()) {
            tpd_end.dismiss();
        }
        if (dpd.isShowing()) {
            dpd.dismiss();
        }

        if(binding.rvHost.getVisibility() == View.VISIBLE){
            binding.svHost.clearFocus();
            binding.rvHost.setVisibility(View.INVISIBLE);
        }

        if (binding.rvGuest.getVisibility() == View.VISIBLE) {
            binding.svGuest.clearFocus();
            binding.rvGuest.setVisibility(View.INVISIBLE);
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    }

    private void initMDialog() {
        if (mDialog == null) {
            mDialog = new MDialog();
            dialogConfirmDeleteEvent = mDialog.confirmDialog(this, "Confirm Delete Event", "Are you sure want to delete event ?");
        }
    }

    private void initTimeAndDatePicker() {
        //get time of event
        int year_event, month_event, day_event, hStart, mStart, hEnd, mEnd;
        Calendar calendarStart = mConvertTime.convertMiliToCalendar(eventWantToEdit.getDateStart());
        Calendar calendarEnd = mConvertTime.convertMiliToCalendar(eventWantToEdit.getDateEnd());
        year_event = calendarStart.get(Calendar.YEAR);
        month_event = calendarStart.get(Calendar.MONTH);
        day_event = calendarStart.get(Calendar.DAY_OF_MONTH);
        hStart = calendarStart.get(Calendar.HOUR_OF_DAY);
        mStart = calendarStart.get(Calendar.MINUTE);
        hEnd = calendarEnd.get(Calendar.HOUR_OF_DAY);
        mEnd = calendarEnd.get(Calendar.MINUTE);

        //set Time for view
        binding.tvStartTime.setText(String.format(MConst.FORMAT_TIME, hStart, mStart));
        binding.tvEndTime.setText(String.format(MConst.FORMAT_TIME, hEnd, mEnd));
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(year_event, month_event, day_event);
        binding.tvDate.setText(mConvertTime.convertDateToString3(mCalendar.getTime()));


        //TimePickerDialog startTime
        tpd_start = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog_MinWidth
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String start_time = String.format(MConst.FORMAT_TIME, hour, minute);
                binding.tvStartTime.setText(start_time);
            }
        }, hStart, mStart, true);
        //TimePickerDialog end Time
        tpd_end = new TimePickerDialog(this, android.R.style.Theme_Holo_Dialog_MinWidth
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String end_time = String.format(MConst.FORMAT_TIME, hour, minute);
                binding.tvEndTime.setText(end_time);
            }
        }, hEnd, mEnd, true);
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
        binding.imgIconBack.setOnClickListener(it -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
        binding.edtTitle.setText(eventWantToEdit.getTitle());
        binding.edtSummary.setText(eventWantToEdit.getSummery());
        //host
        if (fc.userLoginIsAdmin()) {
            binding.tvTitleHost.setVisibility(View.VISIBLE);
            binding.svHost.setVisibility(View.VISIBLE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.tvTitleGuest.getLayoutParams();
            layoutParams.topToBottom = binding.svHost.getId();
            binding.clFormEditEvent.updateViewLayout(binding.tvTitleGuest, layoutParams);

            // host
            List<User> mListHost = new ArrayList<>(MData.arrUser);
            adapterHost = new UserAdapter(mListHost, this, MConst.USER_ADAPTER_TYPE_HOST);
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
            binding.rvHost.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.rvHost.setHasFixedSize(true);
            binding.rvHost.setAdapter(adapterHost);
            binding.svHost.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        binding.rvHost.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvHost.setVisibility(View.INVISIBLE);
                    }
                }
            });
            binding.imgStartHost.setVisibility(View.VISIBLE);

            //add chip for host already add
            Chip chipHost = new Chip(this);
            chipHost.setText(host.getFullName());
            chipHost.setCloseIconVisible(true);
            chipHost.setTextAppearance(R.style.ChipTextAppearance);
            chipHost.setOnCloseIconClickListener(it -> {host = null; binding.cgHost.removeView(chipHost);});
            binding.cgHost.addView(chipHost);

            // guests
            List<User> mListGuest = new ArrayList<>(MData.arrUser);
            UserAdapter adapterGuest = new UserAdapter(mListGuest, this, MConst.USER_ADAPTER_TYPE_GUEST);
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
            binding.rvGuest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.rvGuest.setHasFixedSize(true);
            binding.rvGuest.setAdapter(adapterGuest);
            binding.svGuest.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
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
            UserAdapter adapterGuest = new UserAdapter(mListGuest, this, MConst.USER_ADAPTER_TYPE_GUEST);
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
            binding.rvGuest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.rvGuest.setHasFixedSize(true);
            binding.rvGuest.setAdapter(adapterGuest);
            binding.svGuest.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        binding.rvGuest.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvGuest.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        //add chip for guest already add
        for (int i = 0; i < guestListOld.size(); i++) {
            User user = guestListOld.get(i);
            //create chip
            Chip chip = new Chip(this);
            chip.setText(user.getFullName());
            chip.setCloseIconVisible(true);
            chip.setTextAppearance(R.style.ChipTextAppearance);
            chip.setOnCloseIconClickListener(it -> {
                guestListNew.remove(user);
                binding.cgGuests.removeView(chip);
            });
            //add chip to group
            binding.cgGuests.addView(chip);
        }
        //room
        //get array list for room
        List<String> tempRooms = new ArrayList<>();
        tempRooms.add("None");
        for (int i = 0; i < MData.arrRoom.size(); i++) {
            tempRooms.add(MData.arrRoom.get(i).getNickName());
        }
        ArrayAdapter<String> aaRoom = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, tempRooms);
        binding.actvRoom.setAdapter(aaRoom);
        binding.actvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index_room_choice = i;
            }
        });
        index_room_choice = presenter.findIndexRoomOfEvent(eventWantToEdit.getRoom_id()) + 1; // because in the select room index 0 is None
        binding.actvRoom.setText(aaRoom.getItem(index_room_choice), false);
        //start time
        binding.tvStartTime.setOnClickListener(it -> {
            initTimeChoiceStart();
        });
        binding.tvStartTime.setInputType(InputType.TYPE_NULL);
        //end time
        binding.tvEndTime.setOnClickListener(it -> {
            initTimeChoiceEnd();
        });
        binding.tvEndTime.setInputType(InputType.TYPE_NULL);
        //date
        binding.tvDate.setOnClickListener(it -> {
            initDateChoice();
        });
        binding.tvDate.setInputType(InputType.TYPE_NULL);
        //button add event
        binding.btnSaveEditEvent.setOnClickListener(it -> {
            if (mDialog.checkConnection(this)) {
                boolean check = false;
                //check fill all data
                if (binding.actvRoom.getText().toString().equals("None")) {
                    check = true;
                    if (!binding.tilRoom.isErrorEnabled()) {
                        binding.tilRoom.setErrorEnabled(true);
                    }
                    binding.tilRoom.setError("This field is required not None");
                } else {
                    if (binding.tilRoom.isErrorEnabled()) {
                        binding.tilRoom.setErrorEnabled(false);
                    }
                }
                if (binding.tvStartTime.getText().toString().isEmpty()) {
                    check = true;
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                } else {
                    binding.tvStartTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if (binding.tvEndTime.getText().toString().isEmpty()) {
                    check = true;
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                } else {
                    binding.tvEndTime.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if (binding.tvDate.getText().toString().isEmpty()) {
                    check = true;
                    binding.tvDate.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                } else {
                    binding.tvDate.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if (binding.edtTitle.getText().toString().isEmpty()){
                    check = true;
                    binding.edtTitle.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_red));
                } else {
                    binding.edtTitle.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_black));
                }
                if(fc.userLoginIsAdmin()){
                    if(host == null){
                        check = true;
                        mDialog.showErrorDialog(this, "Please choice host");
                    }
                }
                if (check) {
                    mDialog.showFillData(this, null);
                } else {
                    if (presenter.compareTimeDateStartAndDateEnd(binding.tvStartTime.getText().toString(), binding.tvEndTime.getText().toString())) {
                        // get data to add into firebase
                        String title = binding.edtTitle.getText().toString();
                        String summary = binding.edtSummary.getText().toString();
                        // remove host of guest <must fix it>
                        if (fc.userLoginIsAdmin() && host != null) {
                            if (guestListNew.contains(host)) {
                                guestListNew.remove(host);
                            }
                        }
                        int numberParticipant = guestListNew.size() + 1; //because host is a participant
                        int room_id = MData.arrRoom.get(index_room_choice - 1).getId();
                        Date dateStart = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvStartTime.getText().toString() + " " + binding.tvDate.getText()));
                        Date dateEnd = mConvertTime.convertMiliToDate(mConvertTime.convertStringToMili(binding.tvEndTime.getText().toString() + " " + binding.tvDate.getText()));
                        //check the number participant if it more than the max value of room's participant
                        if (MData.arrRoom.get(index_room_choice - 1).getMaxNum() >= numberParticipant) {
                            //create event
                            Event tempEvent = new Event();
                            tempEvent.setId(eventWantToEdit.getId());
                            tempEvent.setTitle(title);
                            tempEvent.setSummery(summary);
                            tempEvent.setDateCreated(eventWantToEdit.getDateCreated());
                            tempEvent.setDateUpdated(System.currentTimeMillis());
                            tempEvent.setDateStart(mConvertTime.convertDateToMili(dateStart));
                            tempEvent.setDateEnd(mConvertTime.convertDateToMili(dateEnd));
                            tempEvent.setRoom_id(room_id);
                            tempEvent.setNumberParticipant(numberParticipant);
                            tempEvent.setStatus(0);
                            eventWantToEdit = tempEvent;
                            fc.checkEditEvent(this, tempEvent);
                        } else {
                            mDialog.showErrorDialog(this, "The number of participants exceeds the maximum number of people\n the meeting room can accommodate");
                        }
                    } else {
                        mDialog.showTimeError(this);
                    }
                }
            }
        });
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

    private void initDateChoice() {
        if (dpd != null && !dpd.isShowing()) {
            dpd.show();
        }
    }

    private void initFirebaseController() {
        if (fc == null) {
            fc = new FirebaseController(null, null, this, null, this);
        }
    }

    private void initMConvertTime() {
        if (mConvertTime == null) {
            mConvertTime = new MConvertTime();
        }
    }

    private void initPresenter() {
        if (presenter == null) {
            presenter = new EditEventPresenter(this);
        }
    }

    @Override
    public void callbackEditEvent(Event event, List<Event> eventsOverlap) {
        if (event != null) {
            fc.editEvent(this, event);
        } else {
            fc.getArrHostOfArrEvent(this, eventsOverlap);
        }
    }

    @Override
    public void editEventSuccess(Event event) {
        this.eventWantToEdit = event; // set the event return to activity is the event new update
        // check to update detail participant
        List<User> keepGuest = new ArrayList<>(guestListOld);
        keepGuest.retainAll(guestListNew);
        List<User> addGuest = new ArrayList<>(guestListNew);
        addGuest.removeAll(keepGuest);
        List<User> removeGuest = new ArrayList<>(guestListOld);
        removeGuest.removeAll(keepGuest);
        if (addGuest.equals(removeGuest)) {
            callbackAddDetailParticipant(true);
        } else {
            fc.editEventDetailParticipant(this, event.getId(), addGuest, removeGuest, host);
        }
    }

    @Override
    public void callbackAddDetailParticipant(boolean b) {
        if (b) {
            //show notification success add and update UI to the main home
            dialogEditSuccess = mDialog.showDialogSuccess(this, "Edit Success", "Edit Event Success");
            dialogEditSuccess.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go back month fragment
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(KEY_EVENT_EDIT_ACTIVITY, eventWantToEdit);
                    bundle.putParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY, (ArrayList<? extends Parcelable>) guestListNew);
                    bundle.putParcelable(KEY_HOST_EDIT_ACTIVITY, host);
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
    public void callbackGetHostEventOverlap(List<Event> eventsOverlap, List<User> arrHost) {
        dialogEventOverlap = mDialog.showEventsDuplicate(this, eventsOverlap);
        //set title time
        TextView tv = dialogEventOverlap.findViewById(R.id.tv_date);
        Date date = mConvertTime.convertMiliToDate(eventsOverlap.get(0).getDateStart());
        tv.setText(mConvertTime.convertDateToString3(date));
        //set recycle view
        RecyclerView rv = dialogEventOverlap.findViewById(R.id.rv_event_overlap);

        EventsOverlapAdapter adapter = new EventsOverlapAdapter(this, eventsOverlap, arrHost, new OnItemEventOverlap() {
            @Override
            public void OnItemDeleteCLickListener(int position) {
                showDialogDeleteEvent(eventsOverlap.get(position));
            }

            @Override
            public void OnItemEditCLickListener(Event editEvent) {
                fc.checkEditEventOverlap(getApplicationContext(), editEvent);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
        //show dialog
        dialogEventOverlap.show();
    }

    private void showDialogDeleteEvent(Event event) {
        dialogConfirmDeleteEvent.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fc.deleteEvent(getApplicationContext(), event);
            }
        });
        dialogConfirmDeleteEvent.show();
    }

    @Override
    public void OnItemUserCLickListener(User user, int type) {
        if (type == MConst.USER_ADAPTER_TYPE_GUEST) {
            //check user was added
            if (!guestListNew.contains(user)) {
                guestListNew.add(user);
                //create chip
                Chip chip = new Chip(this);
                chip.setText(user.getFullName());
                chip.setCloseIconVisible(true);
                chip.setTextAppearance(R.style.ChipTextAppearance);
                chip.setOnCloseIconClickListener(it -> {
                    guestListNew.remove(user);
                    binding.cgGuests.removeView(chip);
                });
                //add chip to group
                binding.cgGuests.addView(chip);
            }
        } else if (type == MConst.USER_ADAPTER_TYPE_HOST) {
            host = user;
            if(binding.cgHost.getChildCount() == 0){
                //create chip
                Chip chip = new Chip(this);
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
    public void updateEvent(List<Event> events) {
        //not do any thing here because we do not need to do here and reach here
    }

    @Override
    public void deleteEventSuccess(Event event) {
        //dismiss all dialog when delete success and show delete success
        //dismiss dialog
        if (dialogConfirmDeleteEvent != null && dialogConfirmDeleteEvent.isShowing()) {
            dialogConfirmDeleteEvent.dismiss();
        }
        if (dialogEventOverlap.isShowing()) {
            dialogEventOverlap.dismiss();
        }
        //show delete success
        dialogDeleteEvent = mDialog.dialogDeleteSuccess(this, event);
        dialogDeleteEvent.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update adapter of dialog event overlap by a loop
                fc.checkEditEvent(getApplicationContext(), eventWantToEdit);
                dialogDeleteEvent.dismiss();
            }
        });
        dialogDeleteEvent.show();
    }

    @Override
    public void callbackEditEventOverlap(Event event, List<Event> eventsOverlap) {
        if (eventsOverlap.isEmpty()) {
            fc.editEventOverlap(this, event);
        } else {
            // check event overlap != event want to edit
            if (eventsOverlap.contains(eventWantToEdit)) {
                eventsOverlap.remove(eventWantToEdit);
            }
            if (eventsOverlap.isEmpty()) {
                fc.editEventOverlap(this, event);
            } else {
                dialogErrorEdit = mDialog.dialogError(this, "ERROR", "The time is overlap");
                dialogErrorEdit.setOnDismissListener(it -> {
                    if (dialogEventOverlap != null && dialogEventOverlap.isShowing()) {
                        dialogEventOverlap.dismiss();
                        fc.checkEditEvent(this, eventWantToEdit);
                    }
                });
                dialogErrorEdit.show();
            }
        }
    }

    @Override
    public void editEventSuccessOverlap(Event event) {
        //dismiss dialog
        if (dialogEventOverlap.isShowing()) {
            dialogEventOverlap.dismiss();
        }
        //show edit event success
        dialogEditEventSuccessOverlap = mDialog.dialogEditSuccess(this, event);
        dialogEditEventSuccessOverlap.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditEventSuccessOverlap.dismiss();
            }
        });
        dialogEditEventSuccessOverlap.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //update adapter of dialog event overlap by a loop
                fc.checkEditEvent(getApplicationContext(), eventWantToEdit);
            }
        });
        dialogEditEventSuccessOverlap.show();
    }
}