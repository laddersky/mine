package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Fragment_2 extends Fragment {
    private ImageListAdapter adapter;
    private int SIZE = 10;
    private Cursor cursor;
    ActivityResultLauncher<String> galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                onPermissionAccepted(null);
            }
            else {
                Toast.makeText(getContext(),  "설정에서 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    });

    public Fragment_2() {
        Log.d("fragment2", "created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        adapter = new ImageListAdapter(getContext());
        checkPermission(view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        RecyclerView gallery = (RecyclerView)view.findViewById(R.id.gallery);
        gallery.setLayoutManager(layoutManager);
        gallery.addItemDecoration(new ImageItemDecoration());
        gallery.setAdapter(adapter);
        gallery.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if(!view.canScrollVertically(1)) {
                    loadMore();
                }
            }
        });
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });



        return view;
    }

    private void checkPermission(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            onPermissionAccepted(view);
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "저장공간 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            galleryResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void onPermissionAccepted(View view) {
        Button button;
        //TextView textView;
        if (view == null) {
            button = (Button) getView().findViewById(R.id.button);
        }
        else {
            button = (Button) view.findViewById(R.id.button);
        }
        button.setVisibility(View.INVISIBLE);
        setImageListToAdapter(getInitialImagePaths());
    }

    private void loadMore() {
        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int cnt = 0;
        int positionStart = adapter.getItemCount();
        while (cursor.moveToNext() && cnt < SIZE) {
            String imagePath = cursor.getString(columnIndexData);
            adapter.addItem(new ImageItem(imagePath));
            cnt++;
            Log.d("path", imagePath);
        }
        adapter.notifyItemRangeInserted(positionStart, SIZE);
    }

    private ArrayList<ImageItem> getInitialImagePaths() {
        ArrayList<ImageItem> imageList = new ArrayList<>();
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN};
        cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int cnt = 0;
        while (cursor.moveToNext() && cnt < SIZE) {
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
}