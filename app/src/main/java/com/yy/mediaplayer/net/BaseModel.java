package com.yy.mediaplayer.net;

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

    public List<T> getResult() {
        return music;
    }

    public void setResult(List<T> result) {
        this.music = result;
    }


    @Override
    public String toString() {
        return "BaseModel{" +
                "reason='" + reason + '\'' +
                ", errno=" + errno +
//                ", albumdata=" + music.toString() +
                '}';
    }
}