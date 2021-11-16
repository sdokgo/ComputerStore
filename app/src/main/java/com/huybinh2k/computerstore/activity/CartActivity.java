package com.huybinh2k.computerstore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huybinh2k.computerstore.Adapter.CartAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.model.CartItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView mRecyclerCartItem;
    private CartAdapter mCartAdapter;
    private List<CartItems> mListCart = new ArrayList<>();
    private View mViewCart;
    private TextView mTotalCart, mTotalDiscount, mFinalPrice;
    private NumberFormat mNumberFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initToolbar();
        initView();

    }

    private void initView() {
        mViewCart = findViewById(R.id.layout_cart);
        mViewCart.setVisibility(View.GONE);
        mRecyclerCartItem = findViewById(R.id.recycler_cart_items);
        mCartAdapter = new CartAdapter(this);
        mRecyclerCartItem.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCartItem.setAdapter(mCartAdapter);
        new getCartItemsAsyncTask(this).execute();
        Button button = findViewById(R.id.btnOrder);
        button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(R.string.notification);
            builder.setMessage(R.string.this_feature_in_develop);
            builder.setNegativeButton(android.R.string.ok, (dialogInterface, i) -> {
            });
            builder.show();
        });

        mTotalCart = findViewById(R.id.txt_valuelCart);
        mTotalDiscount = findViewById(R.id.txt_value_discount);
        mFinalPrice = findViewById(R.id.txt_valueTotalPrice);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Giỏ hàng");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private static class getCartItemsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<CartActivity> mWeakReference;
        private boolean mIsSuccess;
        private String token;
        private List<CartItems> list = new ArrayList<>();

        public getCartItemsAsyncTask(CartActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            token = Utils.getStringPreferences(mWeakReference.get(), Constant.TOKEN_LOGIN);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mCartAdapter.updateList(list);
                mWeakReference.get().mViewCart.setVisibility(View.VISIBLE);
                mWeakReference.get().updateTotalPrice(list);
            }else {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/api/cart/get_cart")
                    .method("GET", null)
                    .addHeader("Authorization","Bearer "+ token)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray jsonArray = object.getJSONArray("ltItem");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String idItems = jsonObject.getString(Constant.ID);
                            String name  = jsonObject.getString(Constant.Items.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            int price = jsonObject.getInt(Constant.Items.PRICE);
                            int discountPrice = jsonObject.getInt("promotional_price");
                            int quantityItems = jsonObject.getInt("quanlity");
                            JSONObject objectCart = jsonObject.getJSONObject("pivot");

                            String idCart = objectCart.getString(Constant.ID);
                            int quantityCart = objectCart.getInt("quantity");
                            CartItems cartItems = new CartItems(name, idItems, idCart,price, discountPrice,
                                    quantityCart, quantityItems, img);
                            list.add(cartItems);
                        }
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void updateTotalPrice(List<CartItems> list){
        float cartPrice = 0;
        float discount = 0;
        float finalPrice = 0;
        for (CartItems c :list) {
            cartPrice += c.getPrice()*c.getCartQuantity();
            finalPrice += c.getDiscount()*c.getCartQuantity();
        }
        discount = cartPrice - finalPrice;
        String price = mNumberFormat.format(finalPrice).replaceAll(",",".") + " đ";
        mFinalPrice.setText(price);
        String d = "-"+ mNumberFormat.format(discount).replaceAll(",",".") + " đ";
        mTotalDiscount.setText(d);
        String c = mNumberFormat.format(cartPrice).replaceAll(",",".") + " đ";
        mTotalCart.setText(c);
    }

    public void removeItemInCart(String id){
        new DeleteItemCartAsyncTask(this, id).execute();
        int badgeCount = Utils.getIntPreferences(this, Utils.NUMBER_ITEMS_CART);
        badgeCount--;
        if (badgeCount>=0){
            Utils.saveIntPreferences(this, Utils.NUMBER_ITEMS_CART, badgeCount);
        }
    }

    private static class DeleteItemCartAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<CartActivity> mWeakReference;
        private boolean mIsSuccess;
        private String id;
        private String token;

        public DeleteItemCartAsyncTask(CartActivity activity, String id) {
            mWeakReference = new WeakReference<>(activity);
            this.id = id;
            token = Utils.getStringPreferences(mWeakReference.get(), Constant.TOKEN_LOGIN);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                Toast.makeText(mWeakReference.get(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(mWeakReference.get(), "Lỗi hệ thống, xin thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url = "http://10.0.2.2:8000/api/cart/delete_item_cart";
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String bodyString = "itemId=" + id;
            RequestBody body = RequestBody.create(mediaType, bodyString);
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Authorization","Bearer "+ token)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                }else if (response.code() >= 400){
                    mIsSuccess  = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}