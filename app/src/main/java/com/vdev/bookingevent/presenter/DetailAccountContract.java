package com.vdev.bookingevent.presenter;

import android.net.Uri;

public interface DetailAccountContract {
    interface View{}
    interface Presenter{
        Uri getAccountAvatar();
        String getAccountName();
        String getAccountEmail();
    }
}
