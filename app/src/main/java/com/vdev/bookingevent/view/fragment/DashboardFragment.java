package com.vdev.bookingevent.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.DashboardTypeAdapter;
import com.vdev.bookingevent.adapter.RoomFilterAdapter;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.databinding.DialogFilterBinding;
import com.vdev.bookingevent.databinding.FragmentDashboardBinding;
import com.vdev.bookingevent.model.Event;
import com.vdev.bookingevent.model.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;

    private DashboardTypeAdapter dashboardTypeAdapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RoomFilterAdapter adapterFilter;

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
        dashboardTypeAdapter = new DashboardTypeAdapter(this);
        binding.viewPager.setAdapter(dashboardTypeAdapter);
        binding.viewPager.setUserInputEnabled(false);
        //init tab layout
        new TabLayoutMediator(binding.tabLayout , binding.viewPager , ((tab, position) ->{
            tab.setText(MConst.titleTabDashboard.get(position));
        }
        )).attach();
        //init dialog
        initDialog();
        //init other view
        initOtherView();
    }

    private void initOtherView() {
        binding.imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null){dialog.show();}
            }
        });
    }

    private void initDialog() {
        //check filter
        if(MData.filterChoicedRoom != null && (MData.filterChoicedRoom.contains(true) || MData.filterUser)){
            binding.imgFilter.setImageResource(R.drawable.ic_filter_on);
        } else {
            binding.imgFilter.setImageResource(R.drawable.ic_filter_off);
        }
        // create dialog builder
        dialogBuilder = new AlertDialog.Builder(getContext());
        DialogFilterBinding bindingPopupView = DialogFilterBinding.inflate(getLayoutInflater());
//        if(MData.filterUser){   // check is filter event of user
//            bindingPopupView.cbUserFilter.setChecked(MData.filterUser);
//        }
        adapterFilter = new RoomFilterAdapter(MData.filterChoicedRoom);
        bindingPopupView.rvRooms.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL , false));
        bindingPopupView.rvRooms.setAdapter(adapterFilter);
        bindingPopupView.tvTitleFilterRoom.setText("Filter with room");
        bindingPopupView.btnSave.setOnClickListener(view -> {
            // save information in the filter
            //user
            MData.filterUser = bindingPopupView.cbUserFilter.isChecked();
            //room
            MData.filterChoicedRoom = new ArrayList<>(adapterFilter.getChoiced());
            if(MData.filterChoicedRoom.contains(true) || MData.filterUser){
                binding.imgFilter.setImageResource(R.drawable.ic_filter_on);
            } else {
                binding.imgFilter.setImageResource(R.drawable.ic_filter_off);
            }
            dashboardTypeAdapter.updateDataDisplayInMonth();
            dialog.dismiss();
        });
        bindingPopupView.btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        bindingPopupView.btnClearAllFilter.setOnClickListener(view -> {
            //clear room choices
            adapterFilter.clearAllChoiced();
            //clear filter mine choice
            MData.filterUser = false;
            bindingPopupView.cbUserFilter.setChecked(false);
        });

        dialogBuilder.setView(bindingPopupView.getRoot());
        dialogBuilder.setOnDismissListener(dialogInterface -> {
            //user
            bindingPopupView.cbUserFilter.setChecked(MData.filterUser);
            //room
            adapterFilter.setChoiced(MData.filterChoicedRoom);
        });
        dialog = dialogBuilder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}