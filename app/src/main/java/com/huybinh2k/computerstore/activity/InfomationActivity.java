package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.Information;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfomationActivity extends AppCompatActivity {

    private EditText edit_Name,edit_FullName,edit_Email,edit_Number;
    private Spinner spinner_Provide,spinner_District,spinner_Ward;
    private ImageButton btn_Back;
    private AppCompatButton btn_Update;
    private String Id;
    private Information mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);

        initView();
    }

    private void initView()
    {
        new GetInfoDetailsAsyncTask(this,Id).execute();
        edit_Name = findViewById(R.id.edit_Name);
        edit_FullName = findViewById(R.id.edit_FullName);
        edit_Email = findViewById(R.id.edit_Email);
        edit_Number = findViewById(R.id.edit_FullName);
        spinner_Provide = findViewById(R.id.spinner_Provide);
        spinner_District = findViewById(R.id.spinner_District);
        spinner_Ward = findViewById(R.id.spinner_Ward);
        btn_Back = findViewById(R.id.btn_back);
        btn_Update = findViewById(R.id.btn_Update);

        btn_Back.setOnClickListener(v -> onBackPressed());
    }

    private void updateUI(Information info){
        if (info == null) return;
        mInfo = info;
        edit_Name.setText(info.getName());
        edit_FullName.setText(info.getFullName());
        edit_Email.setText(info.getEmail());
        edit_Number.setText(info.getNumber());
    }
    private static class GetInfoDetailsAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<InfomationActivity> weakReference;
        private boolean isSuccess;
        private String id;
        private Information information;


        public GetInfoDetailsAsyncTask(InfomationActivity activity, String id) {
            weakReference = new WeakReference<>(activity);
            this.id = id;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (isSuccess) {
                weakReference.get().updateUI(information);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            String url = "http://10.0.2.2:8000/api/auth/getone";
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET",null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    isSuccess = true;
                    try {
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONObject info = object.getJSONObject("user");
                        int id = info.getInt("id");
                        String name = info.getString("name");
                        String fullname = info.getString("fullname");
                        String email = info.getString("email");
                        String number = info.getString("phone_number");
                        information = new Information(id,name,fullname,email,number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (response.code() >= 400){
                    isSuccess = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}