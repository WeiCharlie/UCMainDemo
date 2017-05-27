package com.kido.ucmaindemo.widget.main;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;
import com.kido.ucmaindemo.widget.main.helper.HeaderScrollingViewBehavior;

import java.util.List;

/**
 * 多个新闻列表的容器ViewPager
 *
 * @author Kido
 */
@CoordinatorLayout.DefaultBehavior(UcNewsContentPager.Behavior.class)
public class UcNewsContentPager extends ViewPager {

    private boolean isPagingEnabled = true;

    private static int sHeaderOffsetRange;
    private static int sFinalTopHeight;

    public UcNewsContentPager(Context context) {
        super(context);
        init(context);
    }

    public UcNewsContentPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        sHeaderOffsetRange = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
        sFinalTopHeight = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_tabs_height)
                + context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_title_height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    //
    public static class Behavior extends HeaderScrollingViewBehavior {
        private static final String TAG = "UcNewsContentBehavior";

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
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
            child.setTranslationY((int) (-dependency.getTranslationY() / (getHeaderOffsetRange() * 1.0f) * getScrollRange(dependency)));
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
                return Math.max(0, v.getMeasuredHeight() - getFinalTopHeight());
            } else {
                return super.getScrollRange(v);
            }
        }

        private int getHeaderOffsetRange() {
            return sHeaderOffsetRange;
        }

        private int getFinalTopHeight() {
            return sFinalTopHeight;
        }


        private boolean isDependOn(View dependency) {
            return dependency instanceof UcNewsHeaderLayout;
        }
    }

}