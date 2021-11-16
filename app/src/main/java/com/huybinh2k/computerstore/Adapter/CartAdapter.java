package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.activity.CartActivity;
import com.huybinh2k.computerstore.activity.LoginActivity;
import com.huybinh2k.computerstore.model.CartItems;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPickerListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 11/14/2021.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private CartActivity mContext;
    private List<CartItems> mList = new ArrayList<>();
    private NumberFormat numberFormat = new DecimalFormat("#,###");

    public CartAdapter(CartActivity mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_in_cart, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        CartItems cartItems = mList.get(position);
        holder.name.setText(cartItems.getName());
        holder.id.setText(cartItems.getIdItems());
        Glide.with(mContext).load(cartItems.getImage()).into(holder.imageView);
        float percentDiscount = 1 - cartItems.getDiscount() / cartItems.getPrice();
        if (percentDiscount != 0) {
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            String discount = numberFormat.format(cartItems.getPrice()).replaceAll(",",".") + " đ";
            holder.discount.setText(discount);
        } else {
            holder.discount.setVisibility(View.GONE);
        }
        String price = numberFormat.format(cartItems.getDiscount()).replaceAll(",",".") + " đ";
        holder.price.setText(price);
        holder.numberPicker.setValue(cartItems.getCartQuantity());
        holder.numberPicker.setMaxValue(cartItems.getItemQuantity());
        holder.delete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle(R.string.notification);
            builder.setMessage("Bạn có muốn xóa sản phẩm " + cartItems.getName() +" khỏi giỏ hàng?");
            builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
            });
            builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                builder.show();
                mList.remove(cartItems);
                mContext.updateTotalPrice(mList);
                mContext.removeItemInCart(cartItems.getIdCarts());
                notifyItemRemoved(position);
            });
            builder.show();
        });
        holder.numberPicker.setListener(value -> {
            cartItems.setCartQuantity(value);
            mContext.updateTotalPrice(mList);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class CartHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView id;
        private TextView price;
        private TextView discount;
        private ImageView imageView;
        private ImageButton delete;
        private ScrollableNumberPicker numberPicker;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_NameProduct);
            id = itemView.findViewById(R.id.txt_value_id_product);
            price = itemView.findViewById(R.id.txt_Price);
            discount = itemView.findViewById(R.id.txt_discount);
            imageView = itemView.findViewById(R.id.image_product);
            delete = itemView.findViewById(R.id.btn_DeleteProduct);
            numberPicker = itemView.findViewById(R.id.number_of_product);
        }
    }

    public void updateList(List<CartItems> list){
        mList = list;
        notifyDataSetChanged();
    }

    public List<CartItems> getListCart() {
        return mList;
    }


}
