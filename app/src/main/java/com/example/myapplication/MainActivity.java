package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Fragment_1 firstFragment;
    Fragment_2 secondFragment;
    Fragment_3 thirdFragment;
    PermissionViewModel permissionViewModel;
    String[] permissions = {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_CALL_LOG
    };
    ActivityResultLauncher<String[]> permissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        Boolean galleryAccepted = result.get(permissions[0]);
        if (galleryAccepted != null && galleryAccepted) {
            permissionViewModel.getIsGalleryAccepted().postValue(true);
        }
        Boolean calendarAccepted = result.get(permissions[1]);
        if (calendarAccepted != null && calendarAccepted) {
            permissionViewModel.getIsCalendarAccepted().postValue(true);
        }
        Boolean callLogAccepted = result.get(permissions[2]);
        if (callLogAccepted != null && callLogAccepted) {
            permissionViewModel.getIsCallLogAccepted().postValue(true);
        }
    });
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionViewModel = new ViewModelProvider(this).get(PermissionViewModel.class);

        // toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("몰입캠프");

        // ViewPager 설정
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3); //페이지 유지 개수

        // tabLayout에 ViewPager 연결
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Fragment 생성
        firstFragment = new Fragment_1();
        secondFragment = new Fragment_2();
        thirdFragment = new Fragment_3();

        // ViewPagerAdapter를 이용하여 Fragment 연결
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(firstFragment, "연락처");
        viewPagerAdapter.addFragment(secondFragment, "갤러리");
        viewPagerAdapter.addFragment(thirdFragment, "캘린더");
        viewPager.setAdapter(viewPagerAdapter);

        // tabLayout에 아이콘 설정 부분
        tabLayout.getTabAt(0).setIcon(R.drawable.contact);
        tabLayout.getTabAt(1).setIcon(R.drawable.gallery);
        tabLayout.getTabAt(2).setIcon(R.drawable.calendar);


        // 세번째 화면에 Badge 달기
        //BadgeDrawable badgeDrawable = tabLayout.getTabAt(2).getOrCreateBadge();
        //badgeDrawable.setVisible(true);
        //badgeDrawable.setNumber(7);
        checkPermissions();
    }
    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter instanceof ViewPagerAdapter) {
            ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) adapter;
            Fragment currentFragment = viewPagerAdapter.getItem(currentItem);
            if (currentFragment instanceof Fragment_1) {
                Fragment_1 fragment1 = (Fragment_1) currentFragment;
                View fragmentView = fragment1.getView();
                if (fragmentView != null) {
                    SearchView searchView = fragmentView.findViewById(R.id.search_view);
                    if (searchView.getVisibility() == View.VISIBLE){
                        searchView.setQuery("", false);
                        searchView.setVisibility(View.INVISIBLE);
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) searchView.getLayoutParams();
                        layoutParams.height = 0;
                        searchView.setLayoutParams(layoutParams);
                        return;
                    }
                    // searchView를 사용하여 활성화 여부 판단 및 로직 수행
                }
            }
        }
        super.onBackPressed();
    }

    private void checkPermissions() {
        List<String> requestPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_GRANTED) {
                Log.d("main activity", "permission accepted " + i);
                onPermissionAccepted(i);
            }
            else {
                onPermissionDenied(i);
                requestPermissions.add(permissions[i]);
            }
        }
        permissionsLauncher.launch(requestPermissions.toArray(new String[requestPermissions.size()]));
    }

    private void onPermissionAccepted(int index) {
        switch (index) {
            case 0: permissionViewModel.getIsGalleryAccepted().postValue(true);
            case 1: permissionViewModel.getIsCalendarAccepted().postValue(true);
            case 2: permissionViewModel.getIsCallLogAccepted().postValue(true);
        }
    }
    private void onPermissionDenied(int index) {
        switch (index) {
            case 0: permissionViewModel.getIsGalleryAccepted().postValue(false);
            case 1: permissionViewModel.getIsCalendarAccepted().postValue(false);
            case 2: permissionViewModel.getIsCallLogAccepted().postValue(false);
        }
    }
}