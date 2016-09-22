package com.example.topnewgrid.adapter;

/**
 * Created by cxm_lmz on 2016/9/22.
 */

public interface InterAdapter {
    void exchange (int startPosition, int dropPosition);
    void setShowDropItem (boolean isShow);
    void notifyData();
}
