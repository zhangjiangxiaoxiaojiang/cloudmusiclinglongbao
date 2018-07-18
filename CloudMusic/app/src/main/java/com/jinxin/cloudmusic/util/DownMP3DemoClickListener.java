package com.jinxin.cloudmusic.util;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.jinxin.cloudmusic.app.JxscApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ZJ on 2017/3/16 0016.
 * 根据uri下载音乐
 * @Project: Android_MyDownload
 * @Desciption: 读取任意文件，并将文件保存到手机SDCard
 */
public class DownMP3DemoClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(JxscApp.getContext(),"正在下载，请稍候");
                String urlStr="http://yinyueshiting.baidu.com/data2/music/773be41d219da78a36c1ace9efc712ea/523150878/523150878.mp3?xcode=844a54681ec7c4e438b319dd5c3c046e";
                String path="file";
                String fileName="2.mp3";
                OutputStream output=null;
                try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                    URL url=new URL(urlStr);
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
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
                    String SDCard= Environment.getExternalStorageDirectory()+"";
                    String pathName=SDCard+"/"+path+"/"+fileName;//文件存储路径

                    File file=new File(pathName);
                    InputStream input=conn.getInputStream();
                    if(file.exists()){
                        System.out.println("exits");
                        return;
                    }else{
                        String dir=SDCard+"/"+path;
                        new File(dir).mkdir();//新建文件夹
                        file.createNewFile();//新建文件
                        output=new FileOutputStream(file);
                        //读取大文件
                        byte[] buffer=new byte[4*1024];
                        int len;
                        while((len=input.read(buffer))!=-1){
                            output.write(buffer,0,len);
                        }
                        output.flush();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    try {
                        if (output!=null) {
                            output.close();
                        }
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

    }

}
