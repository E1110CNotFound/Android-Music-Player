package com.guoyonghui.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.TimedText;
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
 * MusicService
 *
 * @author Guo Yonghui
 *         <p/>
 *         音乐播放服务类
 */
public class MusicService extends Service {

    private static final String TAG = LogHelper.makeLogTag(MusicService.class);

    /**
     * ACTION - 当前播放的音乐
     */
    public static final String ACTION_CURRENT_PLAYING_MUSIC = "com.guoyonghui.musicplayer.ACTION_CURRENT_PLAYING_MUSIC";

    /**
     * 当前播放音乐在列表中的位置
     */
    private int mCurrentPlayingPosition;

    /**
     * MediaPlayer实例
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 音乐数据
     */
    private ArrayList<Music> mMusicDatas;

    @Override
    public IBinder onBind(Intent intent) {
        mMusicDatas = intent.getParcelableArrayListExtra(MusicPlayerActivity.EXTRA_MUSIC_DATAS);

        return new MusicBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        mMediaPlayer = null;

        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();

        mCurrentPlayingPosition = -1;
    }

    /**
     * 开始播放音乐
     *
     * @param position 音乐的位置
     */
    public void startMusic(int position) {
        mCurrentPlayingPosition = position;
        startMusic();
    }

    /**
     * 开始播放音乐
     */
    public void startMusic() {
        Music music = mMusicDatas.get(mCurrentPlayingPosition);
        if (music == null) {
            return;
        }

        notifyCurrentPlayingMusic();

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
     * 播放/暂停音乐
     *
     * @param play true - 播放 false - 暂停
     */
    public void playpauseMusic(boolean play) {
        if (play) {
            if (mCurrentPlayingPosition == -1) {
                mCurrentPlayingPosition = 0;
                startMusic();
                return;
            }

            mMediaPlayer.start();
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    /**
     * 切换音乐
     *
     * @param switchNext true - 切换至下一首 false - 切换至上一首
     */
    public void switchMusic(boolean switchNext) {
        if (mCurrentPlayingPosition == -1) {
            mCurrentPlayingPosition = 0;
            startMusic();
            return;
        }

        if (switchNext) {
            mCurrentPlayingPosition = (mCurrentPlayingPosition + 1) % mMusicDatas.size();
        } else {
            mCurrentPlayingPosition = ((mCurrentPlayingPosition - 1) < 0) ? (mMusicDatas.size() - 1) : (mCurrentPlayingPosition - 1);
        }
        startMusic();
    }

    /**
     * 发送广播通知fragment当前播放的歌曲
     */
    private void notifyCurrentPlayingMusic() {
        Music music = mMusicDatas.get(mCurrentPlayingPosition);

        Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC);
        intent.putExtra(PlaybackControlFragment.EXTRA_CURRENT_PLAYING_MUSIC_DATA, music);
        intent.putExtra(MusicBrowserFragment.EXTRA_CURRENT_PLAYING_MUSIC_POSITION, mCurrentPlayingPosition);
        sendBroadcast(intent);
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }

    }

}
