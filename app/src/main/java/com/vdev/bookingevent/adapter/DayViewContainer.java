package com.vdev.bookingevent.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.view.ViewContainer;
import com.vdev.bookingevent.callback.CallbackItemDayCalMonth;
import com.vdev.bookingevent.databinding.CalendarDayLayoutBinding;

public class DayViewContainer extends ViewContainer {
    private CalendarDayLayoutBinding binding;
    private CalendarDay day;
    private CallbackItemDayCalMonth callback;

    public DayViewContainer(@NonNull View view , CallbackItemDayCalMonth callback) {
        super(view);
        this.callback = callback;
        //set textview
        binding = CalendarDayLayoutBinding.bind(view);
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

    public CalendarDayLayoutBinding getBinding() {
        return binding;
    }

    public void setHaveEventInDay(boolean check){
        if(check){
            binding.imgRoundBlue.setVisibility(View.INVISIBLE);
        } else{
            binding.imgRoundBlue.setVisibility(View.INVISIBLE);
        }
    }
}
