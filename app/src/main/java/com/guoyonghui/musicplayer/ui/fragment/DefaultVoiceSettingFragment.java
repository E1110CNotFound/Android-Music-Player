package com.guoyonghui.musicplayer.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.constant.Constants;

/**
 * DefaultVoiceSettingFragment
 *
 * @author Ma Min
 *         <p/>
 *         设置默认的声音（默认铃声、默认通知音以及默认闹铃音）
 */
public class DefaultVoiceSettingFragment extends DialogFragment implements View.OnClickListener {

    /**
     * EXTRA - 默认声音的音乐的路径
     */
    public static final String EXTRA_DEFAULT_VOICE_PATH = "com.guoyonghui.musicplayer.EXTRA_DEFAULT_VOICE_PATH";

    /**
     * EXTRA - 默认声音设置的结果
     */
    public static final String EXTRA_DEFAULT_VOICE_SETTING_RESULT = "com.guoyonghui.musicplayer.EXTRA_DEFAULT_VOICE_SETTING_RESULT";

    /**
     * 默认声音的音乐的路径
     */
    private String mPath;

    /**
     * 设置默认铃声按钮
     */
    private Button mSetDefaultRingtoneButton;

    /**
     * 设置默认闹铃音按钮
     */
    private Button mSetDefaultAlarmButton;

    /**
     * 设置默认通知音按钮
     */
    private Button mSetDefaultNotificationButton;

    public static DefaultVoiceSettingFragment newInstance(String path) {
        DefaultVoiceSettingFragment fragment = new DefaultVoiceSettingFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_DEFAULT_VOICE_PATH, path);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPath = getArguments().getString(EXTRA_DEFAULT_VOICE_PATH);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_default_voice_setting, null);

        initViews(view);

        initEvents();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.action_set_voice)
                .create();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        String tip = getString(R.string.tip_set_voice_result);
        switch (id) {
            case R.id.set_default_ringtone:
                if (setDefaultVoice(Constants.RINGTONE)) {
                    tip = String.format(tip, "铃声", "成功");
                } else {
                    tip = String.format(tip, "铃声", "失败");
                }
                break;
            case R.id.set_default_alarm:
                if (setDefaultVoice(Constants.ALARM)) {
                    tip = String.format(tip, "闹铃音", "成功");
                } else {
                    tip = String.format(tip, "闹铃音", "失败");
                }
                break;
            case R.id.set_default_notification:
                if (setDefaultVoice(Constants.NOTIFICATION)) {
                    tip = String.format(tip, "通知音", "成功");
                } else {
                    tip = String.format(tip, "通知音", "失败");
                }
                break;

            default:
                break;
        }

        sendResult(Activity.RESULT_OK, tip);

        dismiss();
    }

    /**
     * 初始化视图
     *
     * @param rootView 根视图
     */
    private void initViews(View rootView) {
        mSetDefaultRingtoneButton = (Button) rootView.findViewById(R.id.set_default_ringtone);
        mSetDefaultAlarmButton = (Button) rootView.findViewById(R.id.set_default_alarm);
        mSetDefaultNotificationButton = (Button) rootView.findViewById(R.id.set_default_notification);
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        mSetDefaultRingtoneButton.setOnClickListener(this);
        mSetDefaultAlarmButton.setOnClickListener(this);
        mSetDefaultNotificationButton.setOnClickListener(this);
    }

    /**
     * 设置默认声音
     *
     * @param type 默认声音类型
     * @return true - 设置成功 false - 设置失败
     */
    private boolean setDefaultVoice(int type) {
        if (mPath == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(mPath);

        Cursor cursor = getActivity().getContentResolver().query(uri,
                null,
                MediaStore.MediaColumns.DATA + "=?",
                new String[]{mPath},
                null);
        try {
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                String _id = cursor.getString(0);

                switch (type) {
                    case Constants.RINGTONE:
                        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                        break;
                    case Constants.ALARM:
                        values.put(MediaStore.Audio.Media.IS_ALARM, true);
                        break;
                    case Constants.NOTIFICATION:
                        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                        break;

                    default:
                        break;
                }

                getActivity().getContentResolver().update(uri,
                        values,
                        MediaStore.MediaColumns.DATA + "=?",
                        new String[]{mPath});
                Uri updateUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));

                switch (type) {
                    case Constants.RINGTONE:
                        RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE, updateUri);
                        break;
                    case Constants.ALARM:
                        RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_ALARM, updateUri);
                        break;
                    case Constants.NOTIFICATION:
                        RingtoneManager.setActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_NOTIFICATION, updateUri);
                        break;

                    default:
                        break;
                }

                return true;
            }
        } finally {
            cursor.close();
        }

        return false;
    }

    /**
     * 向目标fragment发送结果
     *
     * @param resultCode 结果码
     * @param result     结果信息
     */
    private void sendResult(int resultCode, String result) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEFAULT_VOICE_SETTING_RESULT, result);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}