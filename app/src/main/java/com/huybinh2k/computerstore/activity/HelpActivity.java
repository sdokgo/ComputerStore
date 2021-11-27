package com.huybinh2k.computerstore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.huybinh2k.computerstore.R;
import com.huybinh2k.computerstore.fragment.AccountFragment;

public class HelpActivity extends AppCompatActivity {

    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btn_back = findViewById(R.id.btn_backHelp);
        btn_back.setOnClickListener(v -> onBackPressed());
    }
}