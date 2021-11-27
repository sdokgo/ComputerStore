package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.allenliu.badgeview.BadgeView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huybinh2k.computerstore.Adapter.PagerAdapter;
import com.huybinh2k.computerstore.ComputerApplication;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.fragment.AccountFragment;
import com.huybinh2k.computerstore.fragment.CategoryFragment;
import com.huybinh2k.computerstore.fragment.HomeFragment;
import com.huybinh2k.computerstore.fragment.NotificationFragment;
import com.huybinh2k.computerstore.model.CartItems;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class ComputerStoreActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST = 123;
    private RelativeLayout mLayoutToolBar;
    private ViewPager2 mViewpager;
    private ImageView mImageViewCart;
    private BadgeView mBadgeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSearchView();
    }

    CategoryFragment categoryFragment = new CategoryFragment();
    AccountFragment  accountFragment = new AccountFragment();

    private void initView(){
        mImageViewCart = findViewById(R.id.img_cart);
        mImageViewCart.setOnClickListener(view -> {
            if (Utils.getBooleanPreferences(this, Utils.IS_LOGIN)){
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setMessage(R.string.want_login);
                builder.setNegativeButton("Hủy", (dialogInterface, i) -> {

                });
                builder.setPositiveButton("Đồng ý", (dialogInterface, i) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                });
                builder.show();
            }
        });

        int badgeCount = Utils.getIntPreferences(this, Utils.NUMBER_ITEMS_CART);
        mBadgeView = new BadgeView(this);
        mBadgeView.setTextColor(Color.WHITE)
                .setWidthAndHeight(18, 18)
                .setBadgeBackground(Color.RED)
                .setTextSize(12)
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setShape(BadgeView.SHAPE_CIRCLE)
                .setSpace(5, 10)
                .bind(mImageViewCart);
        if (badgeCount>0){
            mBadgeView.setBadgeCount(badgeCount);
            mBadgeView.setVisibility(View.VISIBLE);
        }else {
            mBadgeView.setVisibility(View.GONE);
        }
        mLayoutToolBar = findViewById(R.id.layout_tool_bar);
        mViewpager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);


        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
        pagerAdapter.addFragment(new HomeFragment());
        pagerAdapter.addFragment(categoryFragment);
        pagerAdapter.addFragment(new NotificationFragment());
        pagerAdapter.addFragment(accountFragment);

        mViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mViewpager.setAdapter(pagerAdapter);

        mViewpager.setUserInputEnabled(false); //false disable viewpager swiping, true enable
        new TabLayoutMediator(tabLayout, mViewpager,
                (tab, position) -> {
                    tab.setIcon(Constant.arrImage[position]);
                    tab.setText(getResources().getText(Constant.arrName[position]));
                }
        ).attach();
    }

    private void initSearchView() {
        PersistentSearchView searchView = findViewById(R.id.persistentSearchView);
        searchView.collapse(false, true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        searchView.getInputEt().setTouchEventInterceptor((view, event) -> {
            view.performClick();
            if(event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(ComputerStoreActivity.this, SearchActivity.class));
            }
            return false;
        });
        searchView.getVoiceInputBtnIv().setOnClickListener(view ->{
            startActivity(new Intent(ComputerStoreActivity.this, SearchActivity.class));
        });
        searchView.getLeftBtnIv().setOnClickListener(view ->{
            startActivity(new Intent(ComputerStoreActivity.this, SearchActivity.class));
        });
    }

    public void showHideSearchView(){
        if (mLayoutToolBar.getVisibility() == View.GONE){
            mLayoutToolBar.setVisibility(View.VISIBLE);
        }else {
            mLayoutToolBar.setVisibility(View.GONE);
        }
    }

    public void showSearchBar(){
        mLayoutToolBar.setVisibility(View.VISIBLE);
    }

    public void changePage(int page,int valueMore, String s){
        mViewpager.setCurrentItem(page);
        categoryFragment.moreItemsFromHome(valueMore, s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int badgeCount = Utils.getIntPreferences(this, Utils.NUMBER_ITEMS_CART);
        if (badgeCount>0){
            mBadgeView.setBadgeCount(badgeCount);
            mBadgeView.setVisibility(View.VISIBLE);
        }else {
            mBadgeView.setVisibility(View.GONE);
        }

        if (Utils.getBooleanPreferences(this, Utils.IS_LOGIN)){
            new GetCartItemsAsyncTask(this).execute();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoginActivity.GetCountCartAsyncTask(this, false).execute();
    }

    public void  updateBadgeView(int count){
        if (count ==0) {
            mBadgeView.setVisibility(View.GONE);
        }else {
            mBadgeView.setVisibility(View.VISIBLE);
            mBadgeView.setBadgeCount(count);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST){
            if (resultCode == RESULT_OK){
                accountFragment.updateUILogin(this);
            }
        }
    }

    private static class GetCartItemsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<ComputerStoreActivity> mWeakReference;
        private boolean mIsSuccess;
        private String token;
        private List<CartItems> list = new ArrayList<>();
        private LinkedHashMap<String, CartItems> map = new LinkedHashMap<>();

        public GetCartItemsAsyncTask(ComputerStoreActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            token = Utils.getStringPreferences(mWeakReference.get(), Constant.TOKEN_LOGIN);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                ComputerApplication.mMapCart = map;
            }else {
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
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray jsonArray = object.getJSONArray("ltItem");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String idItems = jsonObject.getString(Constant.ID);
                            String name  = jsonObject.getString(Constant.Item.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            int price = jsonObject.getInt(Constant.Item.PRICE);
                            int discountPrice = jsonObject.getInt("promotional_price");
                            int quantityItems = jsonObject.getInt("quanlity");
                            JSONObject objectCart = jsonObject.getJSONObject("pivot");

                            String idCart = objectCart.getString(Constant.ID);
                            int quantityCart = objectCart.getInt("quantity");
                            CartItems cartItems = new CartItems(name, idItems, idCart,price, discountPrice,
                                    quantityCart, quantityItems, img);
                            list.add(cartItems);
                            map.put(idItems, cartItems);
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