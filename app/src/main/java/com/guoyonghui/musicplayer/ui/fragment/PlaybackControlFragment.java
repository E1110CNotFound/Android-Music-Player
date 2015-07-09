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
 * Created by 永辉 on 2015/6/29.
 */
public class PlaybackControlFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_MUSIC_PLAYED_DATA = "com.guoyonghui.musicplayer.EXTRA_MUSIC_PLAYED_DATA";

    /**
     * 是否正在播放
     */
    private boolean mIsPlaying;

    /**
     * 当前播放歌曲专辑封面视图
     */
    private ImageView mPlayingAlbumArtImageView;

    /**
     * 当前播放歌曲名称视图
     */
    private TextView mPlayingTitleTextView;

    /**
     * 当前播放歌曲演唱者视图
     */
    private TextView mPlayingArtistTextView;

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
     * 开源的第三方图片异步加载库 - ImageLoader
     */
    private ImageLoader mImageLoader;

    /**
     * 开源的第三方图片异步加载库 - DisplayImageOptions
     */
    private DisplayImageOptions mOptions;

    /**
     * 音乐控制事件监听器
     */
    private OnPlaybackControlCallback mOnPlaybackControlCallback;

    /**
     * ACTION_MUSIC_PLAYED广播接收器
     */
    private BroadcastReceiver mMusicPlayedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicService.ACTION_MUSIC_PLAYED.equals(intent.getAction())) {
                Music music = intent.getParcelableExtra(EXTRA_MUSIC_PLAYED_DATA);

                if (music == null) {
                    return;
                }

                updatePlaybackControlUI(music);
            }
        }
    };

    /**
     * 音乐控制事件监听器回调接口
     */
    public interface OnPlaybackControlCallback {
        void onMusicSwitchControl(boolean switchNext);

        void onMusicPlayPauseControl(boolean play);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_control, container, false);

        initViews(rootView);

        initEvents();

        initImageLoader();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnPlaybackControlCallback = (OnPlaybackControlCallback) activity;

        IntentFilter musicPlayedFilter = new IntentFilter();
        musicPlayedFilter.addAction(MusicService.ACTION_MUSIC_PLAYED);
        getActivity().registerReceiver(mMusicPlayedReceiver, musicPlayedFilter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnPlaybackControlCallback = null;

        getActivity().unregisterReceiver(mMusicPlayedReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev:
                mOnPlaybackControlCallback.onMusicSwitchControl(false);
                break;
            case R.id.next:
                mOnPlaybackControlCallback.onMusicSwitchControl(true);
                break;
            case R.id.play_pause:
                mPlayPauseButton.setImageResource(mIsPlaying ? R.drawable.ic_control_play_white_36dp : R.drawable.ic_control_pause_white_36dp);
                mIsPlaying = !mIsPlaying;

                mOnPlaybackControlCallback.onMusicPlayPauseControl(mIsPlaying);
                break;

            default:
                break;
        }
    }

    /**
     * 初始化视图
     *
     * @param rootView 根视图
     */
    private void initViews(View rootView) {
        mPlayingAlbumArtImageView = (ImageView) rootView.findViewById(R.id.playing_album_art);
        mPlayingTitleTextView = (TextView) rootView.findViewById(R.id.playing_title);
        mPlayingArtistTextView = (TextView) rootView.findViewById(R.id.playing_artist);
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
                .showImageOnLoading(null)
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
     * @param music 当前正在播放的歌曲
     */
    private void updatePlaybackControlUI(Music music) {
        mIsPlaying = true;

        mImageLoader.displayImage(ContentUris.withAppendedId(BaseApplication.ALBUM_ART_URI, music.getAlbumId()).toString(),
                mPlayingAlbumArtImageView,
                mOptions);

        mPlayingTitleTextView.setText(music.getTitle());
        mPlayingArtistTextView.setText(music.getArtist());
        mPlayPauseButton.setImageResource(R.drawable.ic_control_pause_white_36dp);
    }

}
