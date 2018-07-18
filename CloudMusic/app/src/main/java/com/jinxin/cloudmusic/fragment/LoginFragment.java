package com.jinxin.cloudmusic.fragment;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.R;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.AcountProduct;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.bean.User;
import com.jinxin.cloudmusic.constant.LocalConstant;
import com.jinxin.cloudmusic.data.UpdateDataService;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.db.SPM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.UpdateDataEvent;
import com.jinxin.cloudmusic.jpush.BroadPushMessage;
import com.jinxin.cloudmusic.jpush.MessageQueue2;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.login.LoginRequest;
import com.jinxin.cloudmusic.net.power.PowerRequest;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cloudmusic.util.SysUtil;
import com.jinxin.cloudmusic.util.ToastUtil;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment {
    @Bind(R.id.review_act_btn)
    Button reviewActBtn;
    @Bind(R.id.useraccount)
    EditText userAcount;
    @Bind(R.id.userpassword)
    EditText userPassword;
    private Timer timer;
    private List<PowerState> dataMusicName = new ArrayList<>();
    private boolean f = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.costom);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        init();//初始化
        return view;
    }

    private void init() {
        //String sb="$data={activityCode\"\:"T000","homeDomain":"P000","actionCode":"0","origDomain":"A001","bipVer":"1.0","bipCode":"B000","processTime":"20170414184619","testFlag":"1","serviceContent":{"password":"111111","account":"C200030014"}}";

        /*ivBcak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });*/
        reviewActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//登录
                //和平台建立seetion连接
                //getActivity().startService(new Intent(JxscApp.getContext(), UpdateDataService.class));
                //连接大网关
                setTimerTask();
                //ToastUtil.showShort(JxscApp.getContext(), "连接大网关");

                final String userAcounts = userAcount.getText().toString();
                final String userPasswords = userPassword.getText().toString();
                //检查用户输入的参数
                if (userAcounts.isEmpty()) {
                    ToastUtil.showShort(JxscApp.getContext(), "请输入用户名");
                    return;
                }
                if (userPasswords.isEmpty()) {
                    ToastUtil.showShort(JxscApp.getContext(), "请输入密码");
                    return;
                }
                HttpManager.getInstance().addRequestMusicPower(new LoginRequest(new Callback<User>() {
                    @Override
                    public void onReceive(User user) {
                        super.onReceive(user);
                        String account = user.getAccount();
                        String secretKey = user.getSecretKey();
                        powerRequest(account, secretKey);
                    }

                    @Override
                    public void onError(String error) {

                    }
                }, userAcounts, userPasswords));

            }
        });
    }

    //发送请求，当前功放
    private void powerRequest(String account, String secretKey) {
        HttpManager.getInstance().addRequestMusicPower(new PowerRequest(new Callback<AcountProduct>() {
            @Override
            public void onReceive(List<AcountProduct> t) {
                super.onReceive(t);
                if (t.size() > 0 && t != null) {
                    String codeName = t.get(0).getCodeName();
                    String whId = t.get(0).getWhId();
                    String mac=t.get(0).getMac();
                    //保存whId到本地的数据库
                    for (int i = 0; i < t.size(); i++) {
                        DBM.getDefaultOrm().save(t.get(i));//保存输入源信息到数据库
                    }
                    //String whid=DBM.getDefaultOrm().query(new QueryBuilder<AcountProduct>(AcountProduct.class).where("whId=?", new String[]{t.get(0).getWhId()})).get(0).getWhId();
                    String whid=DBM.getDefaultOrm().query(AcountProduct.class).get(0).getWhId();

                    dataMusicName.clear();
                    for (int i = 0; i < t.size(); i++) {
                        //PowerState powerState = new PowerState(t.get(i).getWhId(),t.get(i).getCodeName(), f);
                        PowerState powerState = new PowerState(t.get(i).getWhId(),t.get(i).getMac(),t.get(i).getCodeName(), f);
                        dataMusicName.add(powerState);
                    }
                    //登录成功就在这里做保存数据到本地数据库的操作
                    for (int i = 0; i < dataMusicName.size(); i++) {
                        DBM.getDefaultOrm().save(dataMusicName.get(i));//保存输入源信息到数据库
                    }
                    EventBus.getDefault().post(new DataEvent(2020, ""));//首次登录成功后，弹出输入源选择界面
                }
                getDialog().dismiss();
            }

            @Override
            public void onError(String error) {

            }
        }, account, secretKey));
    }

    @Subscribe
    public void onEventMainThread(DataEvent event) {
        if (3000 == event.getDatas()) {
            getDialog().show();
        }
        if (4200 == event.getDatas()) {
            if (getFragmentManager().findFragmentByTag("AcountProduct") != null) {//通过tag销毁另外一个碎片
                getFragmentManager().findFragmentByTag("AcountProduct").onDestroy();
                Log.e("-----123", getFragmentManager().findFragmentByTag("AcountProduct").toString());
            }
        }


    }

    /**
     * 定时任务，发送广播获取网关地址
     */
    private void setTimerTask() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pushMessageToGateway();
            }
        }, 1000, 30 * 1000);
    }

    /**
     * 推送消息至大网关
     */
    private void pushMessageToGateway() {
        try {
            String id_account = SPM.getStr(SPM.CONSTANT,
                    LocalConstant.KEY_ACCOUNT, "");
            // 先推送消息到大网关
            String msgconts = "11005" + "getwayip" + "(4700)";
            MessageQueue2.getInstance().offer(
                    new BroadPushMessage(msgconts, id_account));
            L.i(msgconts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUpdating(UpdateDataEvent event) {
        clearTimer();
        //mHandler.obtainMessage(LOADING_HIDE).sendToTarget();
        //mHandler.obtainMessage(LOADING_SHOW, getString(R.string.login_updating)).sendToTarget();
        //对大网关进行设备验证（云音乐）
        JSONObject jo = new JSONObject();
        jo.put("area", "identification");
        jo.put("time", SysUtil.getNow2());
        JSONObject jo1 = new JSONObject();
        jo1.put("uid", getSerialNumberAndIp());
        jo1.put("type", "05");
        jo.put("content", jo1);
        //上传到大网关
        UpdateDataService.updateSpecialData(jo);

        //网关连接就绪，开始更新数据
        UpdateDataService.updateData(LocalConstant.AREA_ALL);
    }

    //获取本机的mac
    public String getLocalMacAddress() {
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.e("----mac地址", wifiInfo.getMacAddress());
        return wifiInfo.getMacAddress();
    }

    //获得设备的序列号和ip地址
    public String getSerialNumberAndIp() {
        String serialNumber = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
        Log.e("----numAndIp", serialNumber + ipAddress);//815b828af7c3443a192.168.191.18
        String Numberip = serialNumber + ipAddress;
        String[] strs = Numberip.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]);
        }
        return sb.toString();
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    protected void clearTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
