package com.thegroup.rebuild.thegroupalpha.Common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thegroup.rebuild.thegroupalpha.ChartRoom.ChatPrivate;
import com.thegroup.rebuild.thegroupalpha.R;

import java.util.ArrayList;

/**
 * Created by lyy on 2017/3/15.
 */

public class ContactAdapter extends ArrayAdapter<ContactMessage> {
    LayoutInflater inflater;
    private ArrayList<ContactMessage> cmList;

    public ContactAdapter(Context context, int textViewResourceId,ArrayList<ContactMessage> list)
    {
        super(context, textViewResourceId, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cmList=list;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.contact_layout,null);
        }

        final ContactMessage cm = getItem(position);
        //获取item layout 中的元素，添加事件等等。
        Context context = getContext();
        //头像
        int avatar = context.getResources().getIdentifier(cm.getReceiveravatar().toString(), "drawable", context.getPackageName());
        ((ImageView)view.findViewById(R.id.userPic)).setImageResource(avatar);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId=Tools.getId();
                String recName=cm.getRecervername();
                Intent intent=new Intent(getContext(), ChatPrivate.class);
                intent.putExtra("chatId",cm.getChatGroupId());
                intent.putExtra("recName",recName);

                Log.d("chatid >>>>>>>>>>>>>>>>",cm.getChatGroupId()+">>>>>>>>>>>>>>>>");
                Tools.setCurrReceiveId(cm.getReceiverId());
                getContext().startActivity(intent);

            }
        });
        ((TextView)view.findViewById(R.id.sendId)).setText(cm.getRecervername().toString());
        ((TextView)view.findViewById(R.id.sendTime)).setText(cm.getSendTime().toString());
        ((TextView)view.findViewById(R.id.msg)).setText(cm.getMsg());

        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 210);

        view.setLayoutParams(lp);
                /*以上为新增部分*/
        return view;
    }
}
