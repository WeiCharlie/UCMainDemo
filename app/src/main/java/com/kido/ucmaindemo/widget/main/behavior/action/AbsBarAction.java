package com.kido.ucmaindemo.widget.main.behavior.action;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.OverScroller;

import com.kido.ucmaindemo.utils.Logger;

import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * @author Kido
 * @email everlastxgb@gmail.com
 * @create_time 17/6/4 14:45
 */

public abstract class AbsBarAction {
    private static final String TAG = "AbsBarAction";
    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;

    public static final int DEFAULT_DURATION_SHORT = 300;
    public static final int DEFAULT_DURATION_LONG = 600;

    private static final float DEFAULT_DRAG_RATE = 1f / 5f; // 用于消耗下拉dy

    private static final float DEFAULT_UP_DOWN_DIVIDE = 2f / 5f; // 超过该分割线的话松开是自动合拢

    private int mCurState = STATE_OPENED;
    private HashSet<OnPagerStateListener> mPagerStateListeners = new HashSet<>();

    private OverScroller mOverScroller;

    private WeakReference<CoordinatorLayout> mParent;
    private WeakReference<View> mChild;

    protected boolean mWasNestedFlung;

    protected int mDurationShort = DEFAULT_DURATION_SHORT;
    protected int mDurationLong = DEFAULT_DURATION_LONG;
    protected float mDragRate = DEFAULT_DRAG_RATE;
    protected float mUpDownDivide = DEFAULT_UP_DOWN_DIVIDE;

    public AbsBarAction() {
    }

    public void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        mParent = new WeakReference<CoordinatorLayout>(parent);
        mChild = new WeakReference<View>(child);
    }

    public abstract boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes);

    public abstract boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY);

    public abstract void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed);

    public abstract void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target);

    public abstract int getStartTransY(View child);

    public abstract int getStopTransY(View child);

    public void addPagerStateListener(OnPagerStateListener listener) {
        if (listener != null) {
            mPagerStateListeners.add(listener);
        }
    }

    public void removePagerStateListener(OnPagerStateListener listener) {
        if (listener != null) {
            mPagerStateListeners.remove(listener);
        }
    }

    private void ensureScroller(Context context) {
        if (mOverScroller == null) {
            mOverScroller = new OverScroller(context);
        }
    }

    protected boolean isClosed(View child) {
        boolean isClosed = child.getTranslationY() == getStopTransY(child);
        return isClosed;
    }

    public boolean isClosed() {
        return mCurState == STATE_CLOSED;
    }

    private void changeState(int newState) {
        Logger.d(TAG, "changeState-> newState=%s", newState);
        if (mCurState != newState) {
            mCurState = newState;
            if (mCurState == STATE_OPENED) {
                for (OnPagerStateListener listener : mPagerStateListeners) {
                    listener.onBarOpened();
                }
            } else {
                for (OnPagerStateListener listener : mPagerStateListeners) {
                    listener.onBarClosed();
                }
            }
        }
    }

    protected boolean canScroll(View child, float pendingDy) {
        int pendingTranslationY = (int) (child.getTranslationY() - pendingDy);
        if (pendingTranslationY >= getStopTransY(child) && pendingTranslationY <= getStartTransY(child)) {
            return true;
        }
        return false;
    }

    protected void handleActionUp(CoordinatorLayout parent, final View child) {
        boolean isClosed = isClosed(child);
        Logger.d(TAG, "handleActionUp: isClosed=" + isClosed);
        if (mFlingRunnable != null) {
            child.removeCallbacks(mFlingRunnable);
            mFlingRunnable = null;
        }
        mFlingRunnable = new FlingRunnable(parent, child);
        if (child.getTranslationY() < getStopTransY(child) * mUpDownDivide) {
            mFlingRunnable.scrollToClosed(mDurationLong);
        } else {
            mFlingRunnable.scrollToOpen(mDurationShort);
        }
    }

    private void onFlingFinished(CoordinatorLayout coordinatorLayout, View layout) {
        changeState(isClosed(layout) ? STATE_CLOSED : STATE_OPENED);
    }

    public void openPager() {
        openPager(mDurationLong);
    }

    /**
     * @param duration open animation duration
     */
    public void openPager(int duration) {
        View child = mChild.get();
        CoordinatorLayout parent = mParent.get();
        if (isClosed() && child != null) {
            if (mFlingRunnable != null) {
                child.removeCallbacks(mFlingRunnable);
                mFlingRunnable = null;
            }
            mFlingRunnable = new FlingRunnable(parent, child);
            mFlingRunnable.scrollToOpen(duration);
        }
    }

    public void closePager() {
        closePager(mDurationLong);
    }

    /**
     * @param duration close animation duration
     */
    public void closePager(int duration) {
        View child = mChild.get();
        CoordinatorLayout parent = mParent.get();
        if (!isClosed()) {
            if (mFlingRunnable != null) {
                child.removeCallbacks(mFlingRunnable);
                mFlingRunnable = null;
            }
            mFlingRunnable = new FlingRunnable(parent, child);
            mFlingRunnable.scrollToClosed(duration);
        }
    }


    private FlingRunnable mFlingRunnable;

    /**
     * For animation , Why not use {@link android.view.ViewPropertyAnimator } to play animation is of the
     * other {@link CoordinatorLayout.Behavior} that depend on this could not receiving the correct result of
     * {@link View#getTranslationY()} after animation finished for whatever reason that i don't know
     */
    private class FlingRunnable implements Runnable {
        private final CoordinatorLayout mParent;
        private final View mLayout;

        FlingRunnable(CoordinatorLayout parent, View layout) {
            mParent = parent;
            mLayout = layout;
            ensureScroller(parent.getContext());
        }

        public void scrollToClosed(int duration) {
            float curTranslationY = ViewCompat.getTranslationY(mLayout);

            int startY = (int) curTranslationY;
            int deltaY = getStopTransY(mLayout) - startY;
            Logger.d(TAG, "scrollToClose-> mOriginStopTransY=%s, curTranslationY=%s, startY=%s, deltaY=%s",
                    getStopTransY(mLayout), curTranslationY, startY, deltaY);

            mOverScroller.startScroll(0, startY, 0, deltaY, duration);
            start();
            for (OnPagerStateListener listener : mPagerStateListeners) {
                listener.onBarStartClosing();
            }
        }

        public void scrollToOpen(int duration) {
            float curTranslationY = ViewCompat.getTranslationY(mLayout);
            int startY = (int) curTranslationY;
            int deltaY = (int) getStartTransY(mLayout) - startY;
            Logger.d(TAG, "scrollToOpen-> mOriginStartTransY=%s, curTranslationY=%s, startY=%s, deltaY=%s",
                    getStartTransY(mLayout), curTranslationY, startY, deltaY);
            mOverScroller.startScroll(0, startY, 0, deltaY, duration);
            start();
            for (OnPagerStateListener listener : mPagerStateListeners) {
                listener.onBarStartOpening();
            }
        }

        private void start() {
            if (mOverScroller.computeScrollOffset()) {
                mFlingRunnable = new FlingRunnable(mParent, mLayout);
                ViewCompat.postOnAnimation(mLayout, mFlingRunnable);
            } else {
                onFlingFinished(mParent, mLayout);
            }
        }


        @Override
        public void run() {
            if (mLayout != null && mOverScroller != null) {
                if (mOverScroller.computeScrollOffset()) {
                    Logger.d(TAG, "FlingRunnable run-> mOverScroller.getCurrY()=%s", mOverScroller.getCurrY());
                    ViewCompat.setTranslationY(mLayout, mOverScroller.getCurrY());
                    ViewCompat.postOnAnimation(mLayout, this);
                } else {
                    onFlingFinished(mParent, mLayout);
                }
            }
        }
    }

    /**
     * callback for HeaderPager 's state
     */
    public interface OnPagerStateListener {

        void onBarStartClosing();

        void onBarStartOpening();

        /**
         * do callback when pager closed
         */
        void onBarClosed();

        /**
         * do callback when pager opened
         */
        void onBarOpened();
    }


}
