package com.guoyonghui.musicplayer.model;

/**
 * Created by 永辉 on 2015/7/14.
 */
public class Detail {

    private String mTitle;

    private String mContent;

    public Detail(String title, String content) {
        mTitle = title;
        mContent = content;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
