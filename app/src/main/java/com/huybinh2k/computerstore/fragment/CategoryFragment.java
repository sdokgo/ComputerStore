package com.huybinh2k.computerstore.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huybinh2k.computerstore.Adapter.CategoryAdapter;
import com.huybinh2k.computerstore.Adapter.ItemsAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.CategoryItem;
import com.huybinh2k.computerstore.model.Items;

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
public class CategoryFragment extends Fragment {

    public static final String CHANGE_CATE_SELECT = "CHANGE_CATE_SELECT";
    public static final String ID_CATE = "ID_CATE";
    private boolean mIsExpandCategory = false;
    private RecyclerView mRecyclerViewCate, mRecyclerItems;
    private CategoryAdapter mCategoryAdapter;
    private ItemsAdapter mItemsAdapter;
    private RelativeLayout mLayoutExpandCate;
    private ImageView mImageMoreCate;
    private ImageView mImageLessCate;

    private List<CategoryItem> mListCategory = new ArrayList<>();
    private List<Items> mListItems = new ArrayList<>();

    private String mIdCate;
    private boolean isLoadingItems = true;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private int mNumberPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(@NonNull View view) {
        mRecyclerViewCate = view.findViewById(R.id.recycler_select_cate);
        mLayoutExpandCate = view.findViewById(R.id.layout_expand_cate);
        mImageMoreCate = view.findViewById(R.id.img_more_cate);
        mImageLessCate = view.findViewById(R.id.img_less_cate);

        mCategoryAdapter = new CategoryAdapter(mListCategory, getContext());
        mRecyclerViewCate.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCate.setAdapter(mCategoryAdapter);

        mImageMoreCate.setOnClickListener(view1 -> expandCate());
        mImageLessCate.setOnClickListener(view1 -> shrinkCate());
        GetCateAsyncTask getCateAsyncTask = new GetCateAsyncTask(this);
        getCateAsyncTask.execute();

        mRecyclerItems = view.findViewById(R.id.recycler_items);
        mItemsAdapter = new ItemsAdapter(getContext(), mListItems);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerItems.setLayoutManager(mLayoutManager);
        mRecyclerItems.setAdapter(mItemsAdapter);


        mRecyclerItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    mVisibleItemCount = mLayoutManager.getChildCount();
                    mTotalItemCount = mLayoutManager.getItemCount();
                    mPastVisibleItems = mLayoutManager.findFirstVisibleItemPositions(null)[0];

                    if (isLoadingItems) {
                        if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
                            isLoadingItems = false;
                            new GetItemsAsyncTask(CategoryFragment.this, mIdCate, true, ++mNumberPage).execute();
                        }
                    }
                }
            }
        });
    }


    /**
     * BinhBH Hiển thị ra tất category
     */
    private void expandCate(){
        mRecyclerViewCate.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mLayoutExpandCate.setVisibility(View.VISIBLE);
        mImageMoreCate.setVisibility(View.INVISIBLE);
        mRecyclerViewCate.setAdapter(mCategoryAdapter);
    }

    /**
     * BinhBH Thu nhỏ lại danh sách category
     */
    private void shrinkCate(){
        mRecyclerViewCate.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCate.setAdapter(mCategoryAdapter);
        mLayoutExpandCate.setVisibility(View.GONE);
        mImageMoreCate.setVisibility(View.VISIBLE);
    }


    private static class GetCateAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<CategoryFragment> mWeakReference;
        private boolean mIsSuccess;
        List<CategoryItem> listCategory = new ArrayList<>();

        public GetCateAsyncTask(CategoryFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mCategoryAdapter.updateListCate(listCategory);
                if (!listCategory.isEmpty()){
                    mWeakReference.get().mIdCate = listCategory.get(0).getID();
                    mWeakReference.get().mCategoryAdapter.setPositionCateSelect(0);
                    new GetItemsAsyncTask(mWeakReference.get(), mWeakReference.get().mIdCate).execute();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/asset/get_list?parentID=0")
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        listCategory.add(new CategoryItem("0", "Tất cả", ""));
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray jsonArray = object.getJSONArray("ltAsset");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString(Constant.ID);
                            String name = jsonObject.getString(Constant.Category.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            CategoryItem categoryItem = new CategoryItem(id, name, img);
                            listCategory.add(categoryItem);
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

    private BroadcastReceiver mReceiverChangeCateSelect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIdCate = intent.getStringExtra(ID_CATE);
            if (!mIdCate.isEmpty()){
                mNumberPage = 1;
                new GetItemsAsyncTask(CategoryFragment.this, mIdCate).execute();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CHANGE_CATE_SELECT);
        if (getContext() != null){
            getContext().registerReceiver(mReceiverChangeCateSelect, intentFilter);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (getContext() != null){
            getContext().unregisterReceiver(mReceiverChangeCateSelect);
        }
    }

    private static class GetItemsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<CategoryFragment> mWeakReference;
        private boolean mIsSuccess;
        private String idCate;
        private List<Items> list = new ArrayList<>();
        private boolean loadMore;
        private int page;

        public GetItemsAsyncTask(CategoryFragment fragment, String id) {
            mWeakReference = new WeakReference<>(fragment);
            idCate = id;
        }
        public GetItemsAsyncTask(CategoryFragment fragment, String id, boolean more, int page) {
            mWeakReference = new WeakReference<>(fragment);
            idCate = id;
            loadMore = more;
            this.page = page;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                if (loadMore){
                    mWeakReference.get().mItemsAdapter.addDataFromPage(list);
                    mWeakReference.get().isLoadingItems = true;
                }else {
                    mWeakReference.get().mRecyclerItems.scrollToPosition(0);
                    mWeakReference.get().mItemsAdapter.updateList(list);
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url;
            if (loadMore){
                url = "http://10.0.2.2:8000/api/item/get_list_search?assetID=" +
                        idCate + "&pageSize=15&page=" + page;
            }else {
                url = "http://10.0.2.2:8000/api/item/get_list_search?assetID=" +
                        idCate + "&pageSize=15&page=1";
            }
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
                        JSONArray jsonArray = object.getJSONObject("ltItem").getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString(Constant.ID);
                            String name  = jsonObject.getString(Constant.Items.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            int price = jsonObject.getInt(Constant.Items.PRICE);
                            int discountPrice = jsonObject.getInt("promotional_price");
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