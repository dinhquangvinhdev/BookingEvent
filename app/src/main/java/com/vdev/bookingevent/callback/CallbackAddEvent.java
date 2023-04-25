package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;

import java.util.List;

public interface CallbackAddEvent {
    void callbackAddDetailParticipant();

    void callbackCanAddNewEvent(Event event, List<Event> eventsDuplicate);
}
