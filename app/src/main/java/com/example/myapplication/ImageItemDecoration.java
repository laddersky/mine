package com.example.myapplication;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;
    private int numColumns;
    public ImageItemDecoration() {
        this.spacing = 32;
        this.numColumns = 2;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int column = position % this.numColumns;
        if (position >= this.numColumns) {
            outRect.top = spacing;
        }
        if (column == 0) {
            outRect.right = spacing - (column + 1) * spacing / this.numColumns;
        }
        else {
            outRect.left = column * spacing / this.numColumns;
        }
    }
}
