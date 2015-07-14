package com.guoyonghui.musicplayer.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.guoyonghui.musicplayer.R;
import com.guoyonghui.musicplayer.model.Detail;
import com.guoyonghui.musicplayer.model.Music;
import com.guoyonghui.musicplayer.view.ElasticListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 永辉 on 2015/7/14.
 */
public class DetailBrowserFragment extends DialogFragment {

    public static final String EXTRA_MUSIC_DETAIL = "com.guoyonghui.musicplayerEXTRA_MUSIC_DETAIL";


    /**
     * 详情数据
     */
    private ArrayList<Detail> mDetails;

    /**
     * 详情列表
     */
    private ElasticListView mDetailList;

    public static DetailBrowserFragment newInstance(Music music) {
        DetailBrowserFragment fragment = new DetailBrowserFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MUSIC_DETAIL, music);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        analysisMusicDetail((Music) (getArguments().getParcelable(EXTRA_MUSIC_DETAIL)));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_music_detail, null);

        mDetailList = (ElasticListView) view.findViewById(R.id.music_detail_list);
        mDetailList.setAdapter(new DetailBrowserAdapter(mDetails));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.action_music_detail)
                .create();
    }

    /**
     * 获取音乐详情
     *
     * @param music 音乐
     */
    private void analysisMusicDetail(Music music) {
        mDetails = new ArrayList<>();

        mDetails.add(new Detail("标题", music.getTitle()));
        mDetails.add(new Detail("长度", music.getDuration() / 1000 / 60 + ":" + music.getDuration() / 1000 % 60));
        mDetails.add(new Detail("文件名", music.getDisplayName()));
        mDetails.add(new Detail("艺术家", music.getArtist()));
        mDetails.add(new Detail("专辑", music.getAlbum()));
        mDetails.add(new Detail("位置", music.getPath()));
    }

    private class DetailBrowserAdapter extends ArrayAdapter<Detail> {
        
        public DetailBrowserAdapter(List<Detail> details) {
            super(getActivity(), R.layout.detail_item, details);
        }

        @Override
        public int getCount() {
            return mDetails.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.detail_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.detailTitleTextView = (TextView) convertView.findViewById(R.id.detail_title);
                viewHolder.detailContentTextView = (TextView) convertView.findViewById(R.id.detail_content);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Detail detail = mDetails.get(position);

            viewHolder.detailTitleTextView.setText(detail.getTitle());
            viewHolder.detailContentTextView.setText(detail.getContent());

            return convertView;
        }

        private class ViewHolder {
            TextView detailTitleTextView;
            
            TextView detailContentTextView;
        }
    }

}
