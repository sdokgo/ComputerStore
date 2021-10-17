package com.huybinh2k.computerstore.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huybinh2k.computerstore.Adapter.CategoryAdapter;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.CategoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class CategoryFragment extends Fragment {

    private boolean mIsExpandCategory = false;
    RecyclerView mRecyclerView;
    CategoryAdapter mCategoryAdapter;
    private RelativeLayout mLayoutExpandCate;
    private ImageView mImageMoreCate;
    private ImageView mImageLessCate;

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
        mRecyclerView = view.findViewById(R.id.recycler_select_cate);
        mLayoutExpandCate = view.findViewById(R.id.layout_expand_cate);
        mImageMoreCate = view.findViewById(R.id.img_more_cate);
        mImageLessCate = view.findViewById(R.id.img_less_cate);

        List<CategoryItem> list = fakeData();
        mCategoryAdapter = new CategoryAdapter(list, getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mCategoryAdapter);

        mImageMoreCate.setOnClickListener(view1 -> expandCate());
        mImageLessCate.setOnClickListener(view1 -> shrinkCate());
    }


    /**
     * BinhBH Hiển thị ra tất category
     */
    private void expandCate(){
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mLayoutExpandCate.setVisibility(View.VISIBLE);
        mImageMoreCate.setVisibility(View.INVISIBLE);
        mRecyclerView.setAdapter(mCategoryAdapter);
    }

    /**
     * BinhBH Thu nhỏ lại danh sách category
     */
    private void shrinkCate(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mCategoryAdapter);
        mLayoutExpandCate.setVisibility(View.GONE);
        mImageMoreCate.setVisibility(View.VISIBLE);
    }

    //TODO BinhBH Cần xử lý lấy dữ liệu khi có API
    private List<CategoryItem> fakeData(){
        List<CategoryItem> list = new ArrayList<>();
        list.add(new CategoryItem("Máy tính", "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        list.add(new CategoryItem("Điện thoại","https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"));
        list.add(new CategoryItem("Máy tính", "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        list.add(new CategoryItem("Điện thoại","https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"));
        list.add(new CategoryItem("Máy tính", "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        list.add(new CategoryItem("Điện thoại","https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"));
        list.add(new CategoryItem("Máy tính", "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        list.add(new CategoryItem("Điện thoại","https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"));
        list.add(new CategoryItem("Máy tính", "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        list.add(new CategoryItem("Điện thoại","https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"));
        list.add(new CategoryItem("Máy tính", "https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        list.add(new CategoryItem("Điện thoại","https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260"));
        return list;
    }
}