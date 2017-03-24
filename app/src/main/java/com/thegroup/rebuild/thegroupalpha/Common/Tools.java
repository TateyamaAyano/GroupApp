package com.thegroup.rebuild.thegroupalpha.Common;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Timer;

/**
 * Created by yeelee on 17/3/17.
 */

public class Tools {
    public static Timer timer=new Timer(true);
    private static String id;
    private static String name="Android";
    private static String avatar="p00001";
    private static String currReceiveId;
    public static void setId(String str){
        id=str;
    }
    public static String getId(){
        return id;
    }
    public static String getName(){return name;}
    public static void setName(String str){name=str;}
    public static void setCurrReceiveId(String str){
        currReceiveId=str;
    }
    public static String getAvatar(){return avatar;}
    public static void setAvatar(String str){avatar=str;}
    public static String getCurrReceiveId(){
        return currReceiveId;
    }
    public static String getMac(Context context){
        return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }
}
