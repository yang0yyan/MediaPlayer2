package com.yy.mediaplayer.model;

import android.app.Activity;
import android.view.Surface;

public class IMediaPlayer {
    static {
        System.loadLibrary("media_lib");
    }

    public static native int setup(String filePath, Activity activity);

    public static native int setSurface(Surface surface, int toShow);

    public static native int play();

    public static native void release();


    /**
     * 硬编
     */
    public static native int init(Object surface, int width, int height);

    public static native int write(byte[] bytes);
}
