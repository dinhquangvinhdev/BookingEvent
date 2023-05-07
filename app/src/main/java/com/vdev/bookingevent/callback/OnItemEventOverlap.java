package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;

public interface OnItemEventOverlap {
    void OnItemDeleteCLickListener(int position);

    void OnItemEditCLickListener(Event editEvent);
}
