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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.EventsDashMonthAdapter;
import com.vdev.bookingevent.adapter.MonthViewContainer;
import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.databinding.CalendarDayLayoutBinding;
import com.vdev.bookingevent.databinding.FragmentDashboardMonthBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.presenter.DashboardMonthContract;
import com.vdev.bookingevent.presenter.DashboardMonthPresenter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DashboardMonthFragment extends Fragment implements DashboardMonthContract.View , CallbackItemCalDashMonth {

    private FragmentDashboardMonthBinding binding;
    private DashboardMonthPresenter presenter;
    private EventsDashMonthAdapter adapter;
    private LocalDate selectedDay;
    private LocalDate today = LocalDate.now();

    public DashboardMonthFragment() {
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
        binding = FragmentDashboardMonthBinding.inflate(getLayoutInflater() , container , false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPresenter();
        setupMonthCalendar();
        initHeaderCalendar();
        initRVEvents();

        updateTitleTime(today);
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
        //TODO it is just a sample event in here
        List<Event> events = new ArrayList<>();
        for (int i=0 ; i<10 ; i++){
            Event event = new Event();
            event.setSummery("Test summery");
            event.setId(0);
            event.setDate_start(GregorianCalendar.getInstance().getTime());
            Calendar calEndTime = GregorianCalendar.getInstance();
            calEndTime.add(Calendar.HOUR_OF_DAY, 5);
            event.setDate_end(calEndTime.getTime());
            events.add(event);
        }
        adapter.setEvents(events);
        // TODO == END sample
        binding.rvEventData.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false));

    }

    private void initPresenter() {
        if(presenter == null){
            presenter = new DashboardMonthPresenter(this);
        }
    }

    private void setupMonthCalendar() {
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);
        //create class for dayViewContainer
        class DayViewContainer extends ViewContainer {
            private TextView tv;
            private CalendarDay day;

            public DayViewContainer(@NonNull View view) {
                super(view);
                //set textview
                tv = CalendarDayLayoutBinding.bind(view).tvDay;
                //set onclick textview
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(day.getPosition() == DayPosition.MonthDate){
                            if (selectedDay != day.getDate()) {
                                LocalDate oldDate = selectedDay;
                                selectedDay = day.getDate();
                                binding.exOneCalendar.notifyDateChanged(day.getDate());
                                if(oldDate != null){
                                    binding.exOneCalendar.notifyDateChanged(oldDate);
                                }
                                //update title month
                                updateTitleTime(selectedDay);
                            }
                        }
                    }
                });
            }

            public void setDay(CalendarDay day) {
                this.day = day;
            }

            public TextView getTv() {
                return tv;
            }
        }

        //set up calendar
        binding.exOneCalendar.setup(startMonth, endMonth, firstDayOfWeekFromLocale());
        //set day binder
        binding.exOneCalendar.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay calendarDay) {
                container.setDay(calendarDay);
                TextView tv = container.getTv();
                tv.setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
                
                if(calendarDay.getPosition() == DayPosition.MonthDate){
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
                }else {
                    container.getTv().setTextColor(Color.GRAY);
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
                return null;
            }
        });
        //scroll to month now
        binding.exOneCalendar.scrollToMonth(currentMonth);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void openSlidingPanel(String idEvent, String roomColor) {
        //TODO
    }
}