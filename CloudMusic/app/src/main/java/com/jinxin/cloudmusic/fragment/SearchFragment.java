package com.jinxin.cloudmusic.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.base.BaseFragment;
import com.jinxin.cloudmusic.db.SPM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.PosEvent;
import com.jinxin.cloudmusic.event.SpeechContrlEvent;
import com.jinxin.cloudmusic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 搜索主页
 */
public class SearchFragment extends BaseFragment {
    @Bind(R.id.et_info_text)
    EditText etInfoText;
    @Bind(R.id.btn_search)
    Button btnSearch;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_search_delete)
    ImageView ivSearchDelete;

    private SearchMainFragment searchMainFragment;
    private SearchDeatilFragment searchDeatilFragment;

    private String b;
    private List<String> stringList = new ArrayList<>();
    private List<String> stringHistoryData;
    private List<String> currentData = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    btnSearch.setTextColor(getResources().getColor(R.color.darkgrey));

                    break;
                case 2:
                    btnSearch.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 3:
                    EventBus.getDefault().post(new DataEvent(30, msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View inflate(LayoutInflater inflater, ViewGroup container) {

        return inflater.inflate(R.layout.fra_music_search, container, false);
    }

    @Subscribe
    public void onEventMainThread(PosEvent event) {
        if (event.getPos() == 3) {
            handler.obtainMessage(2).sendToTarget();
        }
    }

    @Subscribe
    public void onEventMainThread(SpeechContrlEvent event){
        if (event.getTag()==1008){//语音后退
            if (JxscApp.bSpeechSearchInterface==false&&JxscApp.hidePlayinterface!=0){
                if (JxscApp.searchBack == false) {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(SearchFragment.this).commit();
                }else {
                    addFragment(searchMainFragment, "转化搜索主碎片");
                    JxscApp.searchBack=false;
                }
            }
        }
    }
    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        etInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMainFragment == null) {
                    searchMainFragment = new SearchMainFragment();
                }
                addFragment(searchMainFragment, "转化搜索主碎片");
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JxscApp.searchBack == false) {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(SearchFragment.this).commit();
                }else {
                    addFragment(searchMainFragment, "转化搜索主碎片");
                    JxscApp.searchBack=false;
                }
            }
        });
        ivSearchDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etInfoText.getText().toString().isEmpty()) {
                    etInfoText.setText("");
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JxscApp.searchBack=true;
                //存历史记录（保留搜索前6个）
                if (!TextUtils.isEmpty(etInfoText.getText().toString())) {
                    b = etInfoText.getText().toString();
                    //存历史记录string，得到历史记录的前6个取为一个list

                    //先清空再添加
                    stringList.clear();
                    if (!SPM.getListStr("历史搜索", "historySearch", "").contains(b)) {

                        stringList.add(b);
                        SPM.saveListStr("历史搜索", "historySearch", stringList);
                    }
                    currentData.clear();
                    List<String> a = SPM.getListStr("历史搜索", "historySearch", "");
                    if (SPM.getListStr("历史搜索", "historySearch", "").size() > 6) {
                        for (int i = 0; i < 6; i++) {
                            currentData.add(SPM.getListStr("历史搜索", "historySearch", "").get(i));
                        }
                    } else {
                        for (int i = 0; i < SPM.getListStr("历史搜索", "historySearch", "").size(); i++)
                            currentData.add(SPM.getListStr("历史搜索", "historySearch", "").get(i));
                    }

                } else {
                    ToastUtil.showShort(JxscApp.getContext(), "搜索内容不能为空！");
                    return;
                }
                //功能1：输入文字直接搜索跳转页面
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("searchSongDetail", search);

                if (searchDeatilFragment != null) {
                    EventBus.getDefault().post(new PosEvent(20));
                }
                searchDeatilFragment = new SearchDeatilFragment();
                addFragment(searchDeatilFragment, "searchDeatilFragment");

                handler.obtainMessage(1).sendToTarget();
                handler.obtainMessage(3, etInfoText.getText().toString()).sendToTarget();
            }
        });
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

        //完成存取数据后，通过bundle传递到历史记录碎片
        Bundle bundle = new Bundle();
        bundle.putSerializable("historySearch", (Serializable) stringHistoryData);
        //初始化界面（优先加载搜索列表界面）
        if (searchMainFragment == null) {
            searchMainFragment = new SearchMainFragment();
        }
        searchMainFragment.setArguments(bundle);
        addFragment(searchMainFragment, "historyFragment");
    }

    /**
     * 添加Fragment
     */
    private void addFragment(Fragment fragment, String tag) {
        getFragmentManager().beginTransaction().replace(R.id.fl_main_search, fragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
