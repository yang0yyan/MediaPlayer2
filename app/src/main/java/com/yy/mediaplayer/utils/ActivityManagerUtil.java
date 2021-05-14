package com.yy.mediaplayer.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManagerUtil {

    private List<Activity> activityList = new ArrayList<>();

    private ActivityManagerUtil() {
    }

    public static ActivityManagerUtil getInstance() {

        return ActivityManagerHolder.instance;
    }

    /**
     * 静态内部类获取单例
     */
    static class ActivityManagerHolder {
        public static ActivityManagerUtil instance = new ActivityManagerUtil();

    }

    /**
     * 添加activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }

    }

    /**
     * 移除activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    /**
     * 关闭所有的activity，退出应用
     */
    public void finishActivitys() {
        if (activityList != null && !activityList.isEmpty()) {
            for (Activity activity1 : activityList) {
                activity1.finish();
            }
            activityList.clear();
        }
    }

}

