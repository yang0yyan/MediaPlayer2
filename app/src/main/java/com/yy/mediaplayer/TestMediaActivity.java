package com.yy.mediaplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityTestMediaBinding;
import com.yy.mediaplayer.utils.LogHelper;

import java.io.IOException;

public class TestMediaActivity extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = LogHelper.makeLogTag(TestMediaActivity.class);

    private ActivityTestMediaBinding binding;
    private String baseUrl = "https://storage.googleapis.com/automotive-media/";
    private MediaPlayer mediaPlayer;
    private String musicName = "Jazz_In_Paris.mp3";

    @Override
    protected View getLayoutId() {
        binding = ActivityTestMediaBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.setDataSource(baseUrl + musicName);
                    mediaPlayer.prepareAsync();
                } catch (IOException | IllegalStateException e) {
                    e.printStackTrace();
                    mediaPlayer.reset();
                }
            }
        });
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
            }
        });
    }

    @Override
    protected void initData() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: ");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: ");
        return false;
    }
}