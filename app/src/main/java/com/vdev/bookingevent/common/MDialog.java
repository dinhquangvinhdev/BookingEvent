package com.vdev.bookingevent.common;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;

import com.vdev.bookingevent.R;

public class MDialog {
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
}
