package com.vdev.bookingevent.common;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.vdev.bookingevent.R;
import com.vdev.bookingevent.model.Event;

import java.util.List;

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
     * This method will return Dialog for custom function with button yes and button no
     * Default button yes do not thing for this Dialog
     * @param context The context of the activity that is calling the dialog.
     * @return A Dialog object.
     */
    public Dialog confirmDialog(Context context, String title , String body){
        Dialog dialogConfirm = new Dialog(context);
        dialogConfirm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogConfirm.setCancelable(true);
        dialogConfirm.setContentView(R.layout.dialog_confirm);

        //set text
        TextView tvTitle = dialogConfirm.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        TextView tvBody = dialogConfirm.findViewById(R.id.tv_body);
        tvBody.setText(body);

        dialogConfirm.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
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

    public void showDialogSuccess(Context context, String title , String body){
        Dialog dialogSuccess = new Dialog(context);
        dialogSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess.setCancelable(false);
        dialogSuccess.setContentView(R.layout.dialog_success);

        TextView tvTitle = dialogSuccess.findViewById(R.id.tv_title_title);
        TextView tvBody = dialogSuccess.findViewById(R.id.tv_body);
        tvTitle.setText(title);
        tvBody.setText(body);

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

    public Dialog dialogDeleteSuccess(Context context , Event event) {
        Dialog dialogSuccess = new Dialog(context);
        dialogSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess.setCancelable(false);
        dialogSuccess.setContentView(R.layout.dialog_success);

        TextView tvTitle = dialogSuccess.findViewById(R.id.tv_title_title);
        TextView tvBody = dialogSuccess.findViewById(R.id.tv_body);
        tvTitle.setText("Delete Success");
        tvBody.setText(event.getTitle() + " is deleted");

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

        return dialogSuccess;
    }

    public Dialog showEventsDuplicate(Context context , List<Event> eventsDuplicate){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_confirm_events_duplicate);

        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //TODO show all events and access to edit or delete
        return dialog;
    }
}
