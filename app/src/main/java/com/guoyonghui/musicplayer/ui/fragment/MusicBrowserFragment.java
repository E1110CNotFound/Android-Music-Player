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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.service.MusicService;
import com.guoyonghui.musicplayer.ui.BaseApplication;
import com.guoyonghui.musicplayer.ui.activity.MusicPlayerActivity;
import com.guoyonghui.musicplayer.view.ElasticListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 永辉 on 2015/6/29.
 */
public class MusicBrowserFragment extends Fragment {

    public static final String EXTRA_MUSIC_PLAYED_POSITION = "com.guoyonghui.musicplayer.ui.EXTRA_MUSIC_PLAYED_POSITION";

    /**
     * 当前播放歌曲的位置
     */
    private int mCurrentPlayingPosition = -1;

    /**
     * 音乐列表
     */
    private ElasticListView mMusicList;

    /**
     * 音乐数据
     */
    private ArrayList<Music> mMusicDatas;

    /**
     * 音乐列表项选中事件监听器
     */
    private OnMusicItemSelectedCallback mOnMusicItemSelectedCallback;

    /**
     * 开源的第三方图片异步加载库 - ImageLoader
     */
    private ImageLoader mImageLoader;

    /**
     * 开源的第三方图片异步加载库 - DisplayImageOptions
     */
    private DisplayImageOptions mOptions;

    /**
     * ACTION_MUSIC_CHANGED广播接收器
     */
    private BroadcastReceiver mMusicSwitchedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicService.ACTION_MUSIC_PLAYED.equals(intent.getAction())) {
                updateMusicItemUI(false);

                mCurrentPlayingPosition = intent.getIntExtra(EXTRA_MUSIC_PLAYED_POSITION, 0);

                updateMusicItemUI(true);
            }
        }
    };

    /**
     * 音乐列表项选中事件监听器回调接口
     */
    public interface OnMusicItemSelectedCallback {
        void onMusicItemSelected(int position);
    }

    /**
     * 创建一个附有音乐数据参数的MusicBrowserFragment实例
     *
     * @param musicDatas 音乐数据
     * @return 附有音乐数据参数的MusicBrowserFragment实例
     */
    public static MusicBrowserFragment newInstance(ArrayList<Music> musicDatas) {
        MusicBrowserFragment fragment = new MusicBrowserFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MusicPlayerActivity.EXTRA_MUSIC_DATAS, musicDatas);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mMusicDatas = getArguments().getParcelableArrayList(MusicPlayerActivity.EXTRA_MUSIC_DATAS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music_browser, container, false);

        initViews(rootView);

        initEvents();

        initImageLoader();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mOnMusicItemSelectedCallback = (OnMusicItemSelectedCallback) activity;

        IntentFilter musicSwitchedFilter = new IntentFilter();
        musicSwitchedFilter.addAction(MusicService.ACTION_MUSIC_PLAYED);
        getActivity().registerReceiver(mMusicSwitchedReceiver, musicSwitchedFilter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnMusicItemSelectedCallback = null;

        getActivity().unregisterReceiver(mMusicSwitchedReceiver);
    }

    /**
     * 初始化视图
     *
     * @param rootView 根视图
     */
    private void initViews(View rootView) {
        mMusicList = (ElasticListView) rootView.findViewById(R.id.music_list);
        mMusicList.setAdapter(new MusicBrowserAdapter(mMusicDatas));
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentPlayingPosition == position) {
                    return;
                }
                updateMusicItemUI(false);

                mCurrentPlayingPosition = position;

                updateMusicItemUI(true);

                mOnMusicItemSelectedCallback.onMusicItemSelected(mCurrentPlayingPosition);
            }
        });
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
     * 更新列表项的播放状态UI
     *
     * @param isPlaying true - 正在播放 false - 未在播放
     */
    private void updateMusicItemUI(boolean isPlaying) {
        if (mCurrentPlayingPosition == -1) {
            return;
        }

        Music music = mMusicDatas.get(mCurrentPlayingPosition);

        if (music == null) {
            return;
        }

        music.setIsPlaying(isPlaying);

        int firstVisiblePosition = mMusicList.getFirstVisiblePosition();
        int lastVisiblePosition = mMusicList.getLastVisiblePosition();

        if (mCurrentPlayingPosition >= firstVisiblePosition && mCurrentPlayingPosition <= lastVisiblePosition) {
            MusicBrowserAdapter.ViewHolder viewHolder = (MusicBrowserAdapter.ViewHolder) (mMusicList.getChildAt(mCurrentPlayingPosition - firstVisiblePosition).getTag());
            viewHolder.statusImageView.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 音乐列表适配器
     */
    private class MusicBrowserAdapter extends ArrayAdapter<Music> {

        public MusicBrowserAdapter(List<Music> musicDatas) {
            super(getActivity(), R.layout.music_item, musicDatas);
        }

        @Override
        public int getCount() {
            return mMusicDatas.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.music_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.musicAlbumArtImageView = (ImageView) convertView.findViewById(R.id.music_album_art);
                viewHolder.statusImageView = (ImageView) convertView.findViewById(R.id.status);
                viewHolder.musicTitleTextView = (TextView) convertView.findViewById(R.id.music_title);
                viewHolder.musicArtistTextView = (TextView) convertView.findViewById(R.id.music_artist);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Music music = getItem(position);

            mImageLoader.displayImage(ContentUris.withAppendedId(BaseApplication.ALBUM_ART_URI, music.getAlbumId()).toString(),
                    viewHolder.musicAlbumArtImageView,
                    mOptions);

            viewHolder.statusImageView.setVisibility(music.isPlaying() ? View.VISIBLE : View.GONE);
            viewHolder.musicTitleTextView.setText(music.getTitle());
            viewHolder.musicArtistTextView.setText(music.getArtist());

            return convertView;
        }

        private class ViewHolder {
            ImageView musicAlbumArtImageView;

            ImageView statusImageView;

            TextView musicTitleTextView;

            TextView musicArtistTextView;
        }
    }
}
