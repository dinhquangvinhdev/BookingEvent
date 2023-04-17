package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;

import java.util.List;

public interface CallbackFromFirebase {
    void updateEvent(List<Event> events);
}
