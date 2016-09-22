package com.example.topnewgrid.tools;

import android.content.Context;

/**
 * 作者：陈新明
 * 创建日期：2016/9/21
 * 邮箱：herewinner@163.com
 * 描述：//TODO
 */

public class DataTools {
    /**
     * dipתΪ px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     *  px תΪ dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
