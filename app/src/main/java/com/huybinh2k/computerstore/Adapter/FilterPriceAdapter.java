package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.fragment.CategoryFragment;
import com.huybinh2k.computerstore.model.FilterPrice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 11/11/2021.
 */
public class FilterPriceAdapter extends RecyclerView.Adapter<FilterPriceAdapter.PriceHolder> {

    private final CategoryFragment mContext;
    private List<FilterPrice> mList = new ArrayList<>();
    private int mNumPriceSelect = -1;
    private FilterPrice mPriceSelect;

    public FilterPriceAdapter(CategoryFragment mContext, List<FilterPrice> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public PriceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext.getContext()).inflate(R.layout.filter_item, parent, false);
        return new PriceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceHolder holder, int position) {
        FilterPrice itemStatus = mList.get(position);
        holder.textName.setText(itemStatus.getName());
        holder.itemView.setOnClickListener(view -> {
            mPriceSelect = itemStatus;
            int lastSelect = mNumPriceSelect;
            mNumPriceSelect = holder.getAdapterPosition();
            notifyItemChanged(position);
            if (lastSelect != -1) notifyItemChanged(lastSelect);
            mContext.clearFilterPrice();
        });
        if(mNumPriceSelect == holder.getAdapterPosition()){
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class PriceHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName;
        private View itemView;

        public PriceHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_select_filter);
            textName = itemView.findViewById(R.id.txt_name_filter);
            this.itemView = itemView;
        }
    }

    public void updateSelectPrice(int pos){
        if (pos == -1){
            mPriceSelect = null;
        }
        int lastSelect = mNumPriceSelect;
        mNumPriceSelect = pos;
        if (mNumPriceSelect != -1) notifyItemChanged(mNumPriceSelect);
        if (lastSelect != -1) notifyItemChanged(lastSelect);
    }

    public FilterPrice getPriceSelect() {
        return mPriceSelect;
    }
}
