package com.guoyonghui.musicplayer.model;

/**
 * Detail
 *
 * @author Guo Yonghui
 *         <p/>
 *         音乐详情类
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