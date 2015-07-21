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
 * @author He Yanglong
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

    private ArrayList<Integer> mLoopOrder;

    private ArrayList<Integer> mRandomOrder;

    @Override
    public IBinder onBind(Intent intent) {
        mMusicDatas = intent.getParcelableArrayListExtra(MusicPlayerActivity.EXTRA_MUSIC_DATAS);

        generateLoopRandomMap();

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
        int index = 0;
        if(mCurrentPlayingMode == PLAY_MODE_LOOP) {
            index = mLoopOrder.get(mCurrentPlayingPosition);
        } else if(mCurrentPlayingMode == PLAY_MODE_RANDOM) {
            index = mRandomOrder.get(mCurrentPlayingPosition);
        }
        Music music = mMusicDatas.get(index);
        if (music == null) {
            return;
        }

        notifyCurrentPlayingMusic(index);

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
     * 切换播放模式
     *
     * @param mode PLAY_MODE_LOOP - 循环播放 PLAY_MODE_RANDOM - 随机播放
     */
    public void switchMode(int mode) {
        if(mode == mCurrentPlayingMode) {
            return;
        }

        mCurrentPlayingMode = mode;
        if(mCurrentPlayingPosition == -1) {
            return;
        }
        if(mCurrentPlayingMode == PLAY_MODE_LOOP) {
            mCurrentPlayingPosition = mLoopOrder.indexOf(mRandomOrder.get(mCurrentPlayingPosition));
        } else if(mCurrentPlayingMode == PLAY_MODE_RANDOM) {
            mCurrentPlayingPosition = mRandomOrder.indexOf(mLoopOrder.get(mCurrentPlayingPosition));
        }
    }

    /**
     * 发送广播通知fragment当前播放的歌曲
     *
     * @param index 当前播放歌曲在音乐列表中的位置
     */
    private void notifyCurrentPlayingMusic(int index) {
        Music music = mMusicDatas.get(index);

        Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC);
        intent.putExtra(PlaybackControlFragment.EXTRA_CURRENT_PLAYING_MUSIC_DATA, music);
        intent.putExtra(MusicBrowserFragment.EXTRA_CURRENT_PLAYING_MUSIC_POSITION, index);
        sendBroadcast(intent);
    }

    /**
     * 生成顺序播放列表和随机播放列表
     */
    private void generateLoopRandomMap() {
        int length = mMusicDatas.size();

        mLoopOrder = new ArrayList<>();
        mRandomOrder = new ArrayList<>();

        int[] loopArray = new int[length];

        for (int i = 0; i < length; i++) {
            loopArray[i] = i;
        }

        int randomCount = 0;
        int randomIndex;

        do {
            randomIndex = new Random().nextInt(length - randomCount);

            mLoopOrder.add(randomCount);
            mRandomOrder.add(loopArray[randomIndex]);

            loopArray[randomIndex] = loopArray[length - ++randomCount];
        } while (randomCount < length);
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }

    }

}