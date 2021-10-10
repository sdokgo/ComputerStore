package com.huybinh2k.computerstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huybinh2k.computerstore.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinhBH on 10/10/2021.
 */
public class SuggestionAdapter extends BaseAdapter {

    public SuggestionAdapter(List<String> mListSuggest) {
        this.mListSuggest = mListSuggest;
    }

    private List<String> mListSuggest = new ArrayList<>();

    @Override
    public int getCount() {
        return mListSuggest.size();
    }

    @Override
    public Object getItem(int i) {
        return mListSuggest.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_suggestion, null);
        }
        TextView textSuggestion = view.findViewById(R.id.txt_suggestion);
        textSuggestion.setText(mListSuggest.get(i));
        return view;
    }
}
