package com.jinxin.cloudmusic.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.adapter.mymusic.MyMusicCategoryAdapter;
import com.jinxin.cloudmusic.adapter.mymusic.SdcardMusicAdapter;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.base.BaseFragment;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.cmd.CmdBuilder;
import com.jinxin.cloudmusic.cmd.CmdType;
import com.jinxin.cloudmusic.cmd.OnlineCmdSenderLong;
import com.jinxin.cloudmusic.cmd.RemoteJsonResultInfo;
import com.jinxin.cloudmusic.cmd.Task;
import com.jinxin.cloudmusic.cmd.TaskListener;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ZJ on 2017/3/8 0008.
 * 我的音乐
 */
public class MyMusicFragment extends BaseFragment {
    @Bind(R.id.lv_dynamic_content)
    ListView lvDynamicContent;
    /*@Bind(R.id.rl_my_music_download)
    PercentRelativeLayout rlMyMusicDownload;
    @Bind(R.id.rl_my_music_collect)
    PercentRelativeLayout rlMyMusicCollect;
    @Bind(R.id.rl_my_music_recent_play)
    PercentRelativeLayout rlMyMusicRecentPlay;*/

    @Bind(R.id.lv_my_music_category)
    ListView lvMyMusicCategory;

    private List<String> dataMusicName;
    private SdcardMusicAdapter sdcardMusicAdapter;
    private MyMusicCategoryAdapter myMusicCategoryAdapter;
    private List<String> categoryData;
    private String localAddress;
    private PlayInterfaceFragment playInterfaceFragment;

    private String whid;

    private long time;

    @Override
    public View inflate(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fr_my_music, container, false);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    lvDynamicContent.setAdapter(sdcardMusicAdapter);
                    break;
                case 2:
                    lvDynamicContent.setAdapter(sdcardMusicAdapter);
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void initData() {
        categoryData = new ArrayList<>();
        categoryData.add("下载音乐");
        categoryData.add("收藏音乐");
        categoryData.add("最近播放");
        myMusicCategoryAdapter = new MyMusicCategoryAdapter(getActivity(), R.layout.item_my_music_left_category, categoryData);
        myMusicCategoryAdapter.select(0);
        //进入碎片初始化
        //等于0的时候查找本地sdcard里面的音乐
        //检测SD卡是否存在
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //File root = Environment.getExternalStorageDirectory();
            File root=new File("/mnt/sdcard/file");
            getSDFile(root);
        } else {
            Toast.makeText(getActivity(), "没有SD卡", Toast.LENGTH_LONG).show();
        }
        //添加音乐，适配数据到listview上面
        sdcardMusicAdapter = new SdcardMusicAdapter(getActivity(), R.layout.item_my_music_detail, dataMusicName);
        lvDynamicContent.setAdapter(sdcardMusicAdapter);

        lvMyMusicCategory.setAdapter(myMusicCategoryAdapter);
        //点击事件，点击后更改背景颜色，刷新适配器
        lvMyMusicCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myMusicCategoryAdapter.select(position);
                Log.e("----进入下载音乐","进入下载音乐");
                if (position == 0) {

                    //等于0的时候查找本地sdcard里面的音乐
                    //检测SD卡是否存在
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        //File root = Environment.getExternalStorageDirectory();
                        File root=new File("/mnt/sdcard/file");
                        getSDFile(root);
                    } else {
                        Toast.makeText(getActivity(), "没有SD卡", Toast.LENGTH_LONG).show();
                    }

                    //添加音乐，适配数据到listview上面
                    sdcardMusicAdapter = new SdcardMusicAdapter(getActivity(), R.layout.item_my_music_detail, dataMusicName);
                    lvDynamicContent.setAdapter(sdcardMusicAdapter);
                } else if (position == 1) {
                    sdcardMusicAdapter = new SdcardMusicAdapter(getActivity(), R.layout.item_my_music_detail, null);
                    handler.obtainMessage(1).sendToTarget();
                    Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();

                } else if (position == 2) {
                    sdcardMusicAdapter = new SdcardMusicAdapter(getActivity(), R.layout.item_my_music_detail, null);
                    handler.obtainMessage(2).sendToTarget();
                    Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //我的音乐下载列里面点击对面的listview的每个item，拿到相应的歌名拼成相应的本地地址传递到播放界面进行本地音乐的播放
        //mMediaPlayer.setDataSource("/sdcard/file/上海滩.mp3");
        lvDynamicContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //2秒内不能重复点击
                if (System.currentTimeMillis()-time<2000) {
                    return;
                }
                time = System.currentTimeMillis();

                /*if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                        DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                                DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
                    EventBus.getDefault().post(new DataEvent(1050, ""));//在mainActivity里面创建登录界面
                    ToastUtil.showShort(JxscApp.getContext(),"请先登录...");
                }else {*/
                    JxscApp.booleanJudge = true;
                    //得到返回数据时，发送播放指令
                    for (int a = 0; a < DBM.getDefaultOrm().query(PowerState.class).size(); a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localPlay");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }

                    localAddress = "/sdcard/file/" + dataMusicName.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("dataMusicName", (Serializable) dataMusicName);
                    bundle.putSerializable("title", dataMusicName.get(position));
                    bundle.putSerializable("localAddress", (Serializable) localAddress);
                    bundle.putInt("position", position);//传递本地音乐的坐标

                    //点击传递事件，在mainactivity里面创建播放界面
                    EventBus.getDefault().post(new DataEvent(3010, bundle));
                }
                /*if (getFragmentManager().findFragmentByTag("internet")!=null){//通过tag销毁另外一个碎片
                    getFragmentManager().findFragmentByTag("internet").onDestroy();
                }
                if (getFragmentManager().findFragmentByTag("search")!=null){//通过tag销毁另外一个碎片
                    getFragmentManager().findFragmentByTag("search").onDestroy();
                }
                if (getFragmentManager().findFragmentByTag("sdCard") != null) {//通过tag销毁另外一个碎片
                    getFragmentManager().findFragmentByTag("sdCard").onDestroy();
                }
                if (getFragmentManager().findFragmentByTag("AcountProduct") != null) {//通过tag销毁输入源碎片
                    getFragmentManager().findFragmentByTag("AcountProduct").onDestroy();
                }
                if (playInterfaceFragment == null) {
                    playInterfaceFragment = new PlayInterfaceFragment();
                } else {//主动释放引用

                    playInterfaceFragment.getDialog().dismiss();
                    playInterfaceFragment = null;
                }
                if (playInterfaceFragment==null) {
                    playInterfaceFragment = new PlayInterfaceFragment();
                }
                Log.e("getFragmentManager()", getFragmentManager().toString());
                playInterfaceFragment.setArguments(bundle);
                playInterfaceFragment.show(getActivity().getSupportFragmentManager(), "sdCard");*/
                //EventBus.getDefault().post(new DataEvent(1,localAddress));
            //}
        });

    }

    // 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来
    private void getSDFile(File root) {
        dataMusicName = new ArrayList<>();
        File files[] = root.listFiles();
        //为空的文件夹，不做任何动作
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory())//判断是否是文件夹
                {
                    getSDFile(f);

                } else {
                    if (f.getPath().endsWith(".mp3")) {
                        //拆分字符串
                        String[] name = f.getPath().split("/");
                        for (String a : name) {
                            if (a.endsWith(".mp3")) {
                                dataMusicName.add(a);
                            }
                        }
                        //音乐
                        Log.i("mp3", f.getPath());//输出音乐路径

                    } else if (f.getPath().endsWith(".jpg")) {
                        //jpg图片
                        Log.i("Img", f.getPath());//输出图片路径
                    } else if (f.getPath().endsWith(".txt")) {
                        //文本
                        Log.i("Txt", f.getPath());//t文本
                    }/*else if(){
                        //其他
                    }*/
                }

            }
        }
    }

    private TaskListener<Task> listener = new TaskListener<Task>() {
        @Override
        public void onFail(Task task, Object[] arg) {
            if (arg != null && arg.length > 0) {
                RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
                ToastUtil.showShort(JxscApp.getContext(), "指令发送成功！");
            } else {
                ToastUtil.showShort(JxscApp.getContext(), "操作失败！");
            }
            //isClickable = true;
        }

        @Override
        public void onSuccess(Task task, Object[] arg) {
            //isClickable = true;
            if (arg != null && arg.length > 0) {
                RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
                //L.d(null, resultObj.toString());

                // "0000":正常的返回   "-1":结果不需要做解析
                /*if (resultObj.validResultCode.equals("0000") && !"-1".equals(resultObj.validResultInfo)) {
                    ToastUtil.showShort(getActivity(), "组网成功");*/
                //handler.obtainMessage(0).sendToTarget();
                /*} else {
					handler.obtainMessage(1).sendToTarget();
				}*/
            }
        }
    };
}
