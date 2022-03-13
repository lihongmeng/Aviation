package com.jxntv.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int middleSpace;
    private int bottomSpace;
    private int count;

    public SpaceItemDecoration(int middleSpace, int bottomSpace, int count) {
        this.middleSpace = middleSpace;
        this.bottomSpace = bottomSpace;
        this.count = count;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = bottomSpace;
        if (parent.getChildLayoutPosition(view) % count == 0) {
            outRect.left = 0;
            outRect.right = middleSpace;
        } else {
            outRect.left = middleSpace;
            outRect.right = 0;
        }
    }

}
