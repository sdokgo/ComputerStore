package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huybinh2k.computerstore.Adapter.PagerAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.fragment.AccountFragment;
import com.huybinh2k.computerstore.fragment.CartFragment;
import com.huybinh2k.computerstore.fragment.CategoryFragment;
import com.huybinh2k.computerstore.fragment.HomeFragment;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class ComputerStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView(){
        ViewPager2 viewpager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);


        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
        pagerAdapter.addFragment(new HomeFragment());
        pagerAdapter.addFragment(new CategoryFragment());
        pagerAdapter.addFragment(new CartFragment());
        pagerAdapter.addFragment(new AccountFragment());

        viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewpager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewpager,
                (tab, position) -> {
                    tab.setIcon(Constant.arrImage[position]);
                    tab.setText(getResources().getText(Constant.arrName[position]));
                }
        ).attach();
    }
}