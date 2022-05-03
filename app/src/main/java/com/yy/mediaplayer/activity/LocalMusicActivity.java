package com.yy.mediaplayer.activity;

import android.support.v4.media.session.MediaControllerCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

        binding.etFilename.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.getMusicByFilename(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
    public void onUpdateSuccess(int p) {
        MusicInfoEntity musicInfoEntity = listMusicInfo.get(p);
        musicInfoEntity.setCollection(!musicInfoEntity.isCollection());
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
        MusicInfoEntity musicInfoEntity = listMusicInfo.get(position);
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        linearLayout.setBackgroundColor(0XFF000000);
//
//        TextView deleteTv = new TextView(this);
//        deleteTv.setTextColor(0XFFFFFFFF);
//        deleteTv.setGravity(Gravity.CENTER);
//        deleteTv.setText("刪除");
//
////        LinearLayout.LayoutParams deleteLp = (LinearLayout.LayoutParams) deleteTv.getLayoutParams();
////        deleteLp.weight = 1;
////        deleteTv.setLayoutParams(deleteLp);
//
//        TextView collectionTv = new TextView(this);
//
//        collectionTv.setTextColor(0XFFFFFFFF);
//        collectionTv.setGravity(Gravity.CENTER);
//        collectionTv.setText("收藏");
////        LinearLayout.LayoutParams collectionLp = (LinearLayout.LayoutParams) collectionTv.getLayoutParams();
////        collectionLp.weight = 1;
////        collectionTv.setLayoutParams(collectionLp);
//
//        linearLayout.addView(collectionTv);
//        linearLayout.addView(deleteTv);

        View view = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
        TextView tvCollection = view.findViewById(R.id.tv_collection);
        TextView tvDelete = view.findViewById(R.id.tv_delete);

        if(musicInfoEntity.isCollection()){
            tvCollection.setText("取消收藏");
        }else{
            tvCollection.setText("收藏");
        }

        final PopupWindow popupWindow = new PopupWindow(view, 400, 80);//参数为1.View 2.宽度 3.高度
        popupWindow.setOutsideTouchable(true);//设置点击外部区域可以取消popupWindow
        popupWindow.showAsDropDown(v);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteMusic(musicInfoEntity, position);
                popupWindow.dismiss();
            }
        });
        tvCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MusicInfoEntity copy = new MusicInfoEntity(musicInfoEntity.getId(), musicInfoEntity.getFileName(), musicInfoEntity.getFilePath(), musicInfoEntity.getName(), musicInfoEntity.getAlbum(), musicInfoEntity.getArtist(), musicInfoEntity.getBitrate(), musicInfoEntity.getDuration(), musicInfoEntity.getImageUrl(), musicInfoEntity.isCollection());
                copy.setCollection(!copy.isCollection());

                mPresenter.updateMusic(copy, position);
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