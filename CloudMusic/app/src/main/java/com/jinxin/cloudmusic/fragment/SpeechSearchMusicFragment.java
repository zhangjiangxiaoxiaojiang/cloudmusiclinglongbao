package com.jinxin.cloudmusic.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.cloudmusic.MainActivity;
import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.searchmusic.SearchMusicDetailAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
import com.jinxin.cloudmusic.bean.MusicBean.Song;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.PosEvent;
import com.jinxin.cloudmusic.event.PositionEvent;
import com.jinxin.cloudmusic.event.SpeechContrlEvent;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ZJ on 2017/11/1 0001.
 * 语音搜索结果界面
 */
public class SpeechSearchMusicFragment extends DialogFragment {
    @Bind(R.id.iv_speech_back)
    ImageView ivSpeechBack;
    @Bind(R.id.et_info_speech_text)
    EditText etInfoSpeechText;
    @Bind(R.id.btn_speech_search)
    Button btnSpeechSearch;
    @Bind(R.id.iv_speech_artist)
    ImageView ivSpeechArtist;
    @Bind(R.id.lv_speech_search_detail)
    ListView lvSpeechSearchDetail;
    @Bind(R.id.tv_speeech_search_artist)
    TextView tvSpeechSearchArtist;
    @Bind(R.id.tv_speech_search_name)
    TextView tvSpeechSearchName;
    @Bind(R.id.iv_speech_search_delete)
    ImageView ivSpeechSearchDelete;

    private String strKeyword;
    private Search search;
    private SearchMusicDetailAdapter searchMusicDetailAdapter;
    private Dialog loadingDialog;
    private long time;
    private int serarchPos;
    private boolean isClickable = true;//可点击状态下为true，否则为false
    private MainActivity mainActivity;
    private Search searchPlay;
    private List<Song> songListSearchPlay=new ArrayList<>();
    private Song songSearchplay;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (search != null && search.getSongList().size() > 0) {

                        tvSpeechSearchArtist.setText(search.getSongList().get(0).getArtistname());
                        tvSpeechSearchName.setText(search.getSongList().get(0).getSongname());
                        if (search.getArtistList() != null && !search.getArtistList().get(0).getArtistpic().isEmpty()) {
                            Picasso.with(JxscApp.getContext()).load(search.getArtistList().get(0).getArtistpic()).placeholder(R.drawable.background).error(R.drawable.background).into(ivSpeechArtist);
                        }else {
                            Picasso.with(JxscApp.getContext()).load(R.drawable.background).placeholder(R.drawable.background).error(R.drawable.background).into(ivSpeechArtist);
                        }
                        searchMusicDetailAdapter = new SearchMusicDetailAdapter(JxscApp.getContext(), R.layout.item_music_type_list_detail, search.getSongList());
                        lvSpeechSearchDetail.setAdapter(searchMusicDetailAdapter);
                    }
                    //hideLoading();
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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.costom);
        EventBus.getDefault().register(this);
        JxscApp.bSpeechSearchInterface=true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fra_speech_search_music, container, false);
        ButterKnife.bind(this, view);
        init();//初始化
        return view;
    }

    private void init() {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
        if (null!=getArguments().getString("strKeyword")){
            //showLoading("请稍后...");
            strKeyword=getArguments().getString("strKeyword");
            etInfoSpeechText.setText(strKeyword);
            //发起搜索请求
            requestSearch("search", strKeyword);
        }else {
            ToastUtil.showShort(getActivity(),"搜索的内容为空！");
        }

        //页面关键点击按钮
        ivSpeechBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                JxscApp.bSpeechSearchInterface=false;
                EventBus.getDefault().post(new DataEvent(10001, "主动置空"));
                onDestroy();
            }
        });
        btnSpeechSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSearch("search", etInfoSpeechText.getText().toString().trim());
            }
        });
        //点击相应item，发送请求，跳转的播放界面
        lvSpeechSearchDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //2秒内不能重复点击
                if (System.currentTimeMillis() - time < 2500) {
                    return;
                }
                time = System.currentTimeMillis();

                /*if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                        DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                                DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
                    EventBus.getDefault().post(new DataEvent(1050, ""));//在mainActivity里面创建登录界面
                    ToastUtil.showShort(JxscApp.getContext(), "请先登录...");
                } else {*/
                    //showLoading("请稍后...");
                    lvSpeechSearchDetail.setEnabled(false);//不可点击
                    serarchPos = position;
                    JxscApp.booSearchOrInternet = true;
                    JxscApp.booleanJudge = false;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("search", (Serializable) search);
                    bundle.putInt("searchPos", serarchPos);
                    //点击传递事件，在mainactivity里面创建播放界面
                    EventBus.getDefault().post(new DataEvent(3010, bundle));

                    isClickable = false;//点击后
                //}
            }
        });
        ivSpeechSearchDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etInfoSpeechText.setText("");
            }
        });
    }

    //搜索
    private void requestSearch(String method, String query) {
        HttpManager.getInstance().addRequestMusic(new SearchRequest(new Callback<Search>() {
            @Override
            public void onReceive(Search search1) {
                super.onReceive(search1);
                search = search1;
                String s = search.toString();
                String a = search.getSongList().get(0).getArtistname();
                L.e(search.toString());
                handler.obtainMessage(1).sendToTarget();
                EventBus.getDefault().post(new PosEvent(3));//改变字体颜色
            }

            @Override
            public void onError(String error) {
                hideLoading();
                ToastUtil.showShort(getActivity(), "网络加载失败，请重新加载！");
            }
        }, method, query));

    }

    @Subscribe
    public void onEventMainThread(SpeechContrlEvent event) {
        if (event.getTag()==1001){
            if (null!=search){
                /*if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                        DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                                DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
                    EventBus.getDefault().post(new DataEvent(1050, ""));//在mainActivity里面创建登录界面
                    ToastUtil.showShort(JxscApp.getContext(),"请先登录...");
                }else {*/
                    //遍历歌曲列表去和关键字进行匹配
                    for (int i=0;i<search.getSongList().size();i++){
                        if (event.getObj1().equals(search.getSongList().get(i).getSongname())){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("search", (Serializable) search);
                            bundle.putInt("searchPos", i);
                            JxscApp.booSearchOrInternet = true;
                            JxscApp.booleanJudge = false;
                            //点击传递事件，在mainactivity里面创建播放界面
                            EventBus.getDefault().post(new DataEvent(3010, bundle));
                            return;//搜索歌名时，多首相同时，默认播放第一首
                        }
                    }
                    //通过搜索播放
                    EventBus.getDefault().post(new DataEvent(40000,event.getObj1()));
                //}
            }else {
                EventBus.getDefault().post(new DataEvent(40000,event.getObj1()));
            }
        }else if (event.getTag()==1008){//返回
            if (/*JxscApp.hidePlayinterface==true*/JxscApp.hidePlayinterface!=0){
                getDialog().dismiss();
                JxscApp.bSpeechSearchInterface=false;
                EventBus.getDefault().post(new DataEvent(10001, "主动置空"));
                onDestroy();
            }
        }
    }

    @Subscribe
    public void onEventMainThread(PositionEvent event) {
        if (event.getP()==12){
            //hideLoading();
            lvSpeechSearchDetail.setEnabled(true);//可点击
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    /*//搜索
    private void requestSearch(final String searchStr) {
        //重置
        songListSearchPlay.clear();
        HttpManager.getInstance().addRequestMusic(new SearchRequest(new Callback<Search>() {
            @Override
            public void onReceive(Search search) {
                super.onReceive(search);
                String s = search.toString();
                String a = search.getSongList().get(0).getArtistname();
                if (null!=search){
                    if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                            DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                                    DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
                        EventBus.getDefault().post(new DataEvent(1050, ""));//在mainActivity里面创建登录界面
                        ToastUtil.showShort(JxscApp.getContext(),"请先登录...");
                    }else {
                        //默认播放第一首
                        Bundle bundle = new Bundle();
                        //重组search的内容
                        for (int i=0;i<search.getSongList().size();i++){
                            if (searchStr.equals(search.getSongList().get(i).getSongname())){
                                songSearchplay=search.getSongList().get(i);
                                songListSearchPlay.add(songSearchplay);
                                searchPlay=new Search(songListSearchPlay,search.getAlbumList(),search.getArtistList());
                                bundle.putSerializable("search", (Serializable) searchPlay);
                                bundle.putInt("searchPos", 0);
                                JxscApp.booSearchOrInternet = true;
                                JxscApp.booleanJudge = false;
                                //点击传递事件，在mainactivity里面创建播放界面
                                EventBus.getDefault().post(new DataEvent(3010, bundle));
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        }, "search", *//*"卢冠廷"*//*searchStr));
    }*/
}
