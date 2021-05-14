package com.yy.mediaplayer.activity;

import android.support.v4.media.session.MediaControllerCompat;
import android.view.MenuItem;
import android.view.View;

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
    public void onItemClick(View v, int position) {
        if (!isConnect) return;
        String id = listMusicInfo.get(position).getId();
        MediaControllerCompat.getMediaController(this).getTransportControls()
                .playFromMediaId(id, null);
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