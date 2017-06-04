package com.kido.ucmaindemo.widget.main.behavior.action;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.kido.ucmaindemo.utils.Logger;

/**
 * @author Kido
 * @email everlastxgb@gmail.com
 * @create_time 17/6/4 16:19
 */

public class BarHeaderActionOpened extends AbsBarOffsetAction {

    private static final String TAG = "BarHeaderActionOpened";

    public BarHeaderActionOpened() {
    }

    @Override
    public void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        int dependencyOffsetRange = AbsActionHelper.getBarOffsetRange(dependency);
        int childOffsetRange = AbsActionHelper.getHeaderHeight(dependency);
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
    }


}
