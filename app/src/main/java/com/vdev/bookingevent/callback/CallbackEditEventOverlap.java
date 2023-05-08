package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.util.List;

public interface CallbackEditEventOverlap {
    void callbackEditEventOverlap(Event event, List<Event> eventsOverlap);
    void editEventSuccessOverlap(Event event);
}
