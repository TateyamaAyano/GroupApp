package com.thegroup.rebuild.thegroupalpha;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.thegroup.rebuild.thegroupalpha.Common.Tools;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyy on 2017/3/19.
 */

public class WelcomeActivity extends AppCompatActivity {
    private HttpPost httpPost;
    private HttpResponse httpResponse;
    private Handler mHandler;
    private final int LOGIN_REQUEST=1;
    private final int USER_INFO_REQUEST=2;

    private String deviceLoginUrl ="http://101.200.32.61/thegroupapp/device_login.php";
    private String userInfoUrl ="http://101.200.32.61/thegroupapp/personal_info.php";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mHandler=new Handler(){
            @Override
                public void handleMessage(Message msg){
                    switch (msg.what){
                        case LOGIN_REQUEST:
                            String str = msg.getData().get("json").toString();
                            Log.d("response",str);
                            try {
                                JSONObject jsonObject=new JSONObject(str);
                                int code=Integer.parseInt(jsonObject.get("code").toString());
                                if(code==200){
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                                    JSONObject obj=jsonArray.getJSONObject(0);
                                    final String id=obj.get("Id").toString();
                                    Log.d("id>>>>>>>>",""+id);
                                    Toast.makeText(getApplication(),"id:"+id+" 登录成功",Toast.LENGTH_SHORT).show();
                                    Tools.setId(id);
                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent=new Intent(WelcomeActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    },2000);
                                }else if(code==201){
                                    final String id=jsonObject.get("data").toString();
                                    Log.d("id>>>>>>>>",""+id);
                                    Toast.makeText(getApplication(),"id:"+id+" 新用户注册成功",Toast.LENGTH_SHORT).show();
                                    Tools.setId(id);
                                    Tools.setName("用户"+Tools.getId());
                                    Tools.setAvatar("p00001");
                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent=new Intent(WelcomeActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    },2000);
                                }else if(code==400){
                                    Toast.makeText(getApplication(),"设备信息有误",Toast.LENGTH_SHORT).show();
                                    System.exit(0);
                                }else if(code==403){
                                    Toast.makeText(getApplication(),"服务器开小差了，请稍后重试",Toast.LENGTH_SHORT).show();
                                    System.exit(0);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;

                    }
                }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                String MAC= Tools.getMac(getApplicationContext());
                httpPost=new HttpPost(deviceLoginUrl);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("macaddress", MAC));
                try{
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String  res= EntityUtils.toString(httpResponse.getEntity());
                        Message msg=new Message();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("json",res);
                        msg.setData(bundle);
                        msg.what=LOGIN_REQUEST;
                        mHandler.sendMessage(msg);
                    }else{
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }).start();


    }
}
