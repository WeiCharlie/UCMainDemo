package com.kido.ucmaindemo.widget.main.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.kido.ucmaindemo.utils.Logger;
import com.kido.ucmaindemo.widget.main.UcNewsBarLayout;

/**
 * Behavior for Bar Header.
 * <p>
 * e.g. TitleLayout
 *
 * @author Kido
 */

public class BarHeaderBehavior extends CoordinatorLayout.Behavior<View> {
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
        Logger.d(TAG, "layoutChild-> top=%s, height=%s", child.getTop(), child.getHeight());
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
        int dependencyOffsetRange = getBarOffsetRange(dependency);
        int childOffsetRange = getTitleOffsetRange(dependency);
        if (!isClosed(dependency)) {
            float childTransY = dependency.getTranslationY() == 0 ? 0 :
                    dependency.getTranslationY() == dependencyOffsetRange ? childOffsetRange :
                            ((float) Math.floor(dependency.getTranslationY()) / (dependencyOffsetRange * 1.0f) * childOffsetRange);
            Logger.d(TAG, "offsetChildAsNeeded(!isClosed)-> dependency.getTranslationY()=%s, dependencyOffsetRange=%s, childOffsetRange=%s, childTransY=%s",
                    dependency.getTranslationY(), dependencyOffsetRange, childOffsetRange, childTransY);
            if (Math.abs(childTransY) > Math.abs(childOffsetRange)) {
                childTransY = childOffsetRange;
            }
            Logger.d(TAG, "offsetChildAsNeeded-> real childTransY=%s", childTransY);
            child.setTranslationY(childTransY);
        } else {
            float delta = dependency.getTranslationY() - dependencyOffsetRange;
            float childTransY = child.getTranslationY() + delta;
            Logger.d(TAG, "offsetChildAsNeeded(isClosed)-> dependency.getTranslationY()=%s, child.getTranslationY()=%s, dependencyOffsetRange=%s, childOffsetRange=%s, childTransY=%s, delta=%s",
                    dependency.getTranslationY(), child.getTranslationY(), dependencyOffsetRange, childOffsetRange, childTransY, delta);
            child.setTranslationY(childTransY);

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
            UcNewsBarLayout barLayout = ((UcNewsBarLayout) dependency);
            return barLayout.getHeaderHeight();
        }
        return 0;
    }

    private boolean isClosed(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            UcNewsBarLayout barLayout = ((UcNewsBarLayout) dependency);
            return barLayout.isClosed();
        }
        return false;
    }


    private boolean isDependOn(View dependency) {
        return dependency instanceof UcNewsBarLayout;
    }
}
