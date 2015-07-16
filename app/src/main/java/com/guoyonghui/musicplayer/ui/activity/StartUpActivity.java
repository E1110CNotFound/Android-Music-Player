package com.guoyonghui.musicplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.util.LogHelper;
import com.guoyonghui.musicplayer.util.MusicHelper;

import java.util.ArrayList;

/**
 * StartUpActivity
 *
 * @author Guo Yonghui
 *         <p/>
 *         应用启动界面，扫描本机音乐并加载数据
 */
public class StartUpActivity extends AppCompatActivity {

    public static final String TAG = LogHelper.makeLogTag(StartUpActivity.class);

    private BroadcastReceiver mMediaScannerFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
                Log.i(TAG, "media scanner started.");
            } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
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

        TextView appNameTextView;
        appNameTextView = (TextView) findViewById(R.id.app_name_text);
        SpannableStringBuilder builder = new SpannableStringBuilder(appNameTextView.getText());
        builder.setSpan(new ForegroundColorSpan(Color.RED), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        appNameTextView.setText(builder);

//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));

        ArrayList<Music> musicDatas = MusicHelper.scanMusic(StartUpActivity.this);

        launchMusicPlayerActivity(musicDatas);
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

        if (!isFinishing()) {
            finish();
        }
    }
}
