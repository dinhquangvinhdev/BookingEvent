package com.vdev.bookingevent.presenter;

import android.content.Context;

public interface AccountContract {
    interface View{}
    interface Presenter{
        void logout(Context context);
    }
}
