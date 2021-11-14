package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.ItemMore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by BinhBH on 11/12/2021.
 */
public class FilterMoreAdapter extends RecyclerView.Adapter<FilterMoreAdapter.MoreHolder> {
    private Context mContext;
    private List<ItemMore> mList = new ArrayList<>();
    private int mNumMoreSelect = -1;
    private LinkedHashMap<String, ItemMore> mMapSelectMore = new LinkedHashMap<>();
    private LinkedHashMap<String, ItemMore> mMapAllMore = new LinkedHashMap<>();

    public FilterMoreAdapter(Context mContext, List<ItemMore> mList) {
        this.mContext = mContext;
        this.mList = mList;
        for (ItemMore i: mList) {
            mMapAllMore.put(i.getKey(),i);
        }
    }

    @NonNull
    @Override
    public FilterMoreAdapter.MoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item, parent, false);
        return new FilterMoreAdapter.MoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreHolder holder, int position) {
        ItemMore more = mList.get(position);
        holder.textName.setText(more.getDisplayName());
        holder.itemView.setOnClickListener(view -> {
            int lastSelect = mNumMoreSelect;
            mNumMoreSelect = holder.getAdapterPosition();
            notifyItemChanged(position);
            if (!mMapSelectMore.containsKey(more.getKey())){
                mMapSelectMore.put(more.getKey(), more);
            }else {
                mMapSelectMore.remove(more.getKey());
            }
            if (lastSelect != -1) notifyItemChanged(lastSelect);
        });
        if(mMapSelectMore.containsKey(more.getKey())){
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MoreHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName;
        private View itemView;

        public MoreHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_select_filter);
            textName = itemView.findViewById(R.id.txt_name_filter);
            this.itemView = itemView;
        }
    }


    public void clearSelectMore(){
        if (!mMapSelectMore.isEmpty()){
            mMapSelectMore.clear();
            notifyDataSetChanged();
        }
    }

    public LinkedHashMap<String, ItemMore> getMapSelectMore() {
        return mMapSelectMore;
    }

    public void updateSelectMore(int more){
        mMapSelectMore.clear();
        if (more == 1){
            mMapSelectMore.put("isDiscount", mMapAllMore.get("isDiscount"));
        }else if(more == 2){
            mMapSelectMore.put("isNew", mMapAllMore.get("isNew"));
        }
        notifyDataSetChanged();
    }
}
