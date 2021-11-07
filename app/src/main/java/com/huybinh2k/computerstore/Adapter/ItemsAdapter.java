package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.activity.DetailsItemActivity;
import com.huybinh2k.computerstore.model.Items;
import java.util.List;

/**
 * Created by BinhBH on 11/4/2021.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemHolder> {

    private List<Items> mListItems;
    private final Context mContext;

    public ItemsAdapter(Context mContext, List<Items> list) {
        this.mContext = mContext;
        mListItems = list;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.items_layout, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Items items = mListItems.get(position);
        holder.textName.setText(items.getName());
        holder.textCost.setText(items.getPrice() + "đ");
        Uri uriImage = Uri.parse(items.getPathImage());
        Glide.with(mContext).load(uriImage).into(holder.imageView);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DetailsItemActivity.class);
            intent.putExtra(Constant.ID, items.getID());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private ImageView imageView;
        private TextView textName;
        private TextView textCost;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
            textName = itemView.findViewById(R.id.txt_name_item);
            textCost = itemView.findViewById(R.id.txt_cost_item);
            this.itemView = itemView;
        }
    }

    public void updateList(List<Items> list){
        mListItems.clear();
        mListItems.addAll(list);
        notifyDataSetChanged();
    }

    public void addDataFromPage(List<Items> list){
        mListItems.addAll(list);
        notifyDataSetChanged();
    }
}
