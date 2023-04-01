package com.vdev.bookingevent.presenter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.view.MainActivity;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private GoogleSignInClient gsic;
    private FirebaseAuth firebaseAuth;

    public LoginPresenter(LoginContract.View view, Context contextActivity) {
        this.view = view;

        //configure the Google SignIn
        GoogleSignInOptions gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(contextActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsic =  GoogleSignIn.getClient(contextActivity, gsio);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

    }

    /**
     * This function is called when the user clicks the login button
     */
    @Override
    public Intent login() {
        //begin login
        Log.d(TAG, "login: begin Google SignIn");
        Intent intent = gsic.getSignInIntent();
        return intent;
    }

    @Override
    public void executeIntentLoginGG(Intent data) {
        Log.d(TAG, "executeIntentLoginGG: Google Signin intent result");
        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try{
            //google sign in success, now auth with firebase
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            firebaseAuthWithGoogleAccount(account);
        }catch (Exception e){
            //failed google sign in
            Log.d(TAG, "executeIntentLoginGG: " + e.getMessage());
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG, "onSuccess: Logged In");
                        //get logged in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        Log.d(TAG, "onSuccess: Email: " + email);
                        Log.d(TAG, "onSuccess: UID" + uid);

                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            //user is new - Account Created
                            Log.d(TAG, "onSuccess: Account Created...\n" + email);
                        } else {
                            Log.d(TAG, "onSuccess: Existing user...\n" + email);
                        }

                        //start main activity
                        view.startActivity(MainActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Loggin failed " + e.getMessage());
                    }
                });
    }
}
