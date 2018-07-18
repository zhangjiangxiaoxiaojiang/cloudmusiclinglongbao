package com.jinxin.cloudmusic;

import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.beoneaid.api.IBeoneAidService;
import com.beoneaid.api.IBeoneAidServiceCallback;
import com.jinxin.cloudmusic.bean.AcountProduct;
import com.jinxin.cloudmusic.bean.MusicBean.Song;
import com.jinxin.cloudmusic.bean.User;
import com.jinxin.cloudmusic.db.SPM;
import com.jinxin.cloudmusic.event.SpeechContrlEvent;
import com.jinxin.cloudmusic.fragment.LoginFragment;
import com.jinxin.cloudmusic.fragment.MusicEqualizerFragment;
import com.jinxin.cloudmusic.fragment.MusicSettingFragment;
import com.jinxin.cloudmusic.fragment.PlayInterfaceFragment;
import com.jinxin.cloudmusic.fragment.SearchFragment;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.bean.GroupBean.Lyrics;
import com.jinxin.cloudmusic.bean.GroupBean.MainData;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
import com.jinxin.cloudmusic.bean.MusicBean.ResultList;
import com.jinxin.cloudmusic.bean.MusicBean.SingerInformation;
import com.jinxin.cloudmusic.bean.MusicBean.SongList;
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
import com.jinxin.cloudmusic.event.PosEvent;
import com.jinxin.cloudmusic.fragment.MusicListFragment;
import com.jinxin.cloudmusic.fragment.MusicSearchFragment;
import com.jinxin.cloudmusic.fragment.MyMusicFragment;
import com.jinxin.cloudmusic.fragment.SpeechSearchMusicFragment;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.SongerInfo.SongerInfoRequest;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.getlyrics.GetLyricsRequest;
import com.jinxin.cloudmusic.net.login.LoginRequest;
import com.jinxin.cloudmusic.net.musiclist.MusicDemoRequest;
import com.jinxin.cloudmusic.net.play.PlayRequest;
import com.jinxin.cloudmusic.net.player.PlayerRequest;
import com.jinxin.cloudmusic.net.power.PowerRequest;
import com.jinxin.cloudmusic.net.recommendlist.RecommendListRequest;
import com.jinxin.cloudmusic.net.search.SearchRequest;
import com.jinxin.cloudmusic.net.singersonglist.SingerSongListRequest;
import com.jinxin.cloudmusic.util.DownMP3ClickListener;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cloudmusic.util.ToastUtil;
import com.jinxin.cloudmusic.util.VerticalSeekBar;
import com.jinxin.cloudmusic.util.versionupdate.CheckVersionTask;
import com.jinxin.cloudmusic.util.versionupdate.IParse;
import com.jinxin.cloudmusic.util.versionupdate.VersionInfo;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    Dialog loadingDialog;
    @Bind(R.id.fl_fragment)
    FrameLayout flFragment;
    @Bind(R.id.btn_my_music)
    Button btnMyMusic;
    @Bind(R.id.btn_music_list)
    Button btnMusicList;
    @Bind(R.id.prl_info_search)
    PercentRelativeLayout prlInfoSearch;
    @Bind(R.id.tv_total_time)
    TextView tvToatlTime;
    @Bind(R.id.tv_current_time)
    TextView tvCurrentTime;
    @Bind(R.id.iv_current_music)
    ImageView ivMianCurrentMusic;
    @Bind(R.id.tv_current_name)
    TextView tvMainCurrentName;
    @Bind(R.id.sb_progress)
    SeekBar sbProgress;
    @Bind(R.id.btn_music_next)
    Button btnMusicNext;
    @Bind(R.id.btn_music_last)
    Button btnMisicLast;
    @Bind(R.id.btn_main_music_play)
    Button btnMusicPlay;
    @Bind(R.id.btn_main_music_pause)
    Button btnMusicPause;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.btn_music_collect)
    Button btnMusicCollect;
    @Bind(R.id.btn_music_download)
    Button btnMusicDownLoad;
    @Bind(R.id.rl_music_volume_bar)//音量条的整个布局
            RelativeLayout rlMusicVolumeBar;
    @Bind(R.id.vs_volume_seekbar)
    VerticalSeekBar vsVolumeSeekbar;
    @Bind(R.id.btn_music_voice)
    Button btnMusicVoice;

    private SearchFragment searchFragment;
    BillboardList billboardData;
    List<BillboardList> billboardListData;
    private MusicListFragment musicListFragment;
    private MyMusicFragment myMusicFragment;
    private MusicSearchFragment musicSearchFragment;
    private PlayInterfaceFragment playInterfaceFragment;
    private SpeechSearchMusicFragment speechSearchMusicFragment;
    private MusicEqualizerFragment musicEqualizerFragment;//均衡器
    private int sum;
    private MainData mainData;
    private boolean playState;
    private static final int MUSIC_LIST = 0;//最新
    private Play play;
    private boolean voiceBoolean = false;
    int[] type = new int[]{1, 2, 21, 22, 24, 25};

    //音量拖动
    public AudioManager audioManager;
    private int maxVolume, currentVolume;

    private LoginFragment loginFragment;
    private MusicSettingFragment musicSettingFragment;

    private String macAddress;//获取本机的mac地址
    private List<String> dataMusicName;

    //播放器参数
    private List<PowerState> powerMac = new ArrayList<>();
    private boolean f = false;

    //错误处理
    private int errorRequestCount = 0;//错误请求

    private String whid;
    private Search searchPlay;
    private List<Song> songListSearchPlay = new ArrayList<>();
    private Song songSearchplay;

    private String mac2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MUSIC_LIST:
                    //向榜单碎片传递数据
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("music_list", (Serializable) msg.obj);
                    if (musicListFragment == null) {
                        musicListFragment = new MusicListFragment();
                        musicListFragment.setArguments(bundle);
                    }
                    addFragment(musicListFragment, "音乐榜单");
                    break;
                case 1:
                    play = ((MainData) msg.obj).getPlay();
                    int prass = ((MainData) msg.obj).getPrass();
                    sum = ((MainData) msg.obj).getSum();
                    playState = ((MainData) msg.obj).isPlayState();
                    btnMusicNext.setEnabled(true);
                    btnMisicLast.setEnabled(true);
                    btnMusicPlay.setEnabled(true);
                    btnMusicPause.setEnabled(true);
                    if (playState) {//播放状态（播放为true，暂停为false）
                        btnMusicPause.setVisibility(View.VISIBLE);
                        btnMusicPlay.setVisibility(View.INVISIBLE);
                    } else {
                        btnMusicPause.setVisibility(View.INVISIBLE);
                        btnMusicPlay.setVisibility(View.VISIBLE);
                    }
                    if (JxscApp.booleanJudge == false && play != null && !play.getSongInfo().getPic_small().isEmpty()) {//判断在线音乐或者本地音乐
                        Picasso.with(JxscApp.getContext()).load(play.getSongInfo().getPic_small()).placeholder(R.drawable.background).error(R.drawable.background).into(ivMianCurrentMusic);
                        tvMainCurrentName.setText(play.getSongInfo().getTitle());
                    } else if (JxscApp.booleanJudge == false && play != null && play.getSongInfo().getPic_small().isEmpty()) {
                        //没有图片就加载默认图片
                        Picasso.with(JxscApp.getContext()).load(R.drawable.background).placeholder(R.drawable.background).error(R.drawable.background).into(ivMianCurrentMusic);
                        tvMainCurrentName.setText(((MainData) msg.obj).getMusicName());
                    } else if (JxscApp.booleanJudge == true) {
                        //ivMianCurrentMusic.setImageBitmap(((MainData) msg.obj).getBm());
                        //Picasso.with(JxscApp.getContext()).load(((MainData) msg.obj).getBm()).placeholder(R.drawable.background).error(R.drawable.background).into(ivMianCurrentMusic);
                        tvMainCurrentName.setText(((MainData) msg.obj).getMusicName());

                    }
                    String formatTimeCurentTime = ((MainData) msg.obj).getFormatTimeCurentTime();
                    String formatTimeTotalTime = ((MainData) msg.obj).getFormatTimeTotalTime();
                    tvCurrentTime.setText(formatTimeCurentTime + "/");
                    tvToatlTime.setText(formatTimeTotalTime);
                    sbProgress.setProgress(prass);
                    break;
                /*case 2:
                    ivBack.setVisibility(View.VISIBLE);
                    break;*/
                case 3:
                    //ivBack.setVisibility(View.INVISIBLE);
                    btnMusicList.setTextColor(JxscApp.getContext().getResources().getColor(R.color.white));
                    btnMyMusic.setTextColor(JxscApp.getContext().getResources().getColor(R.color.darkgrey));
                    break;
                case 4:
                    //ivBack.setVisibility(View.INVISIBLE);
                    btnMusicList.setTextColor(JxscApp.getContext().getResources().getColor(R.color.darkgrey));
                    btnMyMusic.setTextColor(JxscApp.getContext().getResources().getColor(R.color.white));
                    break;
                case 5:
                    if (msg.obj != null) {
                        Picasso.with(JxscApp.getContext()).load(new File((String) msg.obj)).placeholder(R.drawable.background).error(R.drawable.background).into(ivMianCurrentMusic);
                    } else {//加载默认图片
                        ivMianCurrentMusic.setBackground(getResources().getDrawable(R.drawable.background));
                    }
                    break;
                case 6:
                    Picasso.with(JxscApp.getContext()).load(R.drawable.background).placeholder(R.drawable.background).error(R.drawable.background).into(ivMianCurrentMusic);
                    break;
                case 7:
                    rlMusicVolumeBar.setVisibility(View.INVISIBLE);
                    break;
                case 8:
                    Double s = (Double) msg.obj;
                    String s1 = String.format("%.2f", s);
                    double d = Double.valueOf(s1);
                    vsVolumeSeekbar.setProgress((int) (maxVolume * d));
                    break;
                case 9://切换模式提醒
                    serviceSpeaking("主人，你想要听什么音乐！");//唤醒语音功能
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onRestart() {
        Log.e("---onRestartMain", "onRestart");
        if (null == iBeoneAidService) {
            final Intent aidlIntent = new Intent();
            aidlIntent.setAction("com.beoneaid.api.IBeoneAidService");
            aidlIntent.setPackage("com.jinxin.beoneaid");
            bindService(aidlIntent, serviceConnection, Service.BIND_AUTO_CREATE);
            enterMusicModel();
            //handler.obtainMessage(9).sendToTarget();
            /*serviceSpeaking("主人，你想要听什么音乐！");//唤醒语音功能*/
        }
        super.onRestart();
        //切换模式提醒
       // bmodel = false;
        //enterMusicModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("----onstart", "onStart");
        //切换模式提醒
        //bmodel=false;
        //enterMusicModel();
    }


    //切换到音乐模式提醒
    /*private boolean bmodel = false;
    Thread thread*/;

    private void enterMusicModel() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                serviceSpeaking("主人，你想要听什么音乐！");
            }
        },2000);
        /*thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (bmodel == false) {
                            thread.sleep(3000);//一秒钟检测一次
                            handler.obtainMessage(9).sendToTarget();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        if (bmodel == false)
            thread.start();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("---onpauseMain", "-----暂停");
    }

    @Override
    protected void onStop() {
        //在主页执行了返回的操作时，让meidiaplayer处在暂停播放的状态
        JxscApp.bChangeOther = true;
        EventBus.getDefault().post(new SpeechContrlEvent(1004, "暂停"));
        //语音控制部分
        try {
            if (iBeoneAidService != null && iBeoneAidServiceCallback != null) {
                iBeoneAidService.unregisterCallback(iBeoneAidServiceCallback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
        iBeoneAidService = null;
//        serviceConnection = null;
        super.onStop();
        Log.e("----stop碎片的个数", getFragmentManager().getBackStackEntryCount() + "");
        Log.e("----stopMain", "停止");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        //mac登录
        String mac1 = setEthernetMac().replaceAll(":", "");
        mac2 = "0000" + mac1.toUpperCase();
        //测试
        //mac2="1111";
        if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                        DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
            getKey(mac2);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ac = SPM.getStr("acse", "account", "0000");
                    String se = SPM.getStr("acse", "secretKey", "0000");
                    if (!getVersion().equals("")) {
                        checkVersionUpdate(ac, se, getVersion());
                    } else {
                        Log.e("----版本号", "当前版本号为空！");
                    }
                }
            }).start();

        }


        if (null == iBeoneAidService) {
            final Intent aidlIntent = new Intent();
            aidlIntent.setAction("com.beoneaid.api.IBeoneAidService");
            aidlIntent.setPackage("com.jinxin.beoneaid");
            bindService(aidlIntent, serviceConnection, Service.BIND_AUTO_CREATE);
            /*handler.obtainMessage(9).sendToTarget();*/
            enterMusicModel();
        }
        //切换模式提醒
        //bmodel=false;
        //enterMusicModel();

        sbProgress.setMax(100);//初始化最大值
        //请求榜单列表
        /*
    method	方法	billboard
    type	列表类型	1-新歌榜,2-热歌榜,11-摇滚榜,12-爵士,16-流行,21-欧美金曲榜,22-经典老歌榜,23-情歌对唱榜,24-影视金曲榜,25-网络歌曲榜
    size	歌曲数量	可不穿，默认10条
    offset	偏移量	默认为0
    */

        if (billboardListData == null) {
            billboardListData = new ArrayList<>();
        } else {
            billboardListData.clear();
        }
        for (int a : type) {
            requestMusicList("billboard", a, 3, 0);
        }
        //音量
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        vsVolumeSeekbar.setMax(maxVolume);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        vsVolumeSeekbar.setProgress(currentVolume);


        //getLocalMacAddress();//获取本机的mac地址

        //点击切换界面
        btnMyMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在主页面点击返回键，跳转到另外一个apk
                JxscApp.mainBack = false;
                JxscApp.bMusicListDetail = 0;
                handler.obtainMessage(4).sendToTarget();
                if (myMusicFragment == null) {
                    myMusicFragment = new MyMusicFragment();
                }
                //btnMyMusic.setTextColor(getResources().getColor(R.color.white));
                addFragment(myMusicFragment, "我的音乐");
            }
        });
        btnMusicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在主页面点击返回键，跳转到另外一个apk
                JxscApp.mainBack = false;
                JxscApp.bMusicListDetail = 0;
                handler.obtainMessage(3).sendToTarget();
                if (musicListFragment == null) {
                    musicListFragment = new MusicListFragment();
                }
                addFragment(musicListFragment, "各类音乐榜单");
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handler.obtainMessage(3).sendToTarget();
                //true:二级页面返回到一级页面 false:云音乐activity跳转到设备控制activity
                if (JxscApp.mainBack == false) {//从云音乐apk跳转到触摸屏apk
                    //onBackPressed();
                    try {
                        Intent mIntent = new Intent();
                        ComponentName comp = new ComponentName("com.jinxin.jxtouch", "com.jinxin.jxtouchscreen.MainActivity");
                        mIntent.setComponent(comp);
                        mIntent.setAction("android.intent.action.MAIN1");
                        startActivity(mIntent);
                    } catch (Exception e) {
                        Log.e("---e", e + "");
                        ToastUtil.showShort(JxscApp.getContext(), "没有找到触摸屏相关apk！");
                    }
                    //startActivity(new Intent(CloudMusicActivity.this, MainActivity.class));
                } else {
                    if (musicListFragment == null) {
                        musicListFragment = new MusicListFragment();
                    }
                    addFragment(musicListFragment, "各类音乐榜单");
                    JxscApp.mainBack = false;
                    JxscApp.bMusicListDetail = 0;
                }
            }
        });

        //搜索
        prlInfoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (musicSearchFragment==null){
                    musicSearchFragment=new MusicSearchFragment();
                }
                addSearchFragment(musicListFragment,"搜索主碎片");*/
                if (searchFragment == null) {
                    searchFragment = new SearchFragment();
                } else if (searchFragment != null && searchFragment.isHidden()) {
                    getSupportFragmentManager().beginTransaction().show(searchFragment).commit();
                }
                addSearchFragment(searchFragment, "搜索主碎片");
            }
        });

        ivMianCurrentMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PosEvent(10));
            }
        });

        //通过主页控制音乐上下首和暂停、播放
        btnMisicLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMusicPause.setVisibility(View.INVISIBLE);
                btnMusicPlay.setVisibility(View.VISIBLE);
                btnMusicNext.setEnabled(false);
                btnMisicLast.setEnabled(false);
                btnMusicPlay.setEnabled(false);
                btnMusicPause.setEnabled(false);
                EventBus.getDefault().post(new PosEvent(11));
            }
        });
        btnMusicNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMusicPause.setVisibility(View.INVISIBLE);
                btnMusicPlay.setVisibility(View.VISIBLE);
                btnMusicNext.setEnabled(false);
                btnMisicLast.setEnabled(false);
                btnMusicPlay.setEnabled(false);
                btnMusicPause.setEnabled(false);
                EventBus.getDefault().post(new PosEvent(12));
            }
        });
        btnMusicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMusicPause.setVisibility(View.VISIBLE);
                btnMusicPlay.setVisibility(View.INVISIBLE);
                EventBus.getDefault().post(new PosEvent(13));
            }
        });
        btnMusicPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMusicPause.setVisibility(View.INVISIBLE);
                btnMusicPlay.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new PosEvent(14));
            }
        });
        btnMusicCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试bugly
                //CrashReport.testJavaCrash();
                //startService(new Intent(JxscApp.getContext(), UpdateDataService.class));
                Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
            }
        });
        btnMusicVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (voiceBoolean == false) {
                    rlMusicVolumeBar.setVisibility(View.VISIBLE);
                } else {
                    rlMusicVolumeBar.setVisibility(View.INVISIBLE);
                }
                voiceBoolean = !voiceBoolean;
            }
        });
        vsVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//获取音量值
                int MusicMax1 = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                seekBar.setProgress(currentVolume);
                Log.e("-----1", "" + "onProgressChanged");

                //调低时发送指令
                if (play != null) {//在线音乐
                    for (int a = 0; a < DBM.getDefaultOrm().query(PowerState.class).size(); a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), currentVolume + "/" + MusicMax1 + ":volume");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(JxscApp.getContext(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                } else {//本地音乐
                    EventBus.getDefault().post(new DataEvent(16, currentVolume + ":" + MusicMax1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("-----1", "" + "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("-----1", "" + "onStopTrackingTouch");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            handler.obtainMessage(7).sendToTarget();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                int a = (int) ((sum / 100.0) * (seekBar.getProgress()));
                EventBus.getDefault().post(new DataEvent(2001, a));
            }
        });


        //requestMusicList("billboard", 1, 10, 0);


        //requestSearch();//搜索
        //requestPlay();//播放
        //requestLyrics();//获取歌词
        //requestSongerInfo();//获取歌手信息
        //requestSongerMusicList();//获取歌手歌曲列表
        //requestRecommendList();//获取推荐列表

        /*if (loginFragment==null){
            loginFragment=new LoginFragment();
            loginFragment.show(getSupportFragmentManager(),"登录界面");
        }*/

        //接收触摸屏apk传递过来的数据
        Intent intent = getIntent();
        String value = intent.getStringExtra("arge1");
        if (value != null && !value.equals("")) {
            Log.e("---触摸屏传递过来的数据", value);
        } else {
            Log.e("---触摸屏传递过来的数据", "内容为空！");
        }

    }

    @Subscribe
    public void onEventMainThread(PosEvent event) {
        if (event.getPos() == 512) {
            serviceSpeaking("主人，没有搜索到相关的音乐！");
        }
    }

    @Subscribe
    public void onEventMainThread(DataEvent event) {
        if (event.getDatas() == 1) {//得到播放对象状态信息
            handler.obtainMessage(1, event.getObj()).sendToTarget();
            //下载当前正在播放的MP3（包括音乐和歌词）
            if (event.getObj() != null) {
                btnMusicDownLoad.setOnClickListener(new DownMP3ClickListener(((MainData) event.getObj()).getPlay()));
            }
        }
        if (event.getDatas() == 2) {
            handler.obtainMessage(5, event.getObj()).sendToTarget();
        }
        if (event.getDatas() == 3) {
            handler.obtainMessage(6).sendToTarget();
        }
        if (event.getDatas() == 1000) {
            //更新同步音量值
            handler.obtainMessage(8, event.getObj()).sendToTarget();
        }
        if (event.getDatas() == 2000) {//获取歌曲播放的时间总数
            //sum=event.getObj()
        }
        if (event.getDatas() == 1050) {//弹出音乐设置框
            //玲珑宝版本不要帐号登录，默认mac登录
            /*if (getFragmentManager().findFragmentByTag("loginFragment") != null) {//通过tag销毁输入源碎片
                getFragmentManager().findFragmentByTag("loginFragment").onDestroy();
            }
            if (loginFragment == null) {
                loginFragment = new LoginFragment();
            } else {
                try {
                    loginFragment.getDialog().dismiss();
                    loginFragment = null;
                } catch (Exception e) {
                    if (loginFragment == null) {
                        loginFragment = new LoginFragment();
                    }
                }
            }


            loginFragment.show(getSupportFragmentManager(), "loginFragment");*/

        }
        if (event.getDatas() == 1101) {//发送服务，建立ssetion，更新数据
            //startService(new Intent(JxscApp.getContext(), UpdateDataService.class));
        }
        if (event.getDatas() == 5001) {//重新发送请求，更新榜单
            JxscApp.mainPage = false;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //一次请求出错就全部重新发送请求
                        Thread.sleep(5000);
                        //把适配器里面的数据置空
                        billboardData = null;
                        //billboardData = new BillboardList();
                        /*billboardListData = null;
                        billboardListData = new ArrayList<>();*/
                        billboardListData.clear();
                        JxscApp.mainCount = 0;
                        for (int a : type) {
                            //if (JxscApp.mainPage=false)
                            requestMusicList("billboard", a, 3, 0);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //把适配器里面的数据置空
            /*billboardData=null;
            billboardData=new BillboardList();
            billboardListData = null;
            billboardListData = new ArrayList<>();
            for (int a : type) {
                requestMusicList("billboard", a, 3, 0);
            }*/
        }
        if (event.getDatas() == 2020) {
            if (getFragmentManager().findFragmentByTag("AcountProduct") != null) {//通过tag销毁输入源碎片
                getFragmentManager().findFragmentByTag("AcountProduct").onDestroy();
            }
            if (musicSettingFragment == null) {
                musicSettingFragment = new MusicSettingFragment();
            } else {
                musicSettingFragment.getDialog().dismiss();
                musicSettingFragment = null;
            }

            if (musicSettingFragment == null) {
                musicSettingFragment = new MusicSettingFragment();
            }
            JxscApp.hideMusicSetting = false;
            musicSettingFragment.show(getSupportFragmentManager(), "AcountProduct");
            /*else {//传递一个eventbus过去让已经产生的碎片显示出来
                EventBus.getDefault().post(new DataEvent(3001,"musicsettingFragment"));
            }*/
        }
        if (event.getDatas() == 3010) {//（3010）在线，搜索和sdCard都在这里面创建PlayInterfaceFragment
            if (getFragmentManager().findFragmentByTag("AcountProduct") != null) {//通过tag销毁输入源碎片
                getFragmentManager().findFragmentByTag("AcountProduct").onDestroy();
            }
            if (getFragmentManager().findFragmentByTag("musicInterFace") != null) {//通过tag销毁另外一个碎片
                getFragmentManager().findFragmentByTag("musicInterFace").onDestroy();
            }
            if (playInterfaceFragment == null) {
                playInterfaceFragment = new PlayInterfaceFragment();
            } else {//主动释放引用
                try {
                    playInterfaceFragment.getDialog().dismiss();
                    playInterfaceFragment.onDestroy();
                    playInterfaceFragment = null;
                } catch (Exception e) {
                    playInterfaceFragment = null;
                    Log.e("---播放界面异常", e + "");
                }
            }
            //强制的销毁均衡器碎片
            if (musicEqualizerFragment != null) {
                musicEqualizerFragment.getDialog().dismiss();
                musicEqualizerFragment = null;
            }

            if (playInterfaceFragment == null) {
                playInterfaceFragment = new PlayInterfaceFragment();
            }
            Log.e("getFragmentManager()", getFragmentManager().toString());
            playInterfaceFragment.setArguments((Bundle) event.getObj());
            //JxscApp.hidePlayinterface=false;
            JxscApp.hidePlayinterface = 0;
            Log.e("----main", "----playInterface");
            playInterfaceFragment.show(getSupportFragmentManager(), "musicInterFace");
            //新加（日期：12_10）
            //EventBus.getDefault().post(new DataEvent(11002,(Bundle) event.getObj()));
        }
        if (event.getDatas() == 3050) {//让主页的返回键显示出来
            handler.obtainMessage(2).sendToTarget();
        }
        if (event.getDatas() == 10001) {//主动置语音搜索界面为空
            speechSearchMusicFragment = null;
        }
        if (event.getDatas() == 40000) {
            requestSearch((String) event.getObj());
        } else if (event.getDatas() == 40001) {//控制显示均衡器
            //云音乐均衡器
            if (musicEqualizerFragment == null) {
                musicEqualizerFragment = new MusicEqualizerFragment();
                if (playInterfaceFragment.mMediaPlayer != null)
                    musicEqualizerFragment.setmPlayer(playInterfaceFragment.mMediaPlayer);
                musicEqualizerFragment.show(getSupportFragmentManager(), "均衡器");
            } else {
                //musicEqualizerFragment.setupVisualizer();
                EventBus.getDefault().post(new PosEvent(123456));//均衡器控制
            }
        } else if (event.getDatas() == 40002) {//均衡器重置
            //重置均衡器
            if (musicEqualizerFragment != null) {
                Log.e("----equalizer重置", "开始");
                musicEqualizerFragment.setmPlayer(playInterfaceFragment.mMediaPlayer);
                musicEqualizerFragment.setupVisualizer();
            }
        }
    }

    private void requestRecommendList() {
        HttpManager.getInstance().addRequestMusic(new RecommendListRequest(new Callback<ResultList>() {
            @Override
            public void onReceive(List<ResultList> t) {
                super.onReceive(t);
                String title = t.get(0).getTitle();
            }

            @Override
            public void onError(String error) {

            }
        }, "recommand", "290008", 10));
    }

    private void requestSongerMusicList() {
        HttpManager.getInstance().addRequestMusic(new SingerSongListRequest(new Callback<SongList>() {
            @Override
            public void onReceive(List<SongList> t) {
                super.onReceive(t);
                String title = t.get(0).getTitle();
            }

            @Override
            public void onError(String error) {

            }
        }, "songlist", "1100", 6));
    }

    private void requestSongerInfo() {
        HttpManager.getInstance().addRequestMusic(new SongerInfoRequest(new Callback<SingerInformation>() {
            @Override
            public void onReceive(SingerInformation singerInformation) {
                super.onReceive(singerInformation);
                String intro = singerInformation.getIntro();
            }

            @Override
            public void onError(String error) {

            }
        }, "artist", "1100"));
    }

    private void requestLyrics() {
        HttpManager.getInstance().addRequestMusic(new GetLyricsRequest(new Callback<Lyrics>() {
            @Override
            public void onReceive(Lyrics lyrics) {
                super.onReceive(lyrics);
                String title = lyrics.getTitle();
            }

            @Override
            public void onError(String error) {

            }
        }, "lry", "877578"));
    }

    private void requestPlay() {
        HttpManager.getInstance().addRequestMusic(new PlayRequest(new Callback<Play>() {
            @Override
            public void onReceive(Play play) {
                super.onReceive(play);
                String name = play.getSongInfo().getAuthor();
            }

            @Override
            public void onError(String error) {

            }
        }, "play", "523150864"));
    }

    //搜索
    private void requestSearch(final String searchStr) {
        //重置
        songListSearchPlay.clear();
        HttpManager.getInstance().addRequestMusic(new SearchRequest(new Callback<Search>() {
            @Override
            public void onReceive(Search search) {
                super.onReceive(search);
                String s = search.toString();
                String a = search.getSongList().get(0).getArtistname();
                if (null != search) {
                    /*if (!DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") ||
                            DBM.getDefaultOrm().getTableManager().isSQLTableCreated("powerState") &&
                                    DBM.getDefaultOrm().query(PowerState.class).size() == 0) {//查询创建了表但是里面的内容为空
                        EventBus.getDefault().post(new DataEvent(1050, ""));//在mainActivity里面创建登录界面
                        ToastUtil.showShort(JxscApp.getContext(),"请先登录...");
                    }else {*/
                    //默认播放第一首
                    Bundle bundle = new Bundle();
                    //重组search的内容
                    for (int i = 0; i < search.getSongList().size(); i++) {
                        if (searchStr.equals(search.getSongList().get(i).getSongname())) {
                            songSearchplay = search.getSongList().get(i);
                            songListSearchPlay.add(songSearchplay);
                            searchPlay = new Search(songListSearchPlay, search.getAlbumList(), search.getArtistList());
                            bundle.putSerializable("search", (Serializable) searchPlay);
                            bundle.putInt("searchPos", 0);
                            JxscApp.booSearchOrInternet = true;
                            JxscApp.booleanJudge = false;
                            //点击传递事件，在mainactivity里面创建播放界面
                            EventBus.getDefault().post(new DataEvent(3010, bundle));
                            break;
                        }
                    }
                    //}
                }/*else {
                    serviceSpeaking("主人，没有搜索到相关的音乐");
                }*/
            }

            @Override
            public void onError(String error) {
                Log.e("---error", "error" + "");
            }
        }, "search", /*"卢冠廷"*/searchStr));
    }

    //请求榜单列表
    private void requestMusicList(String method, int type, int size, int offset) {
        //判断加载的次数
        //JxscApp.mainPage=false;//true:要开始加载标志 false：加载完成标志
        JxscApp.mainCount = JxscApp.mainCount + 1;
        HttpManager.getInstance().addRequestMusic(new MusicDemoRequest(new Callback<BillboardList>() {
            @Override
            public void onReceive(BillboardList billboardList) {
                super.onReceive(billboardList);
                errorRequestCount = errorRequestCount + 1;
                //重新加载主页数据
                if (JxscApp.mainPage == true && errorRequestCount == 6) {
                    EventBus.getDefault().post(new DataEvent(5001, "request"));
                    //初始化判断条件
                    JxscApp.mainPage = false;
                    JxscApp.mainCount = 0;
                    errorRequestCount = 0;
                }

                String name = billboardList.getBillboard().getName();
                billboardData = billboardList;
                billboardListData.add(billboardData);
                Log.e("------1111", billboardData.toString());
                //EventBus.getDefault().post(new DataEvent(0));
                if (billboardListData.size() == 6 && JxscApp.mainPage == false && JxscApp.mainCount == 6) {
                    handler.obtainMessage(MUSIC_LIST, billboardListData).sendToTarget();
                    JxscApp.mainCount = 0;
                }
            }

            @Override
            public void onError(String error) {
                errorRequestCount = errorRequestCount + 1;
                ToastUtil.showShort(JxscApp.getContext(), "连接超时！请检查网络");
                //重新加载主页数据
                JxscApp.mainPage = true;
                //重新加载主页数据
                if (JxscApp.mainPage == true && errorRequestCount == 6) {
                    EventBus.getDefault().post(new DataEvent(5001, "request"));
                    //重置条件
                    JxscApp.mainPage = false;
                    JxscApp.mainCount = 0;
                    errorRequestCount = 0;
                }

            }
        }, method, type, size, offset));
    }


    /**
     * 添加Fragment
     */
    private void addFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, fragment).commit();
    }

    /**
     * 添加Fragment
     */
    private void addSearchFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.prl_main, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    /*public void showLoading(String msg){
        loadingDialog = LoadingUtil.createLoadingDialog(this, msg);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    public void hideLoading(){
        if(loadingDialog!=null)
            loadingDialog.dismiss();
        loadingDialog = null;
    }*/

    //获取本机的mac
    public String getLocalMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.e("----mac地址", wifiInfo.getMacAddress());
        return wifiInfo.getMacAddress();
    }

    //发送指令后的监听器
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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

    @Override
    protected void onResume() {
        super.onResume();
        //切换模式提醒
        JxscApp.bChangeOther = false;
        //EventBus.getDefault().post(new SpeechContrlEvent(1005, "播放"));
        Log.e("---onResumeMain", "mainonResume");
        if (/*JxscApp.hidePlayinterface==true*/JxscApp.hidePlayinterface == 1)
            EventBus.getDefault().post(new SpeechContrlEvent(5001, "隐藏播放界面"));
        if (JxscApp.hideMusicSetting == true)
            EventBus.getDefault().post(new DataEvent(1151, "隐藏设置碎片"));
        if (JxscApp.bEqualizer == false) {
            EventBus.getDefault().post(new PosEvent(123456));//隐藏均衡器界面
        } else if (JxscApp.bEqualizer == true) {
            EventBus.getDefault().post(new PosEvent(123457));//隐藏均衡器界面
        }
    }

    //取消android系统的返回键功能
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
    //返回键推出程序但是不销毁
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    //返回键推出程序但是不销毁
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            Log.e("---main退出到桌面", "退到桌面");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //语音控制模块-----------------------------------------------------------------------------------------------
    private IBeoneAidService iBeoneAidService;
    private IBeoneAidServiceCallback iBeoneAidServiceCallback = new IBeoneAidServiceCallback.Stub() {
        @Override
        public void recognizeResultCallback(final String s) throws RemoteException {
            //触发点
            //praseOrderByModeMusic(s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("-----收到aidl的消息", s);
                    praseOrderByModeMusic(s);
                }
            });
        }
    };

    //启动一个唤醒的功能
    private void serviceSpeaking(String text) {
        if (iBeoneAidService != null) {
            try {
                iBeoneAidService.startSpeakingWithoutRecognize(text);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("TAG", "serviceSpeaking: service is null");
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iBeoneAidService = IBeoneAidService.Stub.asInterface(iBinder);
            try {
                iBeoneAidService.registerCallback(iBeoneAidServiceCallback);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "onServiceConnected: wrong");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iBeoneAidService = null;
            Log.e("TAG", "onServiceDisconnected: 服务断开了");
            ToastUtil.showShort(getApplicationContext(), "onServiceDisconnected: 服务断开了");

        }
    };

    private void praseOrderByModeMusic(String str) {
        Log.e("+++++拿到str", str.toString());
        //判空处理
        if (str.equals("") || str == null) {
            serviceSpeaking("指令为空！请重新操作");
            return;
        } else {
            //在关键字包括搜索的情况下,从关键字搜索开始来分割字符串
            if (str.contains("搜索")) {
                String[] sourceStrArray = str.split("搜索");
            /*for (int i = 0; i < sourceStrArray.length; i++) {
                System.out.println(sourceStrArray[i]);
            }*/
                //获取搜索关键字,然后跳转到搜索界面执行相应的搜索操作
                if (sourceStrArray.length < 2) {
                    return;
                }
                String strKeyword = sourceStrArray[sourceStrArray.length - 1];
                if (getFragmentManager().findFragmentByTag("SpeechSearchMusic") != null) {//通过tag销毁输入源碎片
                    getFragmentManager().findFragmentByTag("SpeechSearchMusic").onDestroy();
                }
                if (speechSearchMusicFragment == null) {
                    speechSearchMusicFragment = new SpeechSearchMusicFragment();
                } else {
                    try {//强行置空
                        speechSearchMusicFragment.getDialog().dismiss();
                        speechSearchMusicFragment = null;
                    } catch (Exception e) {
                        speechSearchMusicFragment = null;
                    }
                }

                if (speechSearchMusicFragment == null) {
                    speechSearchMusicFragment = new SpeechSearchMusicFragment();
                }
                JxscApp.bSpeechSearchInterface = true;
                Bundle bundle = new Bundle();
                bundle.putString("strKeyword", strKeyword);
                speechSearchMusicFragment.setArguments(bundle);
                speechSearchMusicFragment.show(getSupportFragmentManager(), "SpeechSearchMusic");
            } else if (str.contains("播放") && !str.equals("播放") && !str.contains("回到播放")) {
                if (null != speechSearchMusicFragment) {
                    //这里播放分为两种方式：按第几首播放或者直接播放什么歌曲（歌曲名）
                    //目前是搜索歌名播放
                    String[] sourceStrArray = str.split("播放");
                    //获取播放关键字,然后跳转到播放界面
                    String strKeywordPlay = sourceStrArray[sourceStrArray.length - 1];
                    EventBus.getDefault().post(new SpeechContrlEvent(1001, strKeywordPlay));
                } else {
                    //新增一种情况（说了播放后，直接搜索歌单不显示，并且播放第一首）
                    String[] sourceStrArray = str.split("播放");
                    //获取搜索关键字,然后跳转到搜索界面执行相应的搜索操作
                    if (sourceStrArray.length < 2) {
                        return;
                    }
                    String strKeywordPlay = sourceStrArray[sourceStrArray.length - 1];
                    Log.e("-----播放模式", strKeywordPlay);
                    requestSearch(strKeywordPlay);
                }
            } else if (str.equals("上一首")) {
                EventBus.getDefault().post(new SpeechContrlEvent(1002, "上一首"));
            } else if (str.equals("下一首")) {
                EventBus.getDefault().post(new SpeechContrlEvent(1003, "下一首"));
            } else if (str.equals("暂停")) {
                EventBus.getDefault().post(new SpeechContrlEvent(1004, "暂停"));
            } else if (str.equals("播放")) {//对暂停的音乐进行再播放
                EventBus.getDefault().post(new SpeechContrlEvent(1005, "播放"));
            } else if (str.contains("音量调大") || str.contains("声音调大") ||
                    str.contains("音量调小") || str.contains("声音调小") ||
                    str.equals("增加音量") || str.equals("减小音量") || str.equals("增大音量")
                    || str.equals("减少音量") || str.equals("声音大点") || str.equals("声音小点")
                    || str.equals("音量大点") || str.equals("音量小点")) {//音量控制
                EventBus.getDefault().post(new SpeechContrlEvent(1007, str));
            } else if (str.equals("返回") || str.equals("后退")) {//返回控制
                EventBus.getDefault().post(new SpeechContrlEvent(1008, "返回"));
            } else if (str.trim().equals("回到播放界面")) {//回到播放界面
                Log.e("+++++str", str.toString());
                EventBus.getDefault().post(new PosEvent(10));
                Log.e("++++++main", "发送回到播放界面");
            } else if (str.equals("回到主页")) {//回到主页

            } else {
                serviceSpeaking("没有收到能处理的指令！");//唤醒语音功能
                return;
            }
        }
    }

    @Subscribe
    public void onEventMainThread(SpeechContrlEvent event) {
        if (event.getTag() == 1008) {//语音后退
            /*if (JxscApp.bSpeechSearchInterface==false&&JxscApp.hidePlayinterface!=0&&JxscApp.searchBack==false&&JxscApp.iMusicListDetail==1&&JxscApp.mainBack == true){
                if (musicListFragment == null) {
                    musicListFragment = new MusicListFragment();
                }
                addFragment(musicListFragment, "各类音乐榜单");
                JxscApp.mainBack = false;
                JxscApp.bMusicListDetail=0;
            }*/
        }
    }

    //玲珑宝版本，不需要登录帐号，主动根据mac获取对应的player（播放器）
    //进来根据mac获得key，再通过key拿到播放器
    private void getKey(final String mac) {
        HttpManager.getInstance().addRequestMacMusicPower(new PlayerRequest(new Callback<User>() {
            @Override
            public void onReceive(User user) {
                super.onReceive(user);
                final String account = user.getAccount();
                final String secretKey = user.getSecretKey();

                SPM.saveStr("acse", "account", account);
                SPM.saveStr("acse", "secretKey", secretKey);
                /*JxscApp.account = account;
                JxscApp.secretKey = secretKey;*/
                //String account = "C280010033";
                //String secretKey ="2C4AA353FAF2E1C51E5F97E379123BED";//以前33帐号对应的key
                powerRequest(account, secretKey, strSernum[0]);

                //检测版本更新
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!getVersion().equals("")) {
                            checkVersionUpdate(account, secretKey, getVersion());
                        } else {
                            Log.e("----版本号", "当前版本号为空！");
                        }
                    }
                }).start();
            }

            @Override
            public void onError(String error) {

            }
        }, mac));
    }

    /**
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            int ver = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //检测版本更新
    private void checkVersionUpdate(String account, String secretKey, String appVersion) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject.put("activityCode", "T901");
        jsonObject.put("bipCode", "B007");
        jsonObject.put("origDomain", "M000");
        jsonObject.put("homeDomain", "0000");

        jsonObject1.put("account", account);
        jsonObject1.put("secretKey", secretKey);
        jsonObject1.put("appVersion", appVersion);
        //jsonObject1.put("appType", "14");//云音乐普通版
        jsonObject1.put("appType", "21");//云音乐玲珑宝版本
        jsonObject.put("serviceContent", jsonObject1);
        CheckVersionTask.getInstance().setHttpUrlConnGet(MainActivity.this,
                new IParse() {
                    @Override
                    public VersionInfo parseData(String str) throws JSONException {
                        return null;
                    }
                }, NetConstant.VERSION_UPDATE, jsonObject);
    }

    //发送请求，当前功放
    private String[] strSernum = {"072", "308"};
    private int count = 0;//计数器

    private void powerRequest(final String account, final String secretKey, final String code) {
        HttpManager.getInstance().addRequestMusicPower(new PowerRequest(new Callback<AcountProduct>() {
            @Override
            public void onReceive(List<AcountProduct> t) {
                super.onReceive(t);
                count++;
                if (t.size() > 0 && t != null) {
                    String codeName = t.get(0).getCodeName();
                    String whId = t.get(0).getWhId();
                    String mac = t.get(0).getMac();
                    //保存whId到本地的数据库
                    for (int i = 0; i < t.size(); i++) {
                        DBM.getDefaultOrm().save(t.get(i));//保存输入源信息到数据库
                    }
                    //String whid=DBM.getDefaultOrm().query(new QueryBuilder<AcountProduct>(AcountProduct.class).where("whId=?", new String[]{t.get(0).getWhId()})).get(0).getWhId();
                    String whid = DBM.getDefaultOrm().query(AcountProduct.class).get(0).getWhId();

                    powerMac.clear();
                    for (int i = 0; i < t.size(); i++) {
                        //PowerState powerState = new PowerState(t.get(i).getWhId(),t.get(i).getCodeName(), f);
                        PowerState powerState = new PowerState(t.get(i).getWhId(), t.get(i).getMac(), t.get(i).getCodeName(), f);
                        powerMac.add(powerState);
                    }
                    //登录成功就在这里做保存数据到本地数据库的操作
                    for (int i = 0; i < powerMac.size(); i++) {
                        DBM.getDefaultOrm().save(powerMac.get(i));//保存输入源信息到数据库
                    }
                }
                if (count == 2) {
                    EventBus.getDefault().post(new DataEvent(2020, ""));//首次登录成功后，弹出输入源选择界面
                    count = 0;//重置
                } else if (count == 1) {//二次请求
                    powerRequest(account, secretKey, strSernum[1]);
                }
            }

            @Override
            public void onError(String error) {

            }
        }, account, secretKey, code));
    }

    //拿到mac地址
    public static String setEthernetMac() {
        BufferedReader reader = null;
        String ethernetMac = "";
        try {
            reader = new BufferedReader(new FileReader("sys/class/net/eth0/address"));
            ethernetMac = reader.readLine();
            Log.v("MACTAG", "ethernetMac: " + ethernetMac);
            if (ethernetMac == null || ethernetMac.trim().length() == 0) {
                Toast.makeText(JxscApp.getContext(), "以太网mac账号为空!", Toast.LENGTH_LONG).show();
            } else {
                ethernetMac = ethernetMac;

            }

        } catch (Exception e) {
            Log.i("MACTAG", "open sys/class/net/eth0/address failed : " + e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                Log.e("MACTAG", "close sys/class/net/eth0/address failed : " + e);
            }
        }
        return ethernetMac;
    }
}
