package com.yy.mediaplayer.net.bean;

import java.io.Serializable;
import java.util.List;

public class BaseModel<T> implements Serializable {
    private String reason;
    private int errno;
    private List<T> music;

    public BaseModel(String reason, int error_code) {
        this.reason = reason;
        this.errno = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getError_code() {
        return errno;
    }

    public void setError_code(int error_code) {
        this.errno = error_code;
    }

    public List<T> getMusic() {
        return music;
    }

    public void setMusic(List<T> music) {
        this.music = music;
    }


    @Override
    public String toString() {
        return "BaseModel{" +
                "reason='" + reason + '\'' +
                ", errno=" + errno +
                ", music=" + music +
                '}';
    }
}