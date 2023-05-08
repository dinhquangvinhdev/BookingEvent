package com.vdev.bookingevent.view.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.DayViewContainer;
import com.vdev.bookingevent.adapter.EventsDashMonthAdapter;
import com.vdev.bookingevent.adapter.GuestEventDetailAdapter;
import com.vdev.bookingevent.adapter.MonthViewContainer;
import com.vdev.bookingevent.callback.CallbackDetailEvent;
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.callback.CallbackItemDayCalMonth;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.CalendarDayLayoutBinding;
import com.vdev.bookingevent.databinding.FragmentDashboardMonthBinding;
import com.vdev.bookingevent.databinding.LayoutDetailEventBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;
import com.vdev.bookingevent.model.User;
import com.vdev.bookingevent.presenter.DashboardMonthContract;
import com.vdev.bookingevent.presenter.DashboardMonthPresenter;
import com.vdev.bookingevent.view.EditEventActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DashboardMonthFragment extends Fragment
        implements DashboardMonthContract.View, CallbackItemCalDashMonth,
        CallbackItemDayCalMonth, CallbackUpdateEventDisplay , CallbackDetailEvent {
    private final String KEY_GUESTS_EDIT_ACTIVITY = "KEY_GUESTS_EDIT_ACTIVITY";
    private final String KEY_HOST_EDIT_ACTIVITY = "KEY_HOST_EDIT_ACTIVITY";
    private final String KEY_EVENT_EDIT_ACTIVITY = "KEY_EVENT_EDIT_ACTIVITY";
    private final int REQUEST_CODE_EDIT_EVENT_ACTIVITY = 10;
    private FragmentDashboardMonthBinding binding;
    private LayoutDetailEventBinding bindingDetailEvent;
    private DashboardMonthPresenter presenter;
    private EventsDashMonthAdapter adapter;
    private LocalDate selectedDay;
    private LocalDate today = LocalDate.now();
    private FirebaseController fc;
    private MConvertTime mConvertTime;
    private BottomSheetBehavior bsb;
    private Dialog confirmDeleteEvent;
    private Dialog dialogDelete;
    private MDialog mDialog;

    public DashboardMonthFragment() {
        // Required empty public constructor
    }

    public void updateDisplayData(List<Event> events) {
        //update event filter
        presenter.updateFilterEvent(selectedDay , events);
        //update adapter
        adapter.setEvents(MData.arrFilterEvent);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardMonthBinding.inflate(getLayoutInflater(), container, false);
        bindingDetailEvent = binding.includeLayoutDetailEvent;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedDay = LocalDate.now();
        initMDialog();
        initMConvertTime();         //always create convert time before create presenter
        initFirebaseController();   //always create firebase before create presenter
        initPresenter();
        initSlidingPanel();
        setupMonthCalendar();
        initHeaderCalendar();
        initRVEvents();

        updateTitleTime(today);
        updateEvent(MData.arrEvent); // this is called to update the adapter when you change fragment <not good>
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

    private void initMConvertTime() {
        if (mConvertTime == null) {
            mConvertTime = new MConvertTime();
        }
    }

    private void initFirebaseController() {
        if (fc == null) {
            fc = new FirebaseController(this, null, null,this,null);
            //get event in the first time
            int monthNow = Calendar.getInstance().get(Calendar.MONTH);
            fc.getAllEvent();
            //fc.getEventInRange2(MData.getStartMonth(monthNow), MData.getEndMonth(monthNow));
        }
    }

    private void updateTitleTime(LocalDate dateSelected) {
        //set text for title time selected (auto choice today in the first using)
        binding.tvTitleTimeSelected.setText(dateSelected.getDayOfMonth() + " " + dateSelected.getMonth() + " " + dateSelected.getYear());
    }

    private void initHeaderCalendar() {
        //forward month
        binding.imgForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.exOneCalendar.findFirstVisibleMonth() != null) {
                    binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().plusMonths(1));
                }
            }
        });
        //backward month
        binding.imgBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.exOneCalendar.findFirstVisibleMonth() != null) {
                    binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().minusMonths(1));
                }
            }
        });
    }

    private void initRVEvents() {
        adapter = new EventsDashMonthAdapter(this);
        binding.rvEventData.setAdapter(adapter);
        adapter.setEvents(MData.arrEvent);
        binding.rvEventData.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    }

    private void initPresenter() {
        if (presenter == null) {
            presenter = new DashboardMonthPresenter(this, mConvertTime, fc);
        }
    }

    private void setupMonthCalendar() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);

        //set up calendar
        binding.exOneCalendar.setup(startMonth, endMonth, firstDayOfWeekFromLocale());
        //set day binder
        binding.exOneCalendar.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view, DashboardMonthFragment.this::onClickDayCalMonth);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                container.setDay(calendarDay);
                CalendarDayLayoutBinding dayLayoutBinding = container.getBinding();
                dayLayoutBinding.tvDay.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));

                if (calendarDay.getPosition() == DayPosition.MonthDate) {
                    updateUIDayCalendar(calendarDay, dayLayoutBinding.tvDay, dayLayoutBinding.imgRoundBlue);
                } else if (calendarDay.getPosition() == DayPosition.InDate) {
                    dayLayoutBinding.tvDay.setTextColor(Color.GRAY);
                    if (calendarDay.getDate() == selectedDay) {
                        // backward month and choice the day
                        binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().minusMonths(1));
                        binding.exOneCalendar.notifyDateChanged(selectedDay);
                    }
                } else {
                    dayLayoutBinding.tvDay.setTextColor(Color.GRAY);
                    if (calendarDay.getDate() == selectedDay) {
                        // forward month and choice the day
                        binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().plusMonths(1));
                        binding.exOneCalendar.notifyDateChanged(selectedDay);
                    }
                }
            }


        });
        //set header
        binding.exOneCalendar.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {

            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth calendarMonth) {
                if (container.getViewGroup().getTag() == null) {
                    container.getViewGroup().setTag(calendarMonth.getYearMonth());
                    for (int i = 0; i < container.getViewGroup().getChildCount(); i++) {
                        DayOfWeek dayOfWeek = daysOfWeek().get(i);
                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
                        TextView tv = (TextView) container.getViewGroup().getChildAt(i);
                        tv.setText(title);
                    }
                }
            }
        });
        //set Scroll listener
        binding.exOneCalendar.setMonthScrollListener(new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth calendarMonth) {
                binding.tvTitleTime.setText(calendarMonth.getYearMonth().getMonth() + " " + calendarMonth.getYearMonth().getYear());
                //update UI day if click outDate and inDate
                if (selectedDay != null) {
                    binding.exOneCalendar.notifyDateChanged(selectedDay);
                }
                return null;
            }
        });
        //scroll to month now
        binding.exOneCalendar.scrollToMonth(currentMonth);
    }

    private void updateUIDayCalendar(CalendarDay calendarDay, TextView tv, ImageView imgRoundBlue) {
        //set color for day
        if (calendarDay.getDate() == selectedDay) {          // set color for selected day
            tv.setTextColor(Color.WHITE);
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_tab));
        } else if (calendarDay.getDate().compareTo(today) == 0) {         //set color for today
            tv.setTextColor(getResources().getColor(R.color.selectColor));
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_outline_blue));
        } else {                                            //set color for day not selected and not today
            tv.setTextColor(Color.BLACK);
            tv.setBackground(null);
        }

        //set round blue for day have event
        for (int i = 0; i < MData.arrEvent.size(); i++) {
            Event tempEvent = MData.arrEvent.get(i);
            Calendar calendar = mConvertTime.convertMiliToCalendar(tempEvent.getDateStart());
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            if (calendarDay.getDate().getDayOfMonth() == dayOfMonth
                    && calendarDay.getDate().getMonthValue() == month
                    && calendarDay.getDate().getYear() == year && tempEvent.getStatus() == 0) {
                imgRoundBlue.setVisibility(View.VISIBLE);
                break;
            } else {
                imgRoundBlue.setVisibility(View.INVISIBLE);
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
        if (dialogDelete != null && dialogDelete.isShowing()){
            dialogDelete.dismiss();
        }
    }

    @Override
    public void openSlidingPanel(int idEvent, String roomColor) {
        fc.getParticipantOfEvent(getContext(),idEvent);
    }

    @Override
    public void onClickDayCalMonth(View view, CalendarDay day) {
        LocalDate oldDate = selectedDay;
        selectedDay = day.getDate();
        //update title month
        updateTitleTime(selectedDay);
        if (day.getPosition() == DayPosition.OutDate) {
            binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().plusMonths(1));
        } else if (day.getPosition() == DayPosition.InDate) {
            binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().minusMonths(1));
        }
        //update UI calendar
        binding.exOneCalendar.notifyDateChanged(day.getDate());
        if (oldDate != null) {
            binding.exOneCalendar.notifyDateChanged(oldDate);
        }
        //update event filter
        updateDisplayData(null);
    }

    @Override
    public void updateEvent(List<Event> events) {
        //update event filter
        updateDisplayData(events);

        //update adapter event
        adapter.setEvents(MData.arrFilterEvent);
        adapter.notifyDataSetChanged();
        //update calendar
        if (binding != null) {
            binding.exOneCalendar.notifyCalendarChanged();
//            for(int i=0 ; i<events.size() ; i++){
//                LocalDate localDate = Instant.ofEpochMilli(events.get(i).getDateStart()).atZone(ZoneId.systemDefault()).toLocalDate();
//                //TODO just update the day need to draw again
//            }
        }
    }

    @Override
    public void deleteEventSuccess(Event event) {
        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
        dialogDelete = mDialog.dialogDeleteSuccess(getContext(), event);
        dialogDelete.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_EDIT_EVENT_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                Event updatedEvent;
                List<User> guests = new ArrayList<>();
                User host;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    updatedEvent = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY, Event.class);
                    guests = bundle.getParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY);
                    host = bundle.getParcelable(KEY_HOST_EDIT_ACTIVITY, User.class);
                } else {
                    updatedEvent = bundle.getParcelable(KEY_EVENT_EDIT_ACTIVITY);
                    guests = bundle.getParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY);
                    host = bundle.getParcelable(KEY_HOST_EDIT_ACTIVITY);
                }
                updatedEventInSlidingPanel(updatedEvent,guests, host);
            }
        }
    }

    private void updatedEventInSlidingPanel(Event updatedEvent, List<User> guests, User host) {
        if(bsb != null && bsb.getState() == BottomSheetBehavior.STATE_EXPANDED){
            if (updatedEvent != null) {
                Log.d("bibibla", "openSlidingPanel: " + "found event");
                bindingDetailEvent.tvEventDetailTitle.setText(updatedEvent.getTitle());
                String textTime = presenter.convertTimeToStringDE(updatedEvent.getDateStart(), updatedEvent.getDateEnd());
                bindingDetailEvent.tvEventDetailTime.setText(textTime);
                String nameRoom = presenter.getNameRoom(updatedEvent.getRoom_id());
                bindingDetailEvent.tvEventSummary.setText(updatedEvent.getSummery());
                bindingDetailEvent.tvEventDetailNameRoom.setText(nameRoom);
                for(int i=0 ; i<MData.arrRoom.size() ; i++){
                    Room room = MData.arrRoom.get(i);
                    if(room.getId() == updatedEvent.getRoom_id()){
                        bindingDetailEvent.imgColorRoom.setBackgroundColor(Color.parseColor(room.getColor()));
                        break;
                    }
                }
                //need to refresh edit button because new event updated
                bindingDetailEvent.imgEditEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), EditEventActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(KEY_EVENT_EDIT_ACTIVITY , updatedEvent);
                        bundle.putParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY, (ArrayList<? extends Parcelable>) guests);
                        bundle.putParcelable(KEY_HOST_EDIT_ACTIVITY, host);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT_ACTIVITY);
                    }
                });
                bindingDetailEvent.tvEventDetailParticipant.setText((updatedEvent.getNumberParticipant() - 1) + " Guest");
                //update adapter
                GuestEventDetailAdapter adapterGuest = (GuestEventDetailAdapter) bindingDetailEvent.rvGuest.getAdapter();
                adapterGuest.updateDataGuest(guests, host);
            }
        }
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
            if( checkPriority == 1){
                bindingDetailEvent.imgEditEvent.setVisibility(View.INVISIBLE);
                bindingDetailEvent.imgDeleteEvent.setVisibility(View.INVISIBLE);
            } else if(checkPriority == 0 ){
                bindingDetailEvent.imgEditEvent.setVisibility(View.VISIBLE);
                bindingDetailEvent.imgDeleteEvent.setVisibility(View.INVISIBLE);
            } else if(checkPriority == 2 || checkPriority == 3){
                bindingDetailEvent.imgEditEvent.setVisibility(View.VISIBLE);
                bindingDetailEvent.imgDeleteEvent.setVisibility(View.VISIBLE);
            } else {
                //something bad when compare event
                bindingDetailEvent.imgEditEvent.setVisibility(View.INVISIBLE);
                bindingDetailEvent.imgDeleteEvent.setVisibility(View.INVISIBLE);
            }
            for(int i=0 ; i<MData.arrRoom.size() ; i++){
                Room room = MData.arrRoom.get(i);
                if(room.getId() == event.getRoom_id()){
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
                    bundle.putParcelable(KEY_EVENT_EDIT_ACTIVITY , event);
                    bundle.putParcelableArrayList(KEY_GUESTS_EDIT_ACTIVITY, (ArrayList<? extends Parcelable>) guests);
                    bundle.putParcelable(KEY_HOST_EDIT_ACTIVITY, host);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_EVENT_ACTIVITY);
                }
            });
            //create adapter
            GuestEventDetailAdapter adapterGuest = new GuestEventDetailAdapter(guests, host);
            bindingDetailEvent.rvGuest.setAdapter(adapterGuest);
            bindingDetailEvent.rvGuest.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));
        } else {
            //TODO show notification or not do anything when not found event
            Log.d("bibibla", "openSlidingPanel: " + "not found event");
        }
    }
}