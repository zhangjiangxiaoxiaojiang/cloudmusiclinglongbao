package com.jinxin.cloudmusic.cmd;

import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.bean.AcountProduct;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.PowerState;
import com.jinxin.cloudmusic.constant.LocalConstant;
import com.jinxin.cloudmusic.constant.ProductConstants;
import com.jinxin.cloudmusic.db.DBM;
import com.jinxin.cloudmusic.net.HttpManager;
import com.jinxin.cloudmusic.net.base.Callback;
import com.jinxin.cloudmusic.net.play.PlayRequest;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cd.smarthome.product.service.newcmd.CmdEntry;
import com.jinxin.cd.smarthome.product.service.newcmd.CmdPolicy;
import com.jinxin.cd.smarthome.product.service.newcmd.GenGateway;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2017/4/19 0019.
 */
public class CmdBuilder {
    private static class CmdBuilderHolder {
        private static final CmdBuilder INSTANCE = new CmdBuilder();
    }

    private CmdBuilder() {
    }

    public static CmdBuilder build() {
        return CmdBuilderHolder.INSTANCE;
    }

    //云音乐指令（特殊指令）
    public List<byte[]> generateCmdCloudMusic(/*String whid,*/String url,String title,String play) {
        Map<String, Object> params = new HashMap<String, Object>();

        //String url="http://yinyueshiting.baidu.com/data2/music/773be41d219da78a36c1ace9efc712ea/523150878/523150878.mp3?xcode=5b6fd9256c4fff056f333bb99c6701d9";
        //String title="一生所爱";
        params.put("text",url+":"+title+":"+play);

        String type = "textCmd";
        String addr485 = null;

        /********2016-01-27 修改多网关指令*********/
        //String whId485 = getGatewayWhIdByProductWhId(whId);
        /*************END**********************/
        return createCmdWithLength(/*whid,*/null,
                addr485, type, null, ProductConstants.FUN_TYPE_POWER_AMPLIFIER1, params);

    }

    //注册大网关指令（特殊指令）
    /**
     * userregister030:t072 c0802x 0072000000000001 a000
     * @param type
     * @param whId
     * @return
     */
    public List<byte[]> generateCmd4Gateway(String type,String whId){
        List<byte[]> byteList = new ArrayList<>();
        String cmd = "userregister030:t"+type+" c0802x "+whId+" a000";
        try {
            int len = cmd.getBytes("utf-8").length;
            ByteBuffer bbf = ByteBuffer.allocate(len + 8);
            bbf.putInt(12);
            bbf.putInt(len);
            bbf.put(cmd.getBytes("utf-8"));
            bbf.flip();
            byteList.add(bbf.array());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteList;
    }

    //发送播放请求
    private void requestPlay(String method, String songid) {
        HttpManager.getInstance().addRequestMusic(new PlayRequest(new Callback<Play>() {
            @Override
            public void onReceive(Play play) {
                super.onReceive(play);
                String title=play.getSongInfo().getTitle();
                String url=play.getBitrate().getFile_link();

            }

            @Override
            public void onError(String error) {

            }
        }, /*"play", "523150864"*/method, songid));
    }

    /**
     * 生成带长度的指令(带账号)
     *
     * @param customerId 用户id
     * @param addr485    设备的485地址
     * @param operation  操作
     * @param funUnits   设备的funUnits
     * @param funType    设备的funType
     * @param params     生成命令需要的参数
     * @return 生成的命令
     */
    public List<byte[]> createCmdWithLength(/*String whid,*/String customerId,
                                            String addr485, String operation, List<String> funUnits,
                                            String funType, Map<String, Object> params) {

        List<byte[]> byteList = new ArrayList<>();
        String whid=null;
        // 在线模式，返回平台命令
        List<String> _cmdList = CmdOriginGenerator.createCmdWithAccount(customerId, addr485,
                operation, funUnits, funType, params);

        try {
            if (_cmdList != null) {

                for (int i = 0; i < _cmdList.size(); i++) {
                    int len;
                    String _cmd = _cmdList.get(i).toString();
                    L.d("cmd: " + _cmd);
                    //先判断状态（为true就发送，false就不发送）
                    //for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++){
                        boolean b=DBM.getDefaultOrm().query(PowerState.class).get(1).isPowerState();
                        if (b==true){
                            whid= DBM.getDefaultOrm().query(PowerState.class).get(1).getWhId();
                            _cmd=whid+"|"+_cmd;
                            len = _cmd.getBytes("utf-8").length;

                            ByteBuffer bbf = ByteBuffer.allocate(len + 8);
                            bbf.putInt(1);
                            bbf.putInt(len);
                            bbf.put(_cmd.getBytes("utf-8"));
                            bbf.flip();
                            byteList.add(bbf.array());
//					byteList.add("\r\n".getBytes("utf-8"));
                            L.d("send cmd: " + new String(bbf.array()));
                        }
                //}
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return byteList;
}

    //精简云音乐指令发送
    public List<byte[]> generateCmdCloudMusicNew(String whid,String url,String title,String play) {
        Map<String, Object> params = new HashMap<String, Object>();

        //String url="http://yinyueshiting.baidu.com/data2/music/773be41d219da78a36c1ace9efc712ea/523150878/523150878.mp3?xcode=5b6fd9256c4fff056f333bb99c6701d9";
        //String title="一生所爱";
        params.put("text",url+":"+title+":"+play);

        String type = "textCmd";
        String addr485 = null;

        /********2016-01-27 修改多网关指令*********/
        //String whId485 = getGatewayWhIdByProductWhId(whId);
        /*************END**********************/
        return createCmdWithLengthNew(whid,null,type, ProductConstants.FUN_TYPE_POWER_AMPLIFIER1, params);
    }

    public List<byte[]> createCmdWithLengthNew(String mac,String addr485, String operation, String funType, Map<String, Object> params) {

        List<byte[]> byteList = new ArrayList<>();
        // 在线模式，返回平台命令
        List<String> _cmdList =createCmdWithAccountNew(addr485, operation, funType, params);

        try {
            if (_cmdList != null) {

                for (int i = 0; i < _cmdList.size(); i++) {
                    int len;
                    String _cmd = _cmdList.get(i).toString();
                    L.d("cmd: " + _cmd);
                    //先判断状态（为true就发送，false就不发送）
                    for (int a=0;a<DBM.getDefaultOrm().query(PowerState.class).size();a++){
                    boolean b=DBM.getDefaultOrm().query(PowerState.class).get(a).isPowerState();
                    if (b==true){
                        //whid= DBM.getDefaultOrm().query(PowerState.class).get(1).getWhId();
                        //_cmd=whid+"|"+_cmd;
                        //mac直接在数据库里面现取现用
                        mac= DBM.getDefaultOrm().query(PowerState.class).get(a).getMac();
                        _cmd=mac+ LocalConstant.playerUnique+"|"+_cmd;//在原来的mac的后面加上唯一码
                        len = _cmd.getBytes("utf-8").length;

                        ByteBuffer bbf = ByteBuffer.allocate(len + 8);
                        bbf.putInt(1);
                        bbf.putInt(len);
                        bbf.put(_cmd.getBytes("utf-8"));
                        bbf.flip();
                        byteList.add(bbf.array());
//					byteList.add("\r\n".getBytes("utf-8"));
                        L.d("send cmd: " + new String(bbf.array()));
                    }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return byteList;
    }

    public static List<String> createCmdWithAccountNew(String addr485, String type, String funType, Map<String, Object> params) {
        Map<String, Object> map = null;
        if (params == null) {
            map = new HashMap<>();
        } else {
            map = params;
        }
        /*****************指令包对象****************/
        CmdEntry cmdEntry = new CmdPolicy();//指令包对象
        try {
            cmdEntry.setCmdInputStream(JxscApp.getContext().
                            getResources().getAssets().open("cmd.ini"),
                    JxscApp.getContext().getResources().getAssets().open("cmdModel.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /******************************************/

        List<String> _cmdList = null;
        if (funType.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER1)) {//云音乐
            try {
                _cmdList = cmdEntry.getCmd(GenGateway.class, type, addr485, map);
            } catch (Exception e) {

            }
        }
        return _cmdList;
    }
}
