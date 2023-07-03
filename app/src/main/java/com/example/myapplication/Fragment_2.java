package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            Toast.makeText(getContext(),  "설정에서 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
        }
    });

    public Fragment_2() {
        Log.d("fragment2", "created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // refreshGallery();
        View view = inflater.inflate(R.layout.fragment2, container, false);
        adapter = new ImageListAdapter(getContext(), 2);
        checkPermission(view);
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



        return view;
    }

    private void checkPermission(View view) {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        }
        else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), permission)
        == PackageManager.PERMISSION_GRANTED) {
            onPermissionAccepted(view);
        }
        else if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(getContext(), "저장공간 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            galleryResultLauncher.launch(permission);
        }
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
        setImageListToAdapter(getInitialImagePaths());
    }

    private void loadMore() {
        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int cnt = 0;
        int positionStart = adapter.getItemCount();
        while (cnt < SIZE && cursor.moveToNext()) {
            String imagePath = cursor.getString(columnIndexData);
            adapter.addItem(new ImageItem(imagePath));
            cnt++;
            Log.d("path", imagePath);
        }
        adapter.notifyItemRangeInserted(positionStart, SIZE);
    }

    private ArrayList<ImageItem> getInitialImagePaths() {
        ArrayList<ImageItem> imageList = new ArrayList<>();
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID};
        cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID + " DESC");
        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int cnt = 0;
        while (cnt < SIZE && cursor.moveToNext()) {
            String imagePath = cursor.getString(columnIndexData);
            imageList.add(new ImageItem(imagePath));
            cnt++;
            // Log.d("path", imagePath);
        }
        return imageList;
    }

    private void setImageListToAdapter(ArrayList<ImageItem> imageList) {
        adapter.setImageList(imageList);
    }

    void refreshGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        getContext().sendBroadcast(mediaScanIntent);
    }
}