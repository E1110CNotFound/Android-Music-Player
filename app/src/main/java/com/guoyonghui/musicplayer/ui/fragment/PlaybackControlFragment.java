package com.guoyonghui.musicplayer.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.service.MusicService;
import com.guoyonghui.musicplayer.ui.BaseApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * PlaybackControlFragment
 *
 * @author Guo Yonghui
 *
 * 1.封装音乐控制的相关操作
 * 2.通过回调函数将音乐控制相关操作通知托管该fragment的activity
 * 3.接收MusicService发送的当前播放音乐广播并根据附在广播中的数据更新UI
 */
public class PlaybackControlFragment extends Fragment implements View.OnClickListener {

    /**
     * EXTRA - 当前播放的音乐的数据
     */
    public static final String EXTRA_CURRENT_PLAYING_MUSIC_DATA = "com.guoyonghui.musicplayer.EXTRA_CURRENT_PLAYING_MUSIC_DATA";

    /**
     * true - 正在播放 false - 未在播放
     */
    private boolean mIsPlaying;

    /**
     * 当前播放歌曲封面
     */
    private ImageView mCurrentPlayingAlbumArtImageView;

    /**
     * 当前播放歌曲标题
     */
    private TextView mCurrentPlayingTitleTextView;

    /**
     * 当前播放歌曲艺术家
     */
    private TextView mCurrentPlayingArtistTextView;

    /**
     * 上一首按钮
     */
    private ImageButton mPrevButton;

    /**
     * 下一首按钮
     */
    private ImageButton mNextButton;

    /**
     * 播放/暂停按钮
     */
    private ImageButton mPlayPauseButton;

    /**
     * ImageLoader - 开源第三方异步加载图片库
     */
    private ImageLoader mImageLoader;

    /**
     * DisplayImageOptions - 开源第三方异步加载图片库
     */
    private DisplayImageOptions mOptions;

    /**
     * PlaybackControlFragment回调接口实例
     */
    private Callback mCallback;

    /**
     * 当前播放音乐广播接收器
     */
    private BroadcastReceiver mCurrentPlayingMusicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MusicService.ACTION_CURRENT_PLAYING_MUSIC.equals(intent.getAction())) {
                Music music = intent.getParcelableExtra(EXTRA_CURRENT_PLAYING_MUSIC_DATA);
                if(music == null) {
                    return;
                }

                updatePlaybackControlUI(music);
            }
        }
    };

    /**
     * PlaybackControlFragment回调接口
     */
    public interface Callback {
        /**
         * 音乐切换回调函数
         *
         * @param switchNext true - 切换至下一首 false - 切换至上一首
         */
        void onMusicSwitch(boolean switchNext);

        /**
         * 音乐播放/暂停回调函数
         *
         * @param play true - 播放 false - 暂停
         */
        void onMusicPlayPause(boolean play);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_control, container, false);

        initViews(rootView);

        initEvents();

        initImageLoader();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallback= (Callback) activity;

        IntentFilter filter = new IntentFilter(MusicService.ACTION_CURRENT_PLAYING_MUSIC);
        getActivity().registerReceiver(mCurrentPlayingMusicReceiver, filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallback = null;

        getActivity().unregisterReceiver(mCurrentPlayingMusicReceiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.prev:
                mCallback.onMusicSwitch(false);
                break;
            case R.id.next:
                mCallback.onMusicSwitch(true);
                break;
            case R.id.play_pause:
                mIsPlaying = !mIsPlaying;
                mPlayPauseButton.setImageResource(mIsPlaying ? R.drawable.ic_control_pause_white_36dp : R.drawable.ic_control_play_white_36dp);

                mCallback.onMusicPlayPause(mIsPlaying);
                break;

            default:
                break;
        }
    }

    /**
     * 初始化视图
     *
     * @param rootView 当前视图的根视图
     */
    private void initViews(View rootView) {
        mCurrentPlayingAlbumArtImageView = (ImageView) rootView.findViewById(R.id.playing_album_art);
        mCurrentPlayingTitleTextView = (TextView) rootView.findViewById(R.id.playing_title);
        mCurrentPlayingArtistTextView = (TextView) rootView.findViewById(R.id.playing_artist);
        mPrevButton = (ImageButton) rootView.findViewById(R.id.prev);
        mNextButton = (ImageButton) rootView.findViewById(R.id.next);
        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause);
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        mPrevButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mPlayPauseButton.setOnClickListener(this);
    }

    /**
     * 初始化ImageLoader
     */
    private void initImageLoader() {
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_album_art)
                .showImageForEmptyUri(R.drawable.ic_album_art)
                .showImageOnFail(R.drawable.ic_album_art)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        mImageLoader = ImageLoader.getInstance();
    }

    /**
     * 更新控制界面UI
     *
     * @param music 正在播放的歌曲
     */
    private void updatePlaybackControlUI(Music music) {
        mIsPlaying = true;

        mImageLoader.displayImage(ContentUris.withAppendedId(BaseApplication.ALBUM_ART_URI, music.getAlbumId()).toString(),
                mCurrentPlayingAlbumArtImageView,
                mOptions);

        mCurrentPlayingTitleTextView.setText(music.getTitle());
        mCurrentPlayingArtistTextView.setText(music.getArtist());
        mPlayPauseButton.setImageResource(R.drawable.ic_control_pause_white_36dp);

    }
}
