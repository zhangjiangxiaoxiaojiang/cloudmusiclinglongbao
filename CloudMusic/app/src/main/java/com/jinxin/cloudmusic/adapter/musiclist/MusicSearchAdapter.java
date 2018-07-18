package com.jinxin.cloudmusic.adapter.musiclist;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;
import com.jinxin.cloudmusic.event.DataEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ZJ on 2017/3/10 0010.
 * 历史搜索适配器
 */
public class MusicSearchAdapter extends QuickAdapter<String>{
    public MusicSearchAdapter(Context context, int res, List<String> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        String a=data.get(position);
        TextView textView=holder.getView(R.id.tv_history_search);
        ImageView ivPlayDelete=holder.getView(R.id.iv_play_delete);
        ivPlayDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new DataEvent(100,position));
            }
        });
        textView.setText(a);
        return convertView;
    }
}
