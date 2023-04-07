package com.vdev.bookingevent.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.view.ViewContainer;

public class MonthViewContainer extends ViewContainer {
    ViewGroup viewGroup;
    public MonthViewContainer(@NonNull View view) {
        super(view);
        viewGroup = (ViewGroup)  view;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }
}
