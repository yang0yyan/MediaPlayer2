package com.yy.mediaplayer.model.protocol;

import android.content.Context;

import java.io.InputStream;

public class ContentStreamProtocol extends InputStreamProtocol {
    public ContentStreamProtocol(Context sAppContext) {
    }

    @Override
    protected InputStream getInputStream(String uriString) {
        return null;
    }
}
