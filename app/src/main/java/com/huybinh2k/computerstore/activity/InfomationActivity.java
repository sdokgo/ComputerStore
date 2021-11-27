package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.LoadingDialog;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.model.Information;
import com.huybinh2k.computerstore.model.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfomationActivity extends AppCompatActivity {

    private EditText edit_Name,edit_FullName,edit_Number;
    private Spinner mSpinProvince, mSpinDistrict, mSpinWard;
    private ImageView btn_Back;
    private AppCompatButton btn_Update;
    private String Id;
    private Information mInfo;
    private List<Region> mListProvince = new ArrayList<>();
    private List<Region> mListDistrict = new ArrayList<>();
    private List<Region> mListWard = new ArrayList<>();
    private int mIdProvince = -1, mIdDistrict = -1, mIdWard =-1;
    private EditText mEdtHouse;
    private String mData;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        initView();
    }

    private void initView()
    {
        mLoadingDialog = new LoadingDialog(this);
        edit_Name = findViewById(R.id.edit_Name);
        edit_FullName = findViewById(R.id.edit_FullName);
        edit_Number = findViewById(R.id.edit_Number);
        mSpinProvince = findViewById(R.id.spinner_Provide);
        mSpinDistrict = findViewById(R.id.spinner_District);
        mSpinWard = findViewById(R.id.spinner_Ward);
        btn_Back = findViewById(R.id.btn_back);
        btn_Update = findViewById(R.id.btn_Update);
        mEdtHouse = findViewById(R.id.edit_House);
        new GetInfoDetailsAsyncTask(this,Id).execute();

        btn_Back.setOnClickListener(v -> onBackPressed());
        btn_Update.setOnClickListener(v -> update());
    }

    private void update() {
        if (!Utils.isConnectedInternet(this)){
            Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDataInvalid()){
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(Constant.NAME, edit_Name.getText().toString());
            map.put(Constant.FULL_NAME, edit_FullName.getText().toString());
            map.put("phone_number", edit_Number.getText().toString());
            map.put("address", mEdtHouse.getText().toString());
            map.put("province_id", String.valueOf(mIdProvince));
            map.put("district_id", String.valueOf(mIdDistrict));
            map.put("ward_id", String.valueOf(mIdWard));

            mData = Utils.getDataString(map);
            new UpdateAccountAsyncTask(this).execute();
        }
    }


    private void initSpinnerSelect(){
        mSpinProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idParent = mListProvince.get(position).getId();
                new GetRegionAsyncTask(InfomationActivity.this, idParent,2, false).execute();
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
                new GetRegionAsyncTask(InfomationActivity.this, idParent,3, false).execute();
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
    }

    private void updateUI(Information info){
        if (info == null) return;
        mInfo = info;
        edit_Name.setText(info.getName());
        edit_FullName.setText(info.getFullName());
        edit_Number.setText(info.getNumber());
        mEdtHouse.setText(info.getAddress());
    }
    private static class GetInfoDetailsAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<InfomationActivity> weakReference;
        private boolean isSuccess;
        private String id;
        private Information information;
        private String token;


        public GetInfoDetailsAsyncTask(InfomationActivity activity, String id) {
            weakReference = new WeakReference<>(activity);
            this.id = id;
            token = Utils.getStringPreferences(weakReference.get(), Constant.TOKEN_LOGIN);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (isSuccess) {
                weakReference.get().updateUI(information);
                new GetRegionAsyncTask(weakReference.get(), 0, 1, true).execute();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            String url = "http://10.0.2.2:8000/api/auth/getone";
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET",null)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    isSuccess = true;
                    try {
                        String responseString = response.body().string();
                        JSONObject object = new JSONObject(responseString);
                        JSONObject info = object.getJSONObject("user");
                        int id = info.getInt("id");
                        String name = info.getString("name");
                        String fullname = info.getString("fullname");
                        String email = info.getString("email");
                        String number = info.getString("phone_number");
                        String address = info.getString("address");
                        information = new Information(id,name,fullname,email,number, address);
                        weakReference.get().mIdProvince = info.getInt("province_id");
                        weakReference.get().mIdDistrict = info.getInt("district_id");
                        weakReference.get().mIdWard = info.getInt("ward_id");

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

    private static class GetRegionAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<InfomationActivity> mWeakReference;
        private boolean mIsSuccess;
        private int parentId;
        private int level;
        private List<Region> list = new ArrayList<>();
        private boolean loadData;

        public GetRegionAsyncTask(InfomationActivity activity, int parentId, int level, boolean load) {
            mWeakReference = new WeakReference<>(activity);
            this.parentId = parentId;
            this.level = level;
            loadData = load;
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

                        if (loadData){
                            int posProvince = -1;
                            for (int i =0; i<list.size();i++) {
                                if (list.get(i).getId() == mWeakReference.get().mIdProvince){
                                    posProvince = i;
                                    break;
                                }
                            }
                            if (posProvince >=0)
                                mWeakReference.get().mSpinProvince.setSelection(posProvince);
                            new GetRegionAsyncTask(mWeakReference.get(), mWeakReference.get().mIdProvince, 2, true).execute();
                        }
                        break;
                    case 2:
                        mWeakReference.get().mSpinDistrict.setAdapter(adapter);
                        mWeakReference.get().mListDistrict = list;
                        if (loadData){
                            int posDistrict = -1;
                            for (int i =0; i<list.size();i++) {
                                if (list.get(i).getId() == mWeakReference.get().mIdDistrict){
                                    posDistrict = i;
                                    break;
                                }
                            }
                            if (posDistrict >=0)
                                mWeakReference.get().mSpinDistrict.setSelection(posDistrict);
                            new GetRegionAsyncTask(mWeakReference.get(), mWeakReference.get().mIdDistrict, 3, true).execute();
                        }
                        break;
                    case 3:
                        mWeakReference.get().mSpinWard.setAdapter(adapter);
                        mWeakReference.get().mListWard = list;
                        if (loadData){
                            int posWard = -1;
                            for (int i =0; i<list.size();i++) {
                                if (list.get(i).getId() == mWeakReference.get().mIdWard){
                                    posWard = i;
                                    break;
                                }
                            }
                            if (posWard >=0)
                                mWeakReference.get().mSpinWard.setSelection(posWard);

                            mWeakReference.get().initSpinnerSelect();
                        }
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

    /** BinhBH
     * @return Dữ liệu không hợp lệ?
     */
    private boolean isDataInvalid(){
        if (edit_Name.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.ten_tk), Toast.LENGTH_SHORT).show();
            edit_Name.requestFocus();
            return true;
        }else if(edit_FullName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), getString(R.string.ho_ten), Toast.LENGTH_SHORT).show();
            edit_FullName.requestFocus();
            return true;
        }
        else if(!edit_Number.getText().toString().matches(Constant.PHONE_PATTERN)){
            Toast.makeText(getApplicationContext(), getString(R.string.must_sdt), Toast.LENGTH_SHORT).show();
            edit_Number.requestFocus();
            return true;
        }
        else if(mEdtHouse.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Bạn phải nhập địa chỉ nhà", Toast.LENGTH_SHORT).show();
            mEdtHouse.requestFocus();
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

    private static class UpdateAccountAsyncTask extends AsyncTask<Void, Void, Void>{
        private final WeakReference<InfomationActivity> mWeakReference;
        private boolean mIsSuccess;
        private String mess;
        private String token;

        public UpdateAccountAsyncTask(InfomationActivity activity) {
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
                Toast.makeText(mWeakReference.get(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                Utils.saveStringPreferences(mWeakReference.get(), Constant.NAME, mWeakReference.get().edit_FullName.getText().toString());

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
                    .url("http://10.0.2.2:8000/api/auth/update")
                    .method("POST", body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization","Bearer "+ token)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
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