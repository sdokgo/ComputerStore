package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.model.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 11/7/2021.
 */
public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyHolder> {

    private Context mContext;
    private List<Property> mList = new ArrayList<>();

    public PropertyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PropertyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_property, parent, false);
        return new PropertyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyHolder holder, int position) {
        Property property = mList.get(position);
        holder.textName.setText(property.getTitle());
        holder.textValue.setText(property.getValue());
        if ((position+1)%2 ==0){
            holder.itemView.setBackgroundColor(mContext.getColor(R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class PropertyHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private TextView textValue;

        public PropertyHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name_property);
            textValue = itemView.findViewById(R.id.text_value_property);
        }
    }

    public void updateProperty(List<Property> list){
        this.mList = list;
        notifyDataSetChanged();
    }
}
