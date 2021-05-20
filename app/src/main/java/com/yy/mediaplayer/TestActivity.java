package com.yy.mediaplayer;

import android.view.View;

import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityTestBinding;
import com.yy.mediaplayer.model.bean.LrcRow;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BaseActivity {

    private ActivityTestBinding binding;
    @Override
    protected View getLayoutId() {
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        List<LrcRow> lrcRows = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            LrcRow lrcRow = new LrcRow();
            lrcRow.setContent("asdsadsadsad");
            lrcRow.setTime(100 * i * 1000);
            lrcRow.setTimeStr(100 * i + "");
            lrcRow.setTotalTime(1000);
            lrcRows.add(lrcRow);
        }
        binding.lv.setLrcRows(lrcRows);
        binding.lv.setProgress(100, true);
    }
}