package com.guoyonghui.musicplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.guoyonghui.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by 永辉 on 2015/6/23.
 */
public class MusicHelper {

    private static final String[] AUDIO_KEYS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TITLE_KEY,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST_KEY,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM_KEY,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA
    };

    public static ArrayList<Music> scanMusic(Context context) {
        ArrayList<Music> musicList = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_KEYS,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            Bundle bundle = new Bundle();
            for (String key : AUDIO_KEYS) {
                int columnIndex = cursor.getColumnIndexOrThrow(key);
                int type = cursor.getType(columnIndex);

                switch (type) {
                    case Cursor.FIELD_TYPE_FLOAT:
                        float floatValue = cursor.getFloat(columnIndex);
                        bundle.putFloat(key, floatValue);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intvalue = cursor.getInt(columnIndex);
                        bundle.putInt(key, intvalue);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        String stringValue = cursor.getString(columnIndex);
                        bundle.putString(key, stringValue);
                        break;

                    default:
                        break;
                }
            }

            Music music = new Music(bundle);
            if (music.isMusic()) {
                musicList.add(music);
            }
        }
        cursor.close();

        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music lhs, Music rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        return musicList;
    }
}
