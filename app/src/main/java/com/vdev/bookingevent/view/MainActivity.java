package com.vdev.bookingevent.view;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.ActivityMainBinding;
import com.vdev.bookingevent.presenter.MainContract;
import com.vdev.bookingevent.presenter.MainPresenter;
import com.vdev.bookingevent.view.fragment.AccountFragment;
import com.vdev.bookingevent.view.fragment.DashboardFragment;
import com.vdev.bookingevent.view.fragment.AddEventFragment;
import com.vdev.bookingevent.view.fragment.SearchEventFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;

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
        if (mDialog == null) {
            mDialog = new MDialog();
        }
    }

    /**
     * The function is called when the view is created. It sets up the button and text
     */
    private void initView() {

        //bottom navigation
        initBottomNavigation();
    }

    private void initBottomNavigation() {

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case MConst.FRAGMENT_DASHBOARD: {
                        replaceFlag(MConst.FRAGMENT_DASHBOARD);
                        break;
                    }
                    case MConst.FRAGMENT_ADD_EVENT: {
                        replaceFlag(MConst.FRAGMENT_ADD_EVENT);
                        break;
                    }
                    case MConst.FRAGMENT_SEARCH_EVENT: {
                        replaceFlag(MConst.FRAGMENT_SEARCH_EVENT);
                        break;
                    }
                    case MConst.FRAGMENT_ACCOUNT: {
                        replaceFlag(MConst.FRAGMENT_ACCOUNT);
                        break;
                    }
                    default: {
                        replaceFlag(MConst.FRAGMENT_DASHBOARD);
                        break;
                    }
                }
                return true;
            }
        });

        //load fragment in the first time create main activity
        replaceFlag(MConst.FRAGMENT_DASHBOARD);
    }

    private void replaceFlag(int i) {
        Fragment fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (i) {
            case MConst.FRAGMENT_DASHBOARD: {
                fragment = new DashboardFragment();
                transaction.replace(R.id.fcv_container, fragment);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_ADD_EVENT:{
                fragment = new AddEventFragment();
                transaction.replace(R.id.fcv_container, fragment);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_SEARCH_EVENT: {
                fragment = new SearchEventFragment();
                transaction.replace(R.id.fcv_container, fragment);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_ACCOUNT: {
                fragment = new AccountFragment();
                transaction.replace(R.id.fcv_container, fragment);
                transaction.commit();
                break;
            }
            default: {
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
        if (presenter == null) {
            presenter = new MainPresenter(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}