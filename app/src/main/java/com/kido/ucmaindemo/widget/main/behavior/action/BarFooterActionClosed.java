package com.kido.ucmaindemo.widget.main.behavior.action;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.kido.ucmaindemo.utils.Logger;

/**
 * @author Kido
 * @email everlastxgb@gmail.com
 * @create_time 17/6/4 16:19
 */

public class BarFooterActionClosed extends AbsBarOffsetAction {

    private static final String TAG = "BarFooterActionClosed";

    public BarFooterActionClosed() {
    }

    @Override
    public void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        int dependencyOffsetRange = AbsActionHelper.getBarOffsetRange(dependency);
        int childStartTransY = -(dependency.getMeasuredHeight() - AbsActionHelper.getHeaderHeight(dependency));
        int childStopTransY = -dependency.getMeasuredHeight();
        float delta = dependency.getTranslationY() - dependencyOffsetRange;
        float childTransY = childStartTransY + delta;
        Logger.d(TAG, "offsetChildAsNeeded(isClosed)-> dependency.getTranslationY()=%s, child.getTranslationY()=%s, dependencyOffsetRange=%s, childStartTransY=%s, childTransY=%s, delta=%s",
                dependency.getTranslationY(), child.getTranslationY(), dependencyOffsetRange, childStartTransY, childTransY, delta);
        if (childTransY < childStopTransY) { //向上滑出过多
            childTransY = childStopTransY;
        }
        if (childTransY > childStartTransY) { //向下滑出过多
            childTransY = childStartTransY;
        }
        Logger.d(TAG, "offsetChildAsNeeded-> real childTransY=%s", childTransY);
        child.setTranslationY(childTransY);
    }


}
