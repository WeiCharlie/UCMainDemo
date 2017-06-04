package com.kido.ucmaindemo.widget.main.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.kido.ucmaindemo.utils.Logger;
import com.kido.ucmaindemo.widget.main.UcNewsBarLayout;
import com.kido.ucmaindemo.widget.main.behavior.action.AbsActionHelper;
import com.kido.ucmaindemo.widget.main.behavior.action.AbsBarOffsetAction;
import com.kido.ucmaindemo.widget.main.behavior.action.BarHeaderActionClosed;
import com.kido.ucmaindemo.widget.main.behavior.action.BarHeaderActionOpened;

/**
 * Behavior for Bar Header.
 * <p>
 * e.g. TitleLayout
 *
 * @author Kido
 */

public class BarHeaderBehavior extends CoordinatorLayout.Behavior<View> {
    private static final String TAG = "UNBL_HeaderBehavior";

    private AbsBarOffsetAction mActionOpened, mActionClosed;

    public BarHeaderBehavior() {
        init();
    }

    public BarHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mActionOpened = new BarHeaderActionOpened();
        mActionClosed = new BarHeaderActionClosed();
    }

    private AbsBarOffsetAction getAction(View dependency) {
        return AbsActionHelper.isClosed(dependency) ? mActionClosed : mActionOpened;
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = -child.getMeasuredHeight(); //
        parent.onLayoutChild(child, layoutDirection);
        Logger.d(TAG, "layoutChild-> top=%s, height=%s", child.getTop(), child.getHeight());
        return true;
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



    private boolean isDependOn(View dependency) {
        return dependency instanceof UcNewsBarLayout;
    }
}
