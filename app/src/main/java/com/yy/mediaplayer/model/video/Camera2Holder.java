package com.yy.mediaplayer.model.video;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.Collections;

public class Camera2Holder implements SurfaceHolder.Callback{
    private static final String TAG = "CameraHolder";
    Context context;
    private CameraManager mCameraManager;
    private CameraDevice inUseCameraDevice = null;
    String frontCameraId = "";
    String backCameraId = "";
    private Surface preview = null;
    private CaptureRequest.Builder builder;

    public Camera2Holder(Context context) {
        this.context = context;
        try {
            init();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "CameraHolder: 初始化失败");
        }
    }

    private void init() throws CameraAccessException {
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager != null) {
            String[] strings = mCameraManager.getCameraIdList();
            for (String cameraId : strings) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                int lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontCameraId = cameraId;
                    Log.d(TAG, "init: 前置摄像头");
                } else if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    backCameraId = cameraId;
                    Log.d(TAG, "init: 后置摄像头");
                }

                int[] ints = cameraCharacteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES);
                Log.d(TAG, "init: 像差校正模式" + Arrays.toString(ints));
                ints = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES);
                Log.d(TAG, "init: 自动曝光反带模式" + Arrays.toString(ints));
            }
        }
    }

    public void setSurface(Surface surface) {
        this.preview = surface;
    }

    public void openCamera() {
//        if (mCameraManager == null) return;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "权限不足", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (null != inUseCameraDevice) {
                inUseCameraDevice.close();
            }
            mCameraManager.openCamera(backCameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "openCamera: " + e.getMessage());
            Toast.makeText(context, "打开相机失败", Toast.LENGTH_SHORT).show();
        }
    }


    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            inUseCameraDevice = camera;

            Log.d(TAG, "onOpened: 相机已打开");
            if (null == preview) return;
//            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    camera.createCaptureSession(null);
//                } else {
//
//                }
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }

            try {
                builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(preview);
                camera.createCaptureSession(Collections.singletonList(preview),stateCallback2,null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            if (error == CameraDevice.StateCallback.ERROR_CAMERA_IN_USE) {
                Log.d(TAG, "onError: 设备使用中");
            }
        }
    };

    private final CameraCaptureSession.StateCallback stateCallback2 = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigured: ");
            try {
                session.setRepeatingRequest(builder.build(), captureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigureFailed: ");
        }

        @Override
        public void onSurfacePrepared(@NonNull CameraCaptureSession session, @NonNull Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG, "onSurfacePrepared: ");
        }
    };

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            Log.d(TAG, "onCaptureStarted: ");
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            Log.d(TAG, "onCaptureProgressed: ");
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Log.d(TAG, "onCaptureCompleted: ");
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.d(TAG, "onCaptureFailed: ");
        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            Log.d(TAG, "onCaptureSequenceCompleted: ");
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            Log.d(TAG, "onCaptureSequenceAborted: ");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
            Log.d(TAG, "onCaptureBufferLost: ");
        }
    };

    public void close() {
        if (null != inUseCameraDevice)
            inUseCameraDevice.close();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        setSurface(holder.getSurface());
        openCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}
