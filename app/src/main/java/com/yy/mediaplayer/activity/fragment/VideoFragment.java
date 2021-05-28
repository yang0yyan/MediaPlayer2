package com.yy.mediaplayer.activity.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yy.mediaplayer.activity.CastMusicActivity;
import com.yy.mediaplayer.activity.LocalMusicActivity;
import com.yy.mediaplayer.activity.VideoPlayActivity;
import com.yy.mediaplayer.base.BaseFragment;
import com.yy.mediaplayer.databinding.FragmentVideoBinding;
import com.yy.mediaplayer.utils.PermissionUtil;
import com.yy.mediaplayer.utils.imageCache.BitmapUtil;

public class VideoFragment extends BaseFragment implements View.OnClickListener{

    private FragmentVideoBinding binding;

    @Override
    protected View getLayoutId(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initView() {

        binding.ivVideo.setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v == binding.ivVideo) {
            startActivity(new Intent(mContext, VideoPlayActivity.class));
        }
    }
}
