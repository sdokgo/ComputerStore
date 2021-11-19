package com.huybinh2k.computerstore.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huybinh2k.computerstore.Adapter.CategoryAdapter;
import com.huybinh2k.computerstore.Adapter.FilterCategoryAdapter;
import com.huybinh2k.computerstore.Adapter.FilterManufacturerAdapter;
import com.huybinh2k.computerstore.Adapter.FilterMoreAdapter;
import com.huybinh2k.computerstore.Adapter.FilterPriceAdapter;
import com.huybinh2k.computerstore.Adapter.FilterStatusAdapter;
import com.huybinh2k.computerstore.Adapter.ItemsAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.activity.ComputerStoreActivity;
import com.huybinh2k.computerstore.model.CategoryItem;
import com.huybinh2k.computerstore.model.FilterPrice;
import com.huybinh2k.computerstore.model.ItemMore;
import com.huybinh2k.computerstore.model.ItemStatus;
import com.huybinh2k.computerstore.model.Items;
import com.huybinh2k.computerstore.model.Manufacturer;

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
    public static final String CHANGE_CATE_FILTER = "CHANGE_CATE_FILTER";
    public static final String ID_CATE = "ID_CATE";
    public static final String POSITION_CATE = "POSITION_CATE";
    private boolean mIsExpandCategory = false;
    private RecyclerView mRecyclerViewCate, mRecyclerItems;
    private CategoryAdapter mCategoryAdapter;
    private ItemsAdapter mItemsAdapter;
    private RelativeLayout mLayoutExpandCate;
    private RelativeLayout mLayoutFilter;
    private RelativeLayout mLayoutCate;
    private ImageView mImageMoreCate;
    private ImageView mImageLessCate;
    private ImageView mImageFilter;
    private ImageView mImageHideFilter;
    private View mLayoutFragment;

    private RecyclerView mRecyclerFilterCate;
    private FilterCategoryAdapter mFilterCategoryAdapter;

    private RecyclerView mRecyclerFilterManufacturer;
    private FilterManufacturerAdapter mFilterManufacturerAdapter;

    private RecyclerView mRecyclerFilterStatus;
    private FilterStatusAdapter mFilterStatusAdapter;

    private RecyclerView mRecyclerFilterPrice;
    private FilterPriceAdapter mFilterPriceAdapter;

    private RecyclerView mRecyclerMore;
    private FilterMoreAdapter mFilterMoreAdapter;


    private List<CategoryItem> mListCategory = new ArrayList<>();
    private List<Items> mListItems = new ArrayList<>();
    private List<Manufacturer> mListManu = new ArrayList<>();
    private List<ItemStatus> mListStatus = new ArrayList<>();
    private List<FilterPrice> mListPrice = new ArrayList<>();
    private List<ItemMore> mListMore = new ArrayList<>();

    private TextView mTextResetFilter;
    private TextView mTextAcceptFilter;
    private EditText mEditTextSearchFilter, mEditTextMinPrice, mEditTextMaxPrice;
    private TextView mTextSortPrice, mTextSortName;
    private boolean mSortPrice, mSortName;

    private String mIdCate = "0";
    private boolean isLoadingItems = true;
    private int mPastVisibleItems, mVisibleItemCount, mTotalItemCount;
    private int mNumberPage = 1;
    private String mStringFromHomeFragment;
    private int mValueMore = 0;

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
        initAdvanceSearch(view);
    }

    private void initView(@NonNull View view) {
        mLayoutFragment = view.findViewById(R.id.layout_fragment_cate);
        mRecyclerViewCate = view.findViewById(R.id.recycler_select_cate);
        mLayoutExpandCate = view.findViewById(R.id.layout_expand_cate);
        mLayoutFilter = view.findViewById(R.id.layout_filter);
        mLayoutCate = view.findViewById(R.id.layout_cate);
        mImageMoreCate = view.findViewById(R.id.img_more_cate);
        mImageLessCate = view.findViewById(R.id.img_less_cate);
        mImageFilter = view.findViewById(R.id.image_filter);
        mImageHideFilter = view.findViewById(R.id.hide_filter);

        mCategoryAdapter = new CategoryAdapter(mListCategory, getContext());
        mRecyclerViewCate.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewCate.setAdapter(mCategoryAdapter);

        mImageMoreCate.setOnClickListener(view1 -> expandCate());
        mImageLessCate.setOnClickListener(view1 -> shrinkCate());
        mImageFilter.setOnClickListener(view1 -> showHideFilter());
        mImageHideFilter.setOnClickListener(view1 -> showHideFilter());
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

        mTextSortPrice = view.findViewById(R.id.sort_by_price);
        mTextSortName = view.findViewById(R.id.sort_by_name);
        mTextSortPrice.setOnClickListener(view1 -> {
            if (mSortPrice){
                mItemsAdapter.sort(ItemsAdapter.SORT_BY_MAX_TO_MIN);
                mTextSortPrice.setText(getString(R.string.min_to_max));
                mSortPrice = false;
            } else {
                mItemsAdapter.sort(ItemsAdapter.SORT_BY_MIN_TO_MAX);
                mTextSortPrice.setText(getString(R.string.max_to_min));
                mSortPrice = true;
            }
            mTextSortName.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.border_item_filter, null));
            mTextSortPrice.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.border_select_sort, null));
        });
        mTextSortName.setOnClickListener(view1 -> {
            if (mSortName){
                mItemsAdapter.sort(ItemsAdapter.SORT_BY_A_TO_Z);
                mTextSortName.setText(getString(R.string.a_to_z));
                mSortName = false;
            } else {
                mItemsAdapter.sort(ItemsAdapter.SORT_BY_Z_TO_A);
                mSortName = true;
                mTextSortName.setText(getString(R.string.z_to_a));
            }
            mTextSortPrice.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.border_item_filter,null));
            mTextSortName.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.border_select_sort, null));
        });

    }

    private void initAdvanceSearch(View view){
        View layoutFilterCate = view.findViewById(R.id.layout_filter_cate);
        mRecyclerFilterCate = view.findViewById(R.id.recycler_filter_cate);
        mFilterCategoryAdapter = new FilterCategoryAdapter(getContext(), mListCategory);
        mRecyclerFilterCate.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerFilterCate.setAdapter(mFilterCategoryAdapter);
        layoutFilterCate.setOnClickListener(view1 -> {
            if (mRecyclerFilterCate.getVisibility() == View.GONE){
                mRecyclerFilterCate.setVisibility(View.VISIBLE);
            }else {
                mRecyclerFilterCate.setVisibility(View.GONE);
            }
        });

        mRecyclerFilterManufacturer = view.findViewById(R.id.recycler_filter_manu);
        mFilterManufacturerAdapter = new FilterManufacturerAdapter(getContext(), mListManu);
        mRecyclerFilterManufacturer.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerFilterManufacturer.setAdapter(mFilterManufacturerAdapter);
        View layoutFilterManu = view.findViewById(R.id.layout_manufacturer);
        layoutFilterManu.setOnClickListener(view1 -> {
            if (mRecyclerFilterManufacturer.getVisibility() == View.GONE){
                mRecyclerFilterManufacturer.setVisibility(View.VISIBLE);
            }else {
                mRecyclerFilterManufacturer.setVisibility(View.GONE);
            }
        });


        mRecyclerFilterStatus = view.findViewById(R.id.recycler_filter_status);
        hardCodeStatus();
        mFilterStatusAdapter = new FilterStatusAdapter(getContext(), mListStatus);
        mRecyclerFilterStatus.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerFilterStatus.setAdapter(mFilterStatusAdapter);
        View layoutFilterStatus = view.findViewById(R.id.layout_status);
        layoutFilterStatus.setOnClickListener(view1 -> {
            if (mRecyclerFilterStatus.getVisibility() == View.GONE){
                mRecyclerFilterStatus.setVisibility(View.VISIBLE);
            }else {
                mRecyclerFilterStatus.setVisibility(View.GONE);
            }
        });

        mRecyclerFilterPrice = view.findViewById(R.id.recycler_filter_price);
        hardCodePrice();
        mFilterPriceAdapter = new FilterPriceAdapter(this, mListPrice);
        mRecyclerFilterPrice.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerFilterPrice.setAdapter(mFilterPriceAdapter);
        View layoutFilterPrice = view.findViewById(R.id.layout_price);
        View layoutChangePrice = view.findViewById(R.id.layout_edt_price);
        layoutFilterPrice.setOnClickListener(view1 -> {
            if (mRecyclerFilterPrice.getVisibility() == View.GONE){
                mRecyclerFilterPrice.setVisibility(View.VISIBLE);
                layoutChangePrice.setVisibility(View.VISIBLE);
            }else {
                mRecyclerFilterPrice.setVisibility(View.GONE);
                layoutChangePrice.setVisibility(View.GONE);
            }
        });

        mTextResetFilter = view.findViewById(R.id.txt_reset_filter);
        mTextAcceptFilter = view.findViewById(R.id.text_ok_filter);
        mEditTextSearchFilter = view.findViewById(R.id.edt_filter);
        mEditTextMinPrice = view.findViewById(R.id.edt_min_price);
        mEditTextMaxPrice = view.findViewById(R.id.edt_max_price);
        mTextResetFilter.setOnClickListener(view1 -> resetFilter());

        mTextAcceptFilter.setOnClickListener(view1 -> {
            String id = mFilterCategoryAdapter.getCateSelect() == null? "0": mFilterCategoryAdapter.getCateSelect().getID();
            new GetItemsAsyncTask(this, id).execute();
            showHideFilter();
        });

        mEditTextMinPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mEditTextMinPrice.getText().toString().isEmpty()){
                    mFilterPriceAdapter.updateSelectPrice(-1);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEditTextMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!mEditTextMaxPrice.getText().toString().isEmpty()){
                    mFilterPriceAdapter.updateSelectPrice(-1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRecyclerMore = view.findViewById(R.id.recycler_more);
        hardCodeMore();
        mFilterMoreAdapter = new FilterMoreAdapter(getContext(), mListMore);
        mFilterMoreAdapter.updateSelectMore(mValueMore);
        mRecyclerMore.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerMore.setAdapter(mFilterMoreAdapter);
        View layoutFilterMore = view.findViewById(R.id.layout_more);
        layoutFilterMore.setOnClickListener(view1 -> {
            if (mRecyclerMore.getVisibility() == View.GONE){
                mRecyclerMore.setVisibility(View.VISIBLE);
            }else {
                mRecyclerMore.setVisibility(View.GONE);
            }
        });

    }

    private void hardCodeMore() {
        mListMore.add(new ItemMore("Khuyến mại", "isDiscount", "1"));
        mListMore.add(new ItemMore("Hàng mới", "isNew", "1"));
    }

    private void hardCodePrice() {
        mListPrice.add(new FilterPrice("Giá dưới 1 triệu", 0, 1000000));
        mListPrice.add(new FilterPrice("Giá từ 1 triệu đến 5 triệu", 1000000, 5000000));
        mListPrice.add(new FilterPrice("Giá từ 5 triệu đến 10 triệu", 5000000, 10000000));
        mListPrice.add(new FilterPrice("Giá trên 10 triệu", 10000000, 0));
    }

    private void hardCodeStatus() {
        mListStatus.add(new ItemStatus("1", "Còn hàng"));
        mListStatus.add(new ItemStatus("2", "Hết hàng"));
        mListStatus.add(new ItemStatus("3", "Sắp về"));
    }

    private void showHideFilter() {
        if (mLayoutFilter.getVisibility() == View.GONE){
            mLayoutFilter.setVisibility(View.VISIBLE);
            mLayoutCate.setVisibility(View.GONE);
            mRecyclerItems.setVisibility(View.GONE);
        }else {
            mLayoutFilter.setVisibility(View.GONE);
            mLayoutCate.setVisibility(View.VISIBLE);
            mRecyclerItems.setVisibility(View.VISIBLE);
        }
        if (getActivity() instanceof ComputerStoreActivity){
            ((ComputerStoreActivity) getActivity()).showHideSearchView();
        }
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

    private void resetFilter() {
        mCategoryAdapter.setPositionCateSelect(0);
        mFilterCategoryAdapter.updateSelectCate(0);
        new GetManufacturerAsyncTask(this, "0").execute();
        mFilterManufacturerAdapter.clearSelectManu();
        mFilterStatusAdapter.updateSelectStatus(-1);
        mFilterPriceAdapter.updateSelectPrice(-1);
        mFilterMoreAdapter.clearSelectMore();
        mEditTextMinPrice.getText().clear();
        mEditTextMaxPrice.getText().clear();
        mEditTextSearchFilter.getText().clear();
        mValueMore = 0;
        if (mEditTextMinPrice != null && getActivity() != null) {
            Utils.hideKeyboard(mEditTextMinPrice, getActivity());
        }
    }

    private void resetFilterWhenChangeCate(){
        new GetManufacturerAsyncTask(this, mIdCate).execute();
        mFilterManufacturerAdapter.clearSelectManu();
        mFilterStatusAdapter.updateSelectStatus(-1);
        mFilterPriceAdapter.updateSelectPrice(-1);
        mFilterMoreAdapter.clearSelectMore();
        mEditTextMinPrice.getText().clear();
        mEditTextMaxPrice.getText().clear();
        mEditTextSearchFilter.getText().clear();
        if (mEditTextMinPrice != null && getActivity() != null) {
            Utils.hideKeyboard(mEditTextMinPrice, getActivity());
        }
    }

    public void clearFilterPrice(){
        mEditTextMinPrice.getText().clear();
        mEditTextMaxPrice.getText().clear();
        mEditTextMinPrice.clearFocus();
        mEditTextMaxPrice.clearFocus();
        if (mEditTextMinPrice != null && getActivity() != null) {
            Utils.hideKeyboard(mEditTextMinPrice, getActivity());
        }
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
                mWeakReference.get().mFilterCategoryAdapter.updateListCate(listCategory);
                if (!listCategory.isEmpty()){
                    mWeakReference.get().mLayoutFragment.setVisibility(View.VISIBLE);
                    mWeakReference.get().mIdCate = listCategory.get(0).getID();
                    mWeakReference.get().mCategoryAdapter.setPositionCateSelect(0);
                    mWeakReference.get().mFilterCategoryAdapter.updateSelectCate(0);
                    new GetItemsAsyncTask(mWeakReference.get(), mWeakReference.get().mIdCate).execute();
                    new GetManufacturerAsyncTask(mWeakReference.get(), mWeakReference.get().mIdCate).execute();
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
            int pos = intent.getIntExtra(POSITION_CATE, -1);
            if (pos != -1){
                mFilterCategoryAdapter.updateSelectCate(pos);
            }
            if (!mIdCate.isEmpty()){
                mNumberPage = 1;
                resetFilterWhenChangeCate();
                new GetItemsAsyncTask(CategoryFragment.this, mIdCate).execute();
                new GetManufacturerAsyncTask(CategoryFragment.this, mIdCate).execute();
            }

            if (mEditTextMinPrice != null && getActivity() != null) {
                Utils.hideKeyboard(mEditTextMinPrice, getActivity());
            }
        }
    };



    private BroadcastReceiver mReceiverChangeCateFilter = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIdCate = intent.getStringExtra(ID_CATE);
            int pos = intent.getIntExtra(POSITION_CATE, -1);
            if (!mIdCate.isEmpty()){
                mFilterManufacturerAdapter.clearSelectManu();
                new GetManufacturerAsyncTask(CategoryFragment.this, mIdCate).execute();
            }
            if (pos != -1){
                mCategoryAdapter.setPositionCateSelect(pos);
            }
            if (mEditTextMinPrice != null && getActivity() != null) {
                Utils.hideKeyboard(mEditTextMinPrice, getActivity());
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

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(CHANGE_CATE_FILTER);
        if (getContext() != null){
            getContext().registerReceiver(mReceiverChangeCateFilter, intentFilter2);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getContext() != null){
            getContext().unregisterReceiver(mReceiverChangeCateSelect);
            getContext().unregisterReceiver(mReceiverChangeCateFilter);
        }
    }

    private static class GetItemsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<CategoryFragment> mWeakReference;
        private boolean mIsSuccess;
        private String idCate;
        private List<Items> list = new ArrayList<>();
        private boolean loadMore;
        private int page;
        private String stringSearch;
        private String minPrice;
        private String maxPrice;
        private String manu;
        private String status;
        private String more;

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
        protected void onPreExecute() {
            super.onPreExecute();
            if (mWeakReference.get().mFilterCategoryAdapter.getCateSelect() != null){
                idCate = mWeakReference.get().mFilterCategoryAdapter.getCateSelect().getID();
            }
            if (!mWeakReference.get().mFilterManufacturerAdapter.getMapSelectManu().isEmpty()){
                manu = "&manufacturerIds=";
                for (Manufacturer manufacturer: mWeakReference.get().mFilterManufacturerAdapter.getMapSelectManu().values()) {
                    manu+= manufacturer.getId() +",";
                }
                manu = manu.substring(0, manu.lastIndexOf(","));
            }
            if (mWeakReference.get().mFilterStatusAdapter.getItemsStatusSelect() != null){
                status = "statusId=" + mWeakReference.get().mFilterStatusAdapter.getItemsStatusSelect().getId();
            }
            if (mWeakReference.get().mFilterPriceAdapter.getPriceSelect() != null){
                minPrice = "&minPrice=" + mWeakReference.get().mFilterPriceAdapter.getPriceSelect().getMinPrice();
                maxPrice = "&maxPrice=" + mWeakReference.get().mFilterPriceAdapter.getPriceSelect().getMaxPrice();
            }
            if (mWeakReference.get().mEditTextMinPrice.getText() != null
                    && !mWeakReference.get().mEditTextMinPrice.getText().toString().isEmpty()){
                minPrice = "&minPrice=" + mWeakReference.get().mEditTextMinPrice.getText().toString();
            }
            if (mWeakReference.get().mEditTextMaxPrice.getText() != null
                    && !mWeakReference.get().mEditTextMaxPrice.getText().toString().isEmpty()){
                maxPrice = "&maxPrice=" + mWeakReference.get().mEditTextMaxPrice.getText().toString();
            }
            if (mWeakReference.get().mEditTextSearchFilter.getText() != null
                    && !mWeakReference.get().mEditTextSearchFilter.getText().toString().isEmpty()){
                stringSearch = "&textSearch="+ mWeakReference.get().mEditTextSearchFilter.getText().toString();
            }
            if (mWeakReference.get().mStringFromHomeFragment != null
                    && !mWeakReference.get().mStringFromHomeFragment.isEmpty()){
                more = mWeakReference.get().mStringFromHomeFragment;
            }

            if (!mWeakReference.get().mFilterMoreAdapter.getMapSelectMore().isEmpty()){
                more ="";
                for (ItemMore i: mWeakReference.get().mFilterMoreAdapter.getMapSelectMore().values()) {
                    more += "&"+ i.getKey()+"="+i.getValue();
                }
            }
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
                        idCate + "&pageSize=15&page=" + page +
                        (manu == null ? "" : manu) + (status == null ? "" : status)
                        + (minPrice == null ? "" : minPrice) + (maxPrice == null ? "" : maxPrice)
                        + (stringSearch == null ? "" : stringSearch)
                        + (more == null ? "" : more);
            }else {
                url = "http://10.0.2.2:8000/api/item/get_list_search?assetID=" +
                        idCate + "&pageSize=15&page=1" +
                        (manu == null ? "" : manu) + (status == null ? "" : status)
                        + (minPrice == null ? "" : minPrice) + (maxPrice == null ? "" : maxPrice)
                        + (stringSearch == null ? "" : stringSearch)
                        + (more == null ? "" : more);
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
                            String img = Constant.LOCALHOST+ jsonObject.getString(Constant.IMAGE);
                            double price = jsonObject.getDouble(Constant.Items.PRICE);
                            double discountPrice = jsonObject.getDouble(Constant.Items.DISCOUNT);
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


    private static class GetManufacturerAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<CategoryFragment> mWeakReference;
        private boolean mIsSuccess;
        private List<Manufacturer> listManufacturer = new ArrayList<>();
        private String cateId;

        public GetManufacturerAsyncTask(CategoryFragment fragment, String id) {
            mWeakReference = new WeakReference<>(fragment);
            cateId = id;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mFilterManufacturerAdapter.updateListManu(listManufacturer);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url = "http://10.0.2.2:8000/api/manufacturer/get_list?assetID="+ cateId;
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
                        JSONArray jsonArray = object.getJSONArray("ltManufacturer");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString(Constant.ID);
                            String name = jsonObject.getString(Constant.NAME);
                            String logo= "http://10.0.2.2:8000/"+ jsonObject.getString("logo");
                            Manufacturer manufacturer = new Manufacturer(id, name, logo);
                            listManufacturer.add(manufacturer);
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

    @Override
    public void onPause() {
        super.onPause();
        mLayoutFilter.setVisibility(View.GONE);
        mLayoutCate.setVisibility(View.VISIBLE);
        mRecyclerItems.setVisibility(View.VISIBLE);
        if (getActivity() instanceof ComputerStoreActivity){
            ((ComputerStoreActivity) getActivity()).showSearchBar();
        }
    }

    public void moreItemsFromHome(int value, String s){
        mStringFromHomeFragment = s;
        mValueMore = value;
        if (mLayoutFragment != null){
            new GetItemsAsyncTask(this, mIdCate).execute();
            mFilterMoreAdapter.updateSelectMore(value);
        }
    }
}