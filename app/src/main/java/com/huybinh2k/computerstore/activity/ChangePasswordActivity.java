package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.LoadingDialog;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private boolean mIsForgetPass = false;
    private String mOTP_hash;
    private String mEmail;
    private LoadingDialog mLoadingDialog;

    private EditText mPass, mPassConfirm, mOldPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        if (getIntent().getExtras() != null){
            mIsForgetPass = getIntent().getBooleanExtra(Constant.IS_FORGET_PASS,false);
            mOTP_hash = getIntent().getStringExtra(Constant.OTP_HASH);
            mEmail = getIntent().getStringExtra(Constant.EMAIL);
        }
        initView();
    }

    private void initView() {
        mLoadingDialog = new LoadingDialog(this);
        TextView textOldPass = findViewById(R.id.txt_old_pass);
        FrameLayout frameOldPass = findViewById(R.id.frame_old_pass);
        if (mIsForgetPass){
            textOldPass.setVisibility(View.GONE);
            frameOldPass.setVisibility(View.GONE);
        }
        mOldPass = findViewById(R.id.edt_old_pass);
        mPass = findViewById(R.id.edt_pass);
        mPassConfirm = findViewById(R.id.edt_pass2);
        Button buttonRegister = findViewById(R.id.btn_change_pass);
        buttonRegister.setOnClickListener(view -> changePassword());
    }

    private void changePassword() {
        if (!Utils.isConnectedInternet(this)){
            Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDataInvalid()){
            if (mIsForgetPass){
                ChangeForgetPassAsyncTask changeForgetPassAsyncTask = new ChangeForgetPassAsyncTask(this);
                changeForgetPassAsyncTask.execute();
            }else {
                new ChangePassAsyncTask(this).execute();
            }
        }
    }

    private static class ChangeForgetPassAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<ChangePasswordActivity> mWeakReference;
        private boolean mIsSuccess;

        public ChangeForgetPassAsyncTask(ChangePasswordActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeakReference.get().mLoadingDialog.showDialog();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mWeakReference.get().mLoadingDialog.dismissDialog();
            if (mIsSuccess){
                Toast.makeText(mWeakReference.get().getApplicationContext(),
                        "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                mWeakReference.get().setResult(RESULT_OK);
                mWeakReference.get().finish();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String data = "OTP_hash=" +
                    mWeakReference.get().mOTP_hash +
                    "&email=" + mWeakReference.get().mEmail +
                    "&password=" + mWeakReference.get().mPass.getText().toString() +
                    "&password_confirmation=" +mWeakReference.get().mPassConfirm.getText().toString();
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(Constant.API_FORGET_PASS)
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /** BinhBH
     * @return Dữ liệu không hợp lệ?
     */
    private boolean isDataInvalid(){
        if (mOldPass.getText().toString().isEmpty() && !mIsForgetPass){
            Toast.makeText(getApplicationContext(), getString(R.string.must_enter_pass), Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (mPass.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.must_enter_pass), Toast.LENGTH_SHORT).show();
            return true;
        }else if (mPassConfirm.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.must_enter_pass2), Toast.LENGTH_SHORT).show();
            return true;
        }else if (!mPass.getText().toString().equals((mPassConfirm.getText().toString()))){
            Toast.makeText(getApplicationContext(), getString(R.string.repass_not_equals_pass), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private static class ChangePassAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<ChangePasswordActivity> mWeakReference;
        private boolean mIsSuccess;
        private String mess;
        private String token;

        public ChangePassAsyncTask(ChangePasswordActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            token = Utils.getStringPreferences(mWeakReference.get(), Constant.TOKEN_LOGIN);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeakReference.get().mLoadingDialog.showDialog();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mWeakReference.get().mLoadingDialog.dismissDialog();
            if (mIsSuccess){
                Toast.makeText(mWeakReference.get().getApplicationContext(),
                        "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                mWeakReference.get().setResult(RESULT_OK);
                mWeakReference.get().finish();
            }else {
                Toast.makeText(mWeakReference.get().getApplicationContext(),
                        mess, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String data = "password_old=" +
                    mWeakReference.get().mOldPass.getText().toString() +
                    "&password_new=" + mWeakReference.get().mPass.getText().toString() +
                    "&password_new_confirmation=" +mWeakReference.get().mPassConfirm.getText().toString();
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/auth/changePassword")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization","Bearer "+ token)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }
                try {
                    String responseString = response.body().string();
                    JSONObject jObj = new JSONObject(responseString);
                    if (jObj.toString().contains("message")) {
                        mess = jObj.getString("message");
                        if (!mess.isEmpty()) {
                            mIsSuccess = false;
                        }
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}