package com.yy.mediaplayer.utils;

import android.os.Environment;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

public class FileUtilTest extends TestCase {

    public void testGetFileFromPath() {
        List<Map<String, String>> asd = FileUtil.getFileFromPath(Environment.getExternalStorageDirectory().getPath());
        System.out.println(asd.toString());
    }
}