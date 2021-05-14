package com.yy.mediaplayer.activity.fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.base.BaseMediaFragment;
import com.yy.mediaplayer.databinding.FragmentMusicControlBinding;
import com.yy.mediaplayer.utils.LogHelper;
import com.yy.mediaplayer.utils.TimeUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MusicControlsFragment extends BaseMediaFragment implements View.OnClickListener {
    private static final String TAG = LogHelper.makeLogTag(MusicControlsFragment.class);

    private FragmentMusicControlBinding binding;

    @Override
    protected View getLayoutId(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentMusicControlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.tvName.requestFocus();
        binding.ivPlayPause.setOnClickListener(this);
        binding.ivNext.setOnClickListener(this);
        binding.seekTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvCurrentTime.setText(TimeUtil.MilliToMinut(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onMetadataChanged(MediaMetadataCompat metadata) {
        super.onMetadataChanged(metadata);
        binding.tvName.setText(mMediaDescription.getTitle());
        binding.tvTotalTime.setText(TimeUtil.MilliToMinut(mMediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
        binding.seekTime.setMax((int) mMediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    @Override
    protected void onPlaybackStateChanged(PlaybackStateCompat state) {
        super.onPlaybackStateChanged(state);
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                binding.ivPlayPause.setBackgroundResource(R.drawable.ic_black_pause_24);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                binding.ivPlayPause.setBackgroundResource(R.drawable.ic_black_play_arrow_24);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                binding.ivPlayPause.setBackgroundResource(R.drawable.ic_black_language_24);
                break;
            default:
                LogHelper.d(TAG, "Unhandled state ", state.getState());
        }
        updateProgress();
    }

    @Override
    public void onClick(View v) {
        if (v == binding.ivPlayPause) {
            if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().pause();
                stopSeekbarUpdate();
            } else {
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();
                scheduleSeekbarUpdate();
            }
        } else if (v == binding.ivNext) {
            MediaControllerCompat.getMediaController(getActivity()).getTransportControls().skipToNext();
        } else if (v == binding.ivAlbum) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mScheduleFuture;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };
    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, 100,
                    1000, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (mPlaybackState == null) {
            return;
        }
        long currentPosition = mPlaybackState.getPosition();
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mPlaybackState.getPlaybackSpeed();
        }
        binding.seekTime.setProgress((int) currentPosition);
        binding.tvCurrentTime.setText(TimeUtil.MilliToMinut(currentPosition));
    }
}
