package com.yy.mediaplayer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    public static List<Map<String, String>> getFileFromPath(String mPath) {
        return getFileFromPath(new File(mPath));
    }

    public static List<Map<String, String>> getFileFromPath(File file) {
        List<Map<String, String>> list = new ArrayList<>();
        String path = file.getPath();
        File[] files = file.listFiles();
        if (null != files) {
            for (File f : files) {
                if (!f.isDirectory() || f.getName().startsWith(".")) {//如果不是路径或者以 . 开头的文件夹 则直接跳过
                    continue;
                }
                Map<String, String> map = new HashMap<>();
                map.put("name", f.getName());
                map.put("path", path + "/" + f.getName());
                map.put("check", "0");
                list.add(map);
            }
        }
        return list;
    }
}
