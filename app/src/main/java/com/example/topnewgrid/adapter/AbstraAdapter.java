package com.example.topnewgrid.adapter;

import android.widget.BaseAdapter;

/**
 * Created by cxm_lmz on 2016/9/22.
 */

public abstract class AbstraAdapter extends BaseAdapter {
    public abstract void exchange (int startPosition, int dropPosition);

    public abstract void setShowDropItem (boolean isShow);
}
