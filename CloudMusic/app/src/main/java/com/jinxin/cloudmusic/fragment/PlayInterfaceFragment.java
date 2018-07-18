package com.jinxin.cloudmusic.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.bean.GroupBean.Lyrics;
import com.jinxin.cloudmusic.bean.GroupBean.MainData;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
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
import com.jinxin.cloudmusic.event.PositionEvent;
import com.jinxin.cloudmusic.event.SpeechContrlEvent;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.getlyrics.GetLyricsRequest;
import com.jinxin.cloudmusic.net.play.PlayRequest;
import com.jinxin.cloudmusic.util.LoadingUtil;
import com.jinxin.cloudmusic.util.ToastUtil;
import com.jinxin.cloudmusic.util.lrcview.LrcView;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ZJ on 2017/3/8 0008.
 * 播放界面
 */
public class PlayInterfaceFragment extends DialogFragment implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {
    @Bind(R.id.iv_current_music)
    ImageView ivCurrentMusic;
    @Bind(R.id.tv_play_music_name)
    TextView tvPlayMusicName;
    @Bind(R.id.tv_songer_name)
    TextView tvSongerName;
    @Bind(R.id.btn_music_interface_last)
    Button btnMusicInterfaceLast;
    @Bind(R.id.btn_music_interface_next)
    Button btnMusicInterfaceNext;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.btn_music_voice)
    Button btnMusicVoice;
    @Bind(R.id.btn_music_reduce)
    Button btnMusicReduce;
    @Bind(R.id.btn_music_add)
    Button btnMusicAdd;
    @Bind(R.id.btn_music_by_random)
    Button btnMusicByRandom;
    @Bind(R.id.btn_music_only_one)
    Button btnMusicOnlyOne;
    @Bind(R.id.btn_music_by_list)
    Button btnMusicByList;
    @Bind(R.id.lrc_view)
    LrcView lrcView;
    @Bind(R.id.btn_music_download)
    Button btnMusicDownload;
    @Bind(R.id.btn_music_collect)
    Button btnMusicCollect;
    @Bind(R.id.btn_music_similar_music)
    Button btnMusicSimilarMusic;
    @Bind(R.id.btn_music_list)
    Button btnMusicList;
    @Bind(R.id.btn_music_play)
    Button start;
    @Bind(R.id.btn_music_pause)
    Button stop;
    @Bind(R.id.sb_progress)
    SeekBar seek;
    @Bind(R.id.left)
    TextView left;
    @Bind(R.id.right)
    TextView right;

    private String TAG = "MediaPlayerDemo";
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private AssetManager mAssetManager = null;
    private AssetFileDescriptor afd = null; //assets文件下的文件的文件描述器

    private Boolean pause = false; //是否停止Handler消息的循环
    private int START = 1;//开始更新进度条
    private int PAUSE = 0;//展厅进度条
    private int tag = 1;

    private int sum = 0;//总时长 单位毫秒
    private int current;

    private Play play;
    private Play searchPlay;//搜索播放的音乐
    private Search search;
    private int pos;
    private int position;//本地音乐的坐标
    private int searchPos;//搜索音乐的列表
    private Uri uri;
    private BillboardList billboardData;
    private AudioManager mgr;
    private Lyrics lyr;
    int a;
    private String localAddress;
    private List<String> dataMusicName;
    private String title;
    private int prass;
    private String[] str = new String[2];
    private String localTitle;
    private Dialog loadingDialog;
    private MusicSettingFragment musicSettingFragment;

    //随机播放
    private boolean random = false;
    //单曲循环
    private boolean single = false;
    //播放状态（播放为true，暂停为false）
    private boolean playState = false;
    //是否下载歌词
    private boolean downLrc = false;

    private boolean speechStop=false;

    private MainData mainData;
    private Bitmap bm;

    private boolean voiceLoolean = false;
    private String whid;
    private String mac;

    private static AudioManager am;
    private long equalizerTime;

    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    lyricRun();
                    break;
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    //Toast.makeText(JxscApp.getContext(), "正在下载", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    btnMusicAdd.setVisibility(View.INVISIBLE);
                    btnMusicReduce.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    localMusicPlay(localAddress);//放入主线程中装载
                    break;
                case 2:
                    //重置视图
                    left.setText(FormatTime(0 / 1000));
                    seek.setProgress(0);
                    //在线获取更改图片
                    if (JxscApp.booleanJudge == false) {
                        if (play.getSongInfo().getPic_premium() != null && !play.getSongInfo().getPic_premium().isEmpty()) {
                            Picasso.with(JxscApp.getContext()).load(play.getSongInfo().getPic_premium()).placeholder(R.drawable.background).error(R.drawable.background).into(ivCurrentMusic);
                            //Glide.with(JxscApp.getContext()).load(play.getSongInfo().getPic_premium()).placeholder(R.drawable.background).error(R.drawable.background).into(ivCurrentMusic);
                        }
                        tvPlayMusicName.setText(play.getSongInfo().getTitle());
                        tvSongerName.setText(play.getSongInfo().getAuthor());
                    }
                    if (str != null && str.length > 0 && JxscApp.booleanJudge == true) {//播放本地音乐
                        tvPlayMusicName.setText(str[0]);
                    }
                    handler.sendEmptyMessage(START);//开始更新进度条
                    //handler.post(runnable);
                    sureUpdateLrc=false;//歌词控制
                    updateLrcview();
                    //播放时更改按钮图标
                    btnMusicInterfaceNext.setEnabled(true);
                    btnMusicInterfaceLast.setEnabled(true);
                    start.setEnabled(true);
                    stop.setEnabled(true);
                    start.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
                    try {
                        if (mMediaPlayer != null&&mMediaPlayer.isPlaying()) {
                            sum = mMediaPlayer.getDuration();
                            right.setText(FormatTime(sum / 1000));
                        }
                    } catch (Exception e) {

                    }
                    break;
                case 1: {
                    //播放音乐分为播放网络音乐(默认false播放在线音乐)和本地音乐(本地为true)
                    if (mMediaPlayer != null /*&& JxscApp.booleanJudge == false*/) {
                        try {
                            if (mMediaPlayer.isPlaying()) {
                                playState = true;
                                hideLoading();
                            } else {
                                playState = false;
                            }
                            EventBus.getDefault().post(new PositionEvent(12));
                            current = mMediaPlayer.getCurrentPosition();// 得到数值的单位是毫秒
                            prass = (int) (current / (sum * 1.0) * 100);
                            left.setText(FormatTime(current / 1000));
                            seek.setProgress(prass);
                        } catch (Exception e) {
                        }
                        if (JxscApp.booleanJudge == false /*&& mMediaPlayer.isPlaying()*/) {//在线音乐
                            mainData = new MainData(play, prass, playState, FormatTime(sum / 1000), FormatTime(current / 1000), sum);
                            EventBus.getDefault().post(new DataEvent(1, mainData));
                        }
                        if (JxscApp.booleanJudge == true /*&& mMediaPlayer.isPlaying()*/) {//本地音乐
                            //JxscApp.booleanJudge=true;
                            Bitmap bm = (Bitmap) msg.obj;
                            mainData = new MainData(str[0], bm, prass, playState, FormatTime(sum / 1000), FormatTime(current / 1000), sum);
                            EventBus.getDefault().post(new DataEvent(1, mainData));
                        }
                    }
                    if (!pause) {
                        handler.sendEmptyMessageDelayed(1, 1000);//1 秒后继续更新
                    }
                    break;
                }

                case 0: {
                    //停止更新
                    pause = true;
                    break;
                }
                default:
                    break;
            }
        }

    };


    //麦克风争夺问题
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.e("----onAudioFocusChange","争夺焦点");
            if (mMediaPlayer==null){
                return;
            }
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    if (mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                        start.setVisibility(View.VISIBLE);
                        stop.setVisibility(View.INVISIBLE);
                    }

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (mMediaPlayer == null) {
                    //initBeepSound();
                    Log.e("---需要重新初始化","mediaplayer为空");
                } else if (!mMediaPlayer.isPlaying()&&speechStop==false) {
                    Log.e("----onAudioFocusChange","争夺焦点+start");
                    mMediaPlayer.start();
                    start.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
                }
                // Resume playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    start.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.INVISIBLE);
                }
                am.abandonAudioFocus(afChangeListener);
                // Stop playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    start.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.INVISIBLE);
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    start.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.costom);
        //麦克风焦点问题
        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //mediaPlayer.start();
            Log.e("----音頻焦点","没有获取到音频焦点");
            return;
        }else {
            Log.e("----","争夺成功！");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_play_interface, container, false);
        ButterKnife.bind(this, view);
        init();//初始化
        initListener();//初始化监听器
        adjustVolume();//调节音量
        adjustPlayMode();//切换播放模式
        return view;
    }

    private void adjustPlayMode() {
        //初始化播放模式
        if (JxscApp.playMode==0){//循环播放
            btnMusicByList.setVisibility(View.VISIBLE);
            btnMusicByRandom.setVisibility(View.INVISIBLE);
            btnMusicOnlyOne.setVisibility(View.INVISIBLE);
        }else if(JxscApp.playMode==2){//单曲循环
            btnMusicByList.setVisibility(View.INVISIBLE);
            btnMusicByRandom.setVisibility(View.INVISIBLE);
            btnMusicOnlyOne.setVisibility(View.VISIBLE);
        }else if (JxscApp.playMode==1){//随机播放
            btnMusicByList.setVisibility(View.INVISIBLE);
            btnMusicByRandom.setVisibility(View.VISIBLE);
            btnMusicOnlyOne.setVisibility(View.INVISIBLE);
        }
        btnMusicByList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = 2;
                JxscApp.playMode=1;
                btnMusicByList.setVisibility(View.INVISIBLE);
                btnMusicByRandom.setVisibility(View.VISIBLE);
                btnMusicOnlyOne.setVisibility(View.INVISIBLE);
            }
        });
        btnMusicByRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = 3;
                JxscApp.playMode=2;
                btnMusicByList.setVisibility(View.INVISIBLE);
                btnMusicByRandom.setVisibility(View.INVISIBLE);
                btnMusicOnlyOne.setVisibility(View.VISIBLE);
            }
        });
        btnMusicOnlyOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = 1;
                JxscApp.playMode=0;
                btnMusicByList.setVisibility(View.VISIBLE);
                btnMusicByRandom.setVisibility(View.INVISIBLE);
                btnMusicOnlyOne.setVisibility(View.INVISIBLE);
            }
        });
        btnMusicCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
            }
        });
        btnMusicSimilarMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击传递消息，让其在mainactivity里面统一创建
                EventBus.getDefault().post(new DataEvent(2020, ""));
            }
        });
        btnMusicList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(JxscApp.getContext(), "该功能暂没开放", Toast.LENGTH_SHORT).show();
                //防止重复点击
                if (System.currentTimeMillis() - equalizerTime < 2000) {
                    return;
                }
                equalizerTime = System.currentTimeMillis();
                EventBus.getDefault().post(new DataEvent(40001,"控制均衡器"));
            }
        });
        //下载音乐
        btnMusicDownload.setOnClickListener(new DownMP3ClickListener());
    }

    private void adjustVolume() {
        btnMusicVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceLoolean = !voiceLoolean;
                if (voiceLoolean == false) {
                    btnMusicAdd.setVisibility(View.INVISIBLE);
                    btnMusicReduce.setVisibility(View.INVISIBLE);
                } else {
                    btnMusicAdd.setVisibility(View.VISIBLE);
                    btnMusicReduce.setVisibility(View.VISIBLE);
                }
            }
        });
        //定义AudioManager
        //实例化
        mgr = (AudioManager) JxscApp.getContext().getSystemService(Context.AUDIO_SERVICE);
        //加减声音
        btnMusicAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调高音量
                mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                //音乐音量
                int MusicMax1 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int MusicCurrent1 = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                Log.e("MusicMax1/MusicCurrent1", (double) MusicCurrent1 / MusicMax1 + "哇哈哈");
                Log.e("MusicMax1/MusicCurrent1", MusicCurrent1 + "/" + MusicMax1);
                EventBus.getDefault().post(new DataEvent(1000, (double) MusicCurrent1 / MusicMax1));

                //调高时发送指令
                if (play != null) {//针对在线
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), MusicCurrent1 + "/" + MusicMax1 + ":volume");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                } else {//针对本地
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), MusicCurrent1 + "/" + MusicMax1 + ":localVolume");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                }
            }
        });
        btnMusicReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调低音量
                mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                //音乐音量
                int MusicMax1 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int MusicCurrent1 = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                EventBus.getDefault().post(new DataEvent(1000, (double) MusicCurrent1 / MusicMax1));

                //调低时发送指令
                if (play != null) {
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), MusicCurrent1 + "/" + MusicMax1 + ":volume");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                } else {
                    //得到返回数据时，发送播放指令
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid,"localAddress", dataMusicName.get(position), MusicCurrent1 + "/" + MusicMax1 + ":localVolume");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                }
            }
        });
    }

    public void init() {
        //接收数据
        if (getArguments() != null && JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == false) {
            billboardData = (BillboardList) getArguments().getSerializable("billboardData");
            pos = getArguments().getInt("pos", 0);
            requestPlay("play", billboardData.getLists().get(pos).getSong_id());
            requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());
        } else if (getArguments() != null && JxscApp.booleanJudge == true) {
            //localAddress = (String) getArguments().getSerializable("localAddress");
            dataMusicName = (List<String>) getArguments().getSerializable("dataMusicName");
            position = getArguments().getInt("position");
            title = getArguments().getString("title");

            localTitle = dataMusicName.get(position);
            localAddress = "/sdcard/file/" + dataMusicName.get(position);
            resetMediaplayer(mMediaPlayer);
        } else if (getArguments() != null && JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == true) {
            search = (Search) getArguments().getSerializable("search");
            searchPos = getArguments().getInt("searchPos");
            requestPlay("play", search.getSongList().get(searchPos).getSongid());
            requestLyrics("lry", search.getSongList().get(searchPos).getSongid());
        }

        //干掉返回键（暂时这样处理）
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            long time = 0;
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    if (System.currentTimeMillis() - time > 2000) {
                        Toast.makeText(getActivity(), "双击退出", Toast.LENGTH_SHORT).show();
                        time = System.currentTimeMillis();
                        return true;
                    }
                    JxscApp.hidePlayinterface = 1;
                    hideLoading();
                    getDialog().hide();
                    Log.e("---playInterFace退出到桌面", "退到桌面");
                    return true;
                }
                return true;
            }
        });
    }

    //播放本地音乐，2秒重置时间
    private void resetMediaplayer(MediaPlayer mediaPlayer){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //提前重置音乐,播放本地音乐
                    try {
                        if (mMediaPlayer.isPlaying())
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    } catch (Exception e) {
                        mMediaPlayer = null;
                    }
                    Thread.sleep(1500);
                    handler.obtainMessage(3).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean sureUpdateLrc=true;//初始化状态 false:未加载改歌词 true:已加载该歌词
    private void requestLyrics(String method, String songid) {
        sureUpdateLrc=true;
        HttpManager.getInstance().addRequestMusic(new GetLyricsRequest(new Callback<Lyrics>() {
            @Override
            public void onReceive(Lyrics lyrics) {
                super.onReceive(lyrics);
                String tit = lyrics.getTitle();
                lyr = lyrics;
                if (downLrc == false) {
                    if (sureUpdateLrc==false&&lyr!=null){
                        lrcView.loadLrc(lyr.getLrcContent());
                        handler1.obtainMessage(1).sendToTarget();
                        //重置
                        sureUpdateLrc=true;
                    }
                } else {
                    if (DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{tit})).size() > 0) {
                        DBM.getDefaultOrm().update(lyr);
                    } else {
                        DBM.getDefaultOrm().save(lyr);//保存歌词到数据库
                        String a = DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{tit})).get(0).getLrcContent();
                        Log.e("-----a", a);
                    }
                }
                downLrc = false;//置换返回初始状态
            }

            @Override
            public void onError(String error) {
                hideLoading();
                ToastUtil.showShort(getActivity(),"网络加载失败，请重新加载！");
            }
        }, method, songid/*"lry", "877578"*/));
    }

    //歌词分为两种情况，1 先于歌曲请求出来了；2 后于歌曲请求出来了
    private void updateLrcview(){
        if (sureUpdateLrc==false&&lyr!=null){
            lrcView.loadLrc(lyr.getLrcContent());
            handler1.obtainMessage(1).sendToTarget();
            sureUpdateLrc=true;
        }
    }

    //private long time=0;
    //歌词
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (mMediaPlayer){
                try {
                    if (/*mMediaPlayer.isPlaying()*/true) {
                        long time = mMediaPlayer.getCurrentPosition();
                        //time+=100;
                        //lrcView.updateTime(time);
                    }
                } catch (Exception e) {
                    Log.e("----歌词绘制出错","---");
                }
                Log.e("----runnable", runnable.toString());
                //handler1.postDelayed(this, 1000);
            }

        }
    };

    private boolean lyricbo=true;
    Thread thread1;
    //歌词专用线程
    private void lyricRun(){
      thread1= new Thread(new Runnable() {
            @Override
            public void run() {
                JxscApp.lyricSum=-1;
                while (lyricbo){
                    try {
                        if (mMediaPlayer!=null){
                                long time = mMediaPlayer.getCurrentPosition();
                                lrcView.updateTime(time);
                                Thread.sleep(1000);
                            }
                    } catch (Exception e) {
                        Log.e("----歌词绘制出错","---");
                        break;
                    }
                }

            }
        });
        thread1.start();
    }

    public void initListener() {
        seek.setMax(100);//设置长度100
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                a = (int) ((sum / 100.0) * (seekBar.getProgress()));
                //拖动进度时发送指令,这里是在线音乐的拖动播放
                if (play != null) {
                    for (int c = 0; c < DBM.getDefaultOrm().query(PowerState.class).size(); c++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(c).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(c).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), a + ":currentTime");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                } else {//针对本地音乐
                    for (int c = 0; c < DBM.getDefaultOrm().query(PowerState.class).size(); c++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(c).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(c).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), a + ":localCurrentTime");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                }

                try {
                    if (mMediaPlayer != null /*&& mMediaPlayer.isPlaying()*/) {
                        mMediaPlayer.seekTo(a); //seekTo方法接收的单位是:毫秒
                        handler.sendEmptyMessage(START); //更新seekBar
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void media(MediaPlayer m) {
        m.setOnCompletionListener(this);//设置播放完成
        m.setOnErrorListener(this);
    }

    @OnClick({R.id.btn_music_play,R.id.btn_music_pause,R.id.btn_music_interface_last,R.id.btn_music_interface_next,R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_music_play: //开始播放
                speechStop=false;
                //针对在线音乐发送暂停后播放命令
                if (play != null) {
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "replay");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                } else {//针对本地音乐发送暂停后播放命令
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localRePlay");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                }
                mMediaPlayer.start();
                pause = false;//不暂停
                handler.sendEmptyMessage(START);//开始更新进度条
                //播放时更改按钮图标
                start.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_music_pause://暂停
                speechStop=true;
                if (mMediaPlayer.isPlaying()) {
                    //针对在线音乐发送播放中暂停命令
                    if (play != null) {
                        for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                            boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                            if (b == true) {
                                whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                                List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "stop");
                                OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                        NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                                onlineSender.addListener(listener);
                                onlineSender.send();
                            }
                        }
                    } else {//针对本地音乐
                        for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                            boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                            if (b == true) {
                                whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                                List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localStop");
                                OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                        NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                                onlineSender.addListener(listener);
                                onlineSender.send();
                            }
                        }
                    }

                    mMediaPlayer.pause();//停止播放
                    handler.sendEmptyMessage(PAUSE); //停止更新进度条

                    //播放时更改按钮图标
                    start.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btn_music_interface_last:
                playLastSong();
                //pause = true;
                break;
            case R.id.btn_music_interface_next:
                playNextSong();
                break;
            case R.id.iv_back:
                //hideLoading();
                //JxscApp.hidePlayinterface=true;
                JxscApp.hidePlayinterface=1;
                getDialog().hide();
                onPause();
                break;
            default:
                break;
        }
    }

    //播放出错处理
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("---onError", "播放错误");
        //重置
        JxscApp.lyricSum=-1;
        if (what==100&&mMediaPlayer!=null&&JxscApp.booleanJudge==false/*&&JxscApp.bChangeOther==false*/){//记录进度（在当前app播放出错）
            handler.removeMessages(1);
            mMediaPlayer.pause();
            //current = mMediaPlayer.getCurrentPosition();
            current= timeMillisecond(left.getText().toString());
            Log.e("---left","当前："+current+"总值："+sum);
            //播放出错处理
            Log.e("---play",play+"");
            if (play.getBitrate().getFile_link()!=null)
            uri = Uri.parse(play.getBitrate().getFile_link());
        /*else if (what==100&&mMediaPlayer!=null&&JxscApp.bChangeOther==true){//在其他app的页面时
            //重置
            seek.setProgress(0);
            lrcView.onDrag(0);
            left.setText(FormatTime(0 / 1000));
        }*/

            try {//播放完释放资源，主动置空
                if (mMediaPlayer!=null){
                    //mMediaPlayer.stop();
                    //mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            } catch (Exception e) {
                mMediaPlayer = null;
            }
            if (mMediaPlayer == null&&uri!=null) {
                try {
                //mMediaPlayer = MediaPlayer.create(JxscApp.getContext(), uri);
                mMediaPlayer = new MediaPlayer();

                    //设置播放源 ，当然还有其他的重载方法 eg：setDataSource(String path) path可以使网络路径也可以是本地路径，网络的记得加权限
                    mMediaPlayer.setDataSource(JxscApp.getContext(), uri);
                    mMediaPlayer.prepareAsync();//MediaPlayer 开始准备  异步的, 还有prepare()这个是同步的
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            media(mMediaPlayer);
                            if (JxscApp.bChangeOther == false) {
                                if (mMediaPlayer != null) {
                                    try {
                                        mMediaPlayer.seekTo(current);//设置成上次暂停的位置
                                        Log.e("---error", "当前：" + current);
                                        if (mMediaPlayer != null) {
                                            try {
                                                mMediaPlayer.start();
                                            } catch (Exception e) {

                                            }
                                        }
                                        pause = false;//不暂停
                                    } catch (Exception e) {
                                        Log.e("----onPrepared", "错误");
                                    }
                                }
                                //handler.obtainMessage(3).sendToTarget();
                                handler1.obtainMessage(1).sendToTarget();
                                handler.obtainMessage(2).sendToTarget();
                            }
                        }
                    });
                    Log.e("----onError", "播放错误当前进度：" + mMediaPlayer.getCurrentPosition() + "what:" + what + "extra:" + extra);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "设置播放源异常");
                }
            }else if (what==100&&mMediaPlayer!=null&&JxscApp.booleanJudge==true){
                //本地音乐播放出错，直接播放下一首
                playNextSong();
            }
            }
        return true;
    }

    //播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("---onCompletion", "播放完成");
        lyricbo=false;
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //left.setText(FormatTime(0 / 1000));
        //seek.setProgress(0);
        //lrcView.onDrag(0);
        //handler.removeCallbacks(runnable);
        if (mMediaPlayer != null) {//没辞都要先对播放器进行判空操作
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

        }
        //播放完成，自动播放下一首
        playNextSong();

    }

    //格式化时间

    /**
     * 局限性歌曲最长时长59:59
     *
     * @param s 单位是秒
     * @return
     */
    private String FormatTime(int s) {
        StringBuffer result = new StringBuffer();

        int a = s / 60;
        int b = s % 60;
        if (a < 10) {
            result.append("0");
        }
        result.append(String.valueOf(a));
        result.append(":");

        if (b < 10) {
            result.append("0");
        }
        result.append(String.valueOf(b));
        return result.toString();
    }

    //分秒转化成毫秒
    private int timeMillisecond(String strTime){
        int time=0;
        //使用“：”来区分
        String[] strArr=strTime.split(":");
        int a=Integer.parseInt(strArr[0]);
        int b=Integer.parseInt(strArr[1]);
        time=a*60000+b*1000;
        Log.e("----strTime", time + "");
        return time;
    }

    @Override
    public void onStart() {
        super.onStart();
        //目前的逻辑是首次创建该碎片才加载一次loading
        if (hideView==false)
        showLoading("请稍后...");
        hideView=true;
    }

    //发送播放请求
    private void requestPlay(String method, String songid) {
        //播放时更改按钮图标,false不可点击，true可点击
        start.setEnabled(false);
        stop.setEnabled(false);
        btnMusicInterfaceNext.setEnabled(false);
        btnMusicInterfaceLast.setEnabled(false);
        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.INVISIBLE);
        lyr=null;//歌词置为空
        Log.e("----发送请求数据", "正在加载");
        HttpManager.getInstance().addRequestMusic(new PlayRequest(new Callback<Play>() {
            @Override
            public void onReceive(Play play1) {
                super.onReceive(play1);
                Log.e("----收到请求数据", "正在加载");
                play = play1;
                //得到返回数据时，发送播放指令
                for (int a = 0; a < DBM.getDefaultOrm().query(PowerState.class).size(); a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "play");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
                uri = Uri.parse(play.getBitrate().getFile_link());
                if (mMediaPlayer != null) {
                    try {//播放完释放资源，主动置空
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    } catch (Exception e) {
                        mMediaPlayer = null;
                    }
                }
                if (mMediaPlayer == null) {
                    mMediaPlayer = MediaPlayer.create(JxscApp.getContext(), uri);
                    media(mMediaPlayer);
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.e("----请求到列表数据","正在加载");
                            //handler.obtainMessage(3).sendToTarget();
                            if (mMediaPlayer != null) {
                                try {
                                    mMediaPlayer.start();
                                } catch (Exception e) {

                                }
                            }
                            pause = false;//不暂停
                            handler.obtainMessage(2).sendToTarget();
                            EventBus.getDefault().post(new DataEvent(40002,"重置均衡器"));
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                start.setEnabled(true);
                stop.setEnabled(true);
                btnMusicInterfaceNext.setEnabled(true);
                btnMusicInterfaceLast.setEnabled(true);
                hideLoading();
                ToastUtil.showShort(JxscApp.getContext(), "网络加载失败，请重新加载！");
                EventBus.getDefault().post(new PositionEvent(12));
            }
        }, method, songid));
    }

    //发送本地播放音乐
    private void localMusicPlay(String localAddress) {
        //播放时更改按钮图标,false不可点击，true可点击
        start.setEnabled(false);
        stop.setEnabled(false);
        btnMusicInterfaceNext.setEnabled(false);
        btnMusicInterfaceLast.setEnabled(false);
        start.setVisibility(View.VISIBLE);
        stop.setVisibility(View.INVISIBLE);
        //得到返回数据时，发送播放指令
        for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
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

        //播放本地音乐
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            //设置播放源 ，当然还有其他的重载方法 eg：setDataSource(String path) path可以使网络路径也可以是本地路径，网络的记得加权限
            mMediaPlayer.setDataSource(localAddress);
            mMediaPlayer.prepareAsync();//MediaPlayer 开始准备  异步的, 还有prepare()这个是同步的
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "设置播放源异常");
        }
        if (str != null && str.length > 0 && localTitle != null) {
            str = localTitle.split("\\.");
            Log.e("----", str[0]);
        }
        if (DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{str[0]})).size() > 0 && str[0] != null) {
            String a = DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{str[0]})).get(0).getLrcContent();
            lrcView.loadLrc(DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{str[0]})).get(0).getLrcContent());
        }
        media(mMediaPlayer);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //handler.obtainMessage(3).sendToTarget();
                if (mMediaPlayer != null) {
                    try {
                        mMediaPlayer.start();
                    } catch (Exception e) {

                    }
                }
                pause = false;//不暂停
                handler1.obtainMessage(1).sendToTarget();
                handler.obtainMessage(2).sendToTarget();
                readLocalImg(str[0]);
                EventBus.getDefault().post(new DataEvent(40002,"重置均衡器"));
            }
        });

    }

    @Subscribe
    public void onEventMainThread(PosEvent event) {
        if (event.getPos() == 10) {
            //JxscApp.hidePlayinterface=false;
            JxscApp.hidePlayinterface=0;
            getDialog().show();//让当前dialogfragment显示出来
        } else if (event.getPos() == 11) {//上一首
            playLastSong();
        } else if (event.getPos() == 12) {//下一首
            playNextSong();
        } else if (event.getPos() == 13) {//播放
            //发送暂停后播放命令
            speechStop=false;
            if (play != null) {
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "replay");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            } else {//本地播放
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localRePlay");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            }

            mMediaPlayer.start();
            pause = false;//不暂停
            handler.sendEmptyMessage(START);//开始更新进度条

            //播放时更改按钮图标
            start.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
        } else if (event.getPos() == 14) {//暂停
            speechStop=true;
            //发送播放中暂停命令
            if (play != null) {
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "stop");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            } else {
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localStop");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            }

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();//停止播放
                handler.sendEmptyMessage(PAUSE); //停止更新进度条

                //播放时更改按钮图标
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.INVISIBLE);
            }
        } else if (event.getPos() == 15) {//拖动主页的seekbar，改变Mediaplayer的播放进度

        }
    }

    @Subscribe
    public void onEventMainThread(DataEvent event) {
        if (event.getDatas() == 16) {//在主页执行本地音乐调节音量
            String[] str = ((String) event.getObj()).split(":");
            for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                if (b == true) {
                    whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                    List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), str[0] + "/" + str[1] + ":localVolume");
                    OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                            NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                    onlineSender.addListener(listener);
                    onlineSender.send();
                }
            }
        } else if (event.getDatas() == 2001) {
            a = (int) event.getObj();
            //这是在线播放音乐的代码
            if (play != null) {
                for (int c=0;c<DBM.getDefaultOrm().query(PowerState.class).size();c++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(c).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(c).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), a + ":currentTime");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            } else {//针对本地音乐
                for (int c=0;c<DBM.getDefaultOrm().query(PowerState.class).size();c++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(c).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(c).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), a + ":localCurrentTime");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            }

            try {
                if (mMediaPlayer != null /*&& mMediaPlayer.isPlaying()*/) {
                    mMediaPlayer.seekTo(a); //seekTo方法接收的单位是:毫秒
                    handler.sendEmptyMessage(START); //更新seekBar
                }

            } catch (Exception e) {
                //mMediaPlayer = null;
            }
        }

    }

    @Override
    public void onDestroy() {
        Log.e("---onDestroy", "播放界面回收");
            try {
                    mMediaPlayer.stop();
                    //handler.sendEmptyMessage(PAUSE); //停止更新进度条
                    handler.removeMessages(1);
                    seek.setProgress(0);
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                    //handler.removeCallbacks(runnable);
            } catch (Exception e) {
                mMediaPlayer = null;
            }

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //下载MP3和相应歌曲图片到本地sdcard卡
    public class DownMP3ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(JxscApp.booleanJudge == true){
                ToastUtil.showShort(JxscApp.getContext(), "不能对本地音乐进行下载！");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShort(JxscApp.getContext(), "正在下载，请稍候");
                    //发送下载命令
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "downLoad");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }

                    //点击下载，设置downLrc的值为true，请求歌词，并且把请求到的歌词保存到数据库
                    downLrc = true;
                    //获取歌词
                    requestLyrics("lry", play.getSongInfo().getSong_id());
                    String urlStr = play.getBitrate().getFile_link();
                    String path = "file";
                    String fileName = play.getSongInfo().getTitle() + ".mp3";
                    OutputStream output = null;
                    try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                        URL url = new URL(urlStr);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //取得inputStream，并将流中的信息写入SDCard
                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                        String SDCard = Environment.getExternalStorageDirectory() + "";
                        String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

                        File file = new File(pathName);

                        InputStream input = conn.getInputStream();
                        if (file.exists()) {
                            System.out.println("exits");
                            return;
                        } else {
                            String dir = SDCard + "/" + path;
                            new File(dir).mkdir();//新建文件夹
                            file.createNewFile();//新建文件
                            output = new FileOutputStream(file);
                            //读取大文件
                            //byte[] buffer = new byte[64];
                            byte[] buffer = new byte[4*1024];
                            int len = input.read(buffer);
                            while (len != -1) {
                                output.write(buffer,0,len);
                                len = input.read(buffer);

                            }
                            output.flush();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (output != null) {
                                output.close();
                            }
                            handler.obtainMessage(5).sendToTarget();
                            Log.e("------", "mp3文件下载成功");
                            ToastUtil.showShort(JxscApp.getContext(), "mp3文件下载成功");
                            System.out.println("success");
                        } catch (IOException e) {
                            System.out.println("fail");
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //点击下载，设置downLrc的值为true，请求歌词，并且把请求到的歌词保存到数据库
                    downLrc = true;
                    //获取歌词
                    requestLyrics("lry", play.getSongInfo().getSong_id());
                    //String urlStr="http://yinyueshiting.baidu.com/data2/music/773be41d219da78a36c1ace9efc712ea/523150878/523150878.mp3?xcode=844a54681ec7c4e438b319dd5c3c046e";
                    String urlImgStr = play.getSongInfo().getPic_premium();
                    String path = "file";
                    String fileName = play.getSongInfo().getTitle() + ".jpg";
                    OutputStream output = null;
                    try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                        URL urlImg = new URL(urlImgStr);
                        HttpURLConnection connImg = (HttpURLConnection) urlImg.openConnection();
                        //取得inputStream，并将流中的信息写入SDCard
                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                        String SDCard = Environment.getExternalStorageDirectory() + "";
                        String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

                        File file = new File(pathName);
                        InputStream inputImg = connImg.getInputStream();
                        if (file.exists()) {
                            System.out.println("exits");
                            return;
                        } else {
                            String dir = SDCard + "/" + path;
                            new File(dir).mkdir();//新建文件夹
                            file.createNewFile();//新建文件
                            output = new FileOutputStream(file);
                            //读取大文件
                            byte[] buffer = new byte[4 * 1024];
                            int len;
                            while ((len=inputImg.read(buffer)) != -1) {
                                output.write(buffer,0,len);
                            }
                            output.flush();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (output != null) {
                                output.close();
                            }
                            //handler.obtainMessage(5).sendToTarget();
                            Log.e("------", "mp3文件下载成功");
                            System.out.println("success");
                        } catch (IOException e) {
                            System.out.println("fail");
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    //读取本地图片
    private void readLocalImg(String picName) {
        boolean isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (isSdCardExist) {
            String sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();// 获取sdcard的根路径
            String filepath = "/sdcard/file/" + /*"告白气球.jpg"*/picName + ".jpg";
            File file = new File(filepath);
            if (file.exists()) {
                bm = BitmapFactory.decodeFile(filepath);
                // 将图片显示到ImageView中
                ivCurrentMusic.setImageBitmap(bm);

                EventBus.getDefault().post(new DataEvent(2, filepath));

            } else {
                EventBus.getDefault().post(new DataEvent(3, bm));
            }
        } else {
        }
    }

    private void playLastSong() {//上一首
        try {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        } catch (Exception e) {
            mMediaPlayer = null;
        }
        if (JxscApp.booleanJudge == false) {
            if (JxscApp.booSearchOrInternet == false) {
                pos = pos - 1;
                if (pos >= 0) {
                    requestPlay("play", billboardData.getLists().get(pos).getSong_id());
                    requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());
                } else {
                    pos = pos + 1;
                    requestPlay("play", billboardData.getLists().get(pos).getSong_id());
                    requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());
                    Toast.makeText(JxscApp.getContext(), "已经是最上面的一首了", Toast.LENGTH_SHORT).show();
                }
            } else {
                searchPos = searchPos - 1;
                if (searchPos >= 0) {
                    requestPlay("play", search.getSongList().get(searchPos).getSongid());
                    requestLyrics("lry", search.getSongList().get(searchPos).getSongid());
                } else {
                    searchPos = searchPos + 1;
                    requestPlay("play", search.getSongList().get(searchPos).getSongid());
                    requestLyrics("lry", search.getSongList().get(searchPos).getSongid());
                    Toast.makeText(JxscApp.getContext(), "已经是最上面的一首了", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (JxscApp.booleanJudge == true) {
            position = position - 1;
            if (position >= 0) {
                localTitle = dataMusicName.get(position);
                localAddress = "/sdcard/file/" + dataMusicName.get(position);
                localMusicPlay(localAddress);
            } else {
                position = position + 1;
                localTitle = dataMusicName.get(position);
                localAddress = "/sdcard/file/" + dataMusicName.get(position);
                localMusicPlay(localAddress);
                Toast.makeText(JxscApp.getContext(), "已经是最上面一首了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playNextSong() {//下一首
        lyricbo=true;
        try {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        } catch (Exception e) {
            mMediaPlayer = null;
        }
        if (JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == false) {//在线播放并且不是通过搜索得到的音乐
            if (JxscApp.playMode==0) {//顺序播放
                pos = pos + 1;
                if (pos < billboardData.getLists().size()) {
                    requestPlay("play", billboardData.getLists().get(pos).getSong_id());
                    requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());
                } else {
                    pos = pos - 1;
                    requestPlay("play", billboardData.getLists().get(pos).getSong_id());
                    requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());
                    Toast.makeText(JxscApp.getContext(), "已经是最下面一首了", Toast.LENGTH_SHORT).show();
                }
            } else if (JxscApp.playMode==1) {//随机播放
                int n = billboardData.getLists().size();
                int x = (int) (Math.random() * n);//目前列表取的20首歌曲
                requestPlay("play", billboardData.getLists().get(x).getSong_id());
                requestLyrics("lry", billboardData.getLists().get(x).getSong_id());
            } else if (JxscApp.playMode==2) {//单曲循环
                requestPlay("play", billboardData.getLists().get(pos).getSong_id());
                requestLyrics("lry", billboardData.getLists().get(pos).getSong_id());
            }
        } else if (JxscApp.booleanJudge == true) {//本地播放
            if (JxscApp.playMode==0 && pos <= dataMusicName.size()) {//顺序播放
                //pos = pos + 1;
                position = position + 1;
                if (position < dataMusicName.size() && position >= 0) {
                    localTitle = dataMusicName.get(position);
                    localAddress = "/sdcard/file/" + dataMusicName.get(position);
                    localMusicPlay(localAddress);
                } else {
                    position = position - 1;
                    localTitle = dataMusicName.get(position);
                    localAddress = "/sdcard/file/" + dataMusicName.get(position);
                    localMusicPlay(localAddress);
                    Toast.makeText(JxscApp.getContext(), "已经最后一首了", Toast.LENGTH_SHORT).show();
                }
            } else if (JxscApp.playMode==1) {//随机播放
                int n = dataMusicName.size();
                int x = (int) (Math.random() * n);
                if (x < n && x >= 0) {
                    localTitle = dataMusicName.get(x);
                    localAddress = "/sdcard/file/" + dataMusicName.get(x);
                    localMusicPlay(localAddress);
                }
            } else if (JxscApp.playMode==2) {//单曲循环
                localTitle = dataMusicName.get(position);
                localAddress = "/sdcard/file/" + dataMusicName.get(position);
                localMusicPlay(localAddress);
            }
        } else if (JxscApp.booleanJudge == false && JxscApp.booSearchOrInternet == true) {//搜索播放
            //Toast.makeText(JxscApp.getContext(), "当前搜索播放的音乐", Toast.LENGTH_SHORT).show();
            if (JxscApp.playMode==0) {//顺序播放
                searchPos = searchPos + 1;
                if (searchPos < search.getSongList().size()) {
                    requestPlay("play", search.getSongList().get(searchPos).getSongid());
                    requestLyrics("lry", search.getSongList().get(searchPos).getSongid());
                } else {
                    searchPos = searchPos - 1;
                    requestPlay("play", search.getSongList().get(searchPos).getSongid());
                    requestLyrics("lry", search.getSongList().get(searchPos).getSongid());
                    Toast.makeText(JxscApp.getContext(), "已经是最下面一首了", Toast.LENGTH_SHORT).show();
                }
            } else if (JxscApp.playMode==1) {//随机播放
                int n = search.getSongList().size();//歌曲总数目前暂时设为20首
                int x = (int) (Math.random() * n);//目前列表取的20首歌曲
                requestPlay("play", search.getSongList().get(x).getSongid());
                requestLyrics("lry", search.getSongList().get(x).getSongid());
            } else if (JxscApp.playMode==2) {//单曲循环
                requestPlay("play", search.getSongList().get(searchPos).getSongid());
                requestLyrics("lry", search.getSongList().get(searchPos).getSongid());
            }
        }
    }


    private void showLoading(String msg) {
        loadingDialog = LoadingUtil.createLoadingDialog(getActivity(), msg);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null)
            loadingDialog.dismiss();
        loadingDialog = null;
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
        }

        @Override
        public void onSuccess(Task task, Object[] arg) {
            if (arg != null && arg.length > 0) {
                RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
            }
        }
    };

    private boolean hideView=false;//隐藏重新进入的加载页
    //语音控制模块
    @Subscribe
    public void onEventMainThread(SpeechContrlEvent event) {
        if (event.getTag()==5001){//重新进入界面，隐藏该界面
            hideView=true;
            JxscApp.hidePlayinterface=0;
            getDialog().hide();
        }
        if (event.getTag()==1002){//上一首
            playLastSong();
        }else if (event.getTag()==1003){//下一首
            playNextSong();
        }else if (event.getTag()==1004){//暂停
            speechStop=true;
            if (mMediaPlayer.isPlaying()) {
                //针对在线音乐发送播放中暂停命令
                if (play != null) {
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            mac= DBM.getDefaultOrm().query(PowerState.class).get(a).getMac();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "stop");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                                /*OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(JxscApp.getContext(),
                                        NetConstant.HOST_ZEGBING, CmdType.CMD_ZG, cmdList, true, 0);*/
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                } else {//针对本地音乐
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                        boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                        if (b == true) {
                            whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                            mac= DBM.getDefaultOrm().query(PowerState.class).get(a).getMac();
                            List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localStop");
                            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                    NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                                /*OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(JxscApp.getContext(),
                                        NetConstant.HOST_ZEGBING, CmdType.CMD_ZG, cmdList, true, 0);*/
                            onlineSender.addListener(listener);
                            onlineSender.send();
                        }
                    }
                }
                if (mMediaPlayer!=null)
                    mMediaPlayer.pause();//停止播放

                handler.sendEmptyMessage(PAUSE); //停止更新进度条

                //播放时更改按钮图标
                start.setVisibility(View.VISIBLE);
                stop.setVisibility(View.INVISIBLE);
            }
        }else if (event.getTag()==1005){
            speechStop=false;
            //针对在线音乐发送暂停后播放命令
            if (play != null) {
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        mac= DBM.getDefaultOrm().query(PowerState.class).get(a).getMac();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "replay");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            /*OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(JxscApp.getContext(),
                                    NetConstant.HOST_ZEGBING, CmdType.CMD_ZG, cmdList, true, 0);*/
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            } else {//针对本地音乐发送暂停后播放命令
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        mac= DBM.getDefaultOrm().query(PowerState.class).get(a).getMac();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), "localRePlay");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                            /*OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(JxscApp.getContext(),
                                    NetConstant.HOST_ZEGBING, CmdType.CMD_ZG, cmdList, true, 0);*/
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            }
            if (mMediaPlayer!=null){
                Log.e("---othererror", current + "秒");
                //mMediaPlayer.seekTo(current);
                mMediaPlayer.start();
            }
            pause = false;//不暂停
            handler.sendEmptyMessage(START);//开始更新进度条

            //播放时更改按钮图标
            start.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
        }else if (event.getTag()==1006){//播放+歌曲（模式） 搜索直接播放（默认播放第一首，而且只播放一次）

        }else if (event.getTag()==1007){//(声音控制)音量调大2格
            String strVolContrl= (String) event.getObj1();
            Log.e("++++音量控制","音量");
            //加减声音都是默认加两格
            if(strVolContrl.equals("增加音量")||strVolContrl.equals("增大音量")
                    ||strVolContrl.equals("音量调大")||strVolContrl.equals("声音调大")||
                    strVolContrl.equals("声音大点")||strVolContrl.equals("音量大点")) {
                for (int i = 0; i < 2; i++) {
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
            }else if (strVolContrl.equals("减小音量") ||strVolContrl.equals("减少音量")
                    ||strVolContrl.equals("音量调小")||strVolContrl.equals("声音调小")||
                    strVolContrl.equals("声音小点")||strVolContrl.equals("音量大点")){
                for (int i=0;i<2;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
            }
            // 调高音量(判断是加减几格音量)


            /*String str=String.valueOf(strVolContrl.charAt(strVolContrl.indexOf("格") - 1));
            String strVolModel=strVolContrl.substring(0,strVolContrl.length()-2);//声音模式

            Log.e("++++strVolContrl",strVolContrl.toString());
            Log.e("++++str",str.toString());
            Log.e("++++strVolModel",strVolModel.toString());

            Log.e("----音量控制",str+"");*/
            //加音量
            /*if(str.equals("一")&&"声音调大".equals(strVolModel)||
                    str.equals("一")&&"音量调大".equals(strVolModel)){
                for (int i=0;i<1;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
            }else if(str.equals("两")&&"声音调大".equals(strVolModel)||
                    str.equals("两")&&"音量调大".equals(strVolModel)){
                for (int i=0;i<2;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
            }else if(str.equals("三")&&"声音调大".equals(strVolModel)||
                    str.equals("三")&&"音量调大".equals(strVolModel)){
                for (int i=0;i<3;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
            }else if(str.equals("一")&&"声音调小".equals(strVolModel)||
                    str.equals("一")&&"音量调小".equals(strVolModel)){
                for (int i=0;i<1;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
            }else if(str.equals("两")&&"声音调小".equals(strVolModel)||
                    str.equals("两")&&"音量调小".equals(strVolModel)){
                for (int i=0;i<2;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
            }else if(str.equals("三")&&"声音调小".equals(strVolModel)||
                    str.equals("三")&&"音量调小".equals(strVolModel)){
                for (int i=0;i<3;i++){
                    mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
            }*/

            //mgr.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            //音乐音量
            int MusicMax1 = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int MusicCurrent1 = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.e("MusicMax1/MusicCurrent1", (double) MusicCurrent1 / MusicMax1 + "哇哈哈");
            Log.e("MusicMax1/MusicCurrent1", MusicCurrent1 + "/" + MusicMax1);
            EventBus.getDefault().post(new DataEvent(1000, (double) MusicCurrent1 / MusicMax1));

            //调高时发送指令
            if (play != null) {//针对在线
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), MusicCurrent1 + "/" + MusicMax1 + ":volume");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            } else {//针对本地
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, "localAddress", dataMusicName.get(position), MusicCurrent1 + "/" + MusicMax1 + ":localVolume");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }
            }
        }else if (event.getTag()==1008){//返回控制
            hideLoading();
            //JxscApp.hidePlayinterface=true;
            JxscApp.hidePlayinterface=1;
            getDialog().hide();
        }else if (event.getTag()==1009){//

        }
    }

}
