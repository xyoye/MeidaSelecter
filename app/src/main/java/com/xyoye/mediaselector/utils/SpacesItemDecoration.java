package com.xyoye.mediaselector.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xyoye on 2019/1/27.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int spanCount;

    public SpacesItemDecoration(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int eachWidth = (spanCount - 1) * space / spanCount;

        int top = 0;
        int left = itemPosition % spanCount * (space - eachWidth);
        int right = eachWidth - left;
        int bottom = space;
        outRect.set(left, top, right, bottom);
    }
}
