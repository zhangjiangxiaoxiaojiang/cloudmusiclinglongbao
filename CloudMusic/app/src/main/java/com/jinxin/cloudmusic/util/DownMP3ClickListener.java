package com.jinxin.cloudmusic.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.GroupBean.Lyrics;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.cmd.CmdBuilder;
import com.jinxin.cloudmusic.cmd.CmdType;
import com.jinxin.cloudmusic.cmd.OnlineCmdSenderLong;
import com.jinxin.cloudmusic.cmd.RemoteJsonResultInfo;
import com.jinxin.cloudmusic.cmd.Task;
import com.jinxin.cloudmusic.cmd.TaskListener;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.getlyrics.GetLyricsRequest;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by ZJ on 2017/3/31 0031.
 * 下载MP3和相应歌词到本地
 */
//下载MP3和相应歌曲图片到本地sdcard卡
public class DownMP3ClickListener implements View.OnClickListener {
    private Play play;
    private Lyrics lyr;
    private String whid;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Toast.makeText(JxscApp.getContext(),"正在下载文件",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
    public DownMP3ClickListener(Play play, Lyrics lyr) {
        this.play = play;
        this.lyr = lyr;
    }

    public DownMP3ClickListener(Play play) {
        this.play = play;
    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(JxscApp.getContext(),"正在下载，请稍候");
                //发送下载命令
                for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++) {
                    boolean b = DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b == true) {
                        whid = DBM.getDefaultOrm().query(PowerState.class).get(a).getWhId();
                        List<byte[]> cmdList = CmdBuilder.build().generateCmdCloudMusicNew(whid, play.getBitrate().getFile_link(), play.getSongInfo().getTitle(), "downLoad");
                        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(JxscApp.getContext(),
                                NetConstant.HOST_INSPECT, CmdType.CMD_485, cmdList, true, 0);
                        onlineSender.addListener(listener);
                        onlineSender.send();
                    }
                }

                //点击下载，设置downLrc的值为true，请求歌词，并且把请求到的歌词保存到数据库
               // downLrc = true;
                //获取歌词
                requestLyrics("lry", play.getSongInfo().getSong_id());
                //String urlStr="http://yinyueshiting.baidu.com/data2/music/773be41d219da78a36c1ace9efc712ea/523150878/523150878.mp3?xcode=844a54681ec7c4e438b319dd5c3c046e";
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
                       /* while (input.read(buffer) != -1) {
                            output.write(buffer);
                        }*/
                        while(len != -1)
                        {
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
                        //handler.obtainMessage(5).sendToTarget();
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
                //downLrc = true;
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
                       /* byte[] buffer = new byte[1024];
                        while (inputImg.read(buffer) != -1) {
                            output.write(buffer);
                        }*/
                        //byte[] buffer = new byte[64];
                        byte[] buffer = new byte[4*1024];
                        int len = inputImg.read(buffer);
                        while(len != -1)
                        {
                            output.write(buffer,0,len);
                            len = inputImg.read(buffer);

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
                        handler.obtainMessage(1).sendToTarget();
                        System.out.println("success");
                    } catch (IOException e) {
                        System.out.println("fail");
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //下载歌词(查询是否已下载，有就下载并且更新并且保存，没有就下载并且保持)
    private void requestLyrics(String method, String songid) {
        HttpManager.getInstance().addRequestMusic(new GetLyricsRequest(new Callback<Lyrics>() {
            @Override
            public void onReceive(Lyrics lyrics) {
                super.onReceive(lyrics);
                String tit = lyrics.getTitle();
                lyr = lyrics;
                /*if (downLrc == false) {
                    //lrcView.loadLrc(lyr.getLrcContent());
                    //handler.obtainMessage(3).sendToTarget();
                } else {*/
                    if (DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{tit})).size() > 0) {
                        DBM.getDefaultOrm().update(lyr);
                    } else {
                        DBM.getDefaultOrm().save(lyr);//保存歌词到数据库
                        String a = DBM.getDefaultOrm().query(new QueryBuilder<Lyrics>(Lyrics.class).where("title=?", new String[]{tit})).get(0).getLrcContent();
                        Log.e("-----a", a);
                    }

                /*}
                downLrc = false;//置换返回初始状态*/
            }

            @Override
            public void onError(String error) {

            }
        }, method, songid/*"lry", "877578"*/));
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
}