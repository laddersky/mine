package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CirclePagerIndicatorDecoration extends RecyclerView.ItemDecoration {
    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    private final int indicatorHeight = (int) (DP * 16);
    private final float indicatorStrokeWidth = DP * 4;
    private final float indicatorItemLength = DP * 4;
    private final float indicatorItemPadding = DP * 8;
    private final Paint paint = new Paint();
    Context context;

    public CirclePagerIndicatorDecoration(Context context) {
        this.context = context;
        paint.setStrokeWidth(indicatorStrokeWidth);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int itemCount = parent.getAdapter().getItemCount();
        if (itemCount == 1) return;
        float totalLength = indicatorItemLength * itemCount;
        float paddingBetweenItems = Math.max(0, itemCount - 1) * indicatorItemPadding;
        float indicatorTotalWidth = totalLength + paddingBetweenItems;
        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;
        float indicatorPosY = parent.getHeight() - indicatorHeight / 2F;


        LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
        int activePosition = manager.findFirstVisibleItemPosition();
        drawIndicators(c, indicatorStartX, indicatorPosY, itemCount, activePosition);
    }

    private void drawIndicators(Canvas c, float indicatorStartX, float indicatorPosY, int itemCount, int position) {
        final float itemWidth = indicatorItemLength + indicatorItemPadding;
        float start = indicatorStartX;
        int colorInactive = Color.GRAY;
        int colorActive = context.getColor(R.color.colorPrimary);
        for (int i = 0; i < itemCount; i++) {
            if (i == position) {
                paint.setColor(colorActive);
            }
            else {

                paint.setColor(colorInactive);
            }
            c.drawCircle(start, indicatorPosY, indicatorItemLength / 2F, paint);
            start += itemWidth;
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = indicatorHeight;
    }
}
