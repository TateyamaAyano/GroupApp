package com.thegroup.rebuild.thegroupalpha.Common;

import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by lyy on 2017/3/15.
 */

public class GroupMessage implements Serializable {
    private String sendId;
    private String sendName;
    private String sendAvatar;
    private String sendTime;
    private String msg;
    public GroupMessage(String sid,String sname,String savatar,String t, String m){
        sendId=sid;
        sendName=sname;
        sendAvatar=savatar;
        sendTime=t;
        msg=m;
    }
    public String getSendName(){return sendName;}
    public String getSendAvatar(){return sendAvatar;}
    public String getSendId(){return sendId;}
    public String getSendTime(){
        return sendTime;
    }
    public String getMsg(){
        return msg;
    }
}
