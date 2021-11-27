package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.huybinh2k.computerstore.Adapter.ItemsAdapter;
import com.huybinh2k.computerstore.Adapter.SuggestionAdapter;
import com.huybinh2k.computerstore.Constant;
import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.Utils;
import com.huybinh2k.computerstore.database.SuggestionDAO;
import com.huybinh2k.computerstore.model.Items;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener;
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil;
import com.paulrybitskyi.persistentsearchview.utils.VoiceRecognitionDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private PersistentSearchView mSearchView;
    private SuggestionDAO mSuggestionDAO;
    private GridView mGridViewSuggest;
    private RecyclerView mRecyclerItems;
    private ItemsAdapter mItemsAdapter;
    List<Items> mListItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSuggestionDAO = new SuggestionDAO(this);
        fakeDataSuggest();
        initSearchView();
        initView();
    }

    private void initView() {
        mRecyclerItems = findViewById(R.id.recycler_items);
        mItemsAdapter = new ItemsAdapter(this, mListItems);
        mRecyclerItems.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerItems.setAdapter(mItemsAdapter);
    }

    //TODO BinhBH Cần sửa lại data khi có API
    private void fakeDataSuggest() {
        List<String> list = new ArrayList<>();
        list.add("Laptop");
        list.add("Chuột");
        list.add("Màn Hình");
        list.add("Case");
        list.add("CPU");

        SuggestionAdapter suggestionAdapter = new SuggestionAdapter(list);
        mGridViewSuggest = findViewById(R.id.grid_suggest);
        mGridViewSuggest.setAdapter(suggestionAdapter);
        mGridViewSuggest.setOnItemClickListener((a, v, position, id) -> {
            String s = (String) mGridViewSuggest.getItemAtPosition(position);
            new GetItemsAsyncTask(SearchActivity.this, s).execute();
        });
    }


    private void initSearchView(){
        mSearchView = findViewById(R.id.persistentSearchView);
        mSearchView.expand(true);
        // Setting a delegate for the voice recognition input
        mSearchView.setVoiceRecognitionDelegate(new VoiceRecognitionDelegate(this));

        mSearchView.setOnSearchConfirmedListener((searchView, query) -> {
            if (!Utils.isConnectedInternet(this)){
                Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!query.isEmpty()){
                mSuggestionDAO.insertSuggest(query);
                new GetItemsAsyncTask(this, query).execute();
            }
            mSearchView.collapse(true, true);
        });

        mSearchView.setOnSearchQueryChangeListener((searchView, oldQuery, newQuery) -> {
            setSuggestions(newQuery.isEmpty() ?
                    mSuggestionDAO.listAllSuggestion() :
                    mSuggestionDAO.getSuggestionForQuery(newQuery), true);
        });

        mSearchView.setOnSuggestionChangeListener(new OnSuggestionChangeListener() {
            @Override
            public void onSuggestionPicked(SuggestionItem suggestion) {
                if (!Utils.isConnectedInternet(SearchActivity.this)){
                    Toast.makeText(getApplicationContext(), "Không có kết nối tới internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                new GetItemsAsyncTask(SearchActivity.this, suggestion.getItemModel().getText()).execute();
            }
            @Override
            public void onSuggestionRemoved(SuggestionItem suggestion) {
                mSuggestionDAO.deleteSuggest(suggestion.getItemModel().getText());
            }
        });
    }

    List<String> searchQueries = new ArrayList<>();
    @Override
    public void onResume() {
        super.onResume();
        searchQueries = mSuggestionDAO.listAllSuggestion();
        setSuggestions(searchQueries, false);
    }

    private void setSuggestions(List<String> list, boolean expandIfNecessary) {
        // Converting them to recent suggestions and setting them to the widget
        mSearchView.setSuggestions(SuggestionCreationUtil.asRecentSearchSuggestions(list), expandIfNecessary);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Calling the voice recognition delegate to properly handle voice input results
        VoiceRecognitionDelegate.handleResult(mSearchView, requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isExpanded()){
            mSearchView.collapse();
            return;
        }
        super.onBackPressed();
    }

    private static class GetItemsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<SearchActivity> mWeakReference;
        private boolean mIsSuccess;
        private String stringSearch;
        private List<Items> list = new ArrayList<>();

        public GetItemsAsyncTask(SearchActivity activity, String search) {
            mWeakReference = new WeakReference<>(activity);
            stringSearch = search;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (mIsSuccess){
                mWeakReference.get().mItemsAdapter.updateList(list);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            String url = "http://10.0.2.2:8000/api/item/get_list_search?textSearch=" +
                    stringSearch + "&pageSize=15&page=1&assetID=0";
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() >= 200 && response.code() < 300){
                    mIsSuccess = true;
                    try {
                        JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray jsonArray = object.getJSONObject("ltItem").getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString(Constant.ID);
                            String name  = jsonObject.getString(Constant.Item.NAME);
                            String img = "http://10.0.2.2:8000/"+ jsonObject.getString(Constant.IMAGE);
                            double price = jsonObject.getDouble(Constant.Item.PRICE);
                            double discountPrice = jsonObject.getDouble("promotional_price");
                            Items items = new Items(id, name, img, price, discountPrice);

                            list.add(items);
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
}