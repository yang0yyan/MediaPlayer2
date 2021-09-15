package com.yy.mediaplayer.activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yy.mediaplayer.model.media.MediaPlayerManager;
import com.yy.mediaplayer.utils.PermissionUtil;
import com.yy.mediaplayer.view.VisualizerView;

import java.util.ArrayList;
import java.util.List;

public class EqualizerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean hasPermission;
    private Equalizer equalizer;
    private LinearLayout ll;
    private Visualizer visualizer;
    private BassBoost bassBoost;
    private Virtualizer virtualizer;
    private PresetReverb presetReverb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    protected void initView() {
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);
    }

    protected void initData() {
        mediaPlayer = MediaPlayerManager.getMediaPlayer();
        hasPermission = PermissionUtil.getPermission(this, PermissionUtil.RECORD_AUDIO);
        if (hasPermission) {
            setupVisualizer();
            setupEqualizer();
            setupBassBoost();
            setupVirtualizer();
            setupPresetReverb();
        }
    }

//    public void setupEqualizer_() {
//        Equalizer equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
//        int result = equalizer.setEnabled(true);
//        if (result != Equalizer.SUCCESS) return;
//        short bandNum = equalizer.getNumberOfBands();
//        for (short i = 0; i < bandNum; i++) {
//            short[] bb = equalizer.getBandLevelRange();//返回最大最小波段级别
//            short cc = equalizer.getBandLevel(i);//获得音乐的增益等级
//            int dd = equalizer.getCenterFreq(i);//获得波段的中心频率
//            int[] ee = equalizer.getBandFreqRange(i);//获取波段的频率范围。
//            int ff = equalizer.getBand(i);//受频率影响最大的频带
//            int gg = equalizer.getCurrentPreset();//获得当前预设
//            int ii = equalizer.getNumberOfPresets();//获取支持的预置数量
//            String hh = equalizer.getPresetName(i);//获取基于索引的预置名称
//            Log.d("TAG", "setupEqualizer: ");
//        }
//    }


    private int j = 0;

    private void setupVisualizer() {
        final VisualizerView visualizerView = new VisualizerView(this);

        visualizerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (120f * getResources().getDisplayMetrics().density)));
        ll.addView(visualizerView);
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        int result = visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        if (result != Visualizer.SUCCESS) return;
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            //波形数据
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                if (j < 10) {
                    Log.i("onWaveFormDataCapture", "被调用了" + j);
                    j++;
                }
                visualizerView.updataVisualizer(waveform);
            }

            //频率数据
            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                if (j < 10) {
                    Log.i("onFftDataCapture", "被调用了");
                    j++;
                }
                visualizerView.updataVisualizer(fft);
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        visualizer.setEnabled(true);
    }

    private void releaseVisualizer(){
        if (visualizer != null) {
            visualizer.setEnabled(false);
            visualizer.release();
            visualizer = null;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupEqualizer() {
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        equalizer.setEnabled(true);

        TextView eqTitle = new TextView(this);
        eqTitle.setText("均衡器：");
        ll.addView(eqTitle);
        //获取均衡器的最小值

        //获取均衡器支持的所有频率
        final short bands = equalizer.getNumberOfBands();
        for (short i = 0; i < bands; i++) {
            final int minEQLevel = equalizer.getBandLevelRange()[0];
            //获取均衡器的最大值
            int maxEQLevel = equalizer.getBandLevelRange()[1];
            int bandLevel = equalizer.getBandLevel(i);
            //
            final TextView eqTextView = new TextView(this);
            eqTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            eqTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            eqTextView.setText(bandLevel / 100 + " dB");
            ll.addView(eqTextView);
            //
            LinearLayout eqLl = new LinearLayout(this);
            eqLl.setOrientation(LinearLayout.HORIZONTAL);
            //
            TextView minDbTextView = new TextView(this);
            minDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            minDbTextView.setText((minEQLevel / 100) + " dB");
            //
            TextView maxDbTextView = new TextView(this);
            maxDbTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            maxDbTextView.setText((maxEQLevel / 100) + " dB");
            //
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;
            SeekBar seekBar = new SeekBar(this);
            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(maxEQLevel - minEQLevel);
            seekBar.setProgress(bandLevel - minEQLevel);

            final short brand = i;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    equalizer.setBandLevel(brand, (short) (progress + minEQLevel));
                    eqTextView.setText((progress + minEQLevel) / 100 + " HZ");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            eqLl.addView(minDbTextView);
            eqLl.addView(seekBar);
            eqLl.addView(maxDbTextView);
            ll.addView(eqLl);

        }
    }

    private void releaseEqualizer(){
        if (equalizer != null) {
            equalizer.setEnabled(false);
            equalizer.release();
            equalizer = null;
        }
    }

    private void setupBassBoost() {
        bassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
        bassBoost.setEnabled(true);
        if (!bassBoost.getStrengthSupported()) return;
        TextView bbTextView = new TextView(this);
        bbTextView.setText("重低音： ");
        ll.addView(bbTextView);
//        BassBoost.PARAM_STRENGTH
//        BassBoost.PARAM_STRENGTH_SUPPORTED

        SeekBar bbSeekBar = new SeekBar(this);
        bbSeekBar.setMax(100);
        bbSeekBar.setProgress(bassBoost.getRoundedStrength());
        bbSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bassBoost.setStrength((short) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ll.addView(bbSeekBar);
    }

    private void releaseBassBoost(){
        if (bassBoost != null) {
            bassBoost.setEnabled(false);
            bassBoost.release();
            bassBoost = null;
        }
    }

    private void setupVirtualizer() {
        virtualizer = new Virtualizer(0, mediaPlayer.getAudioSessionId());
        virtualizer.setEnabled(true);
        if (!virtualizer.getStrengthSupported()) return;
        TextView bbTextView = new TextView(this);
        bbTextView.setText("虚拟化： ");
        ll.addView(bbTextView);


        SeekBar bbSeekBar = new SeekBar(this);
        bbSeekBar.setMax(100);
        bbSeekBar.setProgress(virtualizer.getRoundedStrength());
        bbSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                virtualizer.setStrength((short) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ll.addView(bbSeekBar);
    }

    private void releaseVirtualizer(){
        if (virtualizer != null) {
            virtualizer.setEnabled(false);
            virtualizer.release();
            virtualizer = null;
        }
    }


    private final List<Short> reverbName = new ArrayList<>();
    private final List<String> reverbVals = new ArrayList<>();

    private void setupPresetReverb() {
        presetReverb = new PresetReverb(0, mediaPlayer.getAudioSessionId());
        presetReverb.setEnabled(true);
        TextView prTitle = new TextView(this);
        prTitle.setText("音场");
        ll.addView(prTitle);
        for (short i = 0; i < equalizer.getNumberOfPresets(); i++) {
            reverbName.add(i);
            reverbVals.add(equalizer.getPresetName(i));
        }

        Spinner sp = new Spinner(this);
        sp.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                reverbVals));
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presetReverb.setPreset(reverbName.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ll.addView(sp);
    }

    private void releasePresetReverb(){
        if (presetReverb != null) {
            presetReverb.setEnabled(false);
            presetReverb.release();
            presetReverb = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0XFF && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
            setupVisualizer();
            setupEqualizer();
            setupBassBoost();
            setupVirtualizer();
            setupPresetReverb();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseBassBoost();
        releaseEqualizer();
        releaseVirtualizer();
        releaseVisualizer();
        releasePresetReverb();
        Log.i("onDestroy", "销毁");

    }
}