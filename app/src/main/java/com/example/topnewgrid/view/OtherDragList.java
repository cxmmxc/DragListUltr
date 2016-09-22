package com.example.topnewgrid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 作者：陈新明
 * 创建日期：2016/9/22
 * 邮箱：herewinner@163.com
 * 描述：//TODO
 */

public class OtherDragList extends ListView {
    public OtherDragList(Context context) {
        this(context, null);
    }

    public OtherDragList(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public OtherDragList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, measureSpec);
    }
}
