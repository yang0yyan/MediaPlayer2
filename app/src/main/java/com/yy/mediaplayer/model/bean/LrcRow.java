package com.yy.mediaplayer.model.bean;

public class LrcRow {
    private String content;
    private int time;
    private String timeStr;
    private long TotalTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getTotalTime() {
        return TotalTime;
    }

    public void setTotalTime(long totalTime) {
        TotalTime = totalTime;
    }
}
