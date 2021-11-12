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
import com.huybinh2k.computerstore.model.ItemStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 11/11/2021.
 */
public class FilterStatusAdapter extends RecyclerView.Adapter<FilterStatusAdapter.StatusHolder> {

    private Context mContext;
    private List<ItemStatus> mList = new ArrayList<>();
    private int mStatusSelect = -1;
    private ItemStatus mItemsStatusSelect;

    public FilterStatusAdapter(Context mContext, List<ItemStatus> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.filter_item, parent, false);
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {
        ItemStatus itemStatus = mList.get(position);
        holder.textName.setText(itemStatus.getName());
        holder.itemView.setOnClickListener(view -> {
            mItemsStatusSelect = itemStatus;
            int lastSelect = mStatusSelect;
            mStatusSelect = holder.getAdapterPosition();
            notifyItemChanged(position);
            if (lastSelect != -1) notifyItemChanged(lastSelect);
        });
        if(mStatusSelect == holder.getAdapterPosition()){
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class StatusHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textName;
        private View itemView;

        public StatusHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_select_filter);
            textName = itemView.findViewById(R.id.txt_name_filter);
            this.itemView = itemView;
        }
    }

    public void updateSelectStatus(int pos){
        if (pos == -1){
            mItemsStatusSelect = null;
        }
        int lastSelect = mStatusSelect;
        mStatusSelect = pos;
        if (mStatusSelect != -1) notifyItemChanged(mStatusSelect);
        if (lastSelect != -1) notifyItemChanged(lastSelect);
    }

    public ItemStatus getItemsStatusSelect() {
        return mItemsStatusSelect;
    }
}
