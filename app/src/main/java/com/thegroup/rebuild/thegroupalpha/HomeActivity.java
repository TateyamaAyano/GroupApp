package com.thegroup.rebuild.thegroupalpha;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.thegroup.rebuild.thegroupalpha.Common.GroupMessage;
import com.thegroup.rebuild.thegroupalpha.Common.NoScrollViewPager;
import com.thegroup.rebuild.thegroupalpha.Common.Tools;
import com.thegroup.rebuild.thegroupalpha.R;

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

public class HomeActivity extends AppCompatActivity {
    private TabLayout home_tablayout;
    private ActionBar mActionBar;
    private ViewPager home_viewpager;
    private HttpPost httpPost;
    private HttpResponse httpResponse;
    private String userInfoUrl ="http://101.200.32.61/thegroupapp/personal_info.php";
    private final int USER_INFO_REQUEST=2;
    private Context mAppContext;
    private String id;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case USER_INFO_REQUEST:
                    String str = msg.getData().get("json").toString();
                    Log.d("response",str);
                    try {
                        JSONObject jsonObject=new JSONObject(str);
                        int code=Integer.parseInt(jsonObject.get("code").toString());
                        if(code==200){
                            JSONArray jsonArr = jsonObject.getJSONArray("data");
                            JSONObject info=jsonArr.getJSONObject(0);
                            Tools.setName(info.get("Name").toString());
                            Tools.setAvatar(info.get("Avatar").toString());
                        }else{
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.thegroup.rebuild.thegroupalpha.R.layout.activity_home);
        mAppContext=getApplicationContext();
        home_tablayout = (TabLayout) findViewById(com.thegroup.rebuild.thegroupalpha.R.id.tabs);
        this.setTitle("圈子");
        List<String> titles = new ArrayList<String>();
        List<Fragment> fragments = new ArrayList<Fragment>();
        titles.add("圈子");
        titles.add("私信");
        titles.add("设置");
        home_tablayout.addTab(home_tablayout.newTab().setText(titles.get(0)));
        home_tablayout.addTab(home_tablayout.newTab().setText(titles.get(1)));
        home_tablayout.addTab(home_tablayout.newTab().setText(titles.get(2)));
        home_tablayout.setTabMode(TabLayout.MODE_FIXED);
        mActionBar = getSupportActionBar();
        home_viewpager=(NoScrollViewPager)findViewById(R.id.viewpager);
        home_viewpager.setOffscreenPageLimit(2);
        // add the custom view to the action bar
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        for (int i = 0; i < 3; i++) {
            MyFragment fragment = new MyFragment(this,titles.get(i));
            fragments.add(fragment);
        }
        final ViewPager viewPager = (ViewPager) findViewById(com.thegroup.rebuild.thegroupalpha.R.id.viewpager);
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(myAdapter);
        home_tablayout.setupWithViewPager(viewPager);
        home_tablayout.setTabsFromPagerAdapter(myAdapter);

        home_tablayout.getTabAt(0).setIcon(R.drawable.guide_selected);
        home_tablayout.getTabAt(1).setIcon(R.drawable.message);
        home_tablayout.getTabAt(2).setIcon(R.drawable.setting);

        home_tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                home_tablayout.getTabAt(0).setIcon(R.drawable.guide);
                home_tablayout.getTabAt(1).setIcon(R.drawable.message);
                home_tablayout.getTabAt(2).setIcon(R.drawable.setting);
                if(tab.getPosition()==0){
                    tab.setIcon(R.drawable.guide_selected);
                    mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_SHOW_TITLE);
                    mActionBar.setTitle("圈子");
                    viewPager.setCurrentItem(0);
                }else if(tab.getPosition()==1){
                    tab.setIcon(R.drawable.message_selected);
                    mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_SHOW_TITLE);
                    mActionBar.setTitle("私信");
                    viewPager.setCurrentItem(1);
                }else if(tab.getPosition()==2){
                    mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_SHOW_TITLE);
                    mActionBar.setTitle("设置");
                    tab.setIcon(R.drawable.setting_selected);
                    viewPager.setCurrentItem(2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                String MAC= Tools.getMac(getApplicationContext());
                httpPost=new HttpPost(userInfoUrl);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("operation", "0"));
                params.add(new BasicNameValuePair("userid", Tools.getId()));
                try{
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        String  res= EntityUtils.toString(httpResponse.getEntity());
                        Message msg=new Message();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("json",res);
                        msg.setData(bundle);
                        msg.what=USER_INFO_REQUEST;
                        mHandler.sendMessage(msg);
                    }else{
                        Toast.makeText(getApplication(),"网络连接出错 code:"+httpResponse.getStatusLine().getStatusCode(),Toast.LENGTH_SHORT).show();
                        System.exit(0);
                    }
                }catch (Exception e){
//                    Toast.makeText(getApplication(),"网络连接出错 code:"+httpResponse.getStatusLine().getStatusCode(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }).start();

    }
}
