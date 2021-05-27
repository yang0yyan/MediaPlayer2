package com.yy.mediaplayer.activity;

import android.os.Environment;
import android.view.View;
import android.widget.MediaController;

import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityVideoPlayBinding;

import java.io.File;

public class VideoPlayActivity extends BaseActivity {


    private ActivityVideoPlayBinding binding;

    @Override
    protected View getLayoutId() {
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

    }

    @Override
    protected void initData() {

    }


    private void play() {
//        String path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/VID_20210526_032036.mp4";
        String path = Environment.getExternalStorageDirectory().getPath() + "/Download/Wildlife.wmv";
        File file = new File(path);
        file.exists();
        binding.videoView.setVideoPath(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        binding.videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        binding.videoView.requestFocus();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
////        if (hasFocus) {
////            hideSystemUI();
////        }
//    }
//
//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            hideSystemUI();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            showSystemUI();
//        }
//    }
//
//    private void hideSystemUI() {
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//    }
//
//    private void showSystemUI() {
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//    }
}