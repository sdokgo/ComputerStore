package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


//TODO BinhBH Sửa lại sau, rối quá, đọc khó hiểu.
public class AuthenticationActivity extends AppCompatActivity {

    private static final int REQUEST_FORGET_PASS = 141;
    private boolean mIsAuthentic;
    private TextView mTitleEditText;
    private EditText mEditTextAuth;
    private Button mButtonAuth;
    private ImageView mImageAuth;
    private TextView mTitleScreen;
    private LoadingDialog mLoadingDialog;
    private String mMailAuthentic;
    private boolean mIsLossPass;
    private String mOTP_hash ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        if (getIntent().getExtras() != null){
            mIsAuthentic = getIntent().getBooleanExtra(Constant.IS_REGISTER,false);
            mMailAuthentic = getIntent().getStringExtra(Constant.EMAIL);
        }
        initView();
    }

    private void initView() {
        mTitleEditText = findViewById(R.id.txt_authentic);
        mEditTextAuth = findViewById(R.id.edt_authentic);
        mButtonAuth = findViewById(R.id.btn_authentic);
        mImageAuth = findViewById(R.id.img_authentic);
        mTitleScreen = findViewById(R.id.txt_screen);
        mLoadingDialog = new LoadingDialog(this);
        changeView();
        mButtonAuth.setOnClickListener(view -> authentic());
    }

    private void changeView() {
        if (!mIsAuthentic){
            mEditTextAuth.setInputType(InputType.TYPE_CLASS_TEXT);
            mEditTextAuth.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
            mTitleEditText.setText(getResources().getString(R.string.email));
            mEditTextAuth.setHint(getResources().getString(R.string.enter_mail_loss_pass));
            mImageAuth.setImageDrawable(getDrawable(R.drawable.ic_mail_black_24));
            mButtonAuth.setText(getResources().getString(R.string.lost_pass));
            mTitleScreen.setText(getResources().getString(R.string.lost_pass));
        }else {
            mEditTextAuth.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            mEditTextAuth.setInputType(InputType.TYPE_CLASS_NUMBER);
            mTitleEditText.setText(getResources().getString(R.string.code_authentic));
            mEditTextAuth.setHint(getResources().getString(R.string.enter_authentic));
            mImageAuth.setImageDrawable(getDrawable(R.drawable.ic_baseline_security_24));
            mButtonAuth.setText(getResources().getString(R.string.authentic));
            mTitleScreen.setText(getResources().getString(R.string.auth_account));
        }
        mEditTextAuth.getText().clear();
    }


    private void authentic(){
        if (!Utils.isConnectedInternet(this)){
            Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mEditTextAuth.getText().toString().isEmpty() && !(mEditTextAuth.getText().toString().length() <6)){
            if (mIsAuthentic){
                AuthenticAsyncTask authenticAsyncTask = new AuthenticAsyncTask(this);
                authenticAsyncTask.execute();
            }else {
                mMailAuthentic = mEditTextAuth.getText().toString();
                AuthenticAsyncTask authenticAsyncTask = new AuthenticAsyncTask(this);
                authenticAsyncTask.execute();
            }
        }
    }


    private static class AuthenticAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<AuthenticationActivity> mWeakReference;
        private boolean mIsSuccess;
        private String mess ="";


        public AuthenticAsyncTask(AuthenticationActivity activity) {
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
            if (mWeakReference.get().mIsAuthentic){
                if (mIsSuccess && !mWeakReference.get().mIsLossPass){
                    Toast.makeText(mWeakReference.get().getApplicationContext(),
                            "Xác thực thành công", Toast.LENGTH_SHORT).show();
                    mWeakReference.get().setResult(RESULT_OK);
                    mWeakReference.get().finish();
                }else if (mIsSuccess && mWeakReference.get().mIsLossPass && !mWeakReference.get().mOTP_hash.isEmpty()){
                    Intent intent = new Intent(mWeakReference.get(), ChangePasswordActivity.class);
                    intent.putExtra(Constant.IS_FORGET_PASS, true);
                    intent.putExtra(Constant.OTP_HASH, mWeakReference.get().mOTP_hash);
                    intent.putExtra(Constant.EMAIL, mWeakReference.get().mMailAuthentic);
                    mWeakReference.get().startActivityForResult(intent, REQUEST_FORGET_PASS);
                }else if (!mess.isEmpty()){
                    Toast.makeText(mWeakReference.get().getApplicationContext(),
                            mess, Toast.LENGTH_SHORT).show();
                }
            }else {
                if (mIsSuccess){
                    Toast.makeText(mWeakReference.get().getApplicationContext(),
                            "Đã gửi mã xác thực đến mail của bạn", Toast.LENGTH_SHORT).show();
                    mWeakReference.get().mIsAuthentic = true;
                    mWeakReference.get().changeView();
                }else {
                    Toast.makeText(mWeakReference.get().getApplicationContext(),
                            "Có lỗi xảy ra, xin thử lại", Toast.LENGTH_SHORT).show();

                }
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url, content;
            //BinhBH Xác thực tài khoản
            if (mWeakReference.get().mIsAuthentic && !mWeakReference.get().mIsLossPass){
                url = Constant.API_VERIFY_ACCOUNT;
                content = Constant.EMAIL +"=" + mWeakReference.get().mMailAuthentic +"&"+
                        Constant.OTP + "="+mWeakReference.get().mEditTextAuth.getText().toString();
            //BinhBH Nhập mail quên mk
            }else if (!mWeakReference.get().mIsLossPass){
                url = Constant.API_SEND_OTP;
                content =Constant.EMAIL +"=" + mWeakReference.get().mMailAuthentic;
            //BinhBH Nhập OTP quên mật khẩu
            }else {
                url = Constant.API_VERIFY_HANDLE;
                content = Constant.EMAIL +"=" + mWeakReference.get().mMailAuthentic +"&"+
                        Constant.OTP + "="+mWeakReference.get().mEditTextAuth.getText().toString();
            }

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType,content);
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    if (mWeakReference.get().mIsLossPass) {
                        try {
                            JSONObject jObj = new JSONObject(Objects.requireNonNull(response.body()).string());
                            if (jObj.toString().contains("message")){
                                mess = jObj.getString("message");
                                if (!mess.isEmpty()){
                                    mIsSuccess = false;
                                }
                            }
                            mWeakReference.get().mOTP_hash = jObj.getString("OTP");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!mWeakReference.get().mIsAuthentic){
                        mWeakReference.get().mIsLossPass = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FORGET_PASS){
            if (resultCode == RESULT_OK){
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}