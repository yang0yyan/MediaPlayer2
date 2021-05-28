package com.yy.mediaplayer.model.protocol;

import java.io.InputStream;

public class FileStreamProtocol extends InputStreamProtocol {

    @Override
    protected InputStream getInputStream(String uriString) {
        return null;
    }
}
