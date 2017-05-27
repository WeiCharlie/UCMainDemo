package com.kido.ucmaindemo.widget.main;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;
import com.kido.ucmaindemo.widget.main.helper.HeaderScrollingViewBehavior;

import java.util.List;

/**
 * 新闻便签栏
 *
 * @author Kido
 */
@CoordinatorLayout.DefaultBehavior(UcNewsTabLayout.Behavior.class)
public class UcNewsTabLayout extends TabLayout {

    private static int sHeaderOffsetRange;
    private static int sFinalTopHeight;

    public UcNewsTabLayout(Context context) {
        super(context);
        init(context);
    }

    public UcNewsTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UcNewsTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {// TODO: 2017/5/27 取值待优化
        sHeaderOffsetRange = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
        sFinalTopHeight = context.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_title_height);
    }

    public static class Behavior extends HeaderScrollingViewBehavior {
        private static final String TAG = "UcNewsTabBehavior";

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
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
            float offsetRange = dependency.getTop() + getFinalTopHeight() - child.getTop();
            int headerOffsetRange = getHeaderOffsetRange();
            if (dependency.getTranslationY() == headerOffsetRange) {
                child.setTranslationY(offsetRange);
            } else if (dependency.getTranslationY() == 0) {
                child.setTranslationY(0);
            } else {
                child.setTranslationY((int) (dependency.getTranslationY() / (getHeaderOffsetRange() * 1.0f) * offsetRange));
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
