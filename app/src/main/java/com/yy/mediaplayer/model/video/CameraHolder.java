package com.yy.mediaplayer.model.video;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraHolder implements SurfaceHolder.Callback {
    private static final String TAG = "CameraHolder";
    private Camera camera;

    public CameraHolder() {
        init();
    }

    private void init() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);
        camera.setPreviewCallback(previewCallback);
    }

    private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            data = null;

            //Log.d(TAG, "onPreviewFrame: "+ Arrays.toString(data));
        }
    };


    public SurfaceHolder.Callback getCallback() {
        return this;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        camera.release();
    }
}
