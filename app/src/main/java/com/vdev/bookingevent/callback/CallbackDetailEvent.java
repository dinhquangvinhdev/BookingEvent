package com.vdev.bookingevent.callback;

import com.vdev.bookingevent.model.User;

import java.util.List;

public interface CallbackDetailEvent {
    void callbackShowSlidingPanel(User host, List<User> guests, int idEvent);
}
