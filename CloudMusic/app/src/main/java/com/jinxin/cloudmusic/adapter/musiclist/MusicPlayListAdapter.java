package com.jinxin.cloudmusic.adapter.musiclist;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.MusicBean.SongList;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.play.PlayRequest;
import com.jinxin.cloudmusic.util.DownMP3ClickListener;
import com.jinxin.cloudmusic.util.ToastUtil;

import java.util.List;

/**
 * Created by ZJ on 2017/12/10 0010.
 * 播放列表（暂不用）
 */
public class MusicPlayListAdapter extends QuickAdapter<Object>{
    public MusicPlayListAdapter(Context context, int res, List<Object> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(int position, View convertView, QuickAdapter.ViewHolder holder) {
        if (JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == false){//在通过榜单列表播放
            final SongList songList = (SongList)data.get(position);
            TextView tvMusicListDetailNum = holder.getView(R.id.tv_music_list_detail_num);
            TextView tvMusicListDetailName = holder.getView(R.id.tv_music_list_detail_name);
            TextView tvMusicListDetailSonger = holder.getView(R.id.tv_music_list_detail_songer);
            final ImageView ivListDetailDownLoad=holder.getView(R.id.iv_music_list_detail_download);//下载
            ImageView ivListDetailCollect=holder.getView(R.id.iv_music_list_detail_collect);//收集

            //String转int
            try {
                if (Integer.parseInt(songList.getRank()) < 10) {
                    tvMusicListDetailNum.setText("0" + songList.getRank());
                } else {
                    tvMusicListDetailNum.setText(songList.getRank());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ivListDetailDownLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPlay("play",songList.getSong_id(),ivListDetailDownLoad);//发送播放请求，拿到当前歌名的play
                }
            });
            ivListDetailCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showShort(JxscApp.getContext(), "该功能暂没开放");
                }
            });
            tvMusicListDetailName.setText(songList.getTitle());
            tvMusicListDetailSonger.setText(songList.getAuthor());
            return convertView;
        }else if (JxscApp.booleanJudge == true){//本地播放

            return convertView;
        }else if (JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == true){//搜索播放

            return convertView;
        }
            return convertView;
    }

    //播放请求
    private void requestPlay(String method, String songid,final View view) {
        HttpManager.getInstance().addRequestMusic(new PlayRequest(new Callback<Play>() {
            @Override
            public void onReceive(Play play) {
                super.onReceive(play);
                String name = play.getSongInfo().getAuthor();
                new DownMP3ClickListener(play).onClick(view);
            }

            @Override
            public void onError(String error) {

            }
        },method,songid));
    }
}
