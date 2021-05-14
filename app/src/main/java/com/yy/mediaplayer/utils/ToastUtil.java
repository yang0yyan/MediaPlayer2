package com.yy.mediaplayer.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Context context;

    public static void init(Context context) {
        ToastUtil.context = context;
    }

    public static void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
