package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.fragment.CategoryFragment;
import com.huybinh2k.computerstore.model.CategoryItem;

import java.util.List;

/**
 * Created by BinhBH on 11/10/2021.
 */
public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.FilterCategoryHolder> {

    private Context mContext;
    private List<CategoryItem> mList;
    private CategoryItem mCateSelect;

    public FilterCategoryAdapter(Context mContext, List<CategoryItem> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }
    private int mPosCateSelect = -1;

    @NonNull
    @Override
    public FilterCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item, parent, false);
        return new  FilterCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterCategoryHolder holder, int position) {
        CategoryItem categoryItem = mList.get(position);
        holder.textName.setText(categoryItem.getNameCategory());
        holder.itemView.setOnClickListener(view -> {
            int lastSelect = mPosCateSelect;
            mPosCateSelect = holder.getAdapterPosition();
            notifyItemChanged(position);
            mCateSelect = categoryItem;
            if (lastSelect != -1) notifyItemChanged(lastSelect);
            sendBroadCastChangeCateFilter(position);
        });
        if(mPosCateSelect == holder.getAdapterPosition()){
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class FilterCategoryHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName;
        private View itemView;

        public FilterCategoryHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_select_filter);
            textName = itemView.findViewById(R.id.txt_name_filter);
            this.itemView = itemView;
        }
    }

    public void updateListCate(List<CategoryItem> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateSelectCate(int pos){
        if (pos == -1){
            mCateSelect = null;
        }else {
            mCateSelect = mList.get(pos);
        }
        int lastSelect = mPosCateSelect;
        mPosCateSelect = pos;
        if (mPosCateSelect != -1) notifyItemChanged(mPosCateSelect);
        if (lastSelect != -1) notifyItemChanged(lastSelect);
    }

    private void sendBroadCastChangeCateFilter(int pos) {
        Intent intent = new Intent();
        intent.setAction(CategoryFragment.CHANGE_CATE_FILTER);
        intent.putExtra(CategoryFragment.ID_CATE, mCateSelect.getID());
        intent.putExtra(CategoryFragment.POSITION_CATE, pos);
        mContext.sendBroadcast(intent);
    }

    public CategoryItem getCateSelect() {
        return mCateSelect;
    }
}
