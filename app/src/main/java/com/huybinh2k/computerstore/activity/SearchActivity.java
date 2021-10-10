package com.huybinh2k.computerstore.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.database.SuggestionDAO;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener;
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil;
import com.paulrybitskyi.persistentsearchview.utils.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private PersistentSearchView mSearchView;
    private SuggestionDAO mSuggestionDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSuggestionDAO = new SuggestionDAO(this);
        initSearchView();
    }


    private void initSearchView(){
        mSearchView = findViewById(R.id.persistentSearchView);
        mSearchView.expand(true);
        // Setting a delegate for the voice recognition input
        mSearchView.setVoiceRecognitionDelegate(new VoiceRecognitionDelegate(this));

        mSearchView.setOnSearchConfirmedListener((searchView, query) -> {
            mSuggestionDAO.insertSuggest(query);
        });

        mSearchView.setOnSearchQueryChangeListener((searchView, oldQuery, newQuery) -> {
            setSuggestions(newQuery.isEmpty() ?
                    mSuggestionDAO.listAllSuggestion() :
                    mSuggestionDAO.getSuggestionForQuery(newQuery), true);
        });

        mSearchView.setOnSuggestionChangeListener(new OnSuggestionChangeListener() {
            @Override
            public void onSuggestionPicked(SuggestionItem suggestion) {
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
}