package com.yy.mediaplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.Response;

public class FileUtil {
    public static List<Map<String, String>> getFileFromPath(String mPath) {
        if (null == mPath || mPath.equals("")) return null;
        return getFileFromPath(new File(mPath));
    }

    public static List<Map<String, String>> getFileFromPath(File file) {
        List<Map<String, String>> list = new ArrayList<>();
        String path = file.getPath();
        File[] files = file.listFiles();
        if (null != files) {
            Set<String> treeSet = new TreeSet<>();//用于排序
            for (File f : files) {
                if (!f.isDirectory() || f.getName().startsWith(".")) {//如果不是路径或者以 . 开头的文件夹 则直接跳过
                    continue;
                }
                treeSet.add(f.getName());
            }
            for(String name : treeSet){
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("path", path + "/" + name);
                map.put("check", "0");
                list.add(map);
            }
        }
        return list;
    }

    public static boolean deleteFile(String filePath){
        File file = new File(filePath);
        if(file.exists())
            return file.delete();
        return true;
    }
}
