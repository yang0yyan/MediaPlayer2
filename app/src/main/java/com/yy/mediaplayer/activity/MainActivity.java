package com.yy.mediaplayer.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yy.mediaplayer.R;
import com.yy.mediaplayer.activity.adapter.ViewPagerAdapter;
import com.yy.mediaplayer.activity.fragment.MusicControlsFragment;
import com.yy.mediaplayer.activity.fragment.MusicFragment;
import com.yy.mediaplayer.activity.fragment.VideoFragment;
import com.yy.mediaplayer.base.BaseNetMediaActivity;
import com.yy.mediaplayer.databinding.ActivityMainBinding;
import com.yy.mediaplayer.net.bean.MusicBean;
import com.yy.mediaplayer.net.presenter.MainPresenter;
import com.yy.mediaplayer.net.view.MainView;
import com.yy.mediaplayer.service.MediaService;
import com.yy.mediaplayer.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNetMediaActivity<MainPresenter> implements MainView.view {

    private static final String TAG = LogHelper.makeLogTag(MusicPlayActivity.class);

    private ActivityMainBinding binding;

    List<Fragment> listFragment = new ArrayList<>();
    List<String> listTitle = new ArrayList<>();
    private ViewPagerAdapter adapter;
    private MusicControlsFragment mMusicControlsFragment;

    @Override
    protected View getLayoutId() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        LogHelper.d(TAG, "initView: ");
        setActionBar();
        listFragment.clear();
        listTitle.clear();
        FragmentManager manager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        listFragment.add(new VideoFragment());
        listFragment.add(new MusicFragment());
        listTitle.add("视频");
        listTitle.add("音乐");
        adapter.setData(listFragment, listTitle);
        binding.viewPager.setAdapter(adapter);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        binding.viewPager.addOnPageChangeListener(mOnPageChangeListener);

        mMusicControlsFragment = (MusicControlsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_music_controls);
        if (mMusicControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (null == actionBar) return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("title");
        actionBar.setSubtitle("subtitle");
    }

    @Override
    protected MainPresenter createRoomPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void initData() {
//        mPresenter.getMusicList();
        startService(new Intent(this, MediaService.class));
    }


    @Override
    protected void onMediaControllerConnected() {
        mMusicControlsFragment.onConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_tools, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.addLocal:
                startActivity(new Intent(this, ScanActivity.class));
                break;
            case R.id.actionSet:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            case R.id.finish:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                boolean is = isTaskRoot();
                Toast.makeText(this, "再按一次退出" + is, Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            }
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MediaService.class));
        binding.viewPager.clearOnPageChangeListeners();
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_video:
                    binding.viewPager.setCurrentItem(listTitle.indexOf("视频"));
                    return true;
                case R.id.navigation_music:
                    binding.viewPager.setCurrentItem(listTitle.indexOf("音乐"));
                    return true;
            }
            return false;
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            binding.bottomNavigationView.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onSuccess(List<MusicBean> list) {

    }
}