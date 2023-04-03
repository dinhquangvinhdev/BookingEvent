package com.vdev.bookingevent.presenter;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class AccountPresenter implements AccountContract.Presenter{

    private AccountContract.View view;
    private GoogleSignInOptions gsio;
    private GoogleSignInClient gsic;

    public AccountPresenter(AccountContract.View view) {
        this.view = view;
    }

    @Override
    public void logout(Context context) {
        FirebaseAuth.getInstance().signOut();
        gsio = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        gsic = GoogleSignIn.getClient(context, gsio);
        gsic.signOut();
    }
}
