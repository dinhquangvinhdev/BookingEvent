package com.vdev.bookingevent.presenter;

import android.content.Intent;
import android.widget.Toast;

public interface LoginContract {
    interface View{
        void startActivity(Class activityClass);
    }

    interface Presenter{
        Intent login();

        void executeIntentLoginGG(Intent data);
    }
}
