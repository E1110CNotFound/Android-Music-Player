package com.guoyonghui.musicplayer.util;

/**
 * Created by 永辉 on 2015/7/9.
 */
public class LogHelper {

    private static final String APP_PREFIX = "FEVER_";

    public static String makeLogTag(Class cls) {
        return APP_PREFIX + cls.getSimpleName();
    }

}
