package com.thegroup.rebuild.thegroupalpha.Common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thegroup.rebuild.thegroupalpha.ChartRoom.ChatRoom;

import java.util.Vector;


/**
 * Created by lyy on 2017/2/24.
 */

public class TheGroupView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder=null; //控制对象
    private Vector<Group> groupArray=new Vector<Group>();
    private ScaleGestureDetector mScaleGestureDetector;
    private float mLastTouchX=0f;
    private float mLastTouchY=0f;
    private float defaultScale=100.0f;
    public static float curScale=100.0f;
    private float scaleFactor=1.0f;
    private TopGroup hardWareGroup;

    public TheGroupView(Context context, AttributeSet attr) {
        super(context,attr);
        // TODO Auto-generated constructor stub
        holder=getHolder();
        holder.addCallback(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float cur = detector.getCurrentSpan();
                float pre = detector.getPreviousSpan();
                if(curScale*scaleFactor*cur/pre>=10&&curScale*scaleFactor*cur/pre<=200){
                    scaleFactor *=(cur/pre);
                    curScale = scaleFactor*defaultScale;
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                detector.getCurrentSpan();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
        //测试数据



        hardWareGroup=new TopGroup("Hardware",1,2.0f,400.0f,200.0f);
        SecondaryGroup GPUGroup=new SecondaryGroup("GPU",1001,1.3f);
        SecondaryGroup CPUGroup=new SecondaryGroup("CPU",1002,1.3f);
        SecondaryGroup SOCGroup=new SecondaryGroup("SOC",1003,1.0f);
        SecondaryGroup peripheralGroup=new SecondaryGroup("peripheral",1004,1.6f);

        SecondaryGroup mouseGroup=new SecondaryGroup("mouse",1005,0.6f);
        SecondaryGroup keyboardGroup=new SecondaryGroup("keyboard",1006,0.8f);
        SecondaryGroup earphoneGroup=new SecondaryGroup("earphone",1007,0.8f);
        SecondaryGroup screenGroup=new SecondaryGroup("screen",1008,0.6f);

        SecondaryGroup AMDCPUGroup=new SecondaryGroup("AMD",1009,0.5f);
        SecondaryGroup IntelCPUGroup=new SecondaryGroup("Intel",1010,0.8f);

        SecondaryGroup AMDGPUGroup=new SecondaryGroup("AMD",1011,0.7f);
        SecondaryGroup NvidiaGroup=new SecondaryGroup("Nvidia",1012,0.8f);
        SecondaryGroup IntelGPUGroup=new SecondaryGroup("Intel",1013,0.4f);



        hardWareGroup.addChild(GPUGroup);
        hardWareGroup.addChild(CPUGroup);
        hardWareGroup.addChild(SOCGroup);
        hardWareGroup.addChild(peripheralGroup);

        peripheralGroup.addChild(mouseGroup);
        peripheralGroup.addChild(keyboardGroup);
        peripheralGroup.addChild(earphoneGroup);
        peripheralGroup.addChild(screenGroup);

        CPUGroup.addChild(AMDCPUGroup);
        CPUGroup.addChild(IntelCPUGroup);

        GPUGroup.addChild(AMDGPUGroup);
        GPUGroup.addChild(NvidiaGroup);
        GPUGroup.addChild(IntelGPUGroup);


        groupArray.add(hardWareGroup);
        groupArray.add(GPUGroup);
        groupArray.add(CPUGroup);
        groupArray.add(SOCGroup);
        groupArray.add(peripheralGroup);
        groupArray.add(mouseGroup);
        groupArray.add(keyboardGroup);
        groupArray.add(earphoneGroup);
        groupArray.add(screenGroup);
        groupArray.add(AMDCPUGroup);
        groupArray.add(IntelCPUGroup);
        groupArray.add(AMDGPUGroup);
        groupArray.add(NvidiaGroup);
        groupArray.add(IntelGPUGroup);

        hardWareGroup.refreshView();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        new Thread(new MyLoop()).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    public void doDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);//这里是绘制背景
        Paint p=new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(28.0f);
        float holderWidth=holder.getSurfaceFrame().width();
        float holderHeight=holder.getSurfaceFrame().height();
        hardWareGroup.draw(canvas,p,holderWidth,holderHeight,scaleFactor,curScale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        mScaleGestureDetector.onTouchEvent(event);

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                float downX=event.getX();
                float downY=event.getY();
                mLastTouchX=downX;
                mLastTouchY=downY;
                break;
            case MotionEvent.ACTION_MOVE:
                float thisX = event.getX();
                float thisY = event.getY();
                if(!mScaleGestureDetector.isInProgress()) {
                    float moveX = thisX - mLastTouchX;
                    float moveY = thisY - mLastTouchY;

                    hardWareGroup.move(moveX,moveY);

                }
                mLastTouchX = thisX;
                mLastTouchY = thisY;
                break;
            case MotionEvent.ACTION_UP:
                float upX=event.getX();
                float upY=event.getY();
                int id=0;
                for(Group g:groupArray){
                    float x =(float) (g.getX() - holder.getSurfaceFrame().width() / 2) * scaleFactor +holder.getSurfaceFrame().width() / 2;
                    float y =(float) (g.getY() - holder.getSurfaceFrame().height() / 2) * scaleFactor + holder.getSurfaceFrame().height() / 2;
                    float r = (float) g.getRadius() * curScale;
                    double d_mul_d=(y-upY)*(y-upY)+(x-upX)*(x-upX);
                    Log.d("d_mul_d",""+d_mul_d);
                    if(d_mul_d<r*r) {
                        id=g.getId();
                        break;
                    }
                }
                if(id!=0) {
                    Intent intent = new Intent(getContext(), ChatRoom.class);
                    intent.putExtra("groupId", ""+id);
                    String name="";
                    for(Group g: groupArray){
                        if(g.getId()==id)
                            name=g.getGroupName();
                    }
                    intent.putExtra("groupName", ""+name);

                    getContext().startActivity(intent);
                }
                break;
        }
        return true;
    }

    class MyLoop implements Runnable{
        //熟悉游戏编程的应该很面熟吧，主循环
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true){
                try{
                    Canvas c=holder.lockCanvas();
                    doDraw(c);
                    holder.unlockCanvasAndPost(c);
                    Thread.sleep(20);
                }catch(Exception e){

                }
            }
        }

    }

}