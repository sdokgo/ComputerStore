package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.fragment.CategoryFragment;
import com.huybinh2k.computerstore.model.CategoryItem;

import java.util.List;

/**
 * Created by BinhBH on 10/17/2021.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    public CategoryAdapter(List<CategoryItem> mListCate, Context context) {
        this.mListCate = mListCate;
        this.mContext = context;
    }

    private int mPosCateSelect = -1;
    private List<CategoryItem> mListCate;
    private final Context mContext;
    private CategoryItem mCateSelect;


    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        CategoryItem categoryItem = mListCate.get(position);
        holder.textName.setText(categoryItem.getNameCategory());
        if (categoryItem.getID().equals("0")){
            holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.ic_all_24));
        }else {
            Uri uriImage = Uri.parse(categoryItem.getUriImage());
            Glide.with(mContext).load(uriImage).into(holder.imageView);
        }
        holder.layout.setOnClickListener(view ->{
            int lastSelect = mPosCateSelect;
            mPosCateSelect = holder.getAdapterPosition();
            mCateSelect = categoryItem;
            sendBroadCastChangeCate(position);
            notifyItemChanged(position);
            notifyItemChanged(lastSelect);
        });
        if(mPosCateSelect == holder.getAdapterPosition()){
            holder.layout.setBackground(mContext.getDrawable(R.drawable.border_cate_select));
        } else {
            holder.layout.setBackground(null);
        }

    }

    private void sendBroadCastChangeCate(int pos) {
        Intent intent = new Intent();
        intent.setAction(CategoryFragment.CHANGE_CATE_SELECT);
        intent.putExtra(CategoryFragment.ID_CATE, mCateSelect.getID());
        intent.putExtra(CategoryFragment.POSITION_CATE, pos);
        mContext.sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return mListCate.size();
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName;
        private RelativeLayout layout;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_category);
            textName = itemView.findViewById(R.id.txt_category);
            layout = itemView.findViewById(R.id.cate_item);
        }
    }

    public void updateListCate(List<CategoryItem> list){
        mListCate.clear();
        mListCate.addAll(list);
        notifyDataSetChanged();
    }

    public CategoryItem getCateSelect() {
        return mCateSelect;
    }

    public void setPositionCateSelect(int pos) {
        int lastPost = mPosCateSelect;
        this.mPosCateSelect = pos;
        if (pos == -1){
            mCateSelect = null;
        }else {
            mCateSelect = mListCate.get(pos);
            notifyItemChanged(pos);
        }
        notifyItemChanged(lastPost);
    }
}
