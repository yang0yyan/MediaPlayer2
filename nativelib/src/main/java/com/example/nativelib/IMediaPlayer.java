package com.example.nativelib;

import android.app.Activity;
import android.view.Surface;

public class IMediaPlayer {
    static {
        System.loadLibrary("media_lib");
    }

    public static native int readFile(String filePath, Activity activity);

    public static native void release();

    //--------------------------------视频相关-----------------------------------------

    public static native int initVideo(int width, int height, int rotation);

    public static native int setSurface(Surface surface);

    public static native int removeSurface();

    public static native int videoWrite(byte[] bytes);
}
