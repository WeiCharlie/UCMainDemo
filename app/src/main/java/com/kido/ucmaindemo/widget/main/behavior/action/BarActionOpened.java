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

public class BarActionOpened extends AbsBarAction {

    private static final String TAG = "BarActionOpened";

    public BarActionOpened() {
        mDragRate = 1f / 5f;
        mDurationLong = 600;
        mDurationShort = 300;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && canScroll(child, 0) && !isClosed(child);
        Logger.d(TAG, "onStartNestedScroll: nestedScrollAxes=%s, started=%s", nestedScrollAxes, started);
        return started;
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        // consumed the flinging behavior until Closed
        boolean consumed = !isClosed(child);
        Logger.d(TAG, "onNestedPreFling: velocityX=%s, velocityY=%s, consumed=%s", velocityX, velocityY, consumed);
        if (consumed) {
            mWasNestedFlung = true;
            closePager();
        }
        return consumed;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        //dy>0 scroll up;dy<0,scroll down
        float dealDis = dy * mDragRate; // 处理过的dis，为了不那么敏感
        Logger.d(TAG, "onNestedPreScroll-> dy=%s, dealDis=%s", dy, dealDis);
        if (!canScroll(child, dealDis)) {
//            ViewCompat.setTranslationY(child, dealDis > 0 ? getStopTransY(child) : 0);
            dy = 0;
        } else {
            ViewCompat.setTranslationY(child, child.getTranslationY() - dealDis);
        }
        //consumed all scroll behavior after we started Nested Scrolling
        consumed[1] = dy;
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        if (!mWasNestedFlung) {
            if (!isClosed()) {
                handleActionUp(coordinatorLayout, child);
            }
        }
        mWasNestedFlung = false;
    }

    @Override
    public int getStartTransY(View child) {
        return 0;
    }

    @Override
    public int getStopTransY(View child) {
        return AbsActionHelper.getBarOffsetRange(child);
    }

}
