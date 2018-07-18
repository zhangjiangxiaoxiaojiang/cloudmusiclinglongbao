package com.jinxin.cloudmusic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.musiclist.BillboardListAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.base.BaseFragment;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.event.DataEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ZJ on 2017/3/7 0007.
 * 榜单碎片
 */
public class MusicListFragment extends BaseFragment {

    BillboardListAdapter billboardListAdapter;
    List<BillboardList> billboardListData;
    BillboardList billboardData;
    private MusicListDetailFragment musicListDetailFragment;
    private static final int MUSIC_LIST_DETAIL = 1;

    @Bind(R.id.gv_music_category)
    GridView gvMusicCategory;

    private boolean isClickable = true;//可点击状态下为true，否则为false

    @Override
    public View inflate(LayoutInflater inflater, ViewGroup container) {

        return inflater.inflate(R.layout.item_gv_music_list, container, false);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MUSIC_LIST_DETAIL:
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("music_list_detail", (Serializable) msg.obj);
                    if (musicListDetailFragment == null) {
                        musicListDetailFragment = new MusicListDetailFragment();
                    }
                    addFragment(musicListDetailFragment, "每类音乐列表");
                    musicListDetailFragment.setArguments(bundle);
                    break;
            }

        }
    };

    @Override
    public void initData() {
        //适配数据到榜单gridview
        if (getArguments() != null) {
            billboardListData = (List<BillboardList>) getArguments().getSerializable("music_list");
        }
        if (billboardListAdapter == null) {
            billboardListAdapter = new BillboardListAdapter(JxscApp.getContext(), R.layout.item_music_list, billboardListData);
        }
        gvMusicCategory.setAdapter(billboardListAdapter);

        //点击gridview相应位置，发送相应请求，跳转相应界面
        gvMusicCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击发送请求，让loading界面弹出来，如果请求成功就隐藏loading界面
                int a = Integer.parseInt(billboardListData.get(position).getBillboard().getBillboard_type());
                isClickable = false;//点击后
                JxscApp.mainBack=true;//主页面返回键
                JxscApp.bMusicListDetail=1;
                //优化点击直接跳转到第二个界面，在第二个界面发送请求
                Bundle bundle = new Bundle();
                bundle.putInt("type", a);
                if (musicListDetailFragment == null) {
                    musicListDetailFragment = new MusicListDetailFragment();
                }
                musicListDetailFragment.setArguments(bundle);
                addFragment(musicListDetailFragment, "每类音乐列表");

                EventBus.getDefault().post(new DataEvent(3050, ""));

            }
        });

    }

    /**
     * 添加Fragment
     */
    private void addFragment(Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }


}
