package com.kido.ucmaindemo.widget.main;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;

/**
 * 顶部标题栏
 *
 * @author Kido
 */
@CoordinatorLayout.DefaultBehavior(UcNewsTitleLayout.Behavior.class)
public class UcNewsTitleLayout extends FrameLayout {

    private static int sHeaderOffsetRange;
    private static int sTitleHeight;

    public UcNewsTitleLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public UcNewsTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UcNewsTitleLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {// TODO: 2017/5/27 取值待优化
        sHeaderOffsetRange = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
        sTitleHeight = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_title_height);
    }

    public static class Behavior extends CoordinatorLayout.Behavior<View> {
        private static final String TAG = "UcNewsTitleBehavior";

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
            ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = -getTitleHeight();
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
            int headerOffsetRange = getHeaderOffsetRange();
            int titleOffsetRange = getTitleHeight();
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

        private int getHeaderOffsetRange() {
            return sHeaderOffsetRange;
        }

        private int getTitleHeight() {
            return sTitleHeight;
        }


        private boolean isDependOn(View dependency) {
            return dependency instanceof UcNewsHeaderLayout;
        }
    }

}
