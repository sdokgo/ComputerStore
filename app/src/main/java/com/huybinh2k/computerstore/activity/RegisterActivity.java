package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.LoadingDialog;
import com.huybinh2k.computerstore.R;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_AUTHENTICATION = 123;
    private EditText mMail, mPass, mPassConfirm;
    private LoadingDialog mLoadingDialog;
    private String mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mMail = findViewById(R.id.edt_mail);
        mPass = findViewById(R.id.edt_pass);
        Button buttonRegister = findViewById(R.id.btn_register);
        mPassConfirm = findViewById(R.id.edt_pass2);

        mLoadingDialog = new LoadingDialog(this);
        buttonRegister.setOnClickListener(view -> registerAccount());

    }

    /**
     * BinhBH Đăng kí tài khoản nếu dữ liệu hợp lệ
     */
    private void registerAccount() {
        if (!isDataInvalid()){
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(Constant.NAME, "binhbh");
            map.put(Constant.EMAIL, mMail.getText().toString());
            map.put(Constant.PASSWORD, mPass.getText().toString());
            map.put(Constant.PASS_CONFIRM, mPassConfirm.getText().toString());
            mData = getDataString(map);
            RegisterAsyncTask myAsyncTask = new RegisterAsyncTask(this);
            myAsyncTask.execute();
        }
    }

    private static class RegisterAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<RegisterActivity> mWeakReference;
        private boolean mIsSuccess;

        public RegisterAsyncTask(RegisterActivity activity) {
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
                mWeakReference.get().gotoAuthenticationActivity();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, mWeakReference.get().mData);
            Request request = new Request.Builder()
                    .url(Constant.API_REGISTER)
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
        if (!mMail.getText().toString().matches(Constant.EMAIL_PATTERN)){
            Toast.makeText(getApplicationContext(), getString(R.string.mail_invalid), Toast.LENGTH_SHORT).show();
            return true;
        }else if (mPass.getText().toString().isEmpty()){
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

    /** BinhBH
     * @param params
     * @return chuyển về string liên kết key và value
     */
    private static String getDataString(LinkedHashMap<String, String> params){
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }


    private void gotoAuthenticationActivity(){
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.putExtra(Constant.IS_REGISTER, true);
        intent.putExtra(Constant.EMAIL, mMail.getText().toString());
        startActivityForResult(intent, REQUEST_AUTHENTICATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTHENTICATION){
            if (resultCode == RESULT_OK){
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constant.EMAIL, mMail.getText().toString());
                resultIntent.putExtra(Constant.PASSWORD, mPass.getText().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}