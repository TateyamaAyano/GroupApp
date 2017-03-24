package com.thegroup.rebuild.thegroupalpha.Common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by lyy on 2017/2/24.
 */

public abstract class Group extends TreeNode{
    protected double xPos;
    protected double yPos;
    protected String groupName;
    protected double offset;
    protected double selfAngle;
    protected int id;
    protected int userNum;
    protected double radius;
    private Group parent;
    private ArrayList<Group> children;

    public Group(){
        parent=null;
        children=new ArrayList<Group>();
    }
    public int getId(){return  id;}
    public double getX(){
        return xPos;
    }
    public double getY(){
        return yPos;
    }
    public String getGroupName(){
        return groupName;
    }
    public double getRadius(){
        return radius;
    }
    public void setX(double x){
        xPos=x;
    }
    public void setY(double y){
        yPos=y;
    }
    public double getSelfAngle(){
        return selfAngle;
    }
    public void setRadius(float r){
        radius=r;
    }
    public Group getParent(){
        return parent;
    }
    public void setParent(Group g){
        parent=g;
    }
    public void addChild(Group g){
        children.add(g);
        g.setParent(this);
    }

    public void draw(Canvas canvas, Paint p, float holderWidth, float holderHeight, float scaleFactor, float curScale){
        float x =(float) (this.getX() - holderWidth / 2) * scaleFactor + holderWidth / 2;
        float y =(float) (this.getY() - holderHeight / 2) * scaleFactor + holderHeight / 2;
        float r = (float) this.getRadius() * curScale;
        canvas.drawCircle(x, y, r, p);
        canvas.drawText(this.getGroupName(), x, y, p);
        if(this.children.size()>0){
            for(Group g:this.children){
                ((SecondaryGroup)g).draw(canvas,p,holderWidth,holderHeight,scaleFactor,curScale);
            }
        }
    }
}
