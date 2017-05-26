package com.kido.ucmaindemo.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kido.ucmaindemo.BuildConfig;
import com.kido.ucmaindemo.R;


/**
 * 新闻标题
 * <p/>
 */
public class UcNewsTitleBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "UcNewsTitleBehavior";

    private Context mContext;

    public UcNewsTitleBehavior() {
    }

    public UcNewsTitleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
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
        return mContext.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
    }

    private int getTitleHeight() {
        return mContext.getResources().getDimensionPixelOffset(R.dimen.uc_news_header_title_height);
    }


    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.news_header_fLayout;
    }
}
