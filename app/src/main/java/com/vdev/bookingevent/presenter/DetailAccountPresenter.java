package com.vdev.bookingevent.presenter;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetailAccountPresenter implements DetailAccountContract.Presenter{
    private DetailAccountContract.View view;
    private FirebaseUser firebaseUser;

    public DetailAccountPresenter(DetailAccountContract.View view) {
        this.view = view;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public Uri getAccountAvatar() {
        return firebaseUser.getPhotoUrl();
    }

    @Override
    public String getAccountName() {
        return firebaseUser.getDisplayName();
    }

    @Override
    public String getAccountEmail() {
        return firebaseUser.getEmail();
    }
}
