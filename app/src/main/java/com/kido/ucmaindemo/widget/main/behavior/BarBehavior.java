package com.kido.ucmaindemo.widget.main.behavior;


import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.kido.ucmaindemo.widget.main.behavior.action.AbsBarAction;
import com.kido.ucmaindemo.widget.main.behavior.action.BarActionClosed;
import com.kido.ucmaindemo.widget.main.behavior.action.BarActionOpened;
import com.kido.ucmaindemo.widget.main.helper.ViewOffsetBehavior;

/**
 * Behavior for Bar.
 *
 * @author Kido
 */

public class BarBehavior extends ViewOffsetBehavior {
    private static final String TAG = "UNBL_Behavior";

    private AbsBarAction mActionOpened, mActionClosed;

    private AbsBarAction.OnPagerStateListener mTempListener;

    public void addPagerStateListener(AbsBarAction.OnPagerStateListener pagerStateListener) {
        mActionOpened.addPagerStateListener(pagerStateListener);
    }

    public BarBehavior() {
        init();
    }

    public BarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mActionOpened = new BarActionOpened();
        mActionClosed = new BarActionClosed();
    }

    private AbsBarAction getAction() {
        return mActionOpened.isClosed() ? mActionClosed : mActionOpened;
    }

    @Override
    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        super.layoutChild(parent, child, layoutDirection);
        getAction().layoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return getAction().onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }


    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        return getAction().onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        getAction().onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
        getAction().onStopNestedScroll(coordinatorLayout, child, target);
    }


    public boolean isClosed() {
        return mActionOpened.isClosed();
    }

    public void open() {
        if (isClosed()) {
            mTempListener = new AbsBarAction.OnPagerStateListener() {
                @Override
                public void onBarStartClosing() {
                }

                @Override
                public void onBarStartOpening() {
                }

                @Override
                public void onBarClosed() {
                }

                @Override
                public void onBarOpened() {
                    mActionOpened.openPager();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mActionClosed.removePagerStateListener(mTempListener);
                        }
                    });
                }
            };
            mActionClosed.addPagerStateListener(mTempListener);
            mActionClosed.openPager();
        } else {
            mActionOpened.openPager();
        }
    }

    public void close() {
        if (isClosed()) {
        } else {
            mActionOpened.closePager();
        }
    }

}