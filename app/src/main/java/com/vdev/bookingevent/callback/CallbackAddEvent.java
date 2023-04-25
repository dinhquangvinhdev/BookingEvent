package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;

public interface CallbackAddEvent {
    void callbackAddDetailParticipant();

    void callbackCanAddNewEvent(Event event);
}
