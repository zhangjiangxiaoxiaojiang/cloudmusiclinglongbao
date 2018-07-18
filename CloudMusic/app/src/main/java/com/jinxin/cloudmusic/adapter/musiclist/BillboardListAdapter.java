package com.jinxin.cloudmusic.adapter.musiclist;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZJ on 2017/3/7 0007.
 * 榜单适配器
 */
public class BillboardListAdapter extends QuickAdapter<BillboardList>{
    public BillboardListAdapter(Context context, int res, List<BillboardList> data) {
        super(context, res, data);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        BillboardList billboardList=data.get(position);
        ImageView ivIcon=holder.getView(R.id.iv_music_list_icon);
        TextView tvIcon=holder.getView(R.id.tv_music_list_name);
        ListView lvIcon=holder.getView(R.id.lv_music_list);

        String ivPath=billboardList.getBillboard().getPic_s210();
        Picasso.with(context).load(ivPath).placeholder(R.drawable.background).error(R.drawable.background).into(ivIcon);
        tvIcon.setText(billboardList.getBillboard().getName());
        MusicListAdapter musicListAdapter=new MusicListAdapter(JxscApp.getContext(),R.layout.item_home_page_music_list,billboardList.getLists());
        lvIcon.setAdapter(musicListAdapter);
        //设置listview不可点击
        lvIcon.setEnabled(false);
        return convertView;
    }

}
