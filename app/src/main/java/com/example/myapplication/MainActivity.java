/*package com.example.myapplication;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextNumber;

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_editor);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_CONTACTS},
                PackageManager.PERMISSION_GRANTED);
        editTextName = findViewById(R.id.name_edit_text);
        editTextNumber = findViewById(R.id.number_edit_text);
        //btn = findViewById(R.id.save_button);

    }
    public void buttonUpdateContact(View view){

        ArrayList<ContentProviderOperation> contentProviderOperations
                = new ArrayList<ContentProviderOperation>();

        contentProviderOperations.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?",
                        new String[]{editTextName.getText().toString(),
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                        editTextNumber.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY,contentProviderOperations);
            Toast.makeText(this,"success",Toast.LENGTH_LONG).show();

        }
        catch (OperationApplicationException e){
            e.printStackTrace();
        }
        catch (RemoteException e){
            e.printStackTrace();
        }
    }
}
*/

package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Fragment_1 firstFragment;
    private Fragment_2 secondFragment;
    private Fragment_3 thirdFragment;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar 설정
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("몰입캠프");
        toolbar.setTitleTextColor(  Color.BLACK);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("몰입캠프");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // ViewPager 설정
        viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3); //페이지 유지 개수

        // tabLayout에 ViewPager 연결
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.BLACK));
        //tabLayout.setInlineLabelResource(R.drawable.mollibcamp);
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
        tabLayout.getTabAt(0).setIcon(R.drawable.phone_number);
        tabLayout.getTabAt(1).setIcon(R.drawable.gallery);
        tabLayout.getTabAt(2).setIcon(R.drawable.calendar);


        // 세번째 화면에 Badge 달기
        //BadgeDrawable badgeDrawable = tabLayout.getTabAt(2).getOrCreateBadge();
        //badgeDrawable.setVisible(true);
        //badgeDrawable.setNumber(7);
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
                    //이부분 추가
                    LinearLayout bottom_bar = fragmentView.findViewById(R.id.bottom_bar);
                    if (bottom_bar.getVisibility() == View.VISIBLE){
                        bottom_bar.setVisibility(View.INVISIBLE);

                        fragment1.adapter.display = false;
                        fragment1.add_contact.setVisibility(View.VISIBLE);
                        fragment1.adapter.delete_list.clear();
                        fragment1.adapter.notifyDataSetChanged();
                        return;
                    }
                    //여기까지
                    // searchView를 사용하여 활성화 여부 판단 및 로직 수행
                }
            }
        }
        super.onBackPressed();
    }


}