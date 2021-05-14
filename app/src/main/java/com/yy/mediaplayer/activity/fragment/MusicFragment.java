package com.yy.mediaplayer.activity.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yy.mediaplayer.activity.CastMusicActivity;
import com.yy.mediaplayer.activity.LocalMusicActivity;
import com.yy.mediaplayer.base.BaseFragment;
import com.yy.mediaplayer.databinding.FragmentMusicBinding;

public class MusicFragment extends BaseFragment implements View.OnClickListener {

    private FragmentMusicBinding binding;

    @Override
    protected View getLayoutId(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.ivMusic.setOnClickListener(this);
        binding.ivMusic2.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v == binding.ivMusic) {
            startActivity(new Intent(mContext, LocalMusicActivity.class));
        } else if (v == binding.ivMusic2) {
            startActivity(new Intent(mContext, CastMusicActivity.class));
        }
    }
}
