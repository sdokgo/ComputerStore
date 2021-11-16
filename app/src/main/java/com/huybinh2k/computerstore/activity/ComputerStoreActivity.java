package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
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
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.fragment.AccountFragment;
import com.huybinh2k.computerstore.fragment.CategoryFragment;
import com.huybinh2k.computerstore.fragment.HomeFragment;
import com.huybinh2k.computerstore.fragment.NotificationFragment;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class ComputerStoreActivity extends AppCompatActivity {

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
                    startActivity(intent);
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
        pagerAdapter.addFragment(new AccountFragment());

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoginActivity.GetCountCartAsyncTask(this, false).execute();
    }
}