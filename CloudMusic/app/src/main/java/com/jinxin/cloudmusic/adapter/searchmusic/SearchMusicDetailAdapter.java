package com.jinxin.cloudmusic.adapter.searchmusic;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.QuickAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.MusicBean.Song;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.play.PlayRequest;
import com.jinxin.cloudmusic.util.DownMP3ClickListener;
import com.jinxin.cloudmusic.util.ToastUtil;

import java.util.List;

/**
 * Created by ZJ on 2017/3/28 0028.
 */
public class SearchMusicDetailAdapter extends QuickAdapter<Song>{
    public SearchMusicDetailAdapter(Context context, int res, List<Song> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        final Song song = data.get(position);
        TextView tvMusicListDetailNum = holder.getView(R.id.tv_music_list_detail_num);
        TextView tvMusicListDetailName = holder.getView(R.id.tv_music_list_detail_name);
        TextView tvMusicListDetailSonger = holder.getView(R.id.tv_music_list_detail_songer);
       final ImageView ivSearchMusicListDownLoad= holder.getView(R.id.iv_music_list_detail_download);
        ImageView ivSearchMusicListCollect=holder.getView(R.id.iv_music_list_detail_collect);

        ivSearchMusicListDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPlay("play",song.getSongid(),ivSearchMusicListDownLoad);//发送播放请求，拿到当前歌名的play
            }
        });
        ivSearchMusicListCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(JxscApp.getContext(), "该功能暂没开放");
            }
        });
        if (position<9){
            tvMusicListDetailNum.setText("0"+(position+1));
        }else {
            tvMusicListDetailNum.setText((position + 1)+"");
        }
        tvMusicListDetailName.setText(song.getSongname());
        tvMusicListDetailSonger.setText(song.getArtistname());
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
