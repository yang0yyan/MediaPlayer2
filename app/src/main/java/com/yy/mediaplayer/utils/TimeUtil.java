package com.yy.mediaplayer.utils;

public class TimeUtil {
    /**
     * 毫秒转分钟
     *
     * @param Milli
     * @return
     */
    public static String MilliToMinut(long Milli) {
        long minutes = Milli / (1000 * 60);
        long seconds = (Milli - minutes * (1000 * 60)) / (1000);
        String diffTime = "";
        if (seconds < 10) {
            diffTime = minutes + ":0" + seconds;
        } else {
            diffTime = minutes + ":" + seconds;
        }
        return diffTime;
    }
}

