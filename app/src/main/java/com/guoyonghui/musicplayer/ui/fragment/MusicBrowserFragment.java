package com.guoyonghui.musicplayer.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.service.MusicService;
import com.guoyonghui.musicplayer.ui.BaseApplication;
import com.guoyonghui.musicplayer.ui.activity.MusicPlayerActivity;
import com.guoyonghui.musicplayer.util.LogHelper;
import com.guoyonghui.musicplayer.view.ElasticListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * MusicBrowserFragment
 *
 * @author Guo Yonghui
 *         <p/>
 *         1.封装音乐列表的相关操作
 *         2.通过回调函数将音乐列表相关操作通知托管该fragment的activity
 *         3.接收MusicService发送的当前播放音乐广播并根据附在广播中的数据更新UI
 */
public class MusicBrowserFragment extends Fragment {

    /**
     * EXTRA - 当前播放的音乐的位置
     */
    public static final String EXTRA_CURRENT_PLAYING_MUSIC_POSITION = "com.guoyonghui.musicplayer.EXTRA_CURRENT_PLAYING_MUSIC_POSITION";

    /**
     * REQUEST - 请求设置默认声音
     */
    public static final int REQUEST_SET_DEFAULT_VOICE = 0;

    /**
     * 当前播放音乐在列表中的位置
     */
    private int mCurrentPlayingPosition;

    /**
     * 音乐列表
     */
    private ElasticListView mMusicList;

    /**
     * 音乐数据
     */
    private ArrayList<Music> mMusicDatas;

    /**
     * ImageLoader - 开源第三方异步加载图片库
     */
    private ImageLoader mImageLoader;

    /**
     * DisplayImageOptions - 开源第三方异步加载图片库
     */
    private DisplayImageOptions mOptions;

    /**
     * MusicBrowserFragment回调接口实例
     */
    private Callback mCallback;

    /**
     * 当前播放音乐广播接收器
     */
    private BroadcastReceiver mCurrentPlayingMusicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicService.ACTION_CURRENT_PLAYING_MUSIC.equals(intent.getAction())) {
                updateMusicListItemUI(false);

                mCurrentPlayingPosition = intent.getIntExtra(EXTRA_CURRENT_PLAYING_MUSIC_POSITION, -1);

                updateMusicListItemUI(true);
            }
        }
    };

    /**
     * MusicBrowserFragment回调接口
     */
    public interface Callback {
        /**
         * 音乐列表项被点击的回调函数
         *
         * @param position 被点击的音乐列表项位置
         */
        void onMusicItemClick(int position);
    }

    /**
     * 创建附有音乐数据参数的MusicBrowserFragment实例
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mCurrentPlayingPosition = -1;

        mMusicDatas = getArguments().getParcelableArrayList(MusicPlayerActivity.EXTRA_MUSIC_DATAS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music_browser, container, false);

        initViews(rootView);

        initEvents();

        initImageLoader();

        registerForContextMenu(mMusicList);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.music_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Music music = (Music) (mMusicList.getAdapter().getItem(position));

        FragmentManager fm = getActivity().getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.action_music_detail:
                DetailBrowserFragment detailBrowserFragment = DetailBrowserFragment.newInstance(music);
                detailBrowserFragment.show(fm, LogHelper.makeLogTag(DetailBrowserFragment.class));
                break;
            case R.id.action_set_voice:
                DefaultVoiceSettingFragment defaultVoiceSettingFragment = DefaultVoiceSettingFragment.newInstance(music.getPath());
                defaultVoiceSettingFragment.setTargetFragment(MusicBrowserFragment.this, REQUEST_SET_DEFAULT_VOICE);
                defaultVoiceSettingFragment.show(fm, LogHelper.makeLogTag(DetailBrowserFragment.class));
                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallback = (Callback) activity;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_SET_DEFAULT_VOICE) {
            String defualtVoiceSettingResult = data.getStringExtra(DefaultVoiceSettingFragment.EXTRA_DEFAULT_VOICE_SETTING_RESULT);

            if(defualtVoiceSettingResult != null) {
                Toast.makeText(getActivity(), defualtVoiceSettingResult, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 初始化视图
     *
     * @param rootView 当前视图的根视图
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

                updateMusicListItemUI(false);

                mCurrentPlayingPosition = position;

                updateMusicListItemUI(true);

                mCallback.onMusicItemClick(mCurrentPlayingPosition);
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
     * 更新音乐列表项UI
     *
     * @param isPlaying true - 正在播放 false - 未在播放
     */
    private void updateMusicListItemUI(boolean isPlaying) {
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
            viewHolder.statusImageView.setVisibility(isPlaying ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 音乐列表适配器
     * 使用ViewHolder模式提高加载效率
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

            mImageLoader.displayImage(ContentUris.withAppendedId(BaseApplication.ALBUM_ART_URI, music.getAlbumId()).toString(), viewHolder.musicAlbumArtImageView, mOptions);
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