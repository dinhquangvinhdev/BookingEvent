package com.vdev.bookingevent.adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vdev.bookingevent.callback.CallbackUpdateEventDisplay;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.view.fragment.DashboardDayFragment;
import com.vdev.bookingevent.view.fragment.DashboardMonthFragment;
import com.vdev.bookingevent.view.fragment.DashboardWeekFragment;

public class DashboardTypeAdapter extends FragmentStateAdapter {

    private DashboardMonthFragment fragmentMonth = new DashboardMonthFragment();
    private DashboardWeekFragment fragmentWeek = new DashboardWeekFragment();
    private DashboardDayFragment fragmentDay = new DashboardDayFragment();

    public DashboardTypeAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        switch (position) {
            case MConst.FRAGMENT_DASHBOARD_MONTH: {
                Bundle args = new Bundle();
                //add something to bundle
                fragmentMonth.setArguments(args);
                return fragmentMonth;
            }
//            case MConst.FRAGMENT_DASHBOARD_WEEK: {
//                Bundle args = new Bundle();
//                //add something to bundle
//                fragmentWeek.setArguments(args);
//                return fragmentWeek;
//            }
//            case MConst.FRAGMENT_DASHBOARD_DAY: {
//                Bundle args = new Bundle();
//                //add something to bundle
//                fragmentDay.setArguments(args);
//                return fragmentDay;
//            }
            default: {
                return null;
            }
        }
    }

    public void updateDataDisplayInMonth(){
        fragmentMonth.updateDisplayData(null);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
