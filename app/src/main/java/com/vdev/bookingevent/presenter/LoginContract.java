package com.vdev.bookingevent.presenter;

import android.content.Context;
import android.content.Intent;

public interface LoginContract {
    interface View{
        void startActivity(Class activityClass);
        void turnOffProgressBar();
        void turnOnProgressBar();

        Context getContext();
    }

    interface Presenter{
        Intent login();

        void executeIntentLoginGG(Intent data);
    }
}
