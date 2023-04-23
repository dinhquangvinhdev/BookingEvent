package com.vdev.bookingevent.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.adapter.OnItemOpAccClickListener;
import com.vdev.bookingevent.adapter.OptionAccountAdapter;
import com.vdev.bookingevent.common.MConst;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.FragmentAccountBinding;
import com.vdev.bookingevent.presenter.AccountContract;
import com.vdev.bookingevent.presenter.AccountPresenter;
import com.vdev.bookingevent.view.DetailAccountActivity;

public class AccountFragment extends Fragment implements AccountContract.View , OnItemOpAccClickListener {

    private FragmentAccountBinding binding;
    private AccountPresenter presenter;
    private Dialog dialogLogout;
    private MDialog mDialog;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPresenter();
        initDialog();
        initView();
    }

    private void initDialog() {
        mDialog = new MDialog();
        dialogLogout = mDialog.confirmLogout(getContext());

        dialogLogout.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog.checkConnection(getContext())){
                    presenter.logout(getContext());
                    getActivity().finish();
                } else {
                    dialogLogout.dismiss();
                }
            }
        });
    }

    private void initView() {
        //create for recycleView
        OptionAccountAdapter adapter = new OptionAccountAdapter(this);
        binding.rvOptions.setAdapter(adapter);
        binding.rvOptions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL , false));
    }

    private void initPresenter() {
        if(presenter == null){
            presenter = new AccountPresenter(this);
        }
    }

    @Override
    public void OnItemCLickListener(String title) {
        if(title.compareTo("Logout") == 0){
            dialogLogout.show();
        } else if(title.compareTo("Detail Account") == 0){
            mDialog.checkConnection(getContext());
            startActivity(new Intent(getContext() , DetailAccountActivity.class));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dialogLogout.dismiss();
        binding = null;
    }
}