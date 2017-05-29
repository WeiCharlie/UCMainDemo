package com.kido.ucmaindemo.widget.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;
import com.kido.ucmaindemo.widget.main.helper.HeaderScrollingViewBehavior;
import com.kido.ucmaindemo.widget.main.helper.ViewOffsetBehavior;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * 新闻列表的顶部Layout
 * </p>
 * 使用时可指定app:unhl_closing_footer, app:unhl_closing_header做为合拢时的头部和尾部，起到合拢动画效果。<br>
 * 另，Bar底部如果要跟内容（比如一个ViewPager）需指定对应的behavior为BarFollowerBehavior，达到嵌套动画效果。
 *
 * @author Kido
 */
@CoordinatorLayout.DefaultBehavior(UcNewsBarLayout.Behavior.class)
public class UcNewsBarLayout extends FrameLayout {

    private UcNewsBarLayout.Behavior mBehavior;
    private OnBarStateListener mBarStateListener;
    private Context mContext;


    private static final int INVALID_SCROLL_RANGE = -1;
    private static final int INVALID_RESOURCE_ID = -1;

    private int mOffsetRange = INVALID_SCROLL_RANGE;

    private int mHeaderId = INVALID_RESOURCE_ID;
    private int mFooterId = INVALID_RESOURCE_ID;
    private int mFollowerId = INVALID_RESOURCE_ID;
    private View mHeaderView;
    private View mFooterView;
    private View mFollowerView;

    public UcNewsBarLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public UcNewsBarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public UcNewsBarLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UcNewsBarLayout);

        mOffsetRange = a.getDimensionPixelSize(R.styleable.UcNewsBarLayout_unbl_offset_range, INVALID_SCROLL_RANGE);
        mHeaderId = a.getResourceId(R.styleable.UcNewsBarLayout_unbl_closing_header, INVALID_RESOURCE_ID);
        mFooterId = a.getResourceId(R.styleable.UcNewsBarLayout_unbl_closing_footer, INVALID_RESOURCE_ID);
        mFollowerId = a.getResourceId(R.styleable.UcNewsBarLayout_unbl_closing_follower, INVALID_RESOURCE_ID);

        a.recycle();

        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ensureCoSiblings();
        ensureBehavior();
    }

    /**
     * 找到指定的header, footer, follower (若有)，然后赋予对应的Behavior（若无）
     */
    private void ensureCoSiblings() {
        mHeaderView = findCoSibling(mHeaderId, BarHeaderBehavior.class);
        mFooterView = findCoSibling(mFooterId, BarFooterBehavior.class);
        mFollowerView = findCoSibling(mFollowerId, BarFollowerBehavior.class);
    }


    private View findCoSibling(int id, Class<? extends CoordinatorLayout.Behavior> behaviorType) {
        View sibling = null;
        ViewGroup parent = (ViewGroup) getParent();
        if (parent instanceof CoordinatorLayout) { // 只在CoordinatorLayout中生效
            if (id != INVALID_RESOURCE_ID) {
                sibling = parent.findViewById(id);
            }
            if (sibling == null) { // findView 找不到的话则遍历parent底下的设置了对应behavior的View(兼容没有指定unbl_closing_xxx之类的情况)
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                    if (layoutParams.getBehavior() != null && layoutParams.getBehavior().getClass() == behaviorType) {
                        sibling = child;
                        break;
                    }
                }
            }
            if (sibling != null) { // 若找到对应的可以协助兄弟，再看看是否设置了Behavior
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) sibling.getLayoutParams();
                if (layoutParams.getBehavior() == null || layoutParams.getBehavior().getClass() != behaviorType) {
                    try {
                        layoutParams.setBehavior(behaviorType.newInstance());
                    } catch (InstantiationException e) {
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        return sibling;

    }

    private void ensureBehavior() {
        if (mBehavior == null) {
            if (getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams coParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
                if (coParams.getBehavior() instanceof UcNewsBarLayout.Behavior) {
                    mBehavior = (UcNewsBarLayout.Behavior) coParams.getBehavior();
                    mBehavior.setPagerStateListener(mBarStateListener);
                }
            }
        }
    }

    private void init(Context context) {
        mContext = context;
    }

    /**
     * 本layout的header的高度
     *
     * @return
     */
    public int getHeaderHeight() {
        int height = 0;
        if (mHeaderView != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mHeaderView.getLayoutParams();
            height = mHeaderView.getMeasuredHeight() /*+ layoutParams.topMargin + layoutParams.bottomMargin*/; // FIXME: 17/5/29 BarHeaderBehavior中的实现是改变topMargin
        }
        return height;
    }

    /**
     * 本layout的footer的高度
     *
     * @return
     */

    public int getFooterHeight() {
        int height = 0;
        if (mFooterView != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mFooterView.getLayoutParams();
            height = mFooterView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
        }
        return height;
    }

    /**
     * 本layout能向上滑动的最大偏移
     *
     * @return
     */
    public int getBarOffsetRange() {
        if (mOffsetRange != INVALID_SCROLL_RANGE) {
            return mOffsetRange;
        }
        mOffsetRange = -(getHeaderHeight() + getFooterHeight()); // 默认offset为header的高度+footer的高度
        return mOffsetRange;
    }


    public void setBarStateListener(OnBarStateListener listener) {
        mBarStateListener = listener;
        if (mBehavior != null) {
            mBehavior.setPagerStateListener(mBarStateListener);
        }
    }

    public void openBar() {
        if (mBehavior != null) {
            mBehavior.openPager();
        }
    }

    public void closeBar() {
        if (mBehavior != null) {
            mBehavior.closePager();
        }
    }

    public boolean isClosed() {
        if (mBehavior != null) {
            return mBehavior.isClosed();
        }
        return false;
    }


    /**
     * callback for HeaderPager 's state
     */
    public interface OnBarStateListener extends Behavior.OnPagerStateListener {
    }


    /**
     * ********************* Behavior for Bar **************************
     * ********************* Behavior for Bar **************************
     */

    public static class Behavior extends ViewOffsetBehavior {
        private static final String TAG = "UNBL_Behavior";
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
            return !isClosed(child);
        }


        private boolean isClosed(View child) {
            boolean isClosed = child.getTranslationY() == getBarOffsetRange(child);
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
                        mPagerStateListener.onBarOpened();
                    }
                } else {
                    if (mPagerStateListener != null) {
                        mPagerStateListener.onBarClosed();
                    }
                }
            }

        }

        private boolean canScroll(View child, float pendingDy) {
            int pendingTranslationY = (int) (child.getTranslationY() - pendingDy);
            if (pendingTranslationY >= getBarOffsetRange(child) && pendingTranslationY <= 0) {
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
            //dy>0 scroll up;dy<0,scroll down
            float halfOfDis = dy / 4.0f; // 为了不那么敏感
            if (!canScroll(child, halfOfDis)) {
                child.setTranslationY(halfOfDis > 0 ? getBarOffsetRange(child) : 0);
            } else {
                child.setTranslationY(child.getTranslationY() - halfOfDis);
            }
            //consumed all scroll behavior after we started Nested Scrolling
            consumed[1] = dy;
        }


        private int getBarOffsetRange(View child) {
            if (child instanceof UcNewsBarLayout) {
                return ((UcNewsBarLayout) child).getBarOffsetRange();
            }
            return 0;
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
            if (child.getTranslationY() < getBarOffsetRange(child) / 3.0f) {
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
                float dy = getBarOffsetRange(mLayout) - curTranslationY;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "scrollToClosed:offest:" + getBarOffsetRange(mLayout));
                    Log.d(TAG, "scrollToClosed: cur0:" + curTranslationY + ",end0:" + dy);
                    Log.d(TAG, "scrollToClosed: cur:" + Math.round(curTranslationY) + ",end:" + Math.round(dy));
                    Log.d(TAG, "scrollToClosed: cur1:" + (int) (curTranslationY) + ",end:" + (int) dy);
                }
                mOverScroller.startScroll(0, Math.round(curTranslationY - 0.1f), 0, Math.round(dy + 0.1f), duration);
                start();
                if (mPagerStateListener != null) {
                    mPagerStateListener.onBarStartClosing();
                }
            }

            public void scrollToOpen(int duration) {
                float curTranslationY = ViewCompat.getTranslationY(mLayout);
                mOverScroller.startScroll(0, (int) curTranslationY, 0, (int) -curTranslationY, duration);
                start();
                if (mPagerStateListener != null) {
                    mPagerStateListener.onBarStartOpening();
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

    /**
     * ********************* Behavior for Bar Header **************************
     * ********************* Behavior for Bar Header **************************
     */

    public static class BarHeaderBehavior extends CoordinatorLayout.Behavior<View> {
        private static final String TAG = "UNBL_HeaderBehavior";

        public BarHeaderBehavior() {
        }

        public BarHeaderBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
            ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = -child.getMeasuredHeight(); //
            parent.onLayoutChild(child, layoutDirection);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "layoutChild:top" + child.getTop() + ",height" + child.getHeight());
            }
            return true;
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return isDependOn(dependency);
        }


        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            offsetChildAsNeeded(parent, child, dependency);
            return false;
        }

        private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
            int headerOffsetRange = getBarOffsetRange(dependency);
            int titleOffsetRange = getTitleOffsetRange(dependency);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "offsetChildAsNeeded:" + dependency.getTranslationY());
            }
            if (dependency.getTranslationY() == headerOffsetRange) {
                child.setTranslationY(titleOffsetRange);
            } else if (dependency.getTranslationY() == 0) {
                child.setTranslationY(0);
            } else {
                child.setTranslationY((int) (dependency.getTranslationY() / (headerOffsetRange * 1.0f) * titleOffsetRange));
            }

        }

        private int getBarOffsetRange(View dependency) {
            if (dependency instanceof UcNewsBarLayout) {
                return ((UcNewsBarLayout) dependency).getBarOffsetRange();
            }
            return 0;
        }

        private int getTitleOffsetRange(View dependency) {
            if (dependency instanceof UcNewsBarLayout) {
                return ((UcNewsBarLayout) dependency).getHeaderHeight();
            }
            return 0;
        }


        private boolean isDependOn(View dependency) {
            return dependency instanceof UcNewsBarLayout;
        }
    }


    /**
     * ********************* Behavior for Bar Footer **************************
     * ********************* Behavior for Bar Footer **************************
     */

    public static class BarFooterBehavior extends HeaderScrollingViewBehavior {
        private static final String TAG = "UNBL_FooterBehavior";

        public BarFooterBehavior() {
        }

        public BarFooterBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        @Override
        protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
            super.layoutChild(parent, child, layoutDirection);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "layoutChild:top" + child.getTop() + ",height" + child.getHeight());
            }
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return isDependOn(dependency);
        }


        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onDependentViewChanged: dependency.getTranslationY():" + dependency.getTranslationY());
            }
            offsetChildAsNeeded(parent, child, dependency);
            return false;
        }

        private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
            float offsetRange = dependency.getTop() + getFinalTopHeight(dependency) - child.getTop();
            // float offsetRange = -(child.getTop() - dependency.getTop() - getFinalTopHeight(dependency));
            int headerOffsetRange = getBarOffsetRange(dependency);
            if (dependency.getTranslationY() == headerOffsetRange) {
                child.setTranslationY(offsetRange);
            } else if (dependency.getTranslationY() == 0) {
                child.setTranslationY(0);
            } else {
                child.setTranslationY((int) (dependency.getTranslationY() / (getBarOffsetRange(dependency) * 1.0f) * offsetRange));
            }
        }


        @Override
        protected View findFirstDependency(List<View> views) {
            for (int i = 0, z = views.size(); i < z; i++) {
                View view = views.get(i);
                if (isDependOn(view))
                    return view;
            }
            return null;
        }

        private int getBarOffsetRange(View dependency) {
            if (dependency instanceof UcNewsBarLayout) {
                return ((UcNewsBarLayout) dependency).getBarOffsetRange();
            }
            return 0;
        }

        private int getFinalTopHeight(View dependency) {
            if (dependency instanceof UcNewsBarLayout) {
                return ((UcNewsBarLayout) dependency).getHeaderHeight();
            }
            return 0;
        }


        private boolean isDependOn(View dependency) {
            return dependency instanceof UcNewsBarLayout;
        }
    }

    /**
     * ********************* Behavior for Bar Follower **************************
     * ********************* Behavior for Bar Follower **************************
     */
    public static class BarFollowerBehavior extends HeaderScrollingViewBehavior {
        private static final String TAG = "UNBL_FollowerBehavior";

        public BarFollowerBehavior() {
        }

        public BarFollowerBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return isDependOn(dependency);
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onDependentViewChanged");
            }
            offsetChildAsNeeded(parent, child, dependency);
            return false;
        }

        private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
            child.setTranslationY((int) (-dependency.getTranslationY() / (getBarOffsetRange(dependency) * 1.0f) * getScrollRange(dependency)));
        }


        @Override
        protected View findFirstDependency(List<View> views) {
            for (int i = 0, z = views.size(); i < z; i++) {
                View view = views.get(i);
                if (isDependOn(view))
                    return view;
            }
            return null;
        }

        @Override
        protected int getScrollRange(View v) {
            if (isDependOn(v)) {
                return Math.max(0, v.getMeasuredHeight() - getFinalTopHeight(v));
            } else {
                return super.getScrollRange(v);
            }
        }

        private int getBarOffsetRange(View dependency) {
            if (dependency instanceof UcNewsBarLayout) {
                return ((UcNewsBarLayout) dependency).getBarOffsetRange();
            }
            return 0;
        }

        private int getFinalTopHeight(View dependency) {
            if (dependency instanceof UcNewsBarLayout) {
                UcNewsBarLayout barLayout = ((UcNewsBarLayout) dependency);
                return barLayout.getHeaderHeight() + barLayout.getFooterHeight();
            }
            return 0;
        }


        private boolean isDependOn(View dependency) {
            return dependency instanceof UcNewsBarLayout;
        }
    }

}
