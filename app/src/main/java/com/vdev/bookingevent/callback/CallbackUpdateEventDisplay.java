package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;

import java.util.List;

public interface CallbackUpdateEventDisplay {
    void updateEvent(List<Event> events);
    void deleteEventSuccess(Event event);
}
