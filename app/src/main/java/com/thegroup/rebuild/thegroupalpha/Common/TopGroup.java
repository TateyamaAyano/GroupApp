package com.thegroup.rebuild.thegroupalpha.Common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.ScaleGestureDetector;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by lyy on 2017/3/12.
 */

public class TopGroup extends Group {
    private ArrayList<SecondaryGroup> children;
    public TopGroup(String name,int i,float r,float x,float y){
        this.xPos=x;
        this.yPos=y;
        this.id=i;
        this.groupName=name;
        this.radius=r;
        this.selfAngle=-1;
        this.children=new ArrayList<SecondaryGroup>();
    }
    public void addChild(SecondaryGroup sg){
        children.add(sg);
        sg.setParent(this);
    }
    public ArrayList<SecondaryGroup> getChildren(){
        return children;
    }
    public void move(float x,float y){
        this.xPos+=x;
        this.yPos+=y;
        for(SecondaryGroup sg:this.children){
            sg.move(x,y);
        }
    }
    public void refreshView(){
        for(SecondaryGroup sg:this.children){
            sg.setOrder(this.getChildren().indexOf(sg));
            if(sg.getOrder()>=0){
                sg.setSelfAngle((Math.PI*2)/this.getChildren().size()*sg.getOrder());
                sg.xPos=Math.sin(sg.getSelfAngle())*sg.offset*TheGroupView.curScale+this.getX();
                sg.yPos=Math.cos(sg.getSelfAngle())*sg.offset*TheGroupView.curScale+this.getY();
            }else {
                Log.e("error>>>>>>>>>","wrong order"+sg.getOrder());
            }
            sg.refreshView();
        }
    }
    public void draw(Canvas canvas, Paint p, float holderWidth, float holderHeight, float scaleFactor, float curScale){
        float x =(float) (this.getX() - holderWidth / 2) * scaleFactor + holderWidth / 2;
        float y =(float) (this.getY() - holderHeight / 2) * scaleFactor + holderHeight / 2;
        float r = (float) this.getRadius() * curScale;
        if(this.children.size()>0){
            for(SecondaryGroup g:this.children){
                ((SecondaryGroup)g).draw(canvas,p,holderWidth,holderHeight,scaleFactor,curScale);
            }
        }
        p.setColor(Color.rgb(32,198,208));
        canvas.drawCircle(x, y, r, p);
        p.setColor(Color.BLACK);
        canvas.drawText(this.getGroupName(), x, y, p);
    }
}
