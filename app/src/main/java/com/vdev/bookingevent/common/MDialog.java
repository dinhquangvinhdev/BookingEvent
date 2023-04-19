package com.vdev.bookingevent.common;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.vdev.bookingevent.R;

public class MDialog {

    /**
     * It checks if the device is connected to the internet return true
     * else show dialog error and return false
     * @param context The context of the activity.
     * @return A boolean value.
     */
    public boolean checkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((mWifiInfo != null && mWifiInfo.isConnected())||(mMobileInfo != null && mMobileInfo.isConnected())){
            return true;
        }else {
            showErrorConnection(context);
            return false;
        }
    }


    /**
     * It return a dialog confirm.
     * This method will return Dialog for custom function with button ok
     * Default button ok do not thing for this Dialog
     * @param context The context of the activity that is calling the dialog.
     * @return A Dialog object.
     */
    public Dialog confirmLogout(Context context){
        Dialog dialogConfirm = new Dialog(context);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConfirm.setCancelable(true);
        dialogConfirm.setContentView(R.layout.dialog_confirm_logout);

        dialogConfirm.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
            }
        });

        dialogConfirm.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
            }
        });

        return dialogConfirm;
    }

    public void showErrorConnection(Context context){
        Dialog dialogError = new Dialog(context);
        dialogError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogError.setCancelable(false);
        dialogError.setContentView(R.layout.dialog_error_connection);

        dialogError.findViewById(R.id.btn_try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogError.dismiss();
            }
        });

        dialogError.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogError.dismiss();
            }
        });

        dialogError.show();
    }

    public void showFillData(Context context, String text){
        Dialog dialogNeedFillData = new Dialog(context);
        dialogNeedFillData.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNeedFillData.setCancelable(false);
        dialogNeedFillData.setContentView(R.layout.dialog_need_fill_data);

        if(text != null){
            TextView tv = dialogNeedFillData.findViewById(R.id.tv_body);
            tv.setText(text);
        }

        dialogNeedFillData.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNeedFillData.dismiss();
            }
        });

        dialogNeedFillData.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNeedFillData.dismiss();
            }
        });

        dialogNeedFillData.show();
    }

    public void showAddEventSuccess(Context context){
        Dialog dialogSuccess = new Dialog(context);
        dialogSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess.setCancelable(false);
        dialogSuccess.setContentView(R.layout.dialog_add_success);

        dialogSuccess.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSuccess.dismiss();
            }
        });

        dialogSuccess.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSuccess.dismiss();
            }
        });

        dialogSuccess.show();
    }

    public void showTimeError(Context context){
        Dialog dialogTimeError = new Dialog(context);
        dialogTimeError.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTimeError.setCancelable(false);
        dialogTimeError.setContentView(R.layout.dialog_time_error);

        dialogTimeError.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTimeError.dismiss();
            }
        });

        dialogTimeError.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTimeError.dismiss();
            }
        });

        dialogTimeError.show();
    }
}
