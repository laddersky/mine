package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setTitle("몰입캠프");

        // ViewPager 설정
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3); //페이지 유지 개수

        // tabLayout에 ViewPager 연결
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.BLACK));

        // Fragment 생성
        Fragment_1 firstFragment = new Fragment_1();
        Fragment_2 secondFragment = new Fragment_2();
        Fragment_3 thirdFragment = new Fragment_3();

        // ViewPagerAdapter를 이용하여 Fragment 연결
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(firstFragment, "연락처");
        viewPagerAdapter.addFragment(secondFragment, "갤러리");
        viewPagerAdapter.addFragment(thirdFragment, "캘린더");
        viewPager.setAdapter(viewPagerAdapter);

        // tabLayout에 아이콘 설정 부분
        tabLayout.getTabAt(0).setIcon(R.drawable.phone_number);
        tabLayout.getTabAt(1).setIcon(R.drawable.gallery);
        tabLayout.getTabAt(2).setIcon(R.drawable.calendar);


        // 세번째 화면에 Badge 달기
        //BadgeDrawable badgeDrawable = tabLayout.getTabAt(2).getOrCreateBadge();
        //badgeDrawable.setVisible(true);
        //badgeDrawable.setNumber(7);
    }
}