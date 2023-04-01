package com.vdev.bookingevent.presenter;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vdev.bookingevent.view.LoginActivity;

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private GoogleSignInOptions gsio;
    private GoogleSignInClient gsic;
    private FirebaseAuth firebaseAuth;

    public MainPresenter(MainContract.View view, Context activityContext) {
        this.view = view;

        gsio = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        gsic = GoogleSignIn.getClient(activityContext, gsio);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
    }


    /**
     *  This function logs the user out of the application when the user click log out
     */
    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        gsic.signOut();
        checkUser();
    }

    @Override
    public void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            //user not logged in
            view.startActivity(LoginActivity.class);
        } else{
            //user logged in
            //get user info
            String email = firebaseUser.getEmail();
            String name = firebaseUser.getDisplayName();
            Uri image = firebaseUser.getPhotoUrl();
            view.showUserInfor(email,name, image);
        }
    }
}
