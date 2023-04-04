package com.vdev.bookingevent.adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.view.fragment.DashboardDayFragment;
import com.vdev.bookingevent.view.fragment.DashboardMonthFragment;
import com.vdev.bookingevent.view.fragment.DashboardWeekFragment;

public class DashboardTypeAdapter extends FragmentStateAdapter {

    public DashboardTypeAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        switch (position) {
            case MConst.FRAGMENT_DASHBOARD_MONTH: {
                Fragment fragment = new DashboardMonthFragment();
                Bundle args = new Bundle();
                //add something to bundle
                fragment.setArguments(args);
                return fragment;
            }
            case MConst.FRAGMENT_DASHBOARD_WEEK: {
                Fragment fragment = new DashboardWeekFragment();
                Bundle args = new Bundle();
                //add something to bundle
                fragment.setArguments(args);
                return fragment;
            }
            case MConst.FRAGMENT_DASHBOARD_DAY: {
                Fragment fragment = new DashboardDayFragment();
                Bundle args = new Bundle();
                //add something to bundle
                fragment.setArguments(args);
                return fragment;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
