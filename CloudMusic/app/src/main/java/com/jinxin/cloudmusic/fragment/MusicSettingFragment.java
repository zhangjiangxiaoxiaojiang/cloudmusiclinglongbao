package com.jinxin.cloudmusic.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.musiclist.MusicPlayListAdapter;
import com.jinxin.cloudmusic.adapter.musicsetting.MusicSettingCategoryAdapter;
import com.jinxin.cloudmusic.adapter.musicsetting.MusicSettingDetailAdapter;
import com.jinxin.cloudmusic.adapter.mymusic.SdcardMusicAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.AcountProduct;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
import com.jinxin.cloudmusic.bean.MusicBean.SongList;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.MusicSettingEvent;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 音乐设置界面
 */
public class MusicSettingFragment extends DialogFragment {
    @Bind(R.id.lv_music_setting_category)
    ListView lvMusicSettingCategory;
    @Bind(R.id.lv_setting_content)
    ListView lvSettingContent;
    @Bind(R.id.iv_setting_music_close)
    ImageView ivSettingMusicClose;
    @Bind(R.id.btn_complete)
    Button btnComplete;

    private List<String> categoryData;
    private List<PowerState> dataMusicName = new ArrayList<>();
    private List<String> powerName;
    private MusicSettingCategoryAdapter musicSettingCategoryAdapter;
    private SdcardMusicAdapter sdcardMusicAdapter;
    private MusicSettingDetailAdapter musicSettingDetailAdapter;
    //新增播放列表适配器
    private MusicPlayListAdapter musicPlayListAdapter;
    private LoginFragment loginFragment;

    private boolean t = false;
    private int pos;
    private List<AcountProduct> acountProductList;
    private List<Object> objList;//播放列表

    //三种歌曲形式
    private BillboardList playBillboardData;//榜单在线
    private String playLocalAddress;//本地
    private Search playSearch;//搜索播放

    private int playPos;
    private int playSearchPos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.costom);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_setting, container, false);
        ButterKnife.bind(this, view);
        init();//初始化
        return view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    lvSettingContent.setAdapter(sdcardMusicAdapter);
                    break;
                case 2:
                    //遍历,点击同时只能选中一个
                    for (int i=0;i<dataMusicName.size();i++){
                        if (i==(int) msg.obj&&t==true){
                            dataMusicName.get((int) msg.obj).setPowerState(t);
                        }else {
                            dataMusicName.get(i).setPowerState(false);
                        }

                        DBM.getDefaultOrm().update(dataMusicName.get(i));//保存输入源信息到数据库
                    }

                    //dataMusicName.get((int) msg.obj).setPowerState(t);

                    musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, dataMusicName);
                    lvSettingContent.setAdapter(musicSettingDetailAdapter);
                    /*for (int i=0;i<dataMusicName.size();i++){
                        DBM.getDefaultOrm().update(dataMusicName.get(i));//保存输入源信息到数据库
                    }*/
                    if (t == false) {
                        //EventBus.getDefault().post(new DataEvent(3000, t));
                    }
                    break;
                case 3:

                    break;
                default:
                    break;

            }
        }
    };


    private void init() {
        //

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JxscApp.hideMusicSetting=true;
                getDialog().hide();

                //no one --all false
                //boolean t=false;
                /*dataMusicName= DBM.getDefaultOrm().query(PowerState.class);
                for (int i=0;i<dataMusicName.size();i++){
                    if (dataMusicName.get(i).isPowerState()){
                        t=true;
                        getDialog().hide();
                        break;
                    }
                }
                if (!t){
                    ToastUtil.showShort(JxscApp.getContext(), "请选择播放器！");
                }*/
            }
        });
        ivSettingMusicClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JxscApp.hideMusicSetting=true;
                getDialog().hide();
            }
        });
        categoryData = new ArrayList<>();
        categoryData.add("输入源");
        categoryData.add("功放");
        categoryData.add("扬声器");
        categoryData.add("查询状态");

        musicSettingCategoryAdapter = new MusicSettingCategoryAdapter(getActivity(), R.layout.item_my_music_left_category, categoryData);
        musicSettingCategoryAdapter.select(1);

        //初始化页面的时候先去本地数据库取输入源数据
        dataMusicName.clear();
        dataMusicName= DBM.getDefaultOrm().query(PowerState.class);

        //初始化，每项内容
        musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, dataMusicName);
        lvSettingContent.setAdapter(musicSettingDetailAdapter);

        lvMusicSettingCategory.setAdapter(musicSettingCategoryAdapter);
        //点击事件，点击后更改背景颜色，刷新适配器
        lvMusicSettingCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicSettingCategoryAdapter.select(position);
                Log.e("----进入下载音乐", "进入下载音乐");
                if (position == 0) {
                    //添加音乐，适配数据到listview上面
                    musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, null);
                    lvSettingContent.setAdapter(musicSettingDetailAdapter);
                    Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    if (dataMusicName != null && dataMusicName.size() > 0) {
                        musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, dataMusicName);
                        lvSettingContent.setAdapter(musicSettingDetailAdapter);

                        //存取状态到本地数据库里面
                        /*for (int i=0;i<dataMusicName.size();i++){
                            DBM.getDefaultOrm().update(dataMusicName.get(i));//保存歌词到数据库
                        }*/
                        boolean a = DBM.getDefaultOrm().query(new QueryBuilder<PowerState>(PowerState.class).where("powerName=?", new String[]{dataMusicName.get(0).getPowerName()})).get(0).isPowerState();
                        Log.e("-----a", a + "");
                    } else {

                        //EventBus.getDefault().post(new DataEvent(3000, t));
                    }


                } else if (position == 2) {
                    musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, null);
                    handler.obtainMessage(1).sendToTarget();
                    Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
                } else if (position == 3) {//查询状态（包括播放列表和当前正在播放的音乐）
                    //先判断是否当前播放的音乐(根据条件在三张表中去取)
                    if (JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == false){//榜单在线
                        //objList=List<Object>(playBillboardData.getLists());
                        /*musicPlayListAdapter=new MusicPlayListAdapter(getActivity(),R.layout.item_music_type_list_detail,playBillboardData.getLists());
                        lvSettingContent.setAdapter(musicPlayListAdapter);*/
                    }else if (JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == true){//搜索播放

                    }else if (JxscApp.booleanJudge == true){//本地

                    }


                    //handler.obtainMessage(3).sendToTarget();
                    musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, null);
                    handler.obtainMessage(1).sendToTarget();
                    Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //点击更换状态
        lvSettingContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //点击返回键，隐藏当前碎片
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    JxscApp.hideMusicSetting=true;
                    getDialog().hide();
                }
                return false;
            }
        });
    }

    //更新点击后的状态(点击置反)
    @Subscribe
    public void onEventMainThread(MusicSettingEvent event) {
        Log.e("----点击", t + "");
        pos = event.getPos();
        t = dataMusicName.get(pos).isPowerState();
        t = !t;
        handler.obtainMessage(2, pos).sendToTarget();


    }

    @Subscribe
    public void onEventMainThread(DataEvent event) {
        if (3001 == event.getDatas()) {
            getDialog().show();
        }if (event.getDatas() == 1051) {//拿到输入源设置界面
            acountProductList= (List<AcountProduct>) event.getObj();

            //刷新数据
            //遍历添加所有功放名称的list
            dataMusicName.clear();
            for (int i = 0; i < acountProductList.size(); i++) {
                PowerState powerState = new PowerState(acountProductList.get(i).getWhId(),acountProductList.get(i).getCodeName(), t);
                dataMusicName.add(powerState);
            }
            //初始化，每项内容
            musicSettingDetailAdapter = new MusicSettingDetailAdapter(getActivity(), R.layout.item_music_setting_detail, dataMusicName);
            lvSettingContent.setAdapter(musicSettingDetailAdapter);

            for (int i=0;i<dataMusicName.size();i++){
                DBM.getDefaultOrm().update(dataMusicName.get(i));//保存输入源信息到数据库
            }
            boolean a = DBM.getDefaultOrm().query(new QueryBuilder<PowerState>(PowerState.class).where("powerName=?", new String[]{dataMusicName.get(0).getPowerName()})).get(0).isPowerState();
            Log.e("-----a", a + "");

        }if (event.getDatas()==1151){
            getDialog().hide();//重新进入隐藏
        }if (event.getDatas()==11002){//先通过eventbus，不存数据库的方式来展示播放列表（后面优化）
            //传递过来三种数据
            //接收数据
            if (event.getObj() != null && JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == false) {
                //play = (Play) getArguments().getSerializable("play");
                playBillboardData = (BillboardList) ((Bundle)event.getObj()).getSerializable("billboardData");
                playPos = ((Bundle)event.getObj()).getInt("pos", 0);
                //requestPlay("play", billboardData.getLists().get(pos).getSong_id());
                //requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());

            } else if (event.getObj() != null && JxscApp.booleanJudge == true) {
                /*playLocalAddress = (String) getArguments().getSerializable("localAddress");
                dataMusicName = (List<String>) getArguments().getSerializable("dataMusicName");
                position = getArguments().getInt("position");
                title = getArguments().getString("title");*/
            } else if (event.getObj() != null && JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == true) {
                playSearch = (Search) getArguments().getSerializable("search");
                playSearchPos = getArguments().getInt("searchPos");
                //保持play不变
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
