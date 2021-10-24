package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTER = 100;
    private static final int REQUEST_LOSS_PASS = 101;
    private EditText mMail, mPass;
    private Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        TextView register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REQUEST_REGISTER);
        });
        TextView lostPass = findViewById(R.id.lost_pass);
        lostPass.setOnClickListener(view -> {
            startActivityForResult(new Intent(LoginActivity.this, AuthenticationActivity.class), REQUEST_LOSS_PASS);
        });

        mMail = findViewById(R.id.edt_mail);
        mPass = findViewById(R.id.edt_pass);
        mButtonLogin = findViewById(R.id.btn_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER){
            if (resultCode == RESULT_OK){
                if (data != null){
                    mMail.setText(data.getExtras().getString(Constant.EMAIL));
                    mPass.setText(data.getExtras().getString(Constant.PASSWORD));
                }
            }
        }else if (requestCode == REQUEST_LOSS_PASS){
            if (resultCode == RESULT_OK){
                //TODO BinhBH
            }
        }
    }
}