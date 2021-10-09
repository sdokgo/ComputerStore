package com.huybinh2k.computerstore.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class PagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mListFragment = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mListFragment.get(position);
    }

    public void addFragment(Fragment fragment) {
        mListFragment.add(fragment);
    }

    @Override
    public int getItemCount() {
        return mListFragment.size();
    }

    public Fragment getItem(int position) {
        return mListFragment.get(position);
    }

}
