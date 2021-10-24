package com.huybinh2k.computerstore;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by BinhBH on 10/23/2021.
 */
public class LoadingDialog {

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private AlertDialog mAlertDialog;

    public LoadingDialog(Activity mActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);
        mAlertDialog = builder.create();
    }

    public void showDialog(){
        mAlertDialog.show();
    }

    public void dismissDialog(){
        mAlertDialog.dismiss();
    }



}
