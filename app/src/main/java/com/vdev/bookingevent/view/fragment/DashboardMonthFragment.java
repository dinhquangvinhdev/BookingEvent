package com.vdev.bookingevent.view.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.vdev.bookingevent.adapter.DayViewContainer;
import com.vdev.bookingevent.adapter.EventsDashMonthAdapter;
import com.vdev.bookingevent.adapter.MonthViewContainer;
import com.vdev.bookingevent.callback.CallbackItemCalDashMonth;
import com.vdev.bookingevent.databinding.FragmentDashboardMonthBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.presenter.DashboardMonthContract;
import com.vdev.bookingevent.presenter.DashboardMonthPresenter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardMonthFragment extends Fragment implements DashboardMonthContract.View , CallbackItemCalDashMonth {

    private FragmentDashboardMonthBinding binding;
    private DashboardMonthPresenter presenter;
    private EventsDashMonthAdapter adapter;

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
        initRVEvents();
    }

    private void initRVEvents() {
        adapter = new EventsDashMonthAdapter(this);
        binding.rvEventData.setAdapter(adapter);
        //TODO it is just a sample event in here
        Event event = new Event();
        event.setSummery("Test summery");
        event.setId(0);
        event.setDate_start(new Date());
        event.setDate_end(new Date(System.currentTimeMillis() + (86400 * 7 * 1000)));
        List<Event> events = new ArrayList<>();
        events.add(event);
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
                container.getTv().setText(String.valueOf(calendarDay.getDate().getDayOfMonth()));
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
                if(container.getViewGroup().getTag() == null){
                    container.getViewGroup().setTag(calendarMonth.getYearMonth());
                    for(int i=0 ; i<container.getViewGroup().getChildCount(); i++){
                        DayOfWeek dayOfWeek = daysOfWeek().get(i);
                        String title = dayOfWeek.getDisplayName(TextStyle.SHORT , Locale.getDefault());
                        TextView tv = (TextView) container.getViewGroup().getChildAt(i);
                        tv.setText(title);
                    }
                }
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