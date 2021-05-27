package com.yy.mediaplayer;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityTestBinding;

public class TestActivity extends BaseActivity {

    private ActivityTestBinding binding;

    @Override
    protected View getLayoutId() {
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog(TestActivity.this,//context
                        R.layout.view_share,//你的布局文件
                        R.style.showDialog,//你的样式文件 在styles.xml文件中定义
                        Gravity.BOTTOM,//弹出的位置
                        WindowManager.LayoutParams.MATCH_PARENT,//宽
                        WindowManager.LayoutParams.WRAP_CONTENT,//高
                        R.style.AnimBottom);
            }
        });

    }

    @Override
    protected void initData() {
//        List<LrcRow> lrcRows = new ArrayList<>();
//        for (int i = 0; i < 11; i++) {
//            LrcRow lrcRow = new LrcRow();
//            lrcRow.setContent("asdsadsadsad");
//            lrcRow.setTime(100 * i * 1000);
//            lrcRow.setTimeStr(100 * i + "");
//            lrcRow.setTotalTime(1000);
//            lrcRows.add(lrcRow);
//        }
//        binding.lv.setLrcRows(lrcRows);
//        binding.lv.setProgress(100, true);
    }

    public void customDialog(Context context, int resource,
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
        dialog.getWindow().setWindowAnimations(animation);
        dialog.show();
    }
}