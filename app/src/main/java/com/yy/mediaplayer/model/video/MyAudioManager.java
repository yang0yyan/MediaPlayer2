package com.yy.mediaplayer.model.video;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.yy.mediaplayer.activity.VideoPlayActivity;
import com.yy.mediaplayer.utils.PcmToWavUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyAudioManager {
    private Context context;
    private VideoPlayActivity activity;
    ContentResolver resolver = null;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/Download";
    private Uri filePath = null;
    private boolean isRecording = false;
    private boolean playRecord = false;

    private final int audioSource = MediaRecorder.AudioSource.MIC;
    private final int sampleRateInHz = 8000;
    private final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInBytes;
    private int bufferSizeInBytes2;

    private AudioRecord mAudioRecord = null;
    private AudioTrack mAudioTrack = null;

    public MyAudioManager(Activity activity) {
        this.context = activity;
        this.activity = (VideoPlayActivity) activity;
        resolver = context.getContentResolver();
        createAudioRecord();
        createAudioTrack();
    }

    private void createAudioRecord() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);  //audioRecord能接受的最小的buffer大小
        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
    }

    private void createAudioTrack() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        AudioFormat format = new AudioFormat.Builder()
                .setChannelMask(channelConfig)
                .setEncoding(audioFormat)
                .setSampleRate(sampleRateInHz)
                .build();
        bufferSizeInBytes2 = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        mAudioTrack = new AudioTrack(attributes, format, bufferSizeInBytes2, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    //开始录音
    public void startRecording() {
        if (null == mAudioRecord) return;
        mAudioRecord.startRecording();
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                write(System.currentTimeMillis() + "");
            }
        }).start();
    }

    //结束录音
    public void stopRecording() {
        if (null == mAudioRecord) return;
        mAudioRecord.stop();
        isRecording = false;
    }

    //播放录音
    public void playRecord() {
        playRecord = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                read(filePath);
            }
        }).start();
    }

    //写数据
    public void write(String filename) {
        OutputStream os = null;
        filePath = createFile(filename);
        try {
            os = resolver.openOutputStream(filePath);
            byte[] data = new byte[bufferSizeInBytes];
            int read = 0;
            while (isRecording) {
                read = mAudioRecord.read(data, 0, bufferSizeInBytes);
                // 如果读取音频数据没有出现错误，就将数据写入到文件
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    os.write(data);
                }
            }
            os.flush();
            os.close();
            //addHeadData(filename);
            playRecord();
        } catch (IOException e) {
            e.printStackTrace();
            mAudioRecord.stop();
            if (null != os) {
                try {
                    os.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    //读数据
    public void read(Uri filePath) {
        InputStream fis = null;
        try {
            fis = resolver.openInputStream(filePath);// new FileInputStream(path + "/" + filename + "pcm");
            byte[] data = new byte[bufferSizeInBytes2];
            int read = 0;
            mAudioTrack.play();
            while (playRecord) {
                read = fis.read(data, 0, bufferSizeInBytes2);
                // 如果读取音频数据没有出现错误，就将数据写入到AudioTrack
                if (read == AudioTrack.ERROR_INVALID_OPERATION || read == AudioTrack.ERROR_BAD_VALUE) {
                    continue;
                }
                if (read != 0 && read != -1) {
                    onAudioFrame(data);

                }
                if (read == -1)
                    playRecord = false;
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            mAudioRecord.stop();
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void onAudioFrame(byte[] data) {
        mAudioTrack.write(data, 0, data.length);
    }

    public Uri createFile(String filename) {

        Uri audioCollection = MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentValues newSongDetails = new ContentValues();
        newSongDetails.put(MediaStore.Audio.Media.DISPLAY_NAME,
                filename);
        newSongDetails.put(MediaStore.Images.Media.MIME_TYPE, "audio/x-wav");
        return resolver
                .insert(audioCollection, newSongDetails);
    }


    private void addHeadData(String filename) {
        File pcmFile = new File(path, filename + ".pcm");
        File handlerWavFile = new File(path, filename + ".wav");
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(sampleRateInHz, channelConfig, audioFormat);
        pcmToWavUtil.pcmToWav(pcmFile.toString(), handlerWavFile.toString());
    }

    public void close() {
        if (null != mAudioRecord) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }

        if (null != mAudioTrack) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }
}
