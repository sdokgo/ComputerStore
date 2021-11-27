package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.LoadingDialog;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.model.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    private EditText mMail, mPass, mPassConfirm, mName, mFullName, mSDT, mHouse;
    private LoadingDialog mLoadingDialog;
    private String mData;
    private Spinner mSpinProvince, mSpinDistrict, mSpinWard;
    private int mIdProvince = -1, mIdDistrict = -1, mIdWard =-1;
    private List<Region> mListProvince = new ArrayList<>();
    private List<Region> mListDistrict = new ArrayList<>();
    private List<Region> mListWard = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mMail = findViewById(R.id.edt_mail);
        mPass = findViewById(R.id.edt_pass);
        mName = findViewById(R.id.edt_name);
        mFullName = findViewById(R.id.edt_full_name);
        mSDT = findViewById(R.id.edt_phone);
        mHouse = findViewById(R.id.edt_house);

        Button buttonRegister = findViewById(R.id.btn_register);
        mPassConfirm = findViewById(R.id.edt_pass2);
        mSpinProvince = findViewById(R.id.spinner_Provide);
        mSpinDistrict = findViewById(R.id.spinner_District);
        mSpinWard = findViewById(R.id.spinner_Ward);
        new GetRegionAsyncTask(this, 0,1).execute();

        mSpinProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idParent = mListProvince.get(position).getId();
                new GetRegionAsyncTask(RegisterActivity.this, idParent,2).execute();
                mIdProvince = idParent;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idParent = mListDistrict.get(position).getId();
                new GetRegionAsyncTask(RegisterActivity.this, idParent,3).execute();
                mIdDistrict = idParent;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpinWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIdWard = mListWard.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        mLoadingDialog = new LoadingDialog(this);
        buttonRegister.setOnClickListener(view -> registerAccount());

    }

    /**
     * BinhBH Đăng kí tài khoản nếu dữ liệu hợp lệ
     */
    private void registerAccount() {
        if (!Utils.isConnectedInternet(this)){
            Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDataInvalid()){
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(Constant.EMAIL, mMail.getText().toString());
            map.put(Constant.PASSWORD, mPass.getText().toString());
            map.put(Constant.PASS_CONFIRM, mPassConfirm.getText().toString());
            map.put(Constant.NAME, mName.getText().toString());
            map.put(Constant.FULL_NAME, mFullName.getText().toString());
            map.put("phone_number", mSDT.getText().toString());
            map.put("address", mSDT.getText().toString());
            map.put("province_id", String.valueOf(mIdProvince));
            map.put("district_id", String.valueOf(mIdDistrict));
            map.put("ward_id", String.valueOf(mIdWard));

            mData = getDataString(map);
            RegisterAsyncTask myAsyncTask = new RegisterAsyncTask(this);
            myAsyncTask.execute();
        }
    }

    private static class RegisterAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<RegisterActivity> mWeakReference;
        private boolean mIsSuccess;
        private String mess;

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
            }else {
                Toast.makeText(mWeakReference.get(), mess, Toast.LENGTH_SHORT).show();
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
                String responseString = response.body().string();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                    try {
                        JSONObject object = new JSONObject(responseString);
                        JSONObject errors = object.getJSONObject("errors");
                        mess = errors.getString("email");
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

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
        if (mName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.ten_tk), Toast.LENGTH_SHORT).show();
            mName.requestFocus();
            return true;
        }else if(mFullName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.ho_ten), Toast.LENGTH_SHORT).show();
            mFullName.requestFocus();
            return true;
        }
        else if(!mSDT.getText().toString().matches(Constant.PHONE_PATTERN)){
            Toast.makeText(getApplicationContext(), getString(R.string.must_sdt), Toast.LENGTH_SHORT).show();
            mSDT.requestFocus();
            return true;
        }
        else if (!mMail.getText().toString().matches(Constant.EMAIL_PATTERN)){
            Toast.makeText(getApplicationContext(), getString(R.string.mail_invalid), Toast.LENGTH_SHORT).show();
            mMail.requestFocus();
            return true;
        }else if (mPass.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.must_enter_pass), Toast.LENGTH_SHORT).show();
            mPass.requestFocus();
            return true;
        }else if (mPassConfirm.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.must_enter_pass2), Toast.LENGTH_SHORT).show();
            mPassConfirm.requestFocus();
            return true;
        }else if (!mPass.getText().toString().equals((mPassConfirm.getText().toString()))){
            Toast.makeText(getApplicationContext(), getString(R.string.repass_not_equals_pass), Toast.LENGTH_SHORT).show();
            mPass.requestFocus();
            return true;
        }
        else if(mHouse.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Bạn phải nhập địa chỉ nhà", Toast.LENGTH_SHORT).show();
            mHouse.requestFocus();
            return true;
        }
        else if(mIdProvince == -1){
            Toast.makeText(getApplicationContext(), "Bạn phải chọn Tỉnh/Thành phố", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(mIdDistrict == -1){
            Toast.makeText(getApplicationContext(), "Bạn phải chọn Quận/Huyện", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(mIdWard == -1){
            Toast.makeText(getApplicationContext(), "Bạn phải chọn Xã/Phường/Thị trấn", Toast.LENGTH_SHORT).show();
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

    private static class GetRegionAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<RegisterActivity> mWeakReference;
        private boolean mIsSuccess;
        private int parentId;
        private int level;
        private List<Region> list = new ArrayList<>();

        public GetRegionAsyncTask(RegisterActivity activity, int parentId, int level) {
            mWeakReference = new WeakReference<>(activity);
            this.parentId = parentId;
            this.level = level;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (mIsSuccess){
                ArrayAdapter<Region> adapter = new ArrayAdapter<Region>(mWeakReference.get(), R.layout.item_region, list);
                switch (level){
                    case 1:
                        mWeakReference.get().mSpinProvince.setAdapter(adapter);
                        mWeakReference.get().mListProvince = list;
                        break;
                    case 2:
                        mWeakReference.get().mSpinDistrict.setAdapter(adapter);
                        mWeakReference.get().mListDistrict = list;
                        break;
                    case 3:
                        mWeakReference.get().mSpinWard.setAdapter(adapter);
                        mWeakReference.get().mListWard = list;
                        break;
                }

            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url = "http://10.0.2.2:8000/api/region/get_list?parentID=" + parentId;
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("Accept", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300) {
                    mIsSuccess = true;
                    String responseString = response.body().string();
                    try {
                        list.add(new Region(-1,-1,""));
                        JSONObject object = new JSONObject(responseString);
                        JSONArray jsonArray = object.getJSONArray("List");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt(Constant.ID);
                            int parentId = jsonObject.getInt(Constant.PARENT_ID);
                            String name  = jsonObject.getString("region_name");
                            Region region = new Region(id, parentId, name);
                            list.add(region);
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}