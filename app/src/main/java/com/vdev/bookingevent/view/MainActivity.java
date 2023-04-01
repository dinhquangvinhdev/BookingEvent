package com.vdev.bookingevent.view;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.databinding.ActivityMainBinding;
import com.vdev.bookingevent.presenter.MainContract;
import com.vdev.bookingevent.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private ActivityMainBinding binding;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPresenter();
        initView();
    }

    /**
     * The function is called when the view is created. It sets up the button and text
     */
    private void initView() {
        presenter.checkUser();

        //button
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.logout();
            }
        });
    }

    /**
     * If the presenter is null, create a new presenter and pass in the view
     * else do not need to create other
     */
    private void initPresenter() {
        if (presenter == null){
            presenter = new MainPresenter(this, this);
        }
    }

    /**
     * It starts a new activity and finishes the current activity.
     *
     * @param classActivity The class of the activity you want to start.
     */
    @Override
    public void startActivity(Class classActivity) {
        Intent intent = new Intent(this, classActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public void showUserInfor(String email, String name, Uri avatar) {
        //show email and name
        String result = email + "\n" + name;
        binding.tvTemp.setText(result);
        Log.d(TAG, "showUserInfor: " + avatar);
        //show avatar
        Picasso.get()
                .load(avatar)
                .placeholder(R.drawable.animation_loading)
                .fit()
                .into(binding.imgAvatar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.logout();
    }
}