package com.yy.mediaplayer.activity.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yy.mediaplayer.base.BaseFragment;
import com.yy.mediaplayer.databinding.FragmentVideoBinding;
import com.yy.mediaplayer.utils.PermissionUtil;
import com.yy.mediaplayer.utils.imageCache.BitmapUtil;

public class VideoFragment extends BaseFragment {

    private FragmentVideoBinding binding;

    private String path = "https://alifei03.cfp.cn/creative/vcg/800/new/VCG41N1172479732.jpg";

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
        PermissionUtil.getPermission(getActivity(), PermissionUtil.WRITE_EXTERNAL_STORAGE);
        BitmapUtil bitmapUtil = new BitmapUtil();
        bitmapUtil.disPlay(binding.imageView, path);
//        CacheUtil.getInstance(mContext).setImageToView(path,binding.imageView);
    }
}
