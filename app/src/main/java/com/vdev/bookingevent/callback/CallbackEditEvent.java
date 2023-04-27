package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.Event;

public interface CallbackEditEvent {
    void callbackEditEvent(Event event);
    void editEventSuccess(Event event);
    void callbackAddDetailParticipant(boolean b);
}
