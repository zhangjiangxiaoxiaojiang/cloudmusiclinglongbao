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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.searchmusic.SearchMusicDetailAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.base.BaseFragment;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.PosEvent;
import com.jinxin.cloudmusic.event.PositionEvent;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.search.SearchRequest;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cloudmusic.util.LoadingUtil;
import com.jinxin.cloudmusic.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;

import butterknife.Bind;

/**
 * 搜索详情碎片
 */
public class SearchDeatilFragment extends BaseFragment {
    @Bind(R.id.lv_search_detail)
    ListView lvSearchDetail;
    @Bind(R.id.iv_artist)
    ImageView ivArtist;
    @Bind(R.id.tv_search_name)
    TextView tvSearchName;
    @Bind(R.id.tv_search_artist)
    TextView tvSearchArtist;

    private PlayInterfaceFragment playInterfaceFragment;
    private BillboardList billboardData;
    private int serarchPos;
    private Dialog loadingDialog;
    private String stringHistoryData;

    private Search search;
    private Play searchPlay;
    private SearchMusicDetailAdapter searchMusicDetailAdapter;

    private boolean isClickable = true;//可点击状态下为true，否则为false

    private long time;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (search != null && search.getSongList().size() > 0) {

                        tvSearchArtist.setText(search.getSongList().get(0).getArtistname());
                        tvSearchName.setText(search.getSongList().get(0).getSongname());
                        if (search.getArtistList() != null && !search.getArtistList().get(0).getArtistpic().isEmpty()) {
                            Picasso.with(JxscApp.getContext()).load(search.getArtistList().get(0).getArtistpic()).placeholder(R.drawable.background).error(R.drawable.background).into(ivArtist);
                        }else {
                            Picasso.with(JxscApp.getContext()).load(R.drawable.background).placeholder(R.drawable.background).error(R.drawable.background).into(ivArtist);
                        }
                        searchMusicDetailAdapter = new SearchMusicDetailAdapter(JxscApp.getContext(), R.layout.item_music_type_list_detail, search.getSongList());
                        lvSearchDetail.setAdapter(searchMusicDetailAdapter);
                    }
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
    public View inflate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_deatil, container, false);
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        showLoading("请稍后...");
        //接收数据
        if (getArguments() != null) {
            stringHistoryData = (String) getArguments().getSerializable("historySearchStr");
        }
        if (stringHistoryData!=null) {
            requestSearch("search", stringHistoryData);
        }


        //点击相应item，发送请求，跳转的播放界面
        lvSearchDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                }else {*/
                    //showLoading("请稍后...");
                    lvSearchDetail.setEnabled(false);//不可点击
                    serarchPos = position;
                    JxscApp.booSearchOrInternet = true;
                    JxscApp.booleanJudge = false;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("search", (Serializable) search);
                    bundle.putInt("searchPos", serarchPos);
                    //点击传递事件，在mainactivity里面创建播放界面
                    EventBus.getDefault().post(new DataEvent(3010, bundle));

                    isClickable = false;//点击后
                }
            //}
        });

    }


    //搜索
    private void requestSearch(String method, String query) {
        HttpManager.getInstance().addRequestMusic(new SearchRequest(new Callback<Search>() {
            @Override
            public void onReceive(Search search1) {
                super.onReceive(search);
                search = search1;
                String s = search.toString();
                String a = search.getSongList().get(0).getArtistname();
                L.e(search.toString());
                handler.obtainMessage(1).sendToTarget();
                EventBus.getDefault().post(new PosEvent(3));
            }

            @Override
            public void onError(String error) {
                hideLoading();
                ToastUtil.showShort(getActivity(), "网络加载失败，请重新加载！");
            }
        }, method, query));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onEventMainThread(PositionEvent event) {
        if (event.getP()==12){
            //hideLoading();
            lvSearchDetail.setEnabled(true);//可点击
        }
    }
    @Subscribe
    public void onEventMainThread(PosEvent event) {
        if (event.getPos() == 20) {
            this.onDestroy();
        }if (event.getPos()==512){
            hideLoading();
            ToastUtil.showShort(JxscApp.getContext(), "没有搜索到相关内容");
            EventBus.getDefault().post(new PosEvent(3));
        }
    }
    @Subscribe
    public void onEventMainThread(DataEvent event) {
        if (event.getDatas() == 30) {
            requestSearch("search", (String) event.getObj());
            handler.obtainMessage(1).sendToTarget();
        }
    }
    public void showLoading(String msg){
        loadingDialog = LoadingUtil.createLoadingDialog(getActivity(), msg);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    public void hideLoading(){
        if(loadingDialog!=null)
            loadingDialog.dismiss();
        loadingDialog = null;
    }

}
