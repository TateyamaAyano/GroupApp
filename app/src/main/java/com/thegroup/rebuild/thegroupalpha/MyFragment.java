package com.thegroup.rebuild.thegroupalpha;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.thegroup.rebuild.thegroupalpha.Common.ContactAdapter;
import com.thegroup.rebuild.thegroupalpha.Common.ContactMessage;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/3/4.
 */
public @SuppressLint("ValidFragment") class MyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
        String mTitle = null;
        View view = null;

    private String originUsername,originSignature,changedUsername,changedSignature;
    private TextView tvOriginUsername,tvOriginSignature,tvChangedUsername,tvChangedSignature;
    private String getUsernameUrl = "http://101.200.32.61/thegroupapp/personal_info.php";
        private static final int REFRESH_COMPLETE = 0X110;
        private static final int REFRESH_USERNAME = 0X111;
        private static final int GET_USERNAME = 0X112;
        private static final int REFRESH_AVATAR = 0X113;
        private SwipeRefreshLayout mSwipeLayout;
        private ListView contactListView;
        private ArrayAdapter<String> mAdapter;
        private Context mContext;
        private HttpPost httpPost;
        private ImageView ivAvatar;
        private String picName;
        private HttpResponse httpResponse;
        private String str;
        private ArrayList<ContactMessage> cmList;
        private String ContactUrl="http://101.200.32.61/thegroupapp/message_list.php";
        private ContactAdapter cntAdapter;

        private AlertDialog.Builder ab;
        private AlertDialog ad;
        private String changedAvatar;

        private Handler mHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case REFRESH_COMPLETE:
                        mSwipeLayout.setRefreshing(false);
                        if(!str.equals(msg.getData().get("json").toString())) {
                            cmList=new ArrayList<ContactMessage>();
                            str = msg.getData().get("json").toString();
                            try {
                                JSONObject jsonObj = new JSONObject(str);
                                int code=Integer.parseInt(jsonObj.get("code").toString());
                                if(code==200) {
                                    JSONArray jsonArr = jsonObj.getJSONArray("data");
                                    for (int i = 0; i <jsonArr.length(); i++) {
                                        JSONObject obj = jsonArr.getJSONObject(i);
                                        cmList.add(new ContactMessage(obj.get("Id").toString(),obj.get("ReceiverId").toString(),obj.get("ReceiverName").toString(),obj.get("ReceiverAvatar").toString(), (String) obj.get("SendTime").toString(), obj.get("Content").toString()));
                                    }
                                }else{
                                    Toast.makeText(getContext(),"服务器开小差了，请稍后重试",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            cntAdapter = new ContactAdapter(getContext(), R.layout.contact_layout, cmList);
                            contactListView.setAdapter(cntAdapter);
                            contactListView.setSelection(contactListView.getBottom());
                        }
                        break;
                    case REFRESH_USERNAME:
                        Bundle b = msg.getData();
                        String username = b.getString("username");
                        String signature = b.getString("signature");
                        Tools.setName(username);
                        tvOriginUsername.setText(username);
                        tvOriginSignature.setText(signature);
                        break;
                    case GET_USERNAME:
                        str = msg.getData().get("json").toString();
                        try {
                            JSONObject jsonObj = new JSONObject(str);
                            JSONArray jsonarr = jsonObj.getJSONArray("data");
                            JSONObject joInfo = (JSONObject)jsonarr.get(0);
                            String usn = joInfo.getString("Name");
                            String sig = joInfo.getString("SelfIntro");
                            picName = joInfo.getString("Avatar");
                            tvOriginUsername.setText(usn);
                            tvOriginSignature.setText(sig);
                            originUsername = usn;
                            originSignature = sig;
                            ApplicationInfo appInfo = getActivity().getApplicationInfo();
                            int avatarID = getResources().getIdentifier(picName,"drawable",appInfo.packageName);
                            ivAvatar = (ImageView)view.findViewById(R.id.ivAvatar);
                            ivAvatar.setImageResource(avatarID);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case REFRESH_AVATAR:
                        Bundle boundle = msg.getData();
                        String avatarName = boundle.getString("picName");
                        Tools.setAvatar(avatarName);

                        ApplicationInfo appInfo = getActivity().getApplicationInfo();
                        int avatarID = getResources().getIdentifier(avatarName,"drawable",appInfo.packageName);
                        ivAvatar = (ImageView)view.findViewById(R.id.ivAvatar);
                        ivAvatar.setImageResource(avatarID);

                        changedAvatar = avatarName;
                        editUsernameAndSignature(2);
                        break;
                }
            };
        };
        public MyFragment(Context context, String title) {
            this.mContext=context;
            this.mTitle = title;
            this.str="";
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            if(this.mTitle=="私信") {
                view = inflater.inflate(com.thegroup.rebuild.thegroupalpha.R.layout.message_fragment_tablayout, container, false);
                contactListView = (ListView) view.findViewById(com.thegroup.rebuild.thegroupalpha.R.id.pm_listview);
                mSwipeLayout = (SwipeRefreshLayout) view.findViewById(com.thegroup.rebuild.thegroupalpha.R.id.id_swipe_ly);
                mSwipeLayout.setOnRefreshListener(this);
                mSwipeLayout.setColorScheme(new int[]{android.R.color.holo_red_dark, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light});
                Tools.timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        refreshPrivateMessageList();
                    }
                },0,3000);
            }else if(this.mTitle=="圈子"){
                view = inflater.inflate(com.thegroup.rebuild.thegroupalpha.R.layout.group_fragment_tablayout, container, false);
            }else if(this.mTitle=="设置"){
                view = inflater.inflate(com.thegroup.rebuild.thegroupalpha.R.layout.setting_fragment_tablayout, container, false);


                ivAvatar = (ImageView)view.findViewById(R.id.ivAvatar);
                requestUsername();


                ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeAvatar();
                    }
                });

//            获取原来的用户名和个性签名
                tvOriginUsername = (TextView)view.findViewById(R.id.tvUsername);
                originUsername = tvOriginUsername.getText().toString();
                tvOriginSignature = (TextView)view.findViewById(R.id.tvSignature);
                originSignature = tvOriginSignature.getText().toString();

                view.findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditDialog(originUsername,originSignature);
                    }
                });


            }
            return view;
        }
        @Override
        public void onRefresh() {
            refreshPrivateMessageList();
//            mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
        }

        public void refreshPrivateMessageList(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpPost = new HttpPost(ContactUrl);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("operation", "0"));
                    params.add(new BasicNameValuePair("userid", Tools.getId()));

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
    private void showEditDialog(String originUsername,String originSignature){
        LayoutInflater f = LayoutInflater.from(getContext());
        final View textEntryView = f.inflate(R.layout.dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setView(textEntryView);

//        设置为原来的用户名和签名
        tvChangedUsername = (TextView)textEntryView.findViewById(R.id.etUsername);
        tvChangedUsername.setText(originUsername.toCharArray(),0,originUsername.length());
        tvChangedSignature = (TextView)textEntryView.findViewById(R.id.etSignature);
        tvChangedSignature.setText(originSignature.toCharArray(),0,originSignature.length());

        ab.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView t = (TextView)textEntryView.findViewById(R.id.etUsername);
                changedUsername = t.getText().toString();
                TextView tt = (TextView)textEntryView.findViewById(R.id.etSignature);
                changedSignature = tt.getText().toString();


//                发送Bundle数据
                Bundle b = new Bundle();
                b.putString("username",changedUsername);
                b.putString("signature",changedSignature);
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_USERNAME;
                msg.setData(b);
                msg.sendToTarget();

                editUsernameAndSignature(0);
                editUsernameAndSignature(1);
//                requestUsername();

            }
        });

        ab.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ab.show();
    }

    private void requestUsername(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                httpPost = new HttpPost(getUsernameUrl);
                List<NameValuePair>params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("operation","0"));
                params.add(new BasicNameValuePair("userid", Tools.getId()));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String res = EntityUtils.toString(httpResponse.getEntity());
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putSerializable("json",res);
                        msg.setData(b);
                        msg.what = GET_USERNAME;
                        mHandler.sendMessage(msg);
                    }else {
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void editUsernameAndSignature(final int flag){
        new Thread(new Runnable(){
            @Override
            public void run(){
                httpPost = new HttpPost(getUsernameUrl);
                List<NameValuePair>params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("operation","1"));
                params.add(new BasicNameValuePair("userid",Tools.getId()));
                if(flag==0) {
                    params.add(new BasicNameValuePair("target", "0"));
                    params.add(new BasicNameValuePair("value", changedUsername));
                }
                else if(flag==1){
                    params.add(new BasicNameValuePair("target", "1"));
                    params.add(new BasicNameValuePair("value", changedSignature));
                }
                else if(flag==2){
                    params.add(new BasicNameValuePair("target", "2"));
                    params.add(new BasicNameValuePair("value", changedAvatar));
                }
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void changeAvatar(){
        LayoutInflater f = LayoutInflater.from(getContext());
        View vChangeAvatar = f.inflate(R.layout.change_avatar,null);
        ab = new AlertDialog.Builder(getActivity());
        ab.setView(vChangeAvatar);
        final Bundle b = new Bundle();
        vChangeAvatar.findViewById(R.id.ivPic1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00001");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00002");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00003");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00004");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00005");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00006");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00007");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00008");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();
            }
        });
        vChangeAvatar.findViewById(R.id.ivPic9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.putString("picName","p00009");
                Message msg = mHandler.obtainMessage();
                msg.what = REFRESH_AVATAR;
                msg.setData(b);
                msg.sendToTarget();
                ad.dismiss();

            }
        });
        ad = ab.show();
    }
}
