package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class RvItemDecore extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    private int adInterval; // Add the ad interval

    public RvItemDecore(int spanCount, int spacing, boolean includeEdge, int adInterval) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.adInterval = adInterval;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        if (position >= 0) {
            if (isAdPosition(position)) {
                handleAdOffsets(outRect);
            } else {
                handleItemOffsets(outRect);
            }
        }
    }

    private boolean isAdPosition(int position) {
        return (position + 1) % (adInterval + 1) == 0; // Assuming ad is after every 'adInterval' items
    }

    private void handleAdOffsets(Rect outRect) {
        outRect.set(0, 0, 0, 0); // Set no spacing for ads, adjust as needed
    }

    private void handleItemOffsets(Rect outRect) {
        outRect.top = spacing;
        outRect.left = spacing;
        outRect.right = spacing;
    }
}