package com.huybinh2k.computerstore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huybinh2k.computerstore.Adapter.PropertyAdapter;
import com.huybinh2k.computerstore.Adapter.SliderAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.Items;
import com.huybinh2k.computerstore.model.Property;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsItemActivity extends AppCompatActivity {
    private ImageView mImageItem;
    private ImageView mImagePlus;
    private ImageView mImageMinus;
    private TextView mItemName;
    private TextView mValuePrice;
    private TextView mKeyDiscount;
    private TextView mValueDiscount;
    private TextView mTextMore;
    private TextView mDescription;
    private TextView mTextQuality;
    private EditText mQualityAddToCart;
    private String mId;
    private RecyclerView mRecyclerView;
    private PropertyAdapter mPropertyAdapter;
    private int mQuality = 1;
    private ScrollView mScrollView;
    private boolean mIsExpandDescription;
    private Items mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_item);
        if (getIntent().getExtras() != null){
            mId = getIntent().getStringExtra(Constant.ID);
        }
        initToolbar();
        initView();
    }

    private void initView() {
        new GetItemDetailsAsyncTask(this, mId).execute();
        mScrollView = findViewById(R.id.scroll_details);
        mImageItem = findViewById(R.id.image_item_details);
        mImagePlus = findViewById(R.id.plus);
        mImageMinus = findViewById(R.id.minus);
        mItemName = findViewById(R.id.text_name_items_details);
        mValuePrice = findViewById(R.id.value_price);
        mKeyDiscount = findViewById(R.id.key_discount_price);
        mValueDiscount = findViewById(R.id.value_discount_price);
        mDescription = findViewById(R.id.description_items);
        mTextMore = findViewById(R.id.txt_more_details);
        mRecyclerView = findViewById(R.id.recycler_property);
        mTextQuality = findViewById(R.id.value_quality);
        mQualityAddToCart = findViewById(R.id.txt_quantity);
        mPropertyAdapter = new PropertyAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mPropertyAdapter);

        mTextMore.setOnClickListener(view -> {
            expandOrShrinkDes(mIsExpandDescription);
        });
        mImagePlus.setOnClickListener(view -> changeQuality(true));
        mImageMinus.setOnClickListener(view -> changeQuality(false));
    }

    private void expandOrShrinkDes(boolean isExpandDescription){
        if (isExpandDescription){
            mDescription.setMaxLines(3);
            mTextMore.setText(getString(R.string.text_more));
            mIsExpandDescription = false;
        }else {
            mDescription.setMaxLines(99);
            mTextMore.setText(getString(R.string.rut_gon));
            mIsExpandDescription = true;
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void updateUI(Items item, List<Property> list){
        if (item == null) return;
        mItem = item;
        Glide.with(this).load(item.getPathImage())
                .fitCenter()
                .into(mImageItem);
        mItemName.setText(item.getName());
        NumberFormat format = new DecimalFormat("#,###");
        format.setMaximumFractionDigits(0);
        mValuePrice.setText(format.format(item.getPrice()).replaceAll(",",".") + " đ");
        if (item.getDiscountPrice() == 0){
            mKeyDiscount.setVisibility(View.GONE);
            mValueDiscount.setVisibility(View.GONE);
        }else {
            mValuePrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mValueDiscount.setText(format.format(item.getDiscountPrice()).replaceAll(",",".")  +" đ");
        }
        if (item.getQuality() == 0){
            mQuality = 0;
            mQualityAddToCart.setText(String.valueOf(mQuality));
            mTextQuality.setText(item.getStatus());
        }else {
            mTextQuality.setText(String.valueOf(item.getQuality()));
        }
        mDescription.setText(item.getDescription());
        mPropertyAdapter.updateProperty(list);
        mScrollView.fullScroll(View.FOCUS_UP);
    }

    private void changeQuality(boolean plus){
        if (mItem.getQuality() == 0){
            return;
        }
        if (plus){
            mQuality++;
            if (mQuality > mItem.getQuality()) mQuality = mItem.getQuality();
        }else {
            mQuality--;
            if (mQuality < 1) mQuality = 1;
        }
        mQualityAddToCart.setText(String.valueOf(mQuality));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_cart) {

        }else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class GetItemDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<DetailsItemActivity> mWeakReference;
        private boolean mIsSuccess;
        private String id;
        private Items items;
        private List<Property> list = new ArrayList<>();

        public GetItemDetailsAsyncTask(DetailsItemActivity activity, String id) {
            mWeakReference = new WeakReference<>(activity);
            this.id = id;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().updateUI(items, list);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url = "http://10.0.2.2:8000/api/item/get_one?itemID=" + id;
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONObject item = object.getJSONObject("item");
                        String id = item.getString("id");
                        String name = item.getString("item_name");
                        String img = "http://10.0.2.2:8000/"+ item.getString(Constant.IMAGE);
                        String des = item.getString("description");
                        int price = item.getInt("price");
                        int discountPrice = item.getInt("promotional_price");
                        int quality = item.getInt("quanlity");
                        String status = item.getString("status_name");
                        items = new Items(id,name,img, price, discountPrice, quality, des, status);
                        JSONArray jsonArray = item.getJSONArray("itemProperties");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String title = jsonObject.getString("asset_property_name");
                            String value = jsonObject.getString("value");
                            Property property = new Property(title, value);
                            list.add(property);
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
}