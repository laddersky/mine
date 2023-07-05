package com.example.myapplication;

import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class Fragment_2 extends Fragment {
    private ImageListAdapter adapter;
    private int SIZE = 10;
    private Cursor cursor;
    ActivityResultLauncher<String> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            onPermissionAccepted(null);
        }
        else {
            Toast.makeText(getContext(),  "저장공간 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });

    public Fragment_2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        adapter = new ImageListAdapter(getContext(), 2, true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        RecyclerView gallery = view.findViewById(R.id.gallery);
        gallery.setLayoutManager(layoutManager);
        gallery.addItemDecoration(new ImageItemDecoration());
        gallery.setAdapter(adapter);
        gallery.setOnScrollChangeListener((view1, i, i1, i2, i3) -> {
            if(!view1.canScrollVertically(1)) {
                loadMore();
            }
        });
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(view12 -> {
            String permission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permission = Manifest.permission.READ_MEDIA_IMAGES;
            }
            else {
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
            }
            galleryResultLauncher.launch(permission);
        });

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.setImageList(getInitialImagePaths());
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PermissionViewModel permissionViewModel = new ViewModelProvider(getActivity()).get(PermissionViewModel.class);
        permissionViewModel.getIsGalleryAccepted().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                onPermissionAccepted(view);
            }
        });
    }

    private void onPermissionAccepted(View view) {
        Button button;
        if (view == null) {
            button = getView().findViewById(R.id.button);
        }
        else {
            button = view.findViewById(R.id.button);
        }
        button.setVisibility(View.INVISIBLE);
        adapter.setImageList(getInitialImagePaths());
    }

    private void loadMore() {
        int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int dateAddedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
        int cnt = 0;
        int positionStart = adapter.getItemCount();
        while (cnt < SIZE && cursor.moveToNext()) {
            String imagePath = cursor.getString(dataColumnIndex);
            long time = cursor.getLong(dateAddedColumnIndex);
            adapter.addItem(new ImageItem(imagePath, time));
            cnt++;
            Log.d("path", imagePath);
        }
        adapter.notifyItemRangeInserted(positionStart, SIZE);
    }

    private ArrayList<ImageItem> getInitialImagePaths() {
        ArrayList<ImageItem> imageList = new ArrayList<>();
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED};
        cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID + " DESC");
        int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int dateAddedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED);
        int cnt = 0;
        while (cnt < SIZE && cursor.moveToNext()) {
            String imagePath = cursor.getString(dataColumnIndex);
            long dateAdded = cursor.getLong(dateAddedColumnIndex);
            imageList.add(new ImageItem(imagePath, dateAdded));
            cnt++;
        }
        return imageList;
    }
}