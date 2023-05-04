package com.vdev.bookingevent.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.vdev.bookingevent.R;
import com.vdev.bookingevent.common.MData;
import com.vdev.bookingevent.common.MDialog;
import com.vdev.bookingevent.databinding.ActivityDetailAccountBinding;
import com.vdev.bookingevent.presenter.DetailAccountContract;
import com.vdev.bookingevent.presenter.DetailAccountPresenter;

public class DetailAccountActivity extends AppCompatActivity implements DetailAccountContract.View {

    private ActivityDetailAccountBinding binding;
    private DetailAccountPresenter presenter;
    private MDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initPresenter();
        initDialog();
        initView();
    }

    private void initView() {
        //show information user
        if(mDialog.checkConnection(this)){
            //load avatar
            Picasso.get()
                    .load(MData.userLogin.getAvatar())
                    .placeholder(R.drawable.animation_loading)
                    .fit()
                    .into(binding.imgAvatar);
            //load display name
            binding.edtUsername.setText(presenter.getAccountName());
            //load display email
            binding.edtEmail.setText(presenter.getAccountEmail());
        }
        // for back arrow
        binding.imgIconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initDialog() {
        mDialog = new MDialog();
    }

    private void initPresenter() {
        if(presenter == null){
            presenter = new DetailAccountPresenter(this);
        }
    }
}