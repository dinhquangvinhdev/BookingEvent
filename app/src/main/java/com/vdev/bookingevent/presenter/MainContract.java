package com.vdev.bookingevent.presenter;

import android.net.Uri;

public interface MainContract {
    interface View{
        void startActivity(Class classActivity);
        void showUserInfor(String email, String name , Uri avatar);
    }

    interface Presenter{
        void logout();
        void checkUser();
    }
}
