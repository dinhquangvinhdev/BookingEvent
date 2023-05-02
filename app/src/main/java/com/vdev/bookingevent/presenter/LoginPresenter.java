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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.view.MainActivity;

public class LoginPresenter implements LoginContract.Presenter {
    private LoginContract.View view;
    private GoogleSignInClient gsic;
    private FirebaseAuth firebaseAuth;
    private FirebaseController fc;
    private MDialog mDialog;

    public LoginPresenter(LoginContract.View view, Context contextActivity) {
        this.view = view;

        //create dialog
        mDialog = new MDialog();

        //create Firebase Controller
        fc = new FirebaseController(null, null,null,null);

        //configure the Google SignIn
        GoogleSignInOptions gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(contextActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsic =  GoogleSignIn.getClient(contextActivity, gsio);


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //check user was logged in
        checkUser();

    }

    private void checkUser() {
        //if user is already sign in then go to main activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if(firebaseUser != null){
//            view.startActivity(MainActivity.class);
//        }
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
        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        Exception exception = accountTask.getException();
        if(accountTask.isSuccessful()){
            try{
                //google sign in success, now auth with firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }catch (Exception e){
                //failed google sign in
                mDialog.showErrorDialog(view.getContext(), "Please check your internet");
                Log.d(TAG, "executeIntentLoginGG: " + e.getMessage());
                view.turnOffProgressBar();
            }
        } else {
            mDialog.showErrorDialog(view.getContext(), "Please check your internet");
            Log.d(TAG, "executeIntentLoginGG: " + exception.getMessage());
            view.turnOffProgressBar();
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //login success
                            Log.d(TAG, "onSuccess: Logged In");
                            //get logged in user
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            //get user info
                            String uid = firebaseUser.getUid();
                            String email = firebaseUser.getEmail();
                            //check uid in firebase
                            fc.checkUserAccount(view , view.getContext(),mDialog, uid , email);
                        }
                        else{
                            mDialog.showErrorDialog(view.getContext(), "Please check your internet");
                            Log.d(TAG, "onFailure: Loggin failed " + task.getException().getMessage());
                            view.turnOffProgressBar();
                        }
                    }
                });
    }
}
