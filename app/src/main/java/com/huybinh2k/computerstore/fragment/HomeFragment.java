package com.huybinh2k.computerstore.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huybinh2k.computerstore.Adapter.ItemsAdapter;
import com.huybinh2k.computerstore.Adapter.SliderAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.activity.ComputerStoreActivity;
import com.huybinh2k.computerstore.model.Items;
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

    private RecyclerView mRecyclerNew, mRecyclerDiscount;
    private ItemsAdapter mItemsNewAdapter, mItemsDiscountAdapter;
    private SliderAdapter mSliderAdapter;
    private List<Items> mListNew = new ArrayList<>();
    private List<Items> mListDiscount= new ArrayList<>();
    private View mLayoutDiscount, mLayoutNew;
    private View mMoreDiscount, mMoreNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initSliderView(view);

    }

    private void initView(View view) {
        mRecyclerNew = view.findViewById(R.id.recycler_new);
        mRecyclerDiscount = view.findViewById(R.id.recycler_giam_gia);
        mItemsNewAdapter = new ItemsAdapter(getContext(), mListNew);
        mItemsDiscountAdapter = new ItemsAdapter(getContext(), mListDiscount);
        mRecyclerNew.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerDiscount.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerNew.setAdapter(mItemsNewAdapter);
        mRecyclerDiscount.setAdapter(mItemsDiscountAdapter);
        mLayoutDiscount = view.findViewById(R.id.layout_giam_gia);
        mLayoutNew = view.findViewById(R.id.layout_new);
        mMoreDiscount = view.findViewById(R.id.text_more_discount);
        mMoreNew = view.findViewById(R.id.text_more_new);
        mMoreNew.setOnClickListener(view1 -> {
            if (getActivity() instanceof ComputerStoreActivity){
                ((ComputerStoreActivity) getActivity()).changePage(1,2, "&isNew=1");
            }
        });

        mMoreDiscount.setOnClickListener(view1 -> {
            if (getActivity() instanceof ComputerStoreActivity){
                ((ComputerStoreActivity) getActivity()).changePage(1,1, "&isDiscount=1");
            }
        });

        new GetItemsDiscountAsyncTask(this).execute();
        new GetItemsNewAsyncTask(this).execute();
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
                        String responseString = response.body().string();
                        JSONObject object = new JSONObject(responseString);
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

    private static class GetItemsNewAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<HomeFragment> mWeakReference;
        private boolean mIsSuccess;
        private List<Items> list = new ArrayList<>();

        public GetItemsNewAsyncTask(HomeFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mLayoutNew.setVisibility(View.VISIBLE);
                mWeakReference.get().mItemsNewAdapter.updateList(list);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/item/get_list_new")
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        String responseString = response.body().string();
                        JSONObject object = new JSONObject(responseString);
                        JSONArray jsonArray = object.getJSONArray("ltItem");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString(Constant.ID);
                            String name  = jsonObject.getString(Constant.Item.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            double price = jsonObject.getDouble(Constant.Item.PRICE);
                            double discountPrice = jsonObject.getDouble("promotional_price");
                            Items items = new Items(id, name, img, price, discountPrice);
                            list.add(items);
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

    private static class GetItemsDiscountAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<HomeFragment> mWeakReference;
        private boolean mIsSuccess;
        private List<Items> list = new ArrayList<>();

        public GetItemsDiscountAsyncTask(HomeFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mLayoutDiscount.setVisibility(View.VISIBLE);
                mWeakReference.get().mItemsDiscountAdapter.updateList(list);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url;
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/item/get_list_discount")
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        String responseString = response.body().string();
                        JSONObject object = new JSONObject(responseString);
                        JSONArray jsonArray = object.getJSONArray("ltItem");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString(Constant.ID);
                            String name  = jsonObject.getString(Constant.Item.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            double price = jsonObject.getDouble(Constant.Item.PRICE);
                            double discountPrice = jsonObject.getDouble("promotional_price");
                            Items items = new Items(id, name, img, price, discountPrice);
                            list.add(items);
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