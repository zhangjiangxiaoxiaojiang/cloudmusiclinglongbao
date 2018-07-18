package com.jinxin.cloudmusic.adapter.mymusic;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;

import java.util.List;

/**
 * Created by ZJ on 2017/3/17 0017.
 */
public class SdcardMusicAdapter extends QuickAdapter<String> {
    public SdcardMusicAdapter(Context context, int res, List<String> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        String musicName=data.get(position);
        TextView songName=holder.getView(R.id.tv_my_music_name);
        TextView songNum=holder.getView(R.id.tv_my_music_num);
        if (position<9){
            songNum.setText("0"+(position+1));
        }else {
            songNum.setText((position+1)+"");
        }
        songName.setText(musicName);
        return convertView;
    }
}
