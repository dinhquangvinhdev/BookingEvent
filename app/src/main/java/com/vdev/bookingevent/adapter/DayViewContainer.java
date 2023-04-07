package com.vdev.bookingevent.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.view.ViewContainer;
import com.vdev.bookingevent.callback.CallbackItemDayCalMonth;
import com.vdev.bookingevent.databinding.CalendarDayLayoutBinding;

public class DayViewContainer extends ViewContainer {
    private TextView tv;
    private CalendarDay day;
    private CallbackItemDayCalMonth callback;

    public DayViewContainer(@NonNull View view , CallbackItemDayCalMonth callback) {
        super(view);
        this.callback = callback;
        //set textview
        tv = CalendarDayLayoutBinding.bind(view).tvDay;
        //set onclick textview
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClickDayCalMonth(view, day);
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
