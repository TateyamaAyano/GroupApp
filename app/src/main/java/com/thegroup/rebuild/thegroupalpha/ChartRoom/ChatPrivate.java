package com.thegroup.rebuild.thegroupalpha.ChartRoom;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thegroup.rebuild.thegroupalpha.Common.GroupMessage;
import com.thegroup.rebuild.thegroupalpha.Common.MessageAdapter;
import com.thegroup.rebuild.thegroupalpha.Common.Tools;
import com.thegroup.rebuild.thegroupalpha.R;
import org.json.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yeelee on 17/3/15.
 */

public class ChatPrivate extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private Button sendMsgBtn;
    private View.OnClickListener requestMsgListener;
    private static final int REFRESH_COMPLETE = 0X110;
    private static final int SEND_MSG_COMPLETE = 0X111;
    private static final int REQUEST_CHAT_ROOM_ID_COMPLETE = 0X112;

    private String chatRoomType;
    private String sendId;
    private String recId;
    private String currGroupId;
    private String ChatUrl ="http://101.200.32.61/thegroupapp/message_chat.php";
    private String ContactUrl="http://101.200.32.61/thegroupapp/message_list.php";
    private HttpPost httpPost;
    private HttpResponse httpResponse;
    private ListView charArea;
    private TextView msgTV;
    private SwipeRefreshLayout mSwipeLayout;
    private ArrayList<GroupMessage> gmList;
    private MessageAdapter msgAdapter;
    private int historyMessageCount;
    private String str;
    private String content;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case REFRESH_COMPLETE:
                    mSwipeLayout.setRefreshing(false);
                    if(!str.equals(msg.getData().get("json").toString())) {
                        gmList=new ArrayList<GroupMessage>();
                        str = msg.getData().get("json").toString();
                        try {
                            JSONObject jsonObj = new JSONObject(str);
                            int code=Integer.parseInt(jsonObj.get("code").toString());
                            if(code==200) {
                                JSONArray jsonArr = jsonObj.getJSONArray("data");
                                for (int i = jsonArr.length()-1; i >=0; i--) {
                                    JSONObject obj = jsonArr.getJSONObject(i);
                                    gmList.add(new GroupMessage(obj.get("SenderId").toString(),obj.get("Name").toString(),obj.get("Avatar").toString(),obj.get("SendTime").toString(), obj.get("Content").toString()));
                                }
                            }else{
                                Toast.makeText(getApplication(),"服务器开小差了，请稍后重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        msgAdapter = new MessageAdapter(ChatPrivate.this, R.layout.message_layout, gmList);
                        charArea.setAdapter(msgAdapter);
                        charArea.setSelection(charArea.getBottom());
                    }
                    break;
                case SEND_MSG_COMPLETE:
                        str = msg.getData().get("json").toString();
                        try {
                            JSONObject jsonObj = new JSONObject(str);
                            int code = Integer.parseInt(jsonObj.get("code").toString());
                            Log.d("res", jsonObj.get("code").toString());
                            if (code == 200) {
                                if (!currGroupId.contentEquals("0")) {
                                    gmList.add(new GroupMessage(Tools.getId(), Tools.getName(), Tools.getAvatar(), "", content));
                                    msgAdapter = new MessageAdapter(ChatPrivate.this, R.layout.message_layout, gmList);
                                    charArea.setAdapter(msgAdapter);
                                    charArea.setSelection(charArea.getBottom());
                                    msgTV.setText("");
                                } else {
                                    currGroupId = jsonObj.get("data").toString();
                                }
                            } else if (code == 403) {
                                Toast.makeText(getApplication(), "数据库连接失败", Toast.LENGTH_SHORT).show();
                                break;
                            } else if (code == 400) {
                                Toast.makeText(getApplication(), "消息发送失败", Toast.LENGTH_SHORT).show();
                                break;
                            } else {
                                Toast.makeText(getApplication(), "未知错误", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    break;
                case REQUEST_CHAT_ROOM_ID_COMPLETE:
                    str=msg.getData().get("json").toString();
                    try{
                        JSONObject jsonObj=new JSONObject(str);
                        int code=Integer.parseInt(jsonObj.get("code").toString());
                        Log.d("res",jsonObj.get("code").toString());
                        if(code==200) {
                            currGroupId=jsonObj.get("data").toString();
                            gmList=new ArrayList<GroupMessage>();
                            msgAdapter = new MessageAdapter(ChatPrivate.this, R.layout.message_layout, gmList);
                            charArea.setAdapter(msgAdapter);
                            charArea.setSelection(charArea.getBottom());
                            msgTV.setText("");
                        }else if(code==403){
                            Toast.makeText(getApplication(),"数据库连接失败",Toast.LENGTH_SHORT).show();
                            break;
                        }else if(code==400) {
                            Toast.makeText(getApplication(), "消息发送失败", Toast.LENGTH_SHORT).show();
                            break;
                        }else if(code==201){
                            currGroupId="0";
                            Log.d("get group id",currGroupId);
                            gmList=new ArrayList<GroupMessage>();
                            msgAdapter = new MessageAdapter(ChatPrivate.this, R.layout.message_layout, gmList);
                            charArea.setAdapter(msgAdapter);
                            charArea.setSelection(charArea.getBottom());
                            msgTV.setText("");
                            Toast.makeText(getApplication(),"新建聊天",Toast.LENGTH_SHORT).show();
                            break;
                        }else{
                            Toast.makeText(getApplication(),"未知错误",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        currGroupId=intent.getStringExtra("chatId");
        this.setTitle(intent.getStringExtra("recName"));

        sendId = Tools.getId();
        Log.d("currGroupId",currGroupId);
        if(currGroupId.equals("-1")) {
            recId = intent.getStringExtra("recId");
            Tools.setCurrReceiveId(recId);
            Log.d("set 0", ">>>>>>>已置零>>>>>>>>>" + currGroupId);
            requestPrivateChatRoomId();
        }
        historyMessageCount=10;
        setContentView(R.layout.activity_chatroom);
        initView();
    }
    public void initView(){
        str="";
        historyMessageCount=10;
        msgTV=(TextView)findViewById(R.id.inputArea);
        mSwipeLayout=(SwipeRefreshLayout) findViewById(R.id.chatRoomSV);
        mSwipeLayout.setOnRefreshListener(ChatPrivate.this);
        mSwipeLayout.setColorScheme(new int[]{android.R.color.holo_red_dark, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light});
        requestMsgListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msgTV.getText().toString().length()>40){
                    Toast.makeText(getApplication(),"输入内容超过40字",Toast.LENGTH_SHORT).show();
                }else{
                    httpPost=new HttpPost(ChatUrl);
                    final String msg=msgTV.getText().toString().replace("\n","");
                    content=msg;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(!currGroupId.contentEquals("-1")) {
                                Log.d("request", "send a message");
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("groupid", currGroupId));
                                params.add(new BasicNameValuePair("operation", "0"));
                                params.add(new BasicNameValuePair("receiverid", recId));
                                params.add(new BasicNameValuePair("senderid", sendId));
                                params.add(new BasicNameValuePair("content", msg));
                                try {
                                    String str = "";
                                    for (NameValuePair nvp : params) {
                                        str += nvp.toString() + " ";
                                    }
                                    Log.d("params", str);
                                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                                    httpResponse = new DefaultHttpClient().execute(httpPost);
                                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                                        String res = EntityUtils.toString(httpResponse.getEntity());
                                        Log.e("response text>>>>>>>>>", "text:" + res);
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("json", res);
                                        message.setData(bundle);
                                        mHandler.sendMessage(message);
                                        message.what = SEND_MSG_COMPLETE;
                                    } else {
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            }
        };
        //bind send Message Button
        sendMsgBtn=(Button)findViewById(R.id.sendMsgBtn);
        sendMsgBtn.setOnClickListener(requestMsgListener);
        charArea=(ListView) findViewById(R.id.chatArea);
        Tools.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                requestNewMessage();
            }
        },0,3000);
    }
    @Override
    public void onRefresh() {
        historyMessageCount+=10;
        requestNewMessage();
    }
    public void requestNewMessage() {
        if(!currGroupId.contentEquals("0")&&!currGroupId.contentEquals("-1")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpPost = new HttpPost(ChatUrl);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("operation", "1"));
                    params.add(new BasicNameValuePair("groupid", currGroupId));
                    params.add(new BasicNameValuePair("historymessagecount", "" + historyMessageCount));

                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                        httpResponse = new DefaultHttpClient().execute(httpPost);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            String res = EntityUtils.toString(httpResponse.getEntity());
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("json", res);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                            message.what = REFRESH_COMPLETE;
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public void requestPrivateChatRoomId(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpPost = new HttpPost(ContactUrl);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("operation", "2"));
                params.add(new BasicNameValuePair("usera", sendId));
                params.add(new BasicNameValuePair("userb", recId));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String res = EntityUtils.toString(httpResponse.getEntity());
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("json", res);
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                        message.what = REQUEST_CHAT_ROOM_ID_COMPLETE;
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

