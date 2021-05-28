package com.yy.mediaplayer.model.protocol;

import androidx.annotation.Keep;

import java.nio.ByteBuffer;

@Keep
public interface IStreamProtocol {

    int SUCCESS        = 0;
    int ERROR_OPEN     = -1;
    int ERROR_GET_SIZE = -2;
    int ERROR_READ     = -3;
    int ERROR_SEEK     = -4;

    @Keep
    int open(String uriString);

    @Keep
    long getSize();

    @Keep
    int read(ByteBuffer buffer, int offset, int size);

    @Keep
    int seek(long position, int whence);

    @Keep
    void close();
}