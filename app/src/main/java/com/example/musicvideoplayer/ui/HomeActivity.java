package com.example.musicvideoplayer.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.example.musicvideoplayer.R;
import com.example.musicvideoplayer.ui.adapters.ViewPagerFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    TabLayout tabLayout_HomeActivity;
    ViewPager2 viewPager_HomeActivity;
    ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        tabLayout_HomeActivity = findViewById(R.id.tabLayout_HomeActivity);
        viewPager_HomeActivity = findViewById(R.id.viewPager_HomeActivity);

        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this);
        viewPager_HomeActivity.setAdapter(viewPagerFragmentAdapter);
        viewPager_HomeActivity.setUserInputEnabled(false);
        String[] labels = {getString(R.string.music), getString(R.string.video)};
        new TabLayoutMediator(tabLayout_HomeActivity, viewPager_HomeActivity, (tab, position) -> {
            tab.setText(labels[position]);
        }).attach();

    }
}