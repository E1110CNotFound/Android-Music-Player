package com.guoyonghui.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.ui.activity.MusicPlayerActivity;
import com.guoyonghui.musicplayer.ui.fragment.MusicBrowserFragment;
import com.guoyonghui.musicplayer.ui.fragment.PlaybackControlFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * MusicService
 *
 * @author Guo Yonghui
 *         <p/>
 *         音乐播放服务类
 */
public class MusicService extends Service {

    /**
     * ACTION - 当前播放的音乐
     */
    public static final String ACTION_CURRENT_PLAYING_MUSIC = "com.guoyonghui.musicplayer.ACTION_CURRENT_PLAYING_MUSIC";

    /**
     * 播放模式 - 随机播放
     */
    public static final int PLAY_MODE_RANDOM = 0;

    /**
     * 播放模式 - 顺序播放
     */
    public static final int PLAY_MODE_LOOP = 1;

    /**
     * 当前播放音乐在列表中的位置
     */
    private int mCurrentPlayingPosition;

    /**
     * 当前音乐播放模式
     */
    private int mCurrentPlayingMode;

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

        mCurrentPlayingMode = PLAY_MODE_LOOP;
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

        if (mCurrentPlayingMode == PLAY_MODE_LOOP) {
            if (switchNext) {
                mCurrentPlayingPosition = (mCurrentPlayingPosition + 1) % mMusicDatas.size();
            } else {
                mCurrentPlayingPosition = ((mCurrentPlayingPosition - 1) < 0) ? (mMusicDatas.size() - 1) : (mCurrentPlayingPosition - 1);
            }
        } else if (mCurrentPlayingMode == PLAY_MODE_RANDOM) {
            Random random = new Random();

            if (mCurrentPlayingPosition == 0) {
                mCurrentPlayingPosition = random.nextInt(mMusicDatas.size() - mCurrentPlayingPosition - 1) + mCurrentPlayingPosition + 1;
            } else if (mCurrentPlayingPosition == mMusicDatas.size() - 1) {
                mCurrentPlayingPosition = random.nextInt(mCurrentPlayingPosition);
            } else {
                int seed = random.nextInt(2);

                if (seed == 0 && mCurrentPlayingPosition > 0) {
                    mCurrentPlayingPosition = random.nextInt(mCurrentPlayingPosition);
                } else {
                    mCurrentPlayingPosition = random.nextInt(mMusicDatas.size() - mCurrentPlayingPosition - 1) + mCurrentPlayingPosition + 1;
                }
            }
        }
        startMusic();
    }

    /**
     * 切换播放模式
     *
     * @param mode PLAY_MODE_LOOP - 循环播放 PLAY_MODE_RANDOM - 随机播放
     */
    public void switchMode(int mode) {
        if (mode != PLAY_MODE_LOOP && mode != PLAY_MODE_RANDOM) {
            return;
        }

        mCurrentPlayingMode = mode;
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