package com.yy.mediaplayer.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yy.mediaplayer.R;
import com.yy.mediaplayer.activity.adapter.FileAdapter;
import com.yy.mediaplayer.base.BaseActivity;
import com.yy.mediaplayer.databinding.ActivityFileBinding;
import com.yy.mediaplayer.utils.FileUtil;
import com.yy.mediaplayer.utils.PermissionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileActivity extends BaseActivity {
    String path;
    String rootPath;

    private ActivityFileBinding binding;
    List<Map<String, String>> listFileInfo = new ArrayList<>();
    boolean isPermission = false;
    private FileAdapter adapter;

    @Override
    protected View getLayoutId() {
        binding = ActivityFileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        isPermission = PermissionUtil.getPermission(this, PermissionUtil.READ_EXTERNAL_STORAGE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("导入本地");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void initData() {
        File[] f = getExternalMediaDirs();
//        rootPath = path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath();
        rootPath = path = Environment.getExternalStorageDirectory().getPath();
        if (isPermission)
            listFileInfo = FileUtil.getFileFromPath(path);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapter = new FileAdapter(listFileInfo);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new FileAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                path = listFileInfo.get(position).get("path");
                loadFile();
            }
        });
    }

    private void loadFile() {
        listFileInfo = FileUtil.getFileFromPath(path);
        adapter.setNewData(listFileInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_file_tools, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.doc_sure:
                ArrayList<String> list = new ArrayList<>();
                for (Map<String, String> map : listFileInfo) {
                    if (map.get("check").equals("1")) {
                        list.add(map.get("path"));
                    }
                }
                if (list.size() != 0) {
                    Intent intent = new Intent();
                    intent.putExtra("path", list);
                    setResult(0X02, intent);
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!path.equals(rootPath)) {
                path = path.substring(0, path.lastIndexOf("/")).toLowerCase();
                loadFile();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0XFF && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermission = true;
            loadFile();
        }
    }
}