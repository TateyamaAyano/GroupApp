package com.thegroup.rebuild.thegroupalpha.Common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by lyy on 2017/3/12.
 */

public class SecondaryGroup extends Group {
    private int order;
    private double selfAngle;
    private Group parent;
    private String parentType;
    private ArrayList<SecondaryGroup> children;
    public SecondaryGroup(String name,int i,float r){
        this.id=i;
        this.groupName=name;
        this.radius=r;
        this.children=new ArrayList<SecondaryGroup>();
        order=-1;
    }
    public void setOrder(int o){
        order=o;
    }
    public int getOrder(){
        return order;
    }
    public void setSelfAngle(double angle){
        selfAngle=angle;
    }
    public double getSelfAngle(){
        return selfAngle;
    }
    public void move(float x,float y){
        this.xPos+=x;
        this.yPos+=y;
        for(SecondaryGroup sg:this.children){
            sg.move(x,y);
        }
    }


    public void setParent(Group g){
        this.parent=g;
        this.offset=this.parent.getRadius()+2*this.radius;
    }
    public void refreshView(){
        if(this.children.size()>0) {
            for(SecondaryGroup sg:this.children){
                sg.setOrder(this.getChildren().indexOf(sg));
                if(sg.getOrder()>=0){
                    sg.setSelfAngle((Math.PI * 2) / (this.getChildren().size()+1) * (sg.getOrder()+1) + this.getSelfAngle()-Math.PI);
                    sg.xPos=Math.sin(sg.getSelfAngle())*sg.offset*TheGroupView.curScale+this.getX();
                    sg.yPos=Math.cos(sg.getSelfAngle())*sg.offset*TheGroupView.curScale+this.getY();
                }else {
                    Log.e("error>>>>>>>>>","wrong order"+sg.getOrder());
                }
                sg.refreshView();
            }
        }
    }
    public Group getParent(){
        if(parent!=null){
            return parent;
        }else{
            return null;
        }
    }
    public void addChild(SecondaryGroup sg){
        children.add(sg);
        sg.setParent(this);
    }

    public ArrayList<SecondaryGroup> getChildren() {
        return children;
    }
    public void draw(Canvas canvas, Paint p, float holderWidth, float holderHeight, float scaleFactor, float curScale){
        float x =(float) (this.getX() - holderWidth / 2) * scaleFactor + holderWidth / 2;
        float y =(float) (this.getY() - holderHeight / 2) * scaleFactor + holderHeight / 2;
        float r = (float) this.getRadius() * curScale;
        float px=(float) (this.getParent().getX() - holderWidth / 2) * scaleFactor + holderWidth / 2;
        float py=(float) (this.getParent().getY() - holderHeight / 2) * scaleFactor + holderHeight / 2;
        p.setColor(Color.rgb(32,198,208));
        canvas.drawLine(px,py,x,y,p);
        if(this.children.size()>0){
            for(SecondaryGroup g:this.children){
                g.draw(canvas,p,holderWidth,holderHeight,scaleFactor,curScale);
            }
        }
        p.setColor(Color.rgb(32,198,208));
        canvas.drawCircle(x, y, r, p);
        p.setColor(Color.BLACK);
        canvas.drawText(this.getGroupName(), x, y, p);
    }
}
