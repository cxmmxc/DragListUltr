package com.example.topnewgrid.app;

import android.app.Application;

import com.example.topnewgrid.db.SQLHelper;

/**
 * 作者：陈新明
 * 创建日期：2016/9/21
 * 邮箱：herewinner@163.com
 * 描述：//TODO
 */

public class AppApplication extends Application {
    private static AppApplication mAppApplication;
    private SQLHelper sqlHelper;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mAppApplication = this;
    }

    /** 获取Application */
    public static AppApplication getApp() {
        return mAppApplication;
    }

    /** 获取数据库Helper */
    public SQLHelper getSQLHelper() {
        if (sqlHelper == null)
            sqlHelper = new SQLHelper(mAppApplication);
        return sqlHelper;
    }

    /** 摧毁应用进程时候调用 */
    public void onTerminate() {
        if (sqlHelper != null)
            sqlHelper.close();
        super.onTerminate();
    }

    public void clearAppCache() {
    }
}
