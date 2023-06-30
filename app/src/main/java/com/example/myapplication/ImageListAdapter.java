package com.example.myapplication;

import static android.widget.RelativeLayout.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ImageItem> imageList;
    Context context;
    final int VIEW_TYPE_ITEM = 0;
    final int VIEW_TYPE_LOADING = 1;


    public ImageListAdapter(Context context) {
        this.imageList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
            return new ItemViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItem((ItemViewHolder) holder, position);
        }
        else if (holder instanceof LoadingViewHolder) {

        }
    }

    public void addItem(ImageItem item) {
        imageList.add(item);
        this.notifyDataSetChanged();
    }

    public void setImageList(ArrayList<ImageItem> imageList) {
        this.imageList.clear();
        this.imageList.addAll(imageList);
        Log.d("image list adapter", "image list length : " + imageList.size());
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return imageList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ItemViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.imageView);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    private void populateItem(ItemViewHolder holder, int position) {
        ImageItem item = imageList.get(position);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        File file = new File(item.getPath());
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            holder.imageView.setImageBitmap(bitmap);
            holder.imageView.getLayoutParams().height = displayMetrics.widthPixels / 2;
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog builder = new Dialog(context);
                    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    builder.getWindow().setBackgroundDrawable(
                            new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //nothing;
                        }
                    });

                    ImageView imageView = new ImageView(context);
                    imageView.setImageBitmap(bitmap);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setAdjustViewBounds(true);
                    builder.addContentView(imageView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    builder.create();
                    builder.setCanceledOnTouchOutside(true);
                    builder.show();
                }
            });
        }
    }
}
