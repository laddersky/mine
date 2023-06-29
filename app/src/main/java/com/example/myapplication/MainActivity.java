package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String[] tabs = { "연락처", "갤러리", "자유 주제" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar setting
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("몰입캠프");

        // view pager setting
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(tabs.length);

        // create fragments
        ArrayList<Fragment> fragments = new ArrayList<>();
        ContactFragment contactFragment = new ContactFragment();
        GalleryFragment galleryFragment = new GalleryFragment();
        fragments.add(contactFragment);
        fragments.add(galleryFragment);

        // connect fragments to view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(viewPagerAdapter);

        // connect view pager to tab layout
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setInlineLabel(true);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs[position]);
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.contact);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.baseline_image_24);
                        break;
                }
            }
        }).attach();
    }
}