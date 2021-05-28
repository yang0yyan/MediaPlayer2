package com.yy.mediaplayer.model.openGL;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class OpenGLES20Activity extends Activity {

    private MyGLSurfaceView mGLView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
}
