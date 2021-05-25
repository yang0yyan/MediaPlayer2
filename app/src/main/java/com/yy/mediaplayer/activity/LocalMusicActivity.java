package com.yy.mediaplayer.activity;

import android.support.v4.media.session.MediaControllerCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.activity.adapter.MusicAdapter;
import com.yy.mediaplayer.activity.fragment.MusicControlsFragment;
import com.yy.mediaplayer.base.BaseNetMediaActivity;
import com.yy.mediaplayer.databinding.ActivityLocalMusicBinding;
import com.yy.mediaplayer.model.MusicQueueManager;
import com.yy.mediaplayer.net.presenter.LocalMusicPresenter;
import com.yy.mediaplayer.net.view.LocalMusicView;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;
import com.yy.mediaplayer.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends BaseNetMediaActivity<LocalMusicPresenter> implements LocalMusicView.view, MusicAdapter.OnItemClickListener {

    List<MusicInfoEntity> listMusicInfo = new ArrayList<>();
    private ActivityLocalMusicBinding binding;
    private MusicAdapter adapter;
    private MusicControlsFragment mMusicControlsFragment;
    boolean isConnect = false;

    @Override
    protected View getLayoutId() {
        binding = ActivityLocalMusicBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle("本地音乐");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mMusicControlsFragment = (MusicControlsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_music_controls);
        if (mMusicControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
    }

    @Override
    protected LocalMusicPresenter createRoomPresenter() {
        return new LocalMusicPresenter(this);
    }

    @Override
    protected void initData() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapter = new MusicAdapter(listMusicInfo);
        adapter.setOnItemClick(this);
        binding.recyclerView.setAdapter(adapter);
        refreshData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        mPresenter.getMusic();
    }

    @Override
    public void onSuccess(List<MusicInfoEntity> listMusicInfo) {
        this.listMusicInfo = listMusicInfo;
        MusicQueueManager.getInstance().setListMusicInfo(listMusicInfo);
        adapter.setNewData(listMusicInfo);
    }

    @Override
    public void onDeleteSuccess(int p) {
        FileUtil.deleteFile(listMusicInfo.get(p).getFilePath());
        listMusicInfo.remove(p);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (!isConnect) return;
        String id = listMusicInfo.get(position).getId();
        MediaControllerCompat.getMediaController(this).getTransportControls()
                .playFromMediaId(id, null);
    }

    @Override
    public void onMoreClick(View v, int position) {
        TextView textView = new TextView(this);
        textView.setBackgroundColor(0XFF000000);
        textView.setTextColor(0XFFFFFFFF);
        textView.setGravity(Gravity.CENTER);
        textView.setText("刪除");

        final PopupWindow popupWindow = new PopupWindow(textView,200,80);//参数为1.View 2.宽度 3.高度
        popupWindow.setOutsideTouchable(true);//设置点击外部区域可以取消popupWindow
        popupWindow.showAsDropDown(v);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteMusic(listMusicInfo.get(position),position);
                popupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onMediaControllerConnected() {
        super.onMediaControllerConnected();
        isConnect = true;
        mMusicControlsFragment.onConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}