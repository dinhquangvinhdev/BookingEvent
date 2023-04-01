package com.vdev.bookingevent.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vdev.bookingevent.databinding.ActivityLoginBinding;
import com.vdev.bookingevent.presenter.LoginContract;
import com.vdev.bookingevent.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    private ActivityLoginBinding binding;
    private LoginPresenter presenter;

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPresenter();
        initView();
    }

    private void initView() {
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = presenter.login();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
    }

    private void initPresenter() {
        if(presenter == null){
            presenter = new LoginPresenter(this, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result returned from launching the Intent from GoogleSignInApi
        if(requestCode == RC_SIGN_IN){
            presenter.executeIntentLoginGG(data);
        }
    }

    @Override
    public void startActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}