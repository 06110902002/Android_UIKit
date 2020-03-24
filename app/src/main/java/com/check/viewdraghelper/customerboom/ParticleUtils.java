package com.check.viewdraghelper.customerboom;

import android.content.res.Resources;

/**
 * Create By 刘胡来
 * Create Date 2020-03-23
 * Sensetime@Copyright
 * Des:
 */
public class ParticleUtils {

    /**
     * 密度
     */
    public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    public static int dp2px(int dp) {
        return Math.round(dp * DENSITY);
    }
}
