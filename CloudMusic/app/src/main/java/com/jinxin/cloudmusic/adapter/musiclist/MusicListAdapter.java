package com.jinxin.cloudmusic.adapter.musiclist;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;
import com.jinxin.cloudmusic.bean.MusicBean.SongList;

import java.util.List;

/**
 * Created by ZJ on 2017/3/7 0007.
 * 榜单每类音乐列表子适配器
 */
public class MusicListAdapter extends QuickAdapter<SongList>{
    public MusicListAdapter(Context context, int res, List<SongList> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        SongList songList=data.get(position);
        TextView tvListName=holder.getView(R.id.tv_home_page_list_name);
        TextView tvSongerName=holder.getView(R.id.tv_home_page_songer_name);
        TextView tvListNum=holder.getView(R.id.tv_home_page_list_num);

        tvListNum.setText(songList.getRank());
        tvListName.setText(songList.getTitle());
        tvSongerName.setText(songList.getAuthor());
        return convertView;
    }
}
