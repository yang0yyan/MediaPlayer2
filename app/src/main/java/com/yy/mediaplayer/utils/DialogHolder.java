package com.yy.mediaplayer.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.yy.mediaplayer.R;

public class DialogHolder {

    public static Dialog customDialog(Context context, int resource) {
        return customDialog(context, resource, R.style.PrimaryLoadingDialog,//你的样式文件 在styles.xml文件中定义
                Gravity.CENTER,//弹出的位置
                WindowManager.LayoutParams.WRAP_CONTENT,//宽
                WindowManager.LayoutParams.WRAP_CONTENT,//高
                0);
    }

    public static Dialog customDialog(Context context, int resource,
                                      int dialogStyle,
                                      int gravity,
                                      int width,
                                      int height,
                                      int animation) {
        View view = View.inflate(context, resource, null);
        final Dialog dialog = new Dialog(context, dialogStyle);
        dialog.setContentView(view);
        WindowManager.LayoutParams layoutParams =
                dialog.getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = height;
//        layoutParams.y = 180;//距离顶部的距离
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setGravity(gravity);
        if (0 != animation)
            dialog.getWindow().setWindowAnimations(animation);
        return dialog;
    }
}
