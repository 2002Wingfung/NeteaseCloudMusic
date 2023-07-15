package com.hongyongfeng.neteasecloudmusic.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 屏幕尺寸及其单位转换工具类
 * @author Wingfung Hung
 */
public class DisplayUtils {
    /**
     * 将px转换为dp
     */
    public static int px2dp(float pxValue) {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 将dp转换为px
     */
    public static int dp2px(float dpValue) {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 将px转换为sp
     */
    public static int px2sp(float pxValue) {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float scaledDensity = displayMetrics.scaledDensity;
        return (int) (pxValue / scaledDensity + 0.5f);
    }

    /**
     * 将sp转换为px
     */
    public static int sp2px(float spValue) {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float scaledDensity = displayMetrics.scaledDensity;
        return (int) (spValue * scaledDensity + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWindowWidth() {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getWindowHeight() {
        Resources resources = Resources.getSystem();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}

