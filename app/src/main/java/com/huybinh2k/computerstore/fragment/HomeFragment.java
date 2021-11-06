package com.huybinh2k.computerstore.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
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
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        new GetSliderAsyncTask(this).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private static class GetSliderAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<HomeFragment> mWeakReference;
        private boolean mIsSuccess;
        List<SliderItem> list = new ArrayList<>();

        public GetSliderAsyncTask(HomeFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mSliderAdapter.renewItems(list);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/slide/get_list")
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray jsonArray = object.getJSONArray("ltSlide");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("item_id");
                            String title = jsonObject.getString("side_title");
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString("slide_image");
                            SliderItem sliderItem = new SliderItem(id, title, img);
                            list.add(sliderItem);
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