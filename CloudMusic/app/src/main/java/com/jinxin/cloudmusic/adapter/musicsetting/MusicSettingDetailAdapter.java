package com.jinxin.cloudmusic.adapter.musicsetting;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.event.MusicSettingEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ZJ on 2017/3/17 0017.
 */
public class MusicSettingDetailAdapter extends QuickAdapter<PowerState> {
    public MusicSettingDetailAdapter(Context context, int res, List<PowerState> data/*,boolean t*/) {
        super(context, res, data);
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        String musicName=data.get(position).getPowerName()+"("+data.get(position).getMac()+")";
        TextView tvMusicSettingDetail=holder.getView(R.id.tv_music_setting_detail);
        final ImageView ivMusicSettingDetail=holder.getView(R.id.iv_music_setting_detail);
        ivMusicSettingDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //传递一个位置出去，更新
                EventBus.getDefault().post(new MusicSettingEvent(position));
            }
        });
        if (data.get(position).isPowerState() == false) {
            ivMusicSettingDetail.setImageResource(R.drawable.speaker_unchecked);
        } else {
            ivMusicSettingDetail.setImageResource(R.drawable.speaker_checked);
        }
        tvMusicSettingDetail.setText(musicName);
        return convertView;
    }
}
