package com.vdev.bookingevent.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.DashboardTypeAdapter;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater , container , false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //init view pager
        binding.viewPager.setAdapter(new DashboardTypeAdapter(this));
        //init tab layout
        new TabLayoutMediator(binding.tabLayout , binding.viewPager , ((tab, position) ->{
            tab.setText(MConst.titleTabDashboard.get(position));
        }
        )).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}