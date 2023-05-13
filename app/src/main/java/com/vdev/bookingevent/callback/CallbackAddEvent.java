package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.util.List;

public interface CallbackAddEvent {
    void callbackAddDetailParticipant(boolean b);

    void callbackCanAddNewEvent(Event event, List<Event> eventsDuplicate);

    void callbackGetHostEventOverlap(List<Event> eventsOverlap , List<User> hosts);

    void callbackAddEventSuccess(boolean b);
}
