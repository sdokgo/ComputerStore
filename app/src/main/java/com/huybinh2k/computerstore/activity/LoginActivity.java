package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.LoadingDialog;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_REGISTER = 100;
    private static final int REQUEST_LOSS_PASS = 101;
    private EditText mMail, mPass;
    private LoadingDialog mLoadingDialog;
    private Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mLoadingDialog = new LoadingDialog(this);
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
        mButtonLogin.setOnClickListener(view -> login());
    }

    private void login() {
        if (!Utils.isConnectedInternet(this)){
            Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDataInvalid()){
            new LoginAsyncTask(this).execute();
        }
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
        }
        return false;
    }


    private Response response = null;
    private static class LoginAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<LoginActivity> mWeakReference;
        private boolean mIsSuccess;
        private String mess;


        public LoginAsyncTask(LoginActivity activity) {
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
                        "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                new GetCountCartAsyncTask(mWeakReference.get(), true).execute();
                Utils.hideKeyboard(mWeakReference.get().mButtonLogin, mWeakReference.get());

            }else {
                Toast.makeText(mWeakReference.get().getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
                if(mess.equalsIgnoreCase("Tài khoản chưa được kích hoạt")){
                    new SendOTPAsyncTask(mWeakReference.get().mMail.getText().toString()).execute();
                    Intent intent = new Intent(mWeakReference.get(), AuthenticationActivity.class);
                    intent.putExtra(Constant.IS_REGISTER, true);
                    intent.putExtra(Constant.EMAIL, mWeakReference.get().mMail.getText().toString());
                    mWeakReference.get().startActivity(intent);
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String data = Constant.EMAIL + "="+ mWeakReference.get().mMail.getText().toString()
                    +"&" + Constant.PASSWORD +"=" + mWeakReference.get().mPass.getText().toString();
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(Constant.API_LOGIN)
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    if (response.body() != null){
                        mWeakReference.get().response = response;
                    }
                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }

                try {
                    String responseString = response.body().string();
                    JSONObject jObj = new JSONObject(responseString);
                    if (jObj.toString().contains("message")){
                        mess = jObj.getString("message");
                        if (!mess.isEmpty()){
                            mIsSuccess = false;
                        }
                    }
                    if (mIsSuccess){
                        Utils.saveBooleanPreferences(mWeakReference.get(), Utils.IS_LOGIN, true);
                        String token = jObj.getString(Constant.TOKEN_LOGIN);
                        JSONObject objectUser = jObj.getJSONObject(Constant.USER);
                        String mail = objectUser.getString("email");
                        String fullName = objectUser.getString("fullname");
                        Utils.saveStringPreferences(mWeakReference.get(), Constant.EMAIL, mail);
                        Utils.saveStringPreferences(mWeakReference.get(), Constant.NAME, fullName);
                        Utils.saveStringPreferences(mWeakReference.get(), Constant.TOKEN_LOGIN, token);
                    }
                }catch (JSONException jsonException){
                    jsonException.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class GetCountCartAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Activity> mWeakReference;
        private boolean mIsSuccess;
        private String token;
        private boolean isLogin;
        int count = 0;

        public GetCountCartAsyncTask(Activity activity, boolean isLogin) {
            mWeakReference = new WeakReference<>(activity);
            this.isLogin = isLogin;
            token = Utils.getStringPreferences(mWeakReference.get(), Constant.TOKEN_LOGIN);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                Utils.saveIntPreferences(mWeakReference.get(), Utils.NUMBER_ITEMS_CART, count);
                if (isLogin){
                    mWeakReference.get().setResult(Activity.RESULT_OK);
                    mWeakReference.get().finish();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/cart/get_cart")
                    .method("GET", null)
                    .addHeader("Authorization","Bearer "+ token)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        String responseString = response.body().string();
                        JSONObject object = new JSONObject(responseString);
                        JSONArray jsonArray = object.getJSONArray("ltItem");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            count++;
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private static class SendOTPAsyncTask extends AsyncTask<Void, Void, Void>{

        private String eMail;

        public SendOTPAsyncTask(String mail) {
            this.eMail = mail;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String mail = "email=" + eMail;
            RequestBody body = RequestBody.create(mediaType, mail);
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/auth/send_OTP")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}