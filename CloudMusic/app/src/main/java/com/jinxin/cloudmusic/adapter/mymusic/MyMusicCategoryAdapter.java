package com.jinxin.cloudmusic.adapter.mymusic;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;

import java.util.List;

/**
 * Created by ZJ on 2017/3/17 0017.
 * 我的音乐左侧类别适配器
 */
public class MyMusicCategoryAdapter extends QuickAdapter<String> {
    private int selectedPos;

    public MyMusicCategoryAdapter(Context context, int res, List<String> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        String a = data.get(position);
        TextView tvCategory = holder.getView(R.id.tv_my_music_category);
        TextView tvCategoryNum = holder.getView(R.id.tv_my_music_num_category);
        ImageView ivCategory = holder.getView(R.id.iv_my_music_category);
        RelativeLayout rlMyMusicCategory = holder.getView(R.id.rl_my_music_category);


        if (position == 0) {
            ivCategory.setImageResource(R.drawable.download);
        } else if (position == 1) {
            ivCategory.setImageResource(R.drawable.collect);
        } else if (position == 2) {
            ivCategory.setImageResource(R.drawable.my_music_recent_play);
        }
        if (selectedPos == position) {
            rlMyMusicCategory.setBackgroundResource(R.color.music_bottom_bar_bg);
        } else {
            rlMyMusicCategory.setBackgroundResource(R.color.transparent);
        }
        tvCategory.setText(a);
        return convertView;
    }

    public void select(int pos) {
        selectedPos = pos;
        notifyDataSetChanged();
    }
}