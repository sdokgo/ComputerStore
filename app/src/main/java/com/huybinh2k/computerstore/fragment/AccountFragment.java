package com.huybinh2k.computerstore.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.activity.LoginActivity;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class AccountFragment extends Fragment {

    private static final int REQUEST_LOGIN = 123;
    private RelativeLayout mLayoutAccountInfo;
    private TextView mAccountName;
    private Button mButtonLogin;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonLogin = view.findViewById(R.id.btn_go_to_login_activity);
        mButtonLogin.setOnClickListener(view1 -> {
            startActivityForResult(new Intent(getContext(), LoginActivity.class), REQUEST_LOGIN);
        });
        mLayoutAccountInfo = view.findViewById(R.id.layout_info);
        mAccountName = view.findViewById(R.id.text_name_person);

        if (Utils.getBooleanPreferences(getContext(), Utils.IS_LOGIN)){
            mLayoutAccountInfo.setVisibility(View.VISIBLE);
            mAccountName.setText(Utils.getStringPreferences(getContext(), Constant.NAME));
            mButtonLogin.setVisibility(View.GONE);
        }else {
            mLayoutAccountInfo.setVisibility(View.GONE);
            mButtonLogin.setVisibility(View.VISIBLE);
        }

        TextView buttonLogout = view.findViewById(R.id.txt_logout);
        buttonLogout.setOnClickListener(view1 -> Logout());
    }

    private void Logout() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Utils.removePreferences(getContext(), Utils.IS_LOGIN);
                        Utils.removePreferences(getContext(), Constant.NAME);
                        Utils.removePreferences(getContext(), Constant.EMAIL);
                        mButtonLogin.setVisibility(View.VISIBLE);
                        mLayoutAccountInfo.setVisibility(View.GONE);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Bạn có muốn đăng xuất không?").setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN){
            if (resultCode == Activity.RESULT_OK){
                mLayoutAccountInfo.setVisibility(View.VISIBLE);
                mAccountName.setText(Utils.getStringPreferences(getContext(), Constant.NAME));
                mButtonLogin.setVisibility(View.GONE);
            }
        }

    }
}