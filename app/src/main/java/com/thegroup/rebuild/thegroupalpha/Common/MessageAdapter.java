package com.thegroup.rebuild.thegroupalpha.Common;

import android.content.Context;
import android.content.Intent;
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

public class MessageAdapter extends ArrayAdapter<GroupMessage> {
    LayoutInflater inflater;
    private ArrayList<GroupMessage> msgList;
    public MessageAdapter (Context context, int textViewResourceId,
                           ArrayList<GroupMessage> list)
    {
        super(context, textViewResourceId, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        msgList=list;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.message_layout,null);
        }

        final GroupMessage gm = getItem(position);
        //获取item layout 中的元素，添加事件等等。
        Context context = getContext();
        //头像
        int avatar = context.getResources().getIdentifier(gm.getSendAvatar().toString(), "drawable", context.getPackageName());
        ((ImageView)view.findViewById(R.id.userPic)).setImageResource(avatar);
        ((ImageView)view.findViewById(R.id.userPic)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId=Tools.getId();
                String recId=gm.getSendId();
                String recName=gm.getSendName();
                if(!sendId.equals(recId)) {
                    Intent intent = new Intent(getContext(), ChatPrivate.class);
                    intent.putExtra("chatId", "-1");
                    intent.putExtra("recId", recId);
                    intent.putExtra("recName", recName);

                    getContext().startActivity(intent);
                }
            }
        });
        ((TextView) view.findViewById(R.id.sendId)).setText(gm.getSendName().toString());
        ((TextView)view.findViewById(R.id.msg)).setText(gm.getMsg());

        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 210);

        view.setLayoutParams(lp);
                /*以上为新增部分*/
        return view;
    }
}
