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
import com.huybinh2k.computerstore.model.CategoryItem;
import com.huybinh2k.computerstore.model.Manufacturer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by BinhBH on 11/10/2021.
 */
public class FilterManufacturerAdapter extends RecyclerView.Adapter<FilterManufacturerAdapter.FilterManuHolder> {

    private Context mContext;
    private List<Manufacturer> mList;
    private LinkedHashMap<String,Manufacturer> mMapSelectManu = new LinkedHashMap<>();

    public FilterManufacturerAdapter(Context mContext, List<Manufacturer> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }
    private int mPosCateSelect = -1;

    @NonNull
    @Override
    public FilterManufacturerAdapter.FilterManuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item, parent, false);
        return new FilterManuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterManuHolder holder, int position) {
        Manufacturer manufacturer = mList.get(position);
        holder.textName.setText(manufacturer.getName());
        holder.itemView.setOnClickListener(view -> {
            mPosCateSelect = holder.getAdapterPosition();
            notifyItemChanged(position);
            if (!mMapSelectManu.containsKey(manufacturer.getId())){
                mMapSelectManu.put(manufacturer.getId(), manufacturer);
            }else {
                mMapSelectManu.remove(manufacturer.getId());
            }
        });
        if(mMapSelectManu.containsKey(manufacturer.getId())){
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class FilterManuHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName;
        private View itemView;

        public FilterManuHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_select_filter);
            textName = itemView.findViewById(R.id.txt_name_filter);
            this.itemView = itemView;
        }
    }

    public void updateListManu(List<Manufacturer> list){
        mList.clear();
        mList.addAll(list);
        mMapSelectManu.clear();
        notifyDataSetChanged();
    }

    public void clearSelectManu(){
        if (!mMapSelectManu.isEmpty()){
            mMapSelectManu.clear();
            notifyDataSetChanged();
        }
    }

    public LinkedHashMap<String, Manufacturer> getMapSelectManu() {
        return mMapSelectManu;
    }
}
