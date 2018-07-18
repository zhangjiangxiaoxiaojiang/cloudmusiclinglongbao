package com.jinxin.cloudmusic.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.musiclist.MusicSearchAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.base.BaseFragment;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
import com.jinxin.cloudmusic.db.SPM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 *
 * 搜索主页碎片
 * 需要完成的功能，点击历史记录继续搜索
 */
public class SearchMainFragment extends BaseFragment {
    @Bind(R.id.lv_history_search)
    ListView lvHistorySearch;
    @Bind(R.id.tv_hot_search)
    TextView tvHotSearch;
    @Bind(R.id.btn_delete_all)
    Button btnDeleteAll;
    private SearchDeatilFragment searchDeatilFragment;
    private MusicSearchAdapter musicSearchAdapter;
    private List<String> stringHistoryData;
    private boolean isClickable=true;//可点击状态下为true，否则为false
    private Search search;
    private String itemStr;
    @Override
    public View inflate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_main, container, false);
    }
    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        //接收数据
        if (getArguments() != null) {
            stringHistoryData = (List<String>) getArguments().getSerializable("historySearch");
        }
        //过滤为空的字符串并且去重
        /*for (int c=0;c<stringHistoryData.size();c++){
            if (stringHistoryData.get(c).equals("null")){
                stringHistoryData.remove(stringHistoryData.get(c));
            }
        }*/

        if (!stringHistoryData.get(0).equals("")) {
            musicSearchAdapter = new MusicSearchAdapter(JxscApp.getContext(), R.layout.item_history_search, stringHistoryData);
            lvHistorySearch.setAdapter(musicSearchAdapter);
        }

        //点击相应历史记录，完成搜索并且跳转
        lvHistorySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JxscApp.searchBack=true;
                Bundle bundle = new Bundle();
                //bundle.putSerializable("searchSongDetail", search);
                bundle.putSerializable("historySearchStr", (Serializable) stringHistoryData.get(position));
                if (searchDeatilFragment == null) {
                    searchDeatilFragment = new SearchDeatilFragment();
                }
                searchDeatilFragment.setArguments(bundle);
                addFragment(searchDeatilFragment, "searchDeatilFragment");

                isClickable=false;//点击后
            }
        });
        //清空历史记录
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移除配置表
                SPM.removeSP("历史搜索");
                if (musicSearchAdapter!=null) {
                    musicSearchAdapter.removeAll();
                }else {
                    ToastUtil.showShort(JxscApp.getContext(),"暂无历史记录");
                }
                stringHistoryData = null;
                //强制清空
                SPM.saveListStr("历史搜索", "historySearch", null,null);
                List<String> q=SPM.getListStr("历史搜索", "historySearch", "");
            }
        });
    }

    @Subscribe
    public void onEventMainThread(DataEvent event) {
        if (event.getDatas() == 100) {
            itemStr= (String)musicSearchAdapter.getItem((int) event.getObj());
        }

        if (itemStr!=null&&musicSearchAdapter!=null) {//判空处理
            musicSearchAdapter.remove(itemStr);
            SPM.saveListStr1("历史搜索", "historySearch", stringHistoryData,itemStr);
        }
    }
    /**
     * 添加Fragment
     */
    private void addFragment(Fragment fragment, String tag) {
        getFragmentManager().beginTransaction().replace(R.id.fl_main_search, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        //取到最新的历史记录
        //取历史记录
        if (SPM.getListStr("历史搜索", "historySearch", "") != null && SPM.getListStr("历史搜索", "historySearch", "").size() > 0) {
            stringHistoryData = new ArrayList<>();
            if (SPM.getListStr("历史搜索", "historySearch", "").size() > 6) {
                for (int i = 0; i < 6; i++) {
                    stringHistoryData.add(SPM.getListStr("历史搜索", "historySearch", "").get(i));
                }
            } else {
                for (String a : SPM.getListStr("历史搜索", "historySearch", "")) {
                    stringHistoryData.add(a);
                }
            }
        }
       /* for (int c=0;c<stringHistoryData.size();c++){
            if (stringHistoryData.get(c).equals("null")){
                stringHistoryData.remove(stringHistoryData.get(c));
            }
        }*/
        if (!stringHistoryData.get(0).equals("")) {
            musicSearchAdapter = new MusicSearchAdapter(JxscApp.getContext(), R.layout.item_history_search, stringHistoryData);
            lvHistorySearch.setAdapter(musicSearchAdapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
