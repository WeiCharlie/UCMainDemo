package com.kido.ucmaindemo.widget.main;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 多个新闻列表的容器
 *
 * @author Kido
 */
@CoordinatorLayout.DefaultBehavior(UcNewsBarLayout.BarFollowerBehavior.class)
public class UcNewsContentPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public UcNewsContentPager(Context context) {
        super(context);
        init(context);
    }

    public UcNewsContentPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
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

}