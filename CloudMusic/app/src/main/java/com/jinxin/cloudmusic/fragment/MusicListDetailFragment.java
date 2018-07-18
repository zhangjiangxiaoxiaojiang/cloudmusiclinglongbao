package com.jinxin.cloudmusic.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.musiclist.MusicListDetailAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.base.BaseFragment;
import com.jinxin.cloudmusic.bean.AcountProduct;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.PositionEvent;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.musiclist.MusicDemoRequest;
import com.jinxin.cloudmusic.util.LoadingUtil;
import com.jinxin.cloudmusic.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ZJ on 2017/3/8 0008.
 * 歌单详情
 */
public class MusicListDetailFragment extends BaseFragment {
    @Bind(R.id.lv_music_list_detail)
    ListView lvMusicListDetail;
    @Bind(R.id.iv_my_music_detail)//图片
            ImageView ivMyMusicDetail;
    @Bind(R.id.tv_my_music_type)//歌曲类型
            TextView tvMyMusicType;
    @Bind(R.id.tv_my_music_introduce)//歌曲介绍
            TextView tvMyMusicIntroduce;
    @Bind(R.id.bt_all_collection)
    Button btAllCollection;
    @Bind(R.id.bt_all_download)
    Button btAllDownload;
    @Bind(R.id.bt_all_play)
    Button btAllPlay;
    PlayInterfaceFragment playInterfaceFragment;
    private BillboardList billboardData;
    private MusicListDetailAdapter musicListDetailAdapter;
    private int pos;
    private boolean isClickable = true;//可点击状态下为true，否则为false

    private List<AcountProduct> acountProductList;
    private List<PowerState> dataMusicName = new ArrayList<>();
    private boolean t = false;

    private int type;
    private Play musicPlay;

    private long time;

    private Dialog loadingDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    billboardData = (BillboardList) msg.obj;
                    if (billboardData!=null)
                    Picasso.with(JxscApp.getContext()).load(billboardData.getBillboard().getPic_s210()).placeholder(R.drawable.background).error(R.drawable.background).into(ivMyMusicDetail);
                    musicListDetailAdapter = new MusicListDetailAdapter(JxscApp.getContext(), R.layout.item_music_type_list_detail, billboardData.getLists());
                    lvMusicListDetail.setAdapter(musicListDetailAdapter);
                    tvMyMusicType.setText(billboardData.getBillboard().getName());
                    tvMyMusicIntroduce.setText(billboardData.getBillboard().getComment());
                    //请求成功则关闭loading
                    hideLoading();
                    break;
                case 2:

                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JxscApp.iMusicListDetail=1;
        Log.e("---onCreate","onCreate");
    }

    @Override
    public View inflate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fr_music_list_detail, container, false);
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        //优化机制
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
        showLoading("请稍后...");
        requestMusicList("billboard", type, 20, 0);

        lvMusicListDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //2秒内不能重复点击
                if (System.currentTimeMillis()-time<2500) {
                    return;
                }
                    time = System.currentTimeMillis();

                /*if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                        DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                                DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
                    EventBus.getDefault().post(new DataEvent(1050, ""));//在mainActivity里面创建登录界面
                    ToastUtil.showShort(JxscApp.getContext(),"请先登录...");
                } else {*/
                    //showLoading("请稍后...");
                    lvMusicListDetail.setEnabled(false);//不可点击
                    pos = position;
                    JxscApp.booleanJudge = false;
                    JxscApp.booSearchOrInternet = false;

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("billboardData", (Serializable) billboardData);
                    bundle.putSerializable("play", (Serializable) musicPlay);
                    bundle.putInt("pos", pos);
                    //点击传递事件，在mainactivity里面创建播放界面
                    EventBus.getDefault().post(new DataEvent(3010, bundle));

                    isClickable = false;

                    Log.e("----碎片的个数",getActivity().getSupportFragmentManager().getBackStackEntryCount()+"");
                //}
            }
        });

        btAllCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
            }
        });
        btAllDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
            }
        });
        btAllPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void onEventMainThread(PositionEvent event) {
        if (event.getP() == 12) {
            //hideLoading();
            lvMusicListDetail.setEnabled(true);
        }
    }

    @Subscribe
    public void onEventMainThread(DataEvent event) {
    }

    //请求榜单列表
    private void requestMusicList(String method, int type, int size, int offset) {
        HttpManager.getInstance().addRequestMusic(new MusicDemoRequest(new Callback<BillboardList>() {
            @Override
            public void onReceive(BillboardList billboardList) {
                super.onReceive(billboardList);
                isClickable = true;//可点击
                String name = billboardList.getBillboard().getName();
                billboardData = billboardList;
                Log.e("----请求到列表数据","正在加载");
                handler.obtainMessage(1, billboardData).sendToTarget();
            }

            @Override
            public void onError(String error) {
                hideLoading();
                ToastUtil.showShort(getActivity(),"网络加载失败，请重新加载！");
            }
        }, method, type, size, offset));
    }

    public void showLoading(String msg) {
        loadingDialog = LoadingUtil.createLoadingDialog(getActivity(), msg);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    public void hideLoading() {
        if (loadingDialog != null)
            loadingDialog.dismiss();
        loadingDialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        JxscApp.iMusicListDetail=0;
        Log.e("---onDestroy","onDestroy");
    }


    @Override
    public void onPause() {
        super.onPause();
        JxscApp.iMusicListDetail=0;
        Log.e("---onPause","onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("---stop","stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        JxscApp.iMusicListDetail=1;
        Log.e("---onResume","onResume");
    }
}
