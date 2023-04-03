package com.vdev.bookingevent.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    public AccountFragment() {
        super(R.layout.fragment_account);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater , container , false);
        return binding.getRoot();
    }
}