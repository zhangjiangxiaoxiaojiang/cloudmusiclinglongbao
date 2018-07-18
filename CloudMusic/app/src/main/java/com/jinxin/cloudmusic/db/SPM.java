package com.jinxin.cloudmusic.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.util.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by XTER on 2016/9/21.
 * 配置存储管理--SharedPreferenceManager
 */
public class SPM {
    /**
     * 存放常量
     */
    public static final String CONSTANT = "constant";
    /**
     * 存放位置信息
     */
    public static final String LOCATION = "location";
    public static String b;
    public static List<String> bList;

    public static void saveBoolean(String name, String key, boolean value) {
        saveBoolean(JxscApp.getContext(), name, key, value);
    }

    public static void saveInt(String name, String key, int value) {
        saveInt(JxscApp.getContext(), name, key, value);
    }

    public static void saveLong(String name, String key, long value) {
        saveLong(JxscApp.getContext(), name, key, value);
    }

    public static void saveStr(String name, String key, String value) {
        saveStr(JxscApp.getContext(), name, key, value);
    }

    //把b强制设置为null
    public static void saveListStr(String name, String key, List<String> value,String c) {
        b=c;
        if (b!=null){
        Log.e("---b",b);
        }
    }

    //把b删除掉个别值，直接对b进行操作
    public static void saveListStr1(String name, String key, List<String> value,String c) {
        //b=c;
        if (b!=null){
            Log.e("---b",b);
        }

        String[] sourceStrArray = b.split(",");
        List<String> userList =Arrays.asList(sourceStrArray);
        List<String> userList1=new ArrayList<>();
        for (int i=0;i<userList.size();i++){
            if (!userList.get(i).equals(c)){
                userList1.add(userList.get(i));
            }
        }
        b=null;
        for (String a : userList1) {
            if (b!=null) {
                b = a + "," + b;
            }else {
                b=a;
            }
        }
    }
    public static void saveListStr(String name, String key, List<String> value) {
        //b=null;
        if (b!=null){
            Log.e("---b",b);
        }
        for (String a : value) {
            if (b!=null) {
                b = a + "," + b;
            }else {
                b=a;
            }
        }

        saveStr(JxscApp.getContext(), name, key, b);
    }

    public static List<String> getListStr(String name, String key, String defaultValue) {
        String[] a = getStr(JxscApp.getContext(), name, key, defaultValue).split(",");
        bList = new ArrayList<String>();
        for (int i = 0; i < a.length; i++) {
            bList.add(a[i]);
        }
        return bList;
    }

    public static boolean getBoolean(String name, String key, boolean defaultValue) {
        return getBoolean(JxscApp.getContext(), name, key, defaultValue);
    }

    public static int getInt(String name, String key, int defaultValue) {
        return getInt(JxscApp.getContext(), name, key, defaultValue);
    }

    public static long getLong(String name, String key, long defaultValue) {
        return getLong(JxscApp.getContext(), name, key, defaultValue);
    }

    public static String getStr(String name, String key, String defaultValue) {
        return getStr(JxscApp.getContext(), name, key, defaultValue);
    }

    private static void saveBoolean(Context context, String name, String key, boolean value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception ex) {
            L.e("SPM:" + ex.toString());
        }
    }

    private static void saveInt(Context context, String name, String key, int value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, value);
            editor.apply();
        } catch (Exception ex) {
            L.e("SPM:" + ex.toString());
        }
    }

    private static void saveLong(Context context, String name, String key, long value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.apply();
        } catch (Exception ex) {
            L.e("SPM:" + ex.toString());
        }
    }

    private static void saveStr(Context context, String name, String key, String value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(name, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception ex) {
            L.e("SPM:" + ex.toString());
        }
    }

    private static boolean getBoolean(Context context, String name, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(name, 0);
        return sp != null && sp.getBoolean(key, defaultValue);
    }

    private static int getInt(Context context, String name, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(name, 0);
        if (sp != null) {
            return sp.getInt(key, defaultValue);
        }
        return -1;
    }

    private static long getLong(Context context, String name, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(name, 0);
        if (sp != null) {
            return sp.getLong(key, defaultValue);
        }
        return -1;
    }

    private static String getStr(Context context, String name, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(name, 0);
        if (sp != null) {
            return sp.getString(key, defaultValue);
        }
        return "";
    }

    /**
     * 清除配置表
     *
     * @param name 配置文件名
     */
    public static void removeSP(String name) {
        SharedPreferences sp = JxscApp.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

}
