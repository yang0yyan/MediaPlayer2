package com.yy.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityTestBinding;
import com.yy.mediaplayer.model.bean.LrcRow;
import com.yy.mediaplayer.view.LyricView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BaseActivity {

    private ActivityTestBinding binding;
    /**
     * The dependencies block in the module-level build configuration file
     * specifies dependencies required to build only the module itself.
     * To learn more, go to Add build dependencies.
     */
    @Override
    protected View getLayoutId() {
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    /**
     * The splits block is where you can configure different APK builds that
     * each contain only code and resources for a supported screen density or
     * ABI. You'll also need to configure your build so that each APK has a
     * different versionCode.
     */
    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        List<LrcRow> lrcRows = new ArrayList<>();
        for(int i=0;i<11;i++){
            LrcRow lrcRow = new LrcRow();
            lrcRow.setContent("asdsadsadsad");
            lrcRow.setTime(100*i*1000);
            lrcRow.setTimeStr(100*i+"");
            lrcRow.setTotalTime(1000);
            lrcRows.add(lrcRow);
        }
        binding.lv.setLrcRows(lrcRows);
        binding.lv.setProgress(100,true);

    }
}