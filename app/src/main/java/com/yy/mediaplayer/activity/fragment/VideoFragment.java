package com.yy.mediaplayer.activity.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yy.mediaplayer.base.BaseFragment;
import com.yy.mediaplayer.databinding.FragmentVideoBinding;

public class VideoFragment extends BaseFragment {

    private FragmentVideoBinding binding;

    @Override
    protected View getLayoutId(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentVideoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
