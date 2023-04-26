package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.User;

public interface CallbackDetailEvent {
    void callbackShowSlidingPanel(User host, int idEvent);
}
