package com.guoyonghui.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.ui.activity.MusicPlayerActivity;
import com.guoyonghui.musicplayer.ui.fragment.MusicBrowserFragment;
import com.guoyonghui.musicplayer.ui.fragment.PlaybackControlFragment;
import com.guoyonghui.musicplayer.util.LogHelper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 永辉 on 2015/6/30.
 */
public class MusicService extends Service {

    private static final String TAG = LogHelper.makeLogTag(MusicService.class);

    public static final String ACTION_MUSIC_PLAYED = "com.guoyonghui.musicplayer.ACTION_MUSIC_PLAYED";

    /**
     * MediaPlayer实例
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 音乐数据
     */
    private ArrayList<Music> mMusicDatas;

    /**
     * 当前播放音乐在列表中的位置
     */
    private int mCurrentPlayingPosition;

    @Override
    public IBinder onBind(Intent intent) {

        mMusicDatas = intent.getParcelableArrayListExtra(MusicPlayerActivity.EXTRA_MUSIC_DATAS);

        Log.i(TAG, "music service onBind(), receive music datas with size " + mMusicDatas.size() + ".");

        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();

        mCurrentPlayingPosition = -1;

    }

    /**
     * 从头开始播放音乐
     */
    public void startMusic() {
        Music music = mMusicDatas.get(mCurrentPlayingPosition);
        if(music == null) {
            return;
        }

        notifyMusicPlayed();

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(music.getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switchMusic(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 从头开始播放音乐
     *
     * @param position 音乐的位置
     */
    public void startMusic(int position) {
        mCurrentPlayingPosition = position;
        startMusic();
    }

    /**
     * 恢复播放音乐
     */
    public void resumeMusic() {
        if(mCurrentPlayingPosition == -1) {
            mCurrentPlayingPosition = 0;
            startMusic();
            return;
        }

        mMediaPlayer.start();
    }

    /**
     * 暂停音乐播放
     */
    public void pauseMusic() {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void switchMusic(boolean switchNext) {
        if(mCurrentPlayingPosition == -1) {
            mCurrentPlayingPosition = 0;
            startMusic();
            return;
        }

        if(switchNext) {
            mCurrentPlayingPosition = (mCurrentPlayingPosition + 1) % mMusicDatas.size();
        } else {
            mCurrentPlayingPosition = ((mCurrentPlayingPosition - 1) < 0) ? (mMusicDatas.size() - 1) : (mCurrentPlayingPosition - 1);
        }
        startMusic();
    }

    private void notifyMusicPlayed() {
        Music music = mMusicDatas.get(mCurrentPlayingPosition);

        Intent intent = new Intent(ACTION_MUSIC_PLAYED);
        intent.putExtra(PlaybackControlFragment.EXTRA_MUSIC_PLAYED_DATA, music);
        intent.putExtra(MusicBrowserFragment.EXTRA_MUSIC_PLAYED_POSITION, mCurrentPlayingPosition);
        sendBroadcast(intent);
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }

    }
}
