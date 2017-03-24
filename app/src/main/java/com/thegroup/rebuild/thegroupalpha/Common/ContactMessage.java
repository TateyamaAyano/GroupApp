package com.thegroup.rebuild.thegroupalpha.Common;

import java.io.Serializable;

/**
 * Created by lyy on 2017/3/15.
 */

public class ContactMessage implements Serializable {
    private String ChatGroupId;
    private String receiverid;
    private String recervername;
    private String sendTime;
    private String msg;
    private String receiveravatar;
    public ContactMessage(String gid, String rid,String rname,String ravatar, String t, String m){
        ChatGroupId=gid;
        recervername=rname;
        receiveravatar=ravatar;
        receiverid=rid;
        sendTime=t;
        msg=m;
    }
    public String getReceiverId(){return receiverid;}
    public String getReceiveravatar(){return receiveravatar;}
    public String getRecervername(){return recervername;}
    public String getChatGroupId(){return ChatGroupId;}
    public String getSendTime(){
        return sendTime;
    }
    public String getMsg(){
        return msg;
    }
}
