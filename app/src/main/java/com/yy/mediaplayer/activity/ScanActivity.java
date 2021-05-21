package com.yy.mediaplayer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yy.mediaplayer.activity.adapter.MusicAdapter;
import com.yy.mediaplayer.base.BaseNetActivity;
import com.yy.mediaplayer.databinding.ActivityScanBinding;
import com.yy.mediaplayer.net.presenter.ScanRoomPresenter;
import com.yy.mediaplayer.net.view.ScanRoomView;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;
import com.yy.mediaplayer.utils.ToastUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseNetActivity<ScanRoomPresenter> implements View.OnClickListener, ScanRoomView.view {

    private ActivityScanBinding binding;
    private List<MusicInfoEntity> listMusicInfo = new ArrayList<>();
    private List<MusicInfoEntity> listMusicInfo2 = new ArrayList<>();
    List<String> listScanFile;
    private MusicAdapter adapter;
    private FileScan fileScan;

    @Override
    protected View getLayoutId() {
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected ScanRoomPresenter createRoomPresenter() {
        return new ScanRoomPresenter(this);
    }

    @Override
    protected void initView() {
        binding.btnScan.setOnClickListener(this);
        binding.tvJumpFile.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("文件扫描");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void initData() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MusicAdapter(listMusicInfo);
        binding.recyclerView.setAdapter(adapter);

        fileScan = new FileScan(this);
        fileScan.setOnScanFileListener(new FileScan.OnScanFileListener() {
            @Override
            public void onStart() {
                binding.btnScan.setText("停止扫描");
                binding.tvTip.setText("扫描中……");
                ToastUtil.showToast("开始扫描");
            }

            @Override
            public void onFinish(List<MusicInfoEntity> list) {
                ToastUtil.showToast("完成扫描");
                binding.tvTip.setText("");
                if (list.size() == 0) {
                    binding.btnScan.setText("开始扫描");
                } else {
                    binding.btnScan.setText("添加音乐");
                    binding.constraintLayout.setVisibility(View.GONE);
                    listMusicInfo = list;
                    adapter.setNewData(listMusicInfo);
                }
            }

            @Override
            public void onScanning(String filePath) {
                binding.tvFileName.setText(filePath);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnScan) {
            String txt = binding.btnScan.getText().toString();
            if (txt.equals("开始扫描")) {
                if (null == listScanFile || listScanFile.size() == 0) {
                    ToastUtil.showToast("请选择自定义路径");
                    return;
                }
                fileScan.start(listScanFile);
            } else if (txt.equals("停止扫描")) {
                fileScan.stop();
            } else if (txt.equals("添加音乐")) {
                addMusic();
            }
        } else if (v == binding.tvJumpFile) {
            startActivityForResult(new Intent(this, FileActivity.class), 0X01);
        }
    }

    private void addMusic() {
        mRoomPresenter.insert(listMusicInfo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileScan.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0X01 && resultCode == 0X02) {
            listScanFile = data.getStringArrayListExtra("path");
        }
    }

    @Override
    public void onInsertSuccess(long[] ls) {
        Intent intent = new Intent();
        intent.putExtra("order", 1);
        setResult(0X02, intent);
        finish();
    }

    /**
     * --------------------------------------------------------
     */
    static class FileScan {
        private Activity activity;
        private boolean isStart = false;
        List<MusicInfoEntity> list = new ArrayList<>();
        MediaMetadataRetriever retriever;

        public FileScan(Activity activity) {
            this.activity = activity;
        }

        public void start(List<String> listScanFile) {
            if (isStart || null == listScanFile) return;
            isStart = true;
            if (null != scanFileListener)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanFileListener.onStart();
                    }
                });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    scanFileMusic(listScanFile);
                }
            }).start();

        }

        public void stop() {
            isStart = false;
        }

        private void scanFileMusic(List<String> listFile) {
            retriever = new MediaMetadataRetriever();
            for (String s : listFile) {
                if (!isStart) break;
                scanFile(new File(s));
            }
            retriever.release();
            isStart = false;
            if (null != scanFileListener)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanFileListener.onFinish(list);
                    }
                });

        }

        private void scanFile(File file) {
            File[] files = file.listFiles();
            if (null == files) return;
            for (File f : files) {
                if (!isStart) break;
                if (f.isDirectory()) {
                    scanFile(f);
                } else {
                    if (null != scanFileListener)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scanFileListener.onScanning(f.getPath());
                            }
                        });
                    MusicInfoEntity bean = getMusicInfo(f);
                    if (null == bean) continue;
                    list.add(bean);
                }
            }

        }


        private MusicInfoEntity getMusicInfo(File file) {
            MusicInfoEntity bean = null;
            if (file.getName().endsWith(".mp3") || file.getName().endsWith(".flac") ||
                    file.getName().endsWith(".wav")) {
                bean = new MusicInfoEntity();
                try {
                    retriever.setDataSource(file + "");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return null;
                }
                //名称
                String name = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                //专辑标题信息
                String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                //艺术家的信息
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                //平均比特率（以比特/秒），如果可用的话
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                //播放时长
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                if (name == null) {
                    name = file.getName();
                }
                if (album == null) {
                    album = file.getName();
                }
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

                String id = "ORD" + sdf.format(System.currentTimeMillis());
                bean.setId(id);
                bean.setFileName(file.getName());
                bean.setFilePath(file.getPath());
                bean.setName(name);
                bean.setAlbum(album);
                bean.setArtist(artist);
                bean.setBitrate(bitrate);
                bean.setDuration(Long.parseLong(duration));
            }
            return bean;
        }

        OnScanFileListener scanFileListener;

        public void setOnScanFileListener(OnScanFileListener scanFileListener) {
            this.scanFileListener = scanFileListener;
        }

        public interface OnScanFileListener {
            void onStart();

            void onFinish(List<MusicInfoEntity> list);

            void onScanning(String filePath);
        }

    }
}