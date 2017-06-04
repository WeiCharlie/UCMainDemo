package com.kido.ucmaindemo.widget.main.behavior.action;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * @author Kido
 * @email everlastxgb@gmail.com
 * @create_time 17/6/4 16:13
 */

public abstract class AbsBarOffsetAction {
    protected static final String TAG = "AbsBarOffsetAction";

    public AbsBarOffsetAction() {
    }

    public abstract void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency);

}
