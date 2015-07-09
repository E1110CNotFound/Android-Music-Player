package com.guoyonghui.musicplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.service.*;
import com.guoyonghui.musicplayer.ui.fragment.MusicBrowserFragment;
import com.guoyonghui.musicplayer.ui.fragment.PlaybackControlFragment;
import com.guoyonghui.musicplayer.util.MusicHelper;

import java.util.ArrayList;


public class MusicPlayerActivity extends AppCompatActivity implements MusicBrowserFragment.OnMusicItemSelectedCallback, PlaybackControlFragment.OnPlaybackControlCallback {

    public static final String ACTION_MUSIC_COMPLETION = "com.guoyonghui.musicplayer.ACTION_MUSIC_COMPLETION";

    public static final String EXTRA_MUSIC_DATAS = "com.guoyonghui.musicplayer.EXTRA_MUSIC_DATAS";

    /**
     * 音乐数据
     */
    private ArrayList<Music> mMusicDatas;

    /**
     * MusicService实例
     */
    private MusicService mMusicService;

    /**
     * ServiceConnection实例
     */
    private ServiceConnection mMusicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicService = ((MusicService.MusicBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private BroadcastReceiver mMusicCompletionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION_MUSIC_COMPLETION.equals(intent.getAction())) {
                onMusicSwitchControl(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initDatas();

        initViews();


        Intent intent = new Intent(this, MusicService.class);
        intent.putParcelableArrayListExtra(EXTRA_MUSIC_DATAS, mMusicDatas);
        bindService(intent, mMusicServiceConnection, BIND_AUTO_CREATE);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        IntentFilter musicCompletionFilter = new IntentFilter();
        musicCompletionFilter.addAction(ACTION_MUSIC_COMPLETION);
        registerReceiver(mMusicCompletionReceiver, musicCompletionFilter);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        unregisterReceiver(mMusicCompletionReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(mMusicServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_music_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMusicItemSelected(int position) {
        mMusicService.startMusic(position);
    }

    @Override
    public void onMusicSwitchControl(boolean switchNext) {
        mMusicService.switchMusic(switchNext);
    }

    @Override
    public void onMusicPlayPauseControl(boolean play) {
        if(play) {
            mMusicService.resumeMusic();
        } else {
            mMusicService.pauseMusic();
        }
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        mMusicDatas = getIntent().getParcelableArrayListExtra(EXTRA_MUSIC_DATAS);
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment musicBrowserFragment = fm.findFragmentById(R.id.music_browser_container);
        if (musicBrowserFragment == null) {
            musicBrowserFragment = MusicBrowserFragment.newInstance(mMusicDatas);
            fm.beginTransaction()
                    .add(R.id.music_browser_container, musicBrowserFragment)
                    .commit();
        }

        Fragment playbackControlFragment = fm.findFragmentById(R.id.playback_control_container);
        if (playbackControlFragment == null) {
            playbackControlFragment = new PlaybackControlFragment();
            fm.beginTransaction()
                    .add(R.id.playback_control_container, playbackControlFragment)
                    .commit();
        }
    }

}
