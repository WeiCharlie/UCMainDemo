package com.kido.ucmaindemo.widget.main;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;
import com.kido.ucmaindemo.widget.main.helper.ViewOffsetBehavior;

import java.lang.ref.WeakReference;

/**
 * 新闻列表的顶部Layout
 *
 * @author Kido
 */
@CoordinatorLayout.DefaultBehavior(UcNewsHeaderLayout.Behavior.class)
public class UcNewsHeaderLayout extends FrameLayout {

    private UcNewsHeaderLayout.Behavior mBehavior;
    private OnHeaderStateListener mHeaderStateListener;
    private Context mContext;

    private static int sHeaderOffsetRange;

    public UcNewsHeaderLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public UcNewsHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public UcNewsHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ensureBehavior();
    }

    private void init(Context context) {// TODO: 2017/5/27 取值待优化
        mContext = context;
        sHeaderOffsetRange = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
    }

    private void ensureBehavior() {
        if (mBehavior == null) {
            if (getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams coParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
                if (coParams.getBehavior() instanceof UcNewsHeaderLayout.Behavior) {
                    mBehavior = (UcNewsHeaderLayout.Behavior) coParams.getBehavior();
                    mBehavior.setPagerStateListener(mHeaderStateListener);
                }
            }
        }
    }

    public void setHeaderStateListener(OnHeaderStateListener listener) {
        mHeaderStateListener = listener;
        if (mBehavior != null) {
            mBehavior.setPagerStateListener(mHeaderStateListener);
        }
    }

    public void openHeader() {
        ensureBehavior();
        mBehavior.openPager();
    }

    public void closeHeader() {
        ensureBehavior();
        mBehavior.closePager();
    }

    public boolean isClosed() {
        ensureBehavior();
        return mBehavior.isClosed();
    }

    /**
     * callback for HeaderPager 's state
     */
    public interface OnHeaderStateListener extends Behavior.OnPagerStateListener {
    }

    public static class Behavior extends ViewOffsetBehavior {
        private static final String TAG = "UcNewsHeaderPager";
        public static final int STATE_OPENED = 0;
        public static final int STATE_CLOSED = 1;
        public static final int DURATION_SHORT = 300;
        public static final int DURATION_LONG = 600;

        private int mCurState = STATE_OPENED;
        private OnPagerStateListener mPagerStateListener;

        private OverScroller mOverScroller;

        private WeakReference<CoordinatorLayout> mParent;
        private WeakReference<View> mChild;


        public void setPagerStateListener(OnPagerStateListener pagerStateListener) {
            mPagerStateListener = pagerStateListener;
        }

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private void ensureScroller(Context context) {
            if (mOverScroller == null) {
                mOverScroller = new OverScroller(context);
            }
        }

        @Override
        protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
            super.layoutChild(parent, child, layoutDirection);
            mParent = new WeakReference<CoordinatorLayout>(parent);
            mChild = new WeakReference<View>(child);
            ensureScroller(child.getContext());
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onStartNestedScroll: ");
            }
            ensureScroller(child.getContext());
            return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && canScroll(child, 0) && !isClosed(child);
        }


        @Override
        public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
            // consumed the flinging behavior until Closed
            ensureScroller(child.getContext());
            return !isClosed(child);
        }


        private boolean isClosed(View child) {
            boolean isClosed = child.getTranslationY() == getHeaderOffsetRange();
            return isClosed;
        }

        public boolean isClosed() {
            return mCurState == STATE_CLOSED;
        }


        private void changeState(int newState) {
            if (mCurState != newState) {
                mCurState = newState;
                if (mCurState == STATE_OPENED) {
                    if (mPagerStateListener != null) {
                        mPagerStateListener.onHeaderOpened();
                    }
                } else {
                    if (mPagerStateListener != null) {
                        mPagerStateListener.onHeaderClosed();
                    }
                }
            }

        }

        private boolean canScroll(View child, float pendingDy) {
            int pendingTranslationY = (int) (child.getTranslationY() - pendingDy);
            if (pendingTranslationY >= getHeaderOffsetRange() && pendingTranslationY <= 0) {
                return true;
            }
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, final View child, MotionEvent ev) {
            ensureScroller(child.getContext());
            if (ev.getAction() == MotionEvent.ACTION_UP && !isClosed()) {
                handleActionUp(parent, child);
            }
            return super.onInterceptTouchEvent(parent, child, ev);
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
            ensureScroller(child.getContext());
            //dy>0 scroll up;dy<0,scroll down
            float halfOfDis = dy / 4.0f; // 为了不那么敏感
            if (!canScroll(child, halfOfDis)) {
                child.setTranslationY(halfOfDis > 0 ? getHeaderOffsetRange() : 0);
            } else {
                child.setTranslationY(child.getTranslationY() - halfOfDis);
            }
            //consumed all scroll behavior after we started Nested Scrolling
            consumed[1] = dy;
        }


        private int getHeaderOffsetRange() {
            return sHeaderOffsetRange;
        }


        private void handleActionUp(CoordinatorLayout parent, final View child) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "handleActionUp: ");
            }
            if (mFlingRunnable != null) {
                child.removeCallbacks(mFlingRunnable);
                mFlingRunnable = null;
            }
            mFlingRunnable = new FlingRunnable(parent, child);
            if (child.getTranslationY() < getHeaderOffsetRange() / 3.0f) {
                mFlingRunnable.scrollToClosed(DURATION_SHORT);
            } else {
                mFlingRunnable.scrollToOpen(DURATION_SHORT);
            }

        }

        private void onFlingFinished(CoordinatorLayout coordinatorLayout, View layout) {
            changeState(isClosed(layout) ? STATE_CLOSED : STATE_OPENED);
        }

        public void openPager() {
            openPager(DURATION_LONG);
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
            closePager(DURATION_LONG);
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
            }

            public void scrollToClosed(int duration) {
                float curTranslationY = ViewCompat.getTranslationY(mLayout);
                float dy = getHeaderOffsetRange() - curTranslationY;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "scrollToClosed:offest:" + getHeaderOffsetRange());
                    Log.d(TAG, "scrollToClosed: cur0:" + curTranslationY + ",end0:" + dy);
                    Log.d(TAG, "scrollToClosed: cur:" + Math.round(curTranslationY) + ",end:" + Math.round(dy));
                    Log.d(TAG, "scrollToClosed: cur1:" + (int) (curTranslationY) + ",end:" + (int) dy);
                }
                mOverScroller.startScroll(0, Math.round(curTranslationY - 0.1f), 0, Math.round(dy + 0.1f), duration);
                start();
                if (mPagerStateListener != null) {
                    mPagerStateListener.onHeaderStartClosing();
                }
            }

            public void scrollToOpen(int duration) {
                float curTranslationY = ViewCompat.getTranslationY(mLayout);
                mOverScroller.startScroll(0, (int) curTranslationY, 0, (int) -curTranslationY, duration);
                start();
                if (mPagerStateListener != null) {
                    mPagerStateListener.onHeaderStartOpening();
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
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "run: " + mOverScroller.getCurrY());
                        }
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

            void onHeaderStartClosing();

            void onHeaderStartOpening();

            /**
             * do callback when pager closed
             */
            void onHeaderClosed();

            /**
             * do callback when pager opened
             */
            void onHeaderOpened();
        }

    }
}
