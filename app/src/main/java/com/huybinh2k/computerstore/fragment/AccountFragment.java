package com.huybinh2k.computerstore.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
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
import com.huybinh2k.computerstore.activity.ChangePasswordActivity;
import com.huybinh2k.computerstore.activity.ComputerStoreActivity;
import com.huybinh2k.computerstore.activity.HelpActivity;
import com.huybinh2k.computerstore.activity.InfomationActivity;
import com.huybinh2k.computerstore.activity.LoginActivity;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class AccountFragment extends Fragment {

    private static final int REQUEST_LOGIN = 123;
    private RelativeLayout mLayoutAccountInfo,layoutBtnFunction;
    private TextView mAccountName;
    private Button mButtonLogin;
    private AppCompatButton btn_history,btn_setting,btn_changeInfo,btn_ChangePassword,btn_Help,btn_Logout;
    private boolean isShow = false;
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
        layoutBtnFunction = view.findViewById(R.id.layout_btnFunction);
        mAccountName = view.findViewById(R.id.text_name_person);
        btn_history = view.findViewById(R.id.btn_History);
        btn_setting = view.findViewById(R.id.btn_setting);
        btn_changeInfo = view.findViewById(R.id.btn_ChangeInfo);
        btn_ChangePassword = view.findViewById(R.id.btn_ChangePassword);
        btn_Help = view.findViewById(R.id.btn_Help);
        btn_Logout = view.findViewById(R.id.btn_Logout);

        if (Utils.getBooleanPreferences(getContext(), Utils.IS_LOGIN)){
            mLayoutAccountInfo.setVisibility(View.VISIBLE);
            layoutBtnFunction.setVisibility(View.VISIBLE);
            mAccountName.setText(Utils.getStringPreferences(getContext(), Constant.NAME));
            mButtonLogin.setVisibility(View.GONE);
        }else {
            mLayoutAccountInfo.setVisibility(View.GONE);
            layoutBtnFunction.setVisibility(View.GONE);
            mButtonLogin.setVisibility(View.VISIBLE);
        }

        btn_Logout.setOnClickListener(view1-> Logout());

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShow){
                    btn_changeInfo.setVisibility(View.VISIBLE);
                    btn_ChangePassword.setVisibility(View.VISIBLE);
                    isShow = true;
                }
                else {
                    btn_changeInfo.setVisibility(View.GONE);
                    btn_ChangePassword.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
        btn_ChangePassword.setOnClickListener(v ->
                startActivity(new Intent(AccountFragment.this.getActivity(), ChangePasswordActivity.class)));
        btn_changeInfo.setOnClickListener(v ->
                startActivity(new Intent(AccountFragment.this.getActivity(), InfomationActivity.class)));
        btn_Help.setOnClickListener(v ->
                startActivity(new Intent(AccountFragment.this.getActivity(), HelpActivity.class)));
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
                        layoutBtnFunction.setVisibility(View.GONE);
                        Utils.saveIntPreferences(getContext(), Utils.NUMBER_ITEMS_CART, 0);
                        if (getActivity() instanceof ComputerStoreActivity){
                            ((ComputerStoreActivity) getActivity()).updateBadgeView(0);
                        }
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
                layoutBtnFunction.setVisibility(View.VISIBLE);
                mAccountName.setText(Utils.getStringPreferences(getContext(), Constant.NAME));
                mButtonLogin.setVisibility(View.GONE);
                int badgeCount = Utils.getIntPreferences(getContext(), Utils.NUMBER_ITEMS_CART);
                if (getActivity() instanceof ComputerStoreActivity){
                    ((ComputerStoreActivity) getActivity()).updateBadgeView(badgeCount);
                }
            }
        }
    }
}