package com.vdev.bookingevent.presenter;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.view.LoginActivity;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private FirebaseController fc;

    public MainPresenter(MainContract.View view, Context activityContext) {
        this.view = view;
    }
}
