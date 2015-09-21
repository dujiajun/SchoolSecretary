package com.dujiajun.schoolsecretary.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dujiajun.schoolsecretary.R;
import com.dujiajun.schoolsecretary.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private FragmentManager fm;
    private FragmentTransaction transaction;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.s_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fm = getFragmentManager();
        transaction = fm.beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        transaction.add(R.id.settings_container,fragment);
        transaction.commit();
    }
}
