package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.User;

import java.util.List;

public interface CallbackEditEvent {
    void callbackEditEvent(Event event, List<Event> eventsOverlap);
    void editEventSuccess(Event event);
    void callbackAddDetailParticipant(boolean b);
    void callbackGetHostEventOverlap(List<Event> eventsOverlap, List<User> arrHost);
}
