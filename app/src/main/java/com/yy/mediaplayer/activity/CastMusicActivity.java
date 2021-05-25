package com.yy.mediaplayer.activity;

import android.annotation.SuppressLint;
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
import com.yy.mediaplayer.databinding.ActivityCastMusicBinding;
import com.yy.mediaplayer.model.MusicQueueManager;
import com.yy.mediaplayer.net.bean.MusicBean;
import com.yy.mediaplayer.net.presenter.CastMusicPresenter;
import com.yy.mediaplayer.net.view.CastMusicView;
import com.yy.mediaplayer.room.entity.MusicInfoEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CastMusicActivity extends BaseNetMediaActivity<CastMusicPresenter> implements CastMusicView.view, MusicAdapter.OnItemClickListener {


    private ActivityCastMusicBinding binding;
    List<MusicInfoEntity> listMusicInfo = new ArrayList<>();
    private MusicAdapter adapter;
    private boolean isConnect;
    private MusicControlsFragment mMusicControlsFragment;

    @Override
    protected View getLayoutId() {
        binding = ActivityCastMusicBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle("在线音乐");
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
    protected CastMusicPresenter createRoomPresenter() {
        return new CastMusicPresenter(this);
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
        mPresenter.getMusicList();
    }

    private static final String CATALOG_URL =
            "https://storage.googleapis.com/automotive-media/music.json";

    @Override
    public void onSuccess(List<MusicBean> list) {
        listMusicInfo.clear();
        for (MusicBean musicBean : list) {
            MusicInfoEntity musicInfo = new MusicInfoEntity();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

            String id = "ORD" + sdf.format(System.currentTimeMillis());
            musicInfo.setId(id);
            musicInfo.setAlbum(musicBean.getAlbum());
            musicInfo.setArtist(musicBean.getArtist());
            musicInfo.setDuration(musicBean.getDuration() * 1000);
            musicInfo.setName(musicBean.getTitle());
            int slashPos = CATALOG_URL.lastIndexOf('/');
            String path = CATALOG_URL.substring(0, slashPos + 1);
            if (!musicBean.getSource().startsWith("http")) {
                musicInfo.setFilePath(path + musicBean.getSource());
            } else {
                musicInfo.setFilePath(musicBean.getSource());
            }
            musicInfo.setImageUrl(path + musicBean.getImage());
            listMusicInfo.add(musicInfo);
        }
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
    public void onMoreClick(View v, int position) {

    }

    @Override
    protected void onMediaControllerConnected() {
        super.onMediaControllerConnected();
        isConnect = true;
        mMusicControlsFragment.onConnected();
    }
}