package com.vdev.bookingevent.view.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.callback.CallbackItemDayCalMonth;
import com.vdev.bookingevent.common.MConvertTime;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.CalendarDayLayoutBinding;
import com.vdev.bookingevent.databinding.FragmentDashboardMonthBinding;
import com.vdev.bookingevent.databinding.LayoutDetailEventBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.presenter.DashboardMonthContract;
import com.vdev.bookingevent.presenter.DashboardMonthPresenter;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DashboardMonthFragment extends Fragment
        implements DashboardMonthContract.View , CallbackItemCalDashMonth , CallbackItemDayCalMonth, CallbackUpdateEventDisplay {

    private FragmentDashboardMonthBinding binding;
    private LayoutDetailEventBinding bindingDetailEvent;
    private DashboardMonthPresenter presenter;
    private EventsDashMonthAdapter adapter;
    private LocalDate selectedDay;
    private LocalDate today = LocalDate.now();
    private FirebaseController fc;
    private MConvertTime mConvertTime;
    private BottomSheetBehavior bsb;

    public DashboardMonthFragment() {
        // Required empty public constructor
    }

    public void updateDisplayData(){
        //update event filter
        presenter.updateFilterEvent(selectedDay);
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
        binding = FragmentDashboardMonthBinding.inflate(getLayoutInflater() , container , false);
        bindingDetailEvent = binding.includeLayoutDetailEvent;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedDay = LocalDate.now();
        initMConvertTime();         //always create convert time before create presenter
        initFirebaseController();   //always create firebase before create presenter
        initPresenter();
        initSlidingPanel();
        setupMonthCalendar();
        initHeaderCalendar();
        initRVEvents();

        updateTitleTime(today);
    }

    private void initSlidingPanel() {
        bsb = BottomSheetBehavior.from(binding.flBottomSheet);
        bsb.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initMConvertTime() {
        if(mConvertTime == null){
            mConvertTime = new MConvertTime();
        }
    }

    private void initFirebaseController() {
        if(fc == null){
            fc = new FirebaseController(this, null);
            //get event in the first time
            int monthNow = Calendar.getInstance().get(Calendar.MONTH);
            fc.getEventInRange2(MData.getStartMonth(monthNow), MData.getEndMonth(monthNow));
            //get room
            fc.getRoom();
            //get department
            fc.getDepartment();
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
                if(binding.exOneCalendar.findFirstVisibleMonth() != null){
                    binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().plusMonths(1));
                }
            }
        });
        //backward month
        binding.imgBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.exOneCalendar.findFirstVisibleMonth() != null){
                    binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().minusMonths(1));
                }
            }
        });
    }

    private void initRVEvents() {
        adapter = new EventsDashMonthAdapter(this);
        binding.rvEventData.setAdapter(adapter);
        adapter.setEvents(MData.arrEvent);
        binding.rvEventData.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));

    }

    private void initPresenter() {
        if(presenter == null){
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
                return new DayViewContainer(view , DashboardMonthFragment.this::onClickDayCalMonth);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                container.setDay(calendarDay);
                CalendarDayLayoutBinding dayLayoutBinding = container.getBinding();
                dayLayoutBinding.tvDay.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
                
                if(calendarDay.getPosition() == DayPosition.MonthDate){
                    updateUIDayCalendar(calendarDay , dayLayoutBinding.tvDay, dayLayoutBinding.imgRoundBlue);
                }else if(calendarDay.getPosition() == DayPosition.InDate){
                    dayLayoutBinding.tvDay.setTextColor(Color.GRAY);
                    if(calendarDay.getDate() == selectedDay){
                        // backward month and choice the day
                        binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().minusMonths(1));
                        binding.exOneCalendar.notifyDateChanged(selectedDay);
                    }
                } else {
                    dayLayoutBinding.tvDay.setTextColor(Color.GRAY);
                    if(calendarDay.getDate() == selectedDay){
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
                if(container.getViewGroup().getTag() == null) {
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
                if(selectedDay != null) {
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
        if (calendarDay.getDate() == selectedDay){          // set color for selected day
            tv.setTextColor(Color.WHITE);
            tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_tab));
        } else if(calendarDay.getDate().compareTo(today) == 0){         //set color for today
            tv.setTextColor(getResources().getColor(R.color.selectColor));
            tv.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.rounded_outline_blue));
        } else {                                            //set color for day not selected and not today
            tv.setTextColor(Color.BLACK);
            tv.setBackground(null);
        }

        //set round blue for day have event
        for(int i=0 ; i<MData.arrEvent.size(); i++){
            Event tempEvent = MData.arrEvent.get(i);
            Calendar calendar = mConvertTime.convertMiliToCalendar(tempEvent.getDateStart());
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            if(calendarDay.getDate().getDayOfMonth() == dayOfMonth
                    && calendarDay.getDate().getMonthValue() == month
                    && calendarDay.getDate().getYear() == year){
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
    }

    @Override
    public void openSlidingPanel(int idEvent, String roomColor) {
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);
        // find the event in data
        Event event = presenter.findEventInData(idEvent);
        if(event != null){
            Log.d("bibibla", "openSlidingPanel: " + "found event");
            bindingDetailEvent.tvEventDetailTitle.setText(event.getTitle());
            String textTime = presenter.convertTimeToStringDE(event.getDateStart(), event.getDateEnd());
            bindingDetailEvent.tvEventDetailTime.setText(textTime);
            String nameRoom = presenter.getNameRoom(event.getRoom_id());
            bindingDetailEvent.tvEventDetailNameRoom.setText(nameRoom);
            bindingDetailEvent.tvEventDetailParticipant.setText((event.getNumberParticipant()-1) + " Guest");
            //TODO recycle view Guest
            //create adapter
            //GuestEventDetailAdapter adapterGuest = new GuestEventDetailAdapter(presenter.getGuests(), presenter.getHost());
            //bindingDetailEvent.rvGuest.setAdapter(adapterGuest);
            //bindingDetailEvent.rvGuest.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));
        } else {
            //TODO show notification or not do anything when not found event
            Log.d("bibibla", "openSlidingPanel: " + "not found event");
        }
    }

    @Override
    public void onClickDayCalMonth(View view , CalendarDay day) {
        LocalDate oldDate = selectedDay;
        selectedDay = day.getDate();
        //update title month
        updateTitleTime(selectedDay);
        if(day.getPosition() == DayPosition.OutDate){
            binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().plusMonths(1));
        } else if(day.getPosition() == DayPosition.InDate){
            binding.exOneCalendar.smoothScrollToMonth(binding.exOneCalendar.findFirstVisibleMonth().getYearMonth().minusMonths(1));
        }
        //update UI
        binding.exOneCalendar.notifyDateChanged(day.getDate());
        if(oldDate != null){
            binding.exOneCalendar.notifyDateChanged(oldDate);
        }
        //update event filter
        updateDisplayData();
    }

    @Override
    public void updateEvent(List<Event> events) {
        //update event filter
        updateDisplayData();

        //update adapter event
        adapter.setEvents(MData.arrFilterEvent);
        adapter.notifyDataSetChanged();
        //update calendar
        if(binding != null){
            binding.exOneCalendar.notifyCalendarChanged();
//            for(int i=0 ; i<events.size() ; i++){
//                LocalDate localDate = Instant.ofEpochMilli(events.get(i).getDateStart()).atZone(ZoneId.systemDefault()).toLocalDate();
//                //TODO just update the day need to draw again
//            }
        }
    }

}