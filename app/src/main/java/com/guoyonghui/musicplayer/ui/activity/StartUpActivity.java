package com.guoyonghui.musicplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.util.LogHelper;
import com.guoyonghui.musicplayer.util.MusicHelper;

import java.util.ArrayList;

/**
 * Created by 永辉 on 2015/7/9.
 */
public class StartUpActivity extends AppCompatActivity {

    public static final String TAG = LogHelper.makeLogTag(StartUpActivity.class);

    private BroadcastReceiver mMediaScannerFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                Log.i(TAG, "media scanner started.");
            } else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                Log.i(TAG, "media scanner finished.");

                ArrayList<Music> musicDatas = MusicHelper.scanMusic(StartUpActivity.this);

                launchMusicPlayerActivity(musicDatas);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");

        registerReceiver(mMediaScannerFinishedReceiver, filter);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        unregisterReceiver(mMediaScannerFinishedReceiver);
    }

    /**
     * 启动MusicPlayerActivity
     *
     * @param musicDatas 音乐数据
     */
    private void launchMusicPlayerActivity(ArrayList<Music> musicDatas) {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putParcelableArrayListExtra(MusicPlayerActivity.EXTRA_MUSIC_DATAS, musicDatas);
        startActivity(intent);

        if(!isFinishing()) {
            finish();
        }
    }
}