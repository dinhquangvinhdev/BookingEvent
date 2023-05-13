package com.vdev.bookingevent.view.fragment;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.databinding.FragmentDashboardWeekBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

public class DashboardWeekFragment extends Fragment {
    private FragmentDashboardWeekBinding binding;

    public DashboardWeekFragment() {
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
        binding = FragmentDashboardWeekBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //create calendar
        initCalendar();
    }

    private void initCalendar() {
        Log.d("bibibla", "initCalendar: calling");
        //TODO temp event
        List<WeekViewEvent> events = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 3);
        WeekViewEvent event1 = new WeekViewEvent(1, "test event 1", startTime, endTime);
        WeekViewEvent event2 = new WeekViewEvent(2, "test event 2", startTime, endTime);
        event1.setColor(Color.GRAY);
        event2.setColor(Color.BLUE);
        events.add(event1);
        events.add(event2);
        //TODO -- end temp
        //set up for calendar
        binding.weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                // Return only the events that matches newYear and newMonth.
                List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
                for (WeekViewEvent event : events) {
                    if (eventMatches(event, newYear, newMonth)) {
                        matchedEvents.add(event);
                    }
                }
                return matchedEvents;
            }
        });
        //set event click
        binding.weekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                Toast.makeText(getContext(), event.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        //set title day and title time
        binding.weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.ENGLISH);

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                if (false)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
        //set calendar to the first day of week
        binding.weekView.setShowFirstDayOfWeekFirst(true);

    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month-1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }
}