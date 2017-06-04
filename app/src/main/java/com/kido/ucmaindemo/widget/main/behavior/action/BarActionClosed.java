package com.kido.ucmaindemo.widget.main.behavior.action;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.kido.ucmaindemo.utils.Logger;

/**
 * @author Kido
 * @email everlastxgb@gmail.com
 * @create_time 17/6/4 14:45
 */

public class BarActionClosed extends BarActionOpened {

    private static final String TAG = "BarActionClosed";

    public BarActionClosed() {
        mDragRate = 1f;
        mDurationLong = 100;
        mDurationShort = 100;
        mUpDownDivide = 1f / 2f;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && canScroll(child, 0);
        Logger.d(TAG, "onStartNestedScroll: nestedScrollAxes=%s, started=%s", nestedScrollAxes, started);
        return started;
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        return false;
    }


    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
//        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public int getStopTransY(View child) {
        return AbsActionHelper.getBarOffsetRange(child) + -AbsActionHelper.getHeaderHeight(child);
    }

    @Override
    public int getStartTransY(View child) {
        return AbsActionHelper.getBarOffsetRange(child);
    }

}
