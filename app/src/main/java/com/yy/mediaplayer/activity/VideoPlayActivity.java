package com.yy.mediaplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityVideoPlayBinding;
import com.yy.mediaplayer.model.IMediaPlayer;
import com.yy.mediaplayer.model.video.MediaCodecManager2;
import com.yy.mediaplayer.utils.ContentUtil;
import com.yy.mediaplayer.utils.PermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VideoPlayActivity extends BaseActivity {
    private ActivityVideoPlayBinding binding;

    private static final String TAG = "MediaActivity";
    List<Map<String, Object>> list = new ArrayList<>();
    LinkedList<String> logLines = new LinkedList<>();
    private MediaCodecManager2 mediaCodecManager;


    public int width;
    public int height;
    private boolean isChecked = false;

    @Override
    protected View getLayoutId() {
        binding = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {

//        button = findViewById(R.id.button);
//        switch1 = findViewById(R.id.switch1);
//        cl_surface_window = findViewById(R.id.cl_surface_window);
//        surfaceView = findViewById(R.id.surface);
//        tvLog = findViewById(R.id.tv_log);
//        scroll = findViewById(R.id.scroll);
//        resolver = getContentResolver();

        binding.surface.getHolder().addCallback(callback);

        mediaCodecManager = new MediaCodecManager2(this, binding.surface.getHolder().getSurface());

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //屏幕宽
        int widthPixels = displayMetrics.widthPixels;
        //屏幕高
        int heightPixels = displayMetrics.heightPixels;
        //屏幕密度
        float density = displayMetrics.density;
        width = widthPixels;
        height = widthPixels * widthPixels / heightPixels;
//        new Thread(runnable).start();

        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) binding.clSurfaceWindow.getLayoutParams();
        params1.height = height;
        params1.width = width;
        binding.clSurfaceWindow.setLayoutParams(params1);

        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) binding.surface.getLayoutParams();
        params2.height = height;
        params2.width = width;
        binding.surface.setLayoutParams(params2);

        binding.button.setOnClickListener(new View.OnClickListener() {
            boolean start = true;

            @Override
            public void onClick(View v) {
                openFile();
            }
        });
        binding.surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mediaCodecManager.play();
            }
        });
        binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                VideoPlayActivity.this.isChecked = isChecked;
            }
        });
    }

    @Override
    protected void initData() {
        PermissionUtil.getPermissions(this, PermissionUtil.READ_EXTERNAL_STORAGE);
    }

    @Keep //混淆后不加注解会报错
    public void setSurfaceViewLayoutParams(final int width, final int height) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int w = width;
                int h = height;
                float scale = 1;
                if ((w / h) > (VideoPlayActivity.this.width / VideoPlayActivity.this.height)) {
                    scale = (float) VideoPlayActivity.this.width / (float) w;
                } else {
                    scale = (float) VideoPlayActivity.this.height / (float) h;
                }
                w = (int) ((float) w * scale);
                h = (int) ((float) h * scale);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.surface.getLayoutParams();
                params.height = h;
                params.width = w;
                binding.surface.setLayoutParams(params);
            }
        });

    }

    public void logFromUi(String msg) {
        logLines.add(msg);
        if (logLines.size() > 200) {
            logLines.removeFirst();
        }
        final StringBuilder stringBuffer = new StringBuilder();
        for (String line : logLines) {
            stringBuffer.append(line).append('\n');
        }
        binding.tvLog.post(new Runnable() {
            @Override
            public void run() {
                binding.tvLog.setText(stringBuffer.toString());
                binding.scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 0X01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0X01 && resultCode == Activity.RESULT_OK) {
            final Uri uri = data.getData();
            final String path = ContentUtil.getPath(this, uri);
//            int code = IMediaPlayer.setup(ContentUtil.getPath(this,uri));
            Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isChecked)
                        mediaCodecManager.init(uri);
                    else
                        play(path);
                }
            }).start();
        }
    }

    public void play(String path_) {
        String path = "/storage/emulated/0/Android/media/2.mkv";
//        String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath() + "/2.mkv";
        if (null != path_)
            path = path_;
        File file = new File(path);
        if (!file.exists())
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoPlayActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                }
            });

        int code = IMediaPlayer.setup(path, this);
        final int finalCode = code;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VideoPlayActivity.this, "init: " + finalCode, Toast.LENGTH_SHORT).show();
            }
        });
        if (code == 1 && surfaceCreate)
            IMediaPlayer.setSurface(binding.surface.getHolder().getSurface(), 1);
        if (code == 1) {
            code = IMediaPlayer.play();
            final int finalCode1 = code;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoPlayActivity.this, "play: " + finalCode1, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    boolean surfaceCreate = false;

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull final SurfaceHolder holder) {
            surfaceCreate = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    IMediaPlayer.setSurface(holder.getSurface(), 1);
                }
            }).start();

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            surfaceCreate = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    IMediaPlayer.setSurface(null, 0);
                }
            }).start();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mediaCodecManager)
            mediaCodecManager.release();
        IMediaPlayer.release();
    }

}