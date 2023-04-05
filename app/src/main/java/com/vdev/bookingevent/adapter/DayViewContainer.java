package com.vdev.bookingevent.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;
import com.vdev.bookingevent.databinding.CalendarDayLayoutBinding;

public class DayViewContainer extends ViewContainer {
    private TextView tv;

    public DayViewContainer(@NonNull View view) {
        super(view);
        tv = CalendarDayLayoutBinding.bind(view).tvDay;
    }

    public TextView getTv() {
        return tv;
    }
}
