package com.vdev.bookingevent.view;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.callback.CallbackFragmentManager;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.database.FirebaseController;
import com.vdev.bookingevent.databinding.ActivityMainBinding;
import com.vdev.bookingevent.presenter.MainContract;
import com.vdev.bookingevent.presenter.MainPresenter;
import com.vdev.bookingevent.view.fragment.AccountFragment;
import com.vdev.bookingevent.view.fragment.DashboardFragment;
import com.vdev.bookingevent.view.fragment.AddEventFragment;
import com.vdev.bookingevent.view.fragment.SearchEventFragment;

import java.util.Date;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements MainContract.View, CallbackFragmentManager {
    private ActivityMainBinding binding;
    private MainPresenter presenter;
    private MDialog mDialog;
    private Dialog dialogConfirmExit;
    private FirebaseController fc;
    private Bundle searchBundle;
    private Bundle addBundle;
    private SearchEventFragment searchEventFragment;
    private AddEventFragment addEventFragment;
    private AccountFragment accountFragment;
    private DashboardFragment dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebaseController();
        initDialog();
        initPresenter();
        initView();
    }

    private void initFirebaseController() {
        if(fc == null){
            fc = new FirebaseController(null, null , null,null);
            fc.getEmail();
            fc.getUser();
            fc.getRole();
            fc.getRoom();
            fc.getDepartment();
        }
    }

    private void initDialog() {
        if (mDialog == null) {
            mDialog = new MDialog();

            dialogConfirmExit = mDialog.confirmDialog(this , "Confirm Exit App" , "Are you sure you want to exit app?");
            dialogConfirmExit.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //check to get saveInstanceState
        checkSaveInstanceState();

        switch (i) {
            case MConst.FRAGMENT_DASHBOARD: {
                dashboardFragment = new DashboardFragment();
                transaction.replace(R.id.fcv_container, dashboardFragment);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_ADD_EVENT: {
                addEventFragment = new AddEventFragment(this);
                if(addBundle != null){
                    addEventFragment.setArguments(addBundle);
                    addBundle = null;
                }
                transaction.replace(R.id.fcv_container, addEventFragment);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_SEARCH_EVENT: {
                searchEventFragment = new SearchEventFragment();
                if(searchBundle != null){
                    searchEventFragment.setArguments(searchBundle);
                }
                transaction.replace(R.id.fcv_container, searchEventFragment);
                transaction.commit();
                break;
            }
            case MConst.FRAGMENT_ACCOUNT: {
                accountFragment = new AccountFragment();
                transaction.replace(R.id.fcv_container, accountFragment);
                transaction.commit();
                break;
            }
            default: {
                Log.d(TAG, "replaceFlag: wrong index fragment");
                break;
            }
        }

    }

    private void checkSaveInstanceState() {
        //check for Search Fragment
        if(getSupportFragmentManager().findFragmentById(R.id.fcv_container) instanceof  SearchEventFragment){
            if(searchEventFragment != null){
                searchBundle = new Bundle();
                searchEventFragment.onSaveInstanceState(searchBundle);
            }
        }
        // check for Add Fragment
        if(getSupportFragmentManager().findFragmentById(R.id.fcv_container) instanceof AddEventFragment){
            if(addEventFragment != null){
                addBundle = new Bundle();
                addEventFragment.onSaveInstanceState(addBundle);
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
        presenter.logout(getApplicationContext());
        if (dialogConfirmExit.isShowing()) {
            dialogConfirmExit.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fcv_container);
        if(f instanceof AddEventFragment){
            ((AddEventFragment) f).closeRVGuest();
        } else {
            dialogConfirmExit.show();
        }
    }

    @Override
    public void goToFragmentDashboard() {
        //set choice bottom to home
        binding.bottomBar.setItemActiveIndex(0);
        //change fragment to home
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fcv_container, new DashboardFragment());
        transaction.commit();
    }
}