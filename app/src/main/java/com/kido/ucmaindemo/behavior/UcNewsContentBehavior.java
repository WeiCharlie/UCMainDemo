package com.kido.ucmaindemo.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;
import com.kido.ucmaindemo.behavior.helper.HeaderScrollingViewBehavior;

import java.util.List;


/**
 * 可滚动的新闻列表Behavior
 * <p/>
 */
public class UcNewsContentBehavior extends HeaderScrollingViewBehavior {
    private static final String TAG = "UcNewsContentBehavior";
    private Context mContext;

    public UcNewsContentBehavior() {
    }

    public UcNewsContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
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
        return mContext.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
    }

    private int getFinalTopHeight() {
        return mContext.getResources().getDimensionPixelOffset(R.dimen.uc_news_tabs_height)
                + mContext.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_title_height);
    }


    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.news_header_fLayout;
    }
}
