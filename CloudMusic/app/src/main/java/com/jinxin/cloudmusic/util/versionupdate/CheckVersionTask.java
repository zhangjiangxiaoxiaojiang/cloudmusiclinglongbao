package com.jinxin.cloudmusic.util.versionupdate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.jinxin.cloudmusic.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import static android.content.ContentValues.TAG;

/**
 * Created by ZJ on 2017/8/25 0025.
 * APK版本的自动升级工具类
 */
public class CheckVersionTask {
    private static final int UPDATA_CLIENT = 0;
    private static final int GET_UNDATAINFO_ERROR = 1;
    private static final int DOWN_ERROR = 2;
    private static final int CONN_FIALED = 3;
    private static final int DOWNLOAD_PROGRESS = 4;

    private VersionInfo info;
    private String versionname;
    private ProgressDialog pd;
    private Context context;
    private IParse p;
    private static CheckVersionTask instance;

    public CheckVersionTask() {
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA_CLIENT:
                    showUpdataDialog(context);
                    break;
                case GET_UNDATAINFO_ERROR:
                    Toast.makeText(context, "获取服务器更新信息失败", Toast.LENGTH_SHORT).show();
                    //LoginMain();
                    break;
                case DOWN_ERROR:
                    Toast.makeText(context, "下载新版本失败", Toast.LENGTH_SHORT).show();
                    //LoginMain();
                    break;
                case CONN_FIALED:
                    Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
                    break;
                case DOWNLOAD_PROGRESS:
                    float b = (float) msg.obj;
                    final int a = (int) (b / (info.getAppSize()) * 100);
                    pd.setProgress(a);
                default:
                    break;
            }
        }
    };

    //单例模式
    public static synchronized CheckVersionTask getInstance() {
        if (null==instance){
            instance=new CheckVersionTask();
        }
        return instance;
    }

    public static void setHttpUrlConnGet(Context context,IParse p,String baseUrl,Map<String, Object> map){
        getInstance().httpUrlConnGet(context, p, baseUrl, map);
    }

    public static void  setHttpUrlConnPost(Context context,IParse p,String baseUrl, Map<String, Object> map){
        getInstance().httpUrlConnPost(context, p, baseUrl, map);
    }

    //1 获取当前程序的版本号
    public String getVersionName() throws Exception {
        //获取packagemanager的实例,getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    //2 发送请求到服务器
    //发送get请求
    public void httpUrlConnGet(Context context,IParse p,String baseUrl, Map<String, Object> map) {
        this.p=p;
        HttpURLConnection urlConnection = null;
        this.context = context;
        URL url = null;
        try {
            url = new URL(baseUrl+"?$data="+map.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                StringBuffer buffer = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                in.close();
                br.close();
                //对result继续解析，拿到平台最新的versionCode
                String result = buffer.toString();
                //info = p.parseData(result);
                parseJson(result);
                versionname = getVersionName();//本地版本
                try {
                    double newVersion= Double.parseDouble(info.getAppVersion());
                    double oldVersion= Double.parseDouble(versionname);
                    if (newVersion>oldVersion){//只有平台版本号大于当前版本号才更新
                        Log.i(TAG, "版本号不同 ,提示用户升级 ");
                        Message msg = new Message();
                        msg.what = UPDATA_CLIENT;
                        handler.sendMessage(msg);
                    }else {
                        Log.i(TAG, "无需升级已是最新的版本！");
                    }
                }catch (Exception e){
                    Log.i(TAG, "转化出错！");
                }

                /*if (info.getAppVersion().equals(versionname)) {
                    Log.i(TAG, "版本号相同无需升级");
                    //LoginMain();
                } else {
                    Log.i(TAG, "版本号不同 ,提示用户升级 ");
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }*/
            } else {
                Log.e("连接异常", "");
                handler.obtainMessage(CONN_FIALED).sendToTarget();
            }
        } catch (Exception e) {
            Log.e("---", e.toString());
            handler.obtainMessage(CONN_FIALED).sendToTarget();
        } finally {
            urlConnection.disconnect();
        }
    }

    //发送post请求
    public void httpUrlConnPost(Context context,IParse p,String baseUrl, Map<String, Object> map) {
        this.p = p;
        this.context = context;
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(baseUrl+"?$data="+map.toString());
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);
            urlConnection.setInstanceFollowRedirects(true);//
            urlConnection.setReadTimeout(3000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
            urlConnection.setRequestMethod("POST");//设置请求的方式
            //urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
            urlConnection.connect();

            OutputStream out = urlConnection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(baseUrl);
            bw.flush();
            out.close();
            bw.close();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//得到服务端的返回码是否连接成功
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    buffer.append(str);
                }
                in.close();
                br.close();
                String result = buffer.toString();
                Log.e("buffer", buffer.toString());
                //对result继续解析，拿到平台最新的versionCode
                //info = p.parseData(result);
                parseJson(result);
                versionname = getVersionName();
                try {
                    double newVersion=Double.parseDouble(info.getAppVersion());
                    double oldVersion=Double.parseDouble(versionname);
                    if (newVersion>oldVersion){//只有平台版本号大于当前版本号才更新
                        Log.i(TAG, "版本号不同 ,提示用户升级 ");
                        Message msg = new Message();
                        msg.what = UPDATA_CLIENT;
                        handler.sendMessage(msg);
                    }else {
                        Log.i(TAG, "无需升级已是最新的版本！");
                    }
                }catch (Exception e){
                    Log.i(TAG, "转化出错！");
                }

                /*if (info.getAppVersion().equals(versionname)) {
                    Log.i(TAG, "版本号相同无需升级");
                    //LoginMain();
                } else {
                    Log.i(TAG, "版本号不同 ,提示用户升级 ");
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);
                }*/
            } else {
                handler.obtainMessage(CONN_FIALED).sendToTarget();
            }
        } catch (Exception e) {
            handler.obtainMessage(CONN_FIALED).sendToTarget();
        } finally {
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
    }

    /*
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    protected void showUpdataDialog(final Context context) {
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("版本升级");
        builer.setMessage(info.getComments());
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载apk,更新");
                downLoadApk(context);
            }
        });
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //LoginMain();
            }
        });
        AlertDialog dialog = builer.create();
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /*
     * 从服务器中下载APK
     * 根据地址下载apk,并将其保存到本地的sd卡
     */
    protected void downLoadApk(final Context context) {
        pd = new ProgressDialog(context);
        //pd.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String urlStr = info.getAppPath();
                    String path = "file";
                    String fileName = info.getNewName();//最新下载下来apk的名字
                    OutputStream output = null;
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    String SDCard = Environment.getExternalStorageDirectory() + "";
                    String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径
                    File file = new File(pathName);
                    InputStream input = conn.getInputStream();
                    /*if (file.exists()) {//重新写一个文件
                        System.out.println("exits");
                        handler.obtainMessage(DOWNLOAD_PROGRESS, (float)info.getAppSize()).sendToTarget();
                        //return;
                    } else {*/
                        String dir = SDCard + "/" + path;
                        new File(dir).mkdir();//新建文件夹
                        file.createNewFile();//新建文件
                        output = new FileOutputStream(file);
                        //读取大文件
                        byte[] buffer = new byte[4 * 1024];
                        int len = 0;
                        float sum = 0;
                        while ((len = input.read(buffer)) != -1) {
                            sum += len;
                            output.write(buffer, 0, len);
                            handler.obtainMessage(DOWNLOAD_PROGRESS, sum).sendToTarget();
                            Log.e("---len", sum + "");
                        }
                        output.flush();
                    //}
                    sleep(1000);
                    installApk(file, context);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    handler.obtainMessage(DOWN_ERROR).sendToTarget();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk，file:要安装的apk的目录
    protected void installApk(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //解析json数据
    private void parseJson(String result) throws JSONException {
        JSONObject jsonObject=new JSONObject(result);
        JSONObject object= jsonObject.getJSONObject("serviceContent");
        int id=object.getInt("id");
        int appType=object.getInt("appType");
        String appVersion=object.getString("appVersion");
        long publishTime=object.getLong("publishTime");
        int publishUser=object.getInt("publishUser");
        long downloadTimes=object.getLong("downloadTimes");
        int status=object.getInt("status");
        String oldName=object.getString("oldName");
        String newName=object.getString("newName");
        String appPath=object.getString("appPath");
        long appSize=object.getLong("appSize");
        if (info==null){
            info=new VersionInfo();
        }
        info.setId(id);
        info.setAppType(appType);
        info.setAppVersion(appVersion);
        info.setPublishTime(publishTime);
        info.setPublishUser(publishUser);
        info.setDownloadTimes(downloadTimes);
        info.setStatus(status);
        info.setOldName(oldName);
        info.setNewName(newName);
        info.setAppPath(appPath);
        info.setAppSize(appSize);
    }

    //进入程序的主界面
    private void LoginMain() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
