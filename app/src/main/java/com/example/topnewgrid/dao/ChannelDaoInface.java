package com.example.topnewgrid.dao;

import android.content.ContentValues;

import com.example.topnewgrid.bean.ChannelItem;

import java.util.List;
import java.util.Map;

/**
 * 作者：陈新明
 * 创建日期：2016/9/21
 * 邮箱：herewinner@163.com
 * 描述：//TODO
 */

public interface ChannelDaoInface {
    public boolean addCache(ChannelItem item);

    public boolean deleteCache(String whereClause, String[] whereArgs);

    public boolean updateCache(ContentValues values, String whereClause,
                               String[] whereArgs);

    public Map<String, String> viewCache(String selection,
                                         String[] selectionArgs);

    public List<Map<String, String>> listCache(String selection,
                                               String[] selectionArgs);

    public void clearFeedTable();
}
