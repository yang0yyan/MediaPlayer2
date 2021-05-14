package com.yy.mediaplayer.model.media;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;

import com.yy.mediaplayer.utils.LogHelper;
import com.yy.mediaplayer.utils.ToastUtil;

import java.io.IOException;

import static android.media.MediaPlayer.SEEK_CLOSEST_SYNC;

public final class MediaPlayerManager implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, Playback {
    private Context context;
    private MediaPlayer mediaPlayer;
    public Status status = Status.INIT;
    public PlayMode mode = PlayMode.ORDER;
    private boolean autoPlay = true;
    Playback.Callback callback;
    private AudioManager mAudioManager;


    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    public static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    public static final float VOLUME_NORMAL = 1.0f;


    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private int mCurrentAudioFocusState;

    private final IntentFilter mAudioNoisyIntentFilter =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private boolean mAudioNoisyReceiverRegistered;

    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (status==Status.STARTED) {
                            pause();
//                            Intent i = new Intent(context, Med.class);
//                            i.setAction(MusicService.ACTION_CMD);
//                            i.putExtra(MusicService.CMD_NAME, MusicService.CMD_PAUSE);
//                            mContext.startService(i);
                        }
                    }
                }
            };


    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    enum Status {
        INIT, IDLE, END, ERROR,PREPARING,
        PREPARED, STARTED, PAUSED, COMPLETED, STOPPED
    }

    enum PlayMode {
        ORDER, OYT_OF_ORDER, LOOP
    }

    public MediaPlayerManager(Context context) {
        this.context = context;
        createMediaPlayer();
        setAudioManager();
    }

    //创建
    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setScreenOnWhilePlaying(false);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);//唤醒锁定
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        status = Status.INIT;
    }

    private void setAudioManager(){
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    //准备
    public void prepareAndPlay(String filePath) {
        if (status != Status.INIT) {
            stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
            status = Status.PREPARING;
            updatePlaybackState();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            mediaPlayer.reset();
            status = Status.INIT;
        }

    }

    //播放
    public void start() {
        if (status == Status.PREPARED || status == Status.PAUSED || status == Status.COMPLETED) {
            registerAudioNoisyReceiver();
            tryToGetAudioFocus();
            mediaPlayer.start();
            status = Status.STARTED;
            updatePlaybackState();
        }else if(status == Status.STOPPED){
            mediaPlayer.prepareAsync();
        }
    }

    //暂停
    public void pause() {
        if (status == Status.STARTED) {
            unregisterAudioNoisyReceiver();
            mediaPlayer.pause();
            status = Status.PAUSED;
        }else if(status==Status.PREPARING){
            mediaPlayer.reset();
        }
        updatePlaybackState();
    }

    //停止
    public void stop() {
        if (status == Status.STARTED || status == Status.PAUSED ||
                status == Status.COMPLETED || status == Status.PREPARED) {
            unregisterAudioNoisyReceiver();
            giveUpAudioFocus();
            mediaPlayer.stop();
            status = Status.STOPPED;
            updatePlaybackState();
        }
    }

    //跳转
    public void seek(long seek) {
        if (status == Status.PREPARED || status == Status.STARTED ||
                status == Status.PAUSED || status == Status.COMPLETED) {
            mediaPlayer.seekTo(seek, SEEK_CLOSEST_SYNC);
            updatePlaybackState();
        }
    }

    public long getCurrentPosition() {
        if (status == Status.STARTED ||
                status == Status.PREPARED ||
                status == Status.COMPLETED ||
                status == Status.PAUSED ||
                status == Status.STOPPED) {
            return mediaPlayer.getCurrentPosition();
        }
        return PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;

    }

    private void tryToGetAudioFocus(){
        int result =
                mAudioManager.requestAudioFocus(
                        mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void configurePlayerState() {
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pause();
        } else {
            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                mediaPlayer.setVolume(VOLUME_DUCK,VOLUME_DUCK);
            } else {
                mediaPlayer.setVolume(VOLUME_NORMAL,VOLUME_NORMAL);
            }
            if (mPlayOnFocusGain) {
                start();
                mPlayOnFocusGain = false;
            }
        }
    }
    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            context.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            context.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    private void updatePlaybackState() {
        int sta = 0;
        switch (status) {
            case STARTED:
                sta = PlaybackStateCompat.STATE_PLAYING;
                break;
            case COMPLETED:
            case STOPPED:
            case PAUSED:
            case PREPARED:
                sta = PlaybackStateCompat.STATE_PAUSED;
                break;
            case PREPARING:
                sta = PlaybackStateCompat.STATE_BUFFERING;
                break;
            default:
                sta = PlaybackStateCompat.STATE_NONE;
                break;
        }
        if (null != callback && sta != 0)
            callback.onPlaybackStatusChanged(sta);
    }

    public void release() {
        if (mediaPlayer != null) {
            stop();
            mediaPlayer.release();
            mediaPlayer = null;
            status = Status.END;
        }
    }

    //播放完成回调
    @Override
    public void onCompletion(MediaPlayer mp) {
        status = Status.COMPLETED;
        if (mode == PlayMode.LOOP) {
            start();
        } else {
            updatePlaybackState();
        }
    }

    //准备完成回调
    @Override
    public void onPrepared(MediaPlayer mp) {
        status = Status.PREPARED;

        if (autoPlay)
            start();
        else
            updatePlaybackState();
    }

    //跳转成功回调
    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        ToastUtil.showToast("播放发生错误");
        status = Status.ERROR;
        updatePlaybackState();
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                //媒体服务器死亡。在这种情况下，应用程序必须释放MediaPlayer对象并实例化一个新对象
                release();
                createMediaPlayer();
                return true;
        }
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                //与文件或网络相关的操作错误。
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                //位流不符合相关的编码标准或文件规范。
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                //位流符合相关的编码标准或文件规范，但媒体框架不支持该特性。
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                //有些操作耗时太长，通常超过3-5秒。
                break;
            case  -2147483648:
                //底层位置错误。
                break;
        }
        return false;
    }

    private boolean mPlayOnFocusGain;
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            mPlayOnFocusGain = mediaPlayer != null &&(status==Status.STARTED);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mediaPlayer != null) {
                        configurePlayerState();
                    }
                }
            };


}
