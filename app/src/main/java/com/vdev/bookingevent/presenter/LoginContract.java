package com.vdev.bookingevent.presenter;

import android.content.Intent;

public interface LoginContract {
    interface View{
        void startActivity(Class activityClass);
        void turnOffProgressBar();
        void turnOnProgressBar();
    }

    interface Presenter{
        Intent login();

        void executeIntentLoginGG(Intent data);
    }
}
