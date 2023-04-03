package com.vdev.bookingevent.view;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.ActivityMainBinding;
import com.vdev.bookingevent.presenter.MainContract;
import com.vdev.bookingevent.presenter.MainPresenter;
import com.vdev.bookingevent.view.fragment.AccountFragment;
import com.vdev.bookingevent.view.fragment.DashboardFragment;
import com.vdev.bookingevent.view.fragment.SearchEventFragment;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    private ActivityMainBinding binding;
    private MainPresenter presenter;
    private MDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDialog();
        initPresenter();
        initView();
    }

    private void initDialog() {
        if(mDialog == null){
            mDialog = new MDialog();
        }
    }

    /**
     * The function is called when the view is created. It sets up the button and text
     */
    private void initView() {
        presenter.checkUser();

        //bottom navigation
        initBottomNavigation();

        //button
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog.checkConnection(getApplicationContext())){
                    presenter.logout();
                }
            }
        });
    }

    private void initBottomNavigation() {

        binding.bnvMainView.setOnItemSelectedListener(item ->
        {
            switch (item.getItemId()){
                case R.id.bnv_dashboard:{
                    replaceFlag(MConst.FRAGMENT_DASHBOARD);
                    break;
                }
                case R.id.bnv_search:{
                    replaceFlag(MConst.FRAGMENT_SEARCH_EVENT);
                    break;
                }
                case R.id.bnv_account: {
                    replaceFlag(MConst.FRAGMENT_ACCOUNT);
                    break;
                }
                default: {
                    replaceFlag(MConst.FRAGMENT_DASHBOARD);
                    break;
                }
            }
            return true;
        });

        //load fragment in the first time create main activity
        replaceFlag(MConst.FRAGMENT_DASHBOARD);
    }

    private void replaceFlag(int i) {
        Fragment fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (i){
            case MConst.FRAGMENT_DASHBOARD:{
                fragment = new DashboardFragment();
                transaction.replace(R.id.fcv_container , fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_SEARCH_EVENT:{
                fragment = new SearchEventFragment();
                transaction.replace(R.id.fcv_container , fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_ACCOUNT:{
                fragment = new AccountFragment();
                transaction.replace(R.id.fcv_container , fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            }
            default:{
                Log.d(TAG, "replaceFlag: wrong index fragment");
                break;
            }
        }

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