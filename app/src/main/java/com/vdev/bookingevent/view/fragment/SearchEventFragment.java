package com.vdev.bookingevent.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.databinding.FragmentSearchEventBinding;

public class SearchEventFragment extends Fragment {
    private FragmentSearchEventBinding binding;


    public SearchEventFragment() {
        super(R.layout.fragment_search_event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchEventBinding.inflate(inflater , container , false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}