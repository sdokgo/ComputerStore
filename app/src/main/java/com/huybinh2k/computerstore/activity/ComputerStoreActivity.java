package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huybinh2k.computerstore.Adapter.PagerAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSearchView();
    }

    CategoryFragment categoryFragment = new CategoryFragment();

    private void initView(){
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
}