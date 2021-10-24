package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.LoadingDialog;
import com.huybinh2k.computerstore.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthenticationActivity extends AppCompatActivity {

    private boolean mIsAuthentic;
    private TextView mTitleEditText;
    private EditText mEditTextAuth;
    private Button mButtonAuth;
    private ImageView mImageAuth;
    private TextView mTitleScreen;
    private LoadingDialog mLoadingDialog;
    private String mMailAuthentic;

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
            mTitleEditText.setText(getResources().getString(R.string.email));
            mEditTextAuth.setHint(getResources().getString(R.string.enter_mail_loss_pass));
            mImageAuth.setImageDrawable(getDrawable(R.drawable.ic_mail_black_24));
            mButtonAuth.setText(getResources().getString(R.string.register));
            mTitleScreen.setText(getResources().getString(R.string.lost_pass));
        }else {
            mTitleEditText.setText(getResources().getString(R.string.code_authentic));
            mEditTextAuth.setHint(getResources().getString(R.string.enter_authentic));
            mImageAuth.setImageDrawable(getDrawable(R.drawable.ic_baseline_security_24));
            mButtonAuth.setText(getResources().getString(R.string.authentic));
            mTitleScreen.setText(getResources().getString(R.string.auth_account));
        }
    }


    private void authentic(){
        if (!mEditTextAuth.getText().toString().isEmpty() && !(mEditTextAuth.getText().toString().length() <6)){
            if (mIsAuthentic){
                AuthenticAsyncTask registerAsyncTask = new AuthenticAsyncTask(this);
                registerAsyncTask.execute();
            }else {

            }
        }
    }


    private static class AuthenticAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<AuthenticationActivity> mWeakReference;
        private boolean mIsSuccess;
        private String mOTP_hash;

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
                if (mIsSuccess){
                    Toast.makeText(mWeakReference.get().getApplicationContext(),
                            "Xác thực thành công", Toast.LENGTH_SHORT).show();
                    mWeakReference.get().setResult(RESULT_OK);
                    mWeakReference.get().finish();
                }else {
                    Toast.makeText(mWeakReference.get().getApplicationContext(),
                            "Mã xác thự không chính xác", Toast.LENGTH_SHORT).show();
                }
            }else {
                if (mIsSuccess){
                    mWeakReference.get().mIsAuthentic = true;
                    mWeakReference.get().changeView();
                }else {

                }
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url, content;
            if (mWeakReference.get().mIsAuthentic){
                url = Constant.API_VERIFY_ACCOUNT;
                content = Constant.EMAIL +"=" + mWeakReference.get().mMailAuthentic +"&"+
                        Constant.OTP + "="+mWeakReference.get().mEditTextAuth.getText().toString();
            }else {
                url = Constant.API_SEND_OTP;
                content =Constant.EMAIL +"=" + mWeakReference.get().mMailAuthentic;
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
                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}