package com.huybinh2k.computerstore.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huybinh2k.computerstore.Adapter.SliderAdapter;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private SliderAdapter mSliderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSliderView(view);

    }

    private void initSliderView(@NonNull View view) {
        SliderView sliderView = view.findViewById(R.id.imageSlider);
        mSliderAdapter = new SliderAdapter(getContext());
        sliderView.setSliderAdapter(mSliderAdapter);
        //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM
        //or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

//        sliderView.setOnIndicatorClickListener(position ->
//                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition())
//        );
    }

    @Override
    public void onResume() {
        super.onResume();
        renewItems();
    }

    //TODO BinhBH Cần sửa lại danh sách image khi có API
    public void renewItems() {
        List<SliderItem> sliderItemList = new ArrayList<>();
        //dummy data
        for (int i = 0; i < 5; i++) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDescription("Slider Item " + i);
            if (i % 2 == 0) {
                sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
            } else {
                sliderItem.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
            }
            sliderItemList.add(sliderItem);
        }
        mSliderAdapter.renewItems(sliderItemList);
    }
}