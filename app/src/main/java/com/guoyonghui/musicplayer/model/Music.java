package com.guoyonghui.musicplayer.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

/**
 * Created by 永辉 on 2015/6/23.
 */
public class Music implements Parcelable {

    public static final Parcelable.Creator<Music> CREATOR = new Creator<Music>() {

        @Override
        public Music createFromParcel(Parcel source) {
            Music music = new Music();

            music.setTitle(source.readString());
            music.setTitleKey(source.readString());
            music.setArtist(source.readString());
            music.setArtistKey(source.readString());
            music.setComposer(source.readString());
            music.setAlbum(source.readString());
            music.setAlbumKey(source.readString());
            music.setDisplayName(source.readString());
            music.setMimeType(source.readString());
            music.setPath(source.readString());
            music.setId(source.readInt());
            music.setArtistId(source.readInt());
            music.setAlbumId(source.readInt());
            music.setYear(source.readInt());
            music.setTrack(source.readInt());
            music.setDuration(source.readInt());
            music.setSize(source.readInt());
            music.setIsRingtone(source.readByte() != 0);
            music.setIsPodcast(source.readByte() != 0);
            music.setIsAlarm(source.readByte() != 0);
            music.setIsMusic(source.readByte() != 0);
            music.setIsNotification(source.readByte() != 0);
            music.setIsPlaying(source.readByte() != 0);

            return music;
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    private String mTitle;

    private String mTitleKey;

    private String mArtist;

    private String mArtistKey;

    private String mComposer;

    private String mAlbum;

    private String mAlbumKey;

    private String mDisplayName;

    private String mMimeType;

    private String mPath;

    private int mId;

    private int mArtistId;

    private int mAlbumId;

    private int mYear;

    private int mTrack;

    private int mDuration = 0;

    private int mSize = 0;

    private boolean mIsRingtone;

    private boolean mIsPodcast;

    private boolean mIsAlarm;

    private boolean mIsMusic;

    private boolean mIsNotification;

    private boolean mIsPlaying;

    public Music() {
    }

    public Music(Bundle bundle) {
        mId = bundle.getInt(MediaStore.Audio.Media._ID);
        mTitle = bundle.getString(MediaStore.Audio.Media.TITLE);
        mTitleKey = bundle.getString(MediaStore.Audio.Media.TITLE_KEY);
        mArtist = bundle.getString(MediaStore.Audio.Media.ARTIST);
        mArtistKey = bundle.getString(MediaStore.Audio.Media.ARTIST_KEY);
        mComposer = bundle.getString(MediaStore.Audio.Media.COMPOSER);
        mAlbum = bundle.getString(MediaStore.Audio.Media.ALBUM);
        mAlbumKey = bundle.getString(MediaStore.Audio.Media.ALBUM_KEY);
        mDisplayName = bundle.getString(MediaStore.Audio.Media.DISPLAY_NAME);
        mYear = bundle.getInt(MediaStore.Audio.Media.YEAR);
        mMimeType = bundle.getString(MediaStore.Audio.Media.MIME_TYPE);
        mPath = bundle.getString(MediaStore.Audio.Media.DATA);
        mArtistId = bundle.getInt(MediaStore.Audio.Media.ARTIST_ID);
        mAlbumId = bundle.getInt(MediaStore.Audio.Media.ALBUM_ID);
        mTrack = bundle.getInt(MediaStore.Audio.Media.TRACK);
        mDuration = bundle.getInt(MediaStore.Audio.Media.DURATION);
        mSize = bundle.getInt(MediaStore.Audio.Media.SIZE);
        mIsRingtone = bundle.getInt(MediaStore.Audio.Media.IS_RINGTONE) == 1;
        mIsPodcast = bundle.getInt(MediaStore.Audio.Media.IS_PODCAST) == 1;
        mIsAlarm = bundle.getInt(MediaStore.Audio.Media.IS_ALARM) == 1;
        mIsMusic = bundle.getInt(MediaStore.Audio.Media.IS_MUSIC) == 1;
        mIsNotification = bundle.getInt(MediaStore.Audio.Media.IS_NOTIFICATION) == 1;
        mIsPlaying = false;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitleKey() {
        return mTitleKey;
    }

    public void setTitleKey(String titleKey) {
        mTitleKey = titleKey;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getArtistKey() {
        return mArtistKey;
    }

    public void setArtistKey(String artistKey) {
        mArtistKey = artistKey;
    }

    public String getComposer() {
        return mComposer;
    }

    public void setComposer(String composer) {
        mComposer = composer;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getAlbumKey() {
        return mAlbumKey;
    }

    public void setAlbumKey(String albumKey) {
        mAlbumKey = albumKey;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getArtistId() {
        return mArtistId;
    }

    public void setArtistId(int artistId) {
        mArtistId = artistId;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(int albumId) {
        mAlbumId = albumId;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getTrack() {
        return mTrack;
    }

    public void setTrack(int track) {
        mTrack = track;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public boolean isRingtone() {
        return mIsRingtone;
    }

    public void setIsRingtone(boolean isRingtone) {
        mIsRingtone = isRingtone;
    }

    public boolean isPodcast() {
        return mIsPodcast;
    }

    public void setIsPodcast(boolean isPodcast) {
        mIsPodcast = isPodcast;
    }

    public boolean isAlarm() {
        return mIsAlarm;
    }

    public void setIsAlarm(boolean isAlarm) {
        mIsAlarm = isAlarm;
    }

    public boolean isMusic() {
        return mIsMusic;
    }

    public void setIsMusic(boolean isMusic) {
        mIsMusic = isMusic;
    }

    public boolean isNotification() {
        return mIsNotification;
    }

    public void setIsNotification(boolean isNotification) {
        mIsNotification = isNotification;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        mIsPlaying = isPlaying;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mTitleKey);
        dest.writeString(mArtist);
        dest.writeString(mArtistKey);
        dest.writeString(mComposer);
        dest.writeString(mAlbum);
        dest.writeString(mAlbumKey);
        dest.writeString(mDisplayName);
        dest.writeString(mMimeType);
        dest.writeString(mPath);
        dest.writeInt(mId);
        dest.writeInt(mArtistId);
        dest.writeInt(mAlbumId);
        dest.writeInt(mYear);
        dest.writeInt(mTrack);
        dest.writeInt(mDuration);
        dest.writeInt(mSize);
        dest.writeByte((byte) (mIsRingtone ? 1 : 0));
        dest.writeByte((byte) (mIsPodcast ? 1 : 0));
        dest.writeByte((byte) (mIsAlarm ? 1 : 0));
        dest.writeByte((byte) (mIsMusic ? 1 : 0));
        dest.writeByte((byte) (mIsNotification ? 1 : 0));
        dest.writeByte((byte) (mIsPlaying ? 1 : 0));
    }
}
