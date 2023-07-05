package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    ArrayList<ImageItem> imageList;
    Context context;
    int numPerRow;

    public ImageListAdapter(Context context, int numPerRow) {
        this.imageList = new ArrayList<>();
        this.context = context;
        this.numPerRow = numPerRow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem item = imageList.get(position);
        File file = new File(item.getPath());
        if (file.exists()) {
            Picasso.get().load(file).placeholder(R.drawable.placeholder_image).into(holder.imageView);
            holder.imageView.getLayoutParams().height =  context.getResources().getDisplayMetrics().widthPixels / this.numPerRow;
            holder.imageView.setOnClickListener(view -> {
                Dialog builder = new Dialog(context);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                ImageView imageView = new ImageView(context);
                int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                float aspectRatio = (float) width / height;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, Math.round(screenWidth / aspectRatio), false);
                imageView.setImageBitmap(scaledBitmap);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                builder.create();
                builder.setCanceledOnTouchOutside(true);
                builder.show();
            });
        }
    }

    public void addItem(ImageItem item) {
        imageList.add(item);
    }

    public void setImageList(ArrayList<ImageItem> imageList) {
        this.imageList.clear();
        this.imageList.addAll(imageList);
        Log.d("image list adapter", "image list length : " + imageList.size());
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}
