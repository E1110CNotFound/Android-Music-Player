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
 * DetailBrowserFragment
 *
 * @author Guo Yonghui
 *         <p/>
 *         音乐详情页
 */
public class DetailBrowserFragment extends DialogFragment {

    /**
     * EXTRA - 详情所属的音乐
     */
    public static final String EXTRA_MUSIC_DETAIL = "com.guoyonghui.musicplayerEXTRA_MUSIC_DETAIL";

    /**
     * 详情数据
     */
    private ArrayList<Detail> mDetails;

    /**
     * 创建一个附有音实例参数的DetailBrowserFragment实例
     *
     * @param music 音乐实例
     * @return 附有音乐实例的DetailBrowserFragment实例
     */
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_detail_browser, null);

        ElasticListView detailList = (ElasticListView) view.findViewById(R.id.music_detail_list);
        detailList.setAdapter(new DetailBrowserAdapter(mDetails));

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

        mDetails.add(new Detail(getResources().getString(R.string.detail_title), music.getTitle()));
        mDetails.add(new Detail(getResources().getString(R.string.detail_duration), music.getDuration() / 1000 / 60 + ":" + music.getDuration() / 1000 % 60));
        mDetails.add(new Detail(getResources().getString(R.string.detail_display_name), music.getDisplayName()));
        mDetails.add(new Detail(getResources().getString(R.string.detail_artist), music.getArtist()));
        mDetails.add(new Detail(getResources().getString(R.string.detail_album), music.getAlbum()));
        mDetails.add(new Detail(getResources().getString(R.string.detail_path), music.getPath()));
    }

    /**
     * 详情列表适配器
     * 使用ViewHolder模式提高加载效率
     */
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
            if (convertView == null) {
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
