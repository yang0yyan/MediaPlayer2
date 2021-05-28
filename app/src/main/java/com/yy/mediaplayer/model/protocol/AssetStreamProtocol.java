package com.yy.mediaplayer.model.protocol;

import android.content.Context;

import java.io.InputStream;

public class AssetStreamProtocol extends InputStreamProtocol {
    public AssetStreamProtocol(Context sAppContext) {
    }

    @Override
    protected InputStream getInputStream(String uriString) {
        return null;
    }
}
