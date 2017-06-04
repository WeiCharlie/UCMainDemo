package com.kido.ucmaindemo.widget.main.behavior;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.kido.ucmaindemo.widget.main.UcNewsBarLayout;
import com.kido.ucmaindemo.widget.main.behavior.action.AbsActionHelper;
import com.kido.ucmaindemo.widget.main.behavior.action.AbsBarOffsetAction;
import com.kido.ucmaindemo.widget.main.behavior.action.BarFollowerActionClosed;
import com.kido.ucmaindemo.widget.main.behavior.action.BarFollowerActionOpened;
import com.kido.ucmaindemo.widget.main.helper.HeaderScrollingViewBehavior;

import java.util.List;

/**
 * Behavior for Bar Follower.
 * <p>
 * e.g. ViewPager
 *
 * @author Kido
 */

public class BarFollowerBehavior extends HeaderScrollingViewBehavior {
    private static final String TAG = "UNBL_FollowerBehavior";

    private AbsBarOffsetAction mActionOpened, mActionClosed;

    public BarFollowerBehavior() {
        init();
    }

    public BarFollowerBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mActionOpened = new BarFollowerActionOpened();
        mActionClosed = new BarFollowerActionClosed();
    }

    private AbsBarOffsetAction getAction(View dependency) {
        return !AbsActionHelper.isClosed(dependency) || AbsActionHelper.isTryingToOpen(dependency) ? mActionOpened : mActionClosed;
    }

    @Override
    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        super.layoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        getAction(dependency).offsetChildAsNeeded(parent, child, dependency);
        return false;
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
            return Math.max(0, v.getMeasuredHeight() - AbsActionHelper.getHeaderHeight(v) - AbsActionHelper.getFooterHeight(v));
        } else {
            return super.getScrollRange(v);
        }
    }

    private boolean isDependOn(View dependency) {
        return dependency instanceof UcNewsBarLayout;
    }
}