package com.kido.ucmaindemo.widget.main.behavior.action;

import android.view.View;

import com.kido.ucmaindemo.widget.main.UcNewsBarLayout;

/**
 * @author Kido
 * @email everlastxgb@gmail.com
 * @create_time 17/6/4 17:10
 */

public class AbsActionHelper {
    public static int getBarOffsetRange(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            return ((UcNewsBarLayout) dependency).getBarOffsetRange();
        }
        return 0;
    }

    public static int getHeaderHeight(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            UcNewsBarLayout barLayout = (UcNewsBarLayout) dependency;
            return barLayout.getHeaderHeight();
        }
        return 0;
    }

    public static int getFooterHeight(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            UcNewsBarLayout barLayout = (UcNewsBarLayout) dependency;
            return barLayout.getFooterHeight();
        }
        return 0;
    }


    public static boolean isClosed(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            UcNewsBarLayout barLayout = ((UcNewsBarLayout) dependency);
            return barLayout.isClosed();
        }
        return false;
    }


    public static boolean isTryingToOpen(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            UcNewsBarLayout barLayout = ((UcNewsBarLayout) dependency);
            return barLayout.getBehavior() != null && barLayout.getBehavior().isTryingToOpen();
        }
        return false;
    }
}
