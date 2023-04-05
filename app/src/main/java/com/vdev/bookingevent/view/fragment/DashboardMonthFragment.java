package com.vdev.bookingevent.view.fragment;

import static com.kizitonwose.calendar.core.ExtensionsKt.daysOfWeek;
import static com.kizitonwose.calendar.core.ExtensionsKt.firstDayOfWeekFromLocale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.vdev.bookingevent.adapter.DayViewContainer;
import com.vdev.bookingevent.adapter.MonthViewContainer;
import com.vdev.bookingevent.databinding.FragmentDashboardMonthBinding;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class DashboardMonthFragment extends Fragment {

    private FragmentDashboardMonthBinding binding;

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
        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(100);
        YearMonth endMonth = currentMonth.plusMonths(100);

        setupMonthCalendar(startMonth , endMonth , currentMonth);

    }

    private void setupMonthCalendar(YearMonth startMonth, YearMonth endMonth, YearMonth currentMonth) {
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
}