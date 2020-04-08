package com.check.viewdraghelper.arcloadview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.DecelerateInterpolator;

import com.check.viewdraghelper.utils.Utils;

/**
 * Create By 刘胡来
 * Create Date 2020/4/5
 * Sensetime@Copyright
 * Des:
 */
public class ArcSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private DrawThread drawThread;
    private Context context;

    private Paint outCirclePaint;
    private Paint animPaint;
    private Paint indexPaint;

    private Path outCirclePath;
    private float threeAngle;
    private float threeAngle2;
    private float threeAngle3;
    private float outCirlceRadius;
    private float outIndexCircleRadius;    //外圈摆动圆弧半径
    private float swingAngle;            //摆动圆弧转动角度
    private float outCirlceScaleBigRadius;  //外环放大动画半径
    private float outCircleOriginRadius;   //原始半径
    private Path midCircleMarkPath;
    private float innerCircleRadius;
    private RectF outArcRect;
    private int width;
    private int height;
    private int innerCircleWidth;
    private int graduateLineLength; //刻度线的长度
    private int innreCirlceGraduateRadius;  //内圆刻度半径

    private PathMeasure outSwingPathMeasure;    //在外圈摆动的路径测试器
    private Path outSwingPath;
    private RectF outSwingArcRect;
    private int outSwingArcColor;
    private int outMid2SegSwingArcColor;
    private int outCircleKeduColor;

    private Path outCirclePointPath;        //外圈摆动的小圆点路径，由outSwingPathMeasure测量给出
    private float[] outCirclePointPos = new float[2];
    private float[] tan = new float[2];
    private boolean isOriginStatus = true;
    private RectF middleCirlceSwingRect;  //中圆环摆动的弧形矩形框
    private RectF innerCirlceSwingRect;  //内圆环摆动的弧形矩形框
    private int midCirlceColor;
    private int swingLingOffSet;
    private Path line1AnimPath;
    private Path line1Path;
    private boolean lineOriginStatus = true;
    private Path line2AnimPath;
    private Path line2Path;
    private PathMeasure line1Measure;
    private PathMeasure line2Measure;
    private RectF midMarkRect;
    private float midMarkRadius;
    private Path threePointPath;
    private RectF threePointRect;

    public ArcSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ArcSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ArcSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        this.context = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        drawThread = new DrawThread(holder);


        outCirclePaint = new Paint();
        outCirclePaint.setAntiAlias(true);
        outCirclePaint.setDither(true);//防抖动
        outCirclePaint.setStyle(Paint.Style.STROKE);
        outCirclePaint.setColor(Color.GREEN);
        outCirclePaint.setStrokeWidth(2);

        animPaint = new Paint();
        animPaint.setAntiAlias(true);
        animPaint.setDither(true);//防抖动
        animPaint.setStyle(Paint.Style.STROKE);
        animPaint.setColor(Color.WHITE);
        animPaint.setStrokeWidth(Utils.dp2px(context,3));

        indexPaint = new Paint();
        indexPaint.setAntiAlias(true);
        indexPaint.setDither(true);//防抖动
        indexPaint.setStyle(Paint.Style.STROKE);
        indexPaint.setColor(Color.BLUE);
        indexPaint.setStrokeWidth(Utils.dp2px(context,1));

        threePointPath = new Path();
        threePointRect = new RectF();

        threeAngle = 60;
        threeAngle2 = 50;
        threeAngle3 = 40;
        outCirlceRadius = Utils.dp2px(context,110);

        outCirlceScaleBigRadius = Utils.dp2px(context,130);
        outCircleOriginRadius = outCirlceRadius;
        innerCircleWidth = Utils.dp2px(context,10);
        innerCircleRadius = Utils.dp2px(context,90);
        innreCirlceGraduateRadius = (int)(innerCircleRadius / 1.8);
        graduateLineLength = Utils.dp2px(context,20);
        outArcRect = new RectF();
        outCirclePath = new Path();


        outIndexCircleRadius = outCirlceRadius;
        swingAngle = -80;

        outSwingPathMeasure = new PathMeasure();
        outCirclePointPath = new Path();
        swingLingOffSet = Utils.dp2px(context,20);

        line1AnimPath = new Path();
        line1Path = new Path();
        line1Measure = new PathMeasure();

        line2AnimPath = new Path();
        line2Path = new Path();
        line2Measure = new PathMeasure();

        midCircleMarkPath = new Path();
        midMarkRect = new RectF();

        outSwingArcRect = new RectF();
        outSwingPath = new Path();
        outSwingArcColor = Color.parseColor("#75C8BD");
        outMid2SegSwingArcColor = Color.parseColor("#BDE091");
        midCirlceColor = Color.parseColor("#75C8BD");
        outCircleKeduColor = Color.parseColor("#5E7379");

        startAnim();

    }

    private void startAnim(){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                startThreePointAnim();
//                startOutCirlceGraduateLineAnim();
//                startSwingAnim();
//                startOutCirclePointSwingAnim();
//                startNotchLineAnim();
//                startMidMarkArcInAnim();
            }
        },1000);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("188----------:"+w+" h:"+h +"widrth:"+getWidth());
        width = getWidth();
        height = getHeight();

        //第一条线的起始点坐标
        float startX  = (float)(width / 2) + (float) Math.sin(arc2Angle(10)) * (innerCircleRadius * 5 + graduateLineLength / 2.0f);
        float startY = (float)(height / 2) - (float) Math.cos(arc2Angle(10)) * (innerCircleRadius * 5 + graduateLineLength / 2.0f );
        float stopX1 = (float)(width / 2) + (float) Math.sin(arc2Angle(10)) * (innerCircleRadius - graduateLineLength / 2.0f);
        float stopY1 = (float)(height / 2) - (float) Math.cos(arc2Angle(10)) * (innerCircleRadius - graduateLineLength / 2.0f);

        line1Path.moveTo(startX,startY);
        line1Path.lineTo(stopX1,stopY1);
        line1Measure.setPath(line1Path,false);

        //第二条线的起始点坐标
        float startX2  = (float)(width / 2) + (float) Math.sin(arc2Angle(350)) * (innerCircleRadius * 5 + graduateLineLength / 2.0f);
        float startY2 = (float)(height / 2) - (float) Math.cos(arc2Angle(350)) * (innerCircleRadius * 5 + graduateLineLength / 2.0f );
        float stopX2 = (float)(width / 2) + (float) Math.sin(arc2Angle(350)) * (innerCircleRadius - graduateLineLength / 2.0f);
        float stopY2 = (float)(height / 2) - (float) Math.cos(arc2Angle(350)) * (innerCircleRadius - graduateLineLength / 2.0f);

        line2Path.moveTo(startX2,startY2);
        line2Path.lineTo(stopX2,stopY2);
        line2Measure.setPath(line2Path,false);

        outArcRect.left = (width - innerCircleRadius * 2) / 2;
        outArcRect.top = (height - innerCircleRadius * 2) / 2;
        outArcRect.right = (width - innerCircleRadius * 2) / 2 + innerCircleRadius * 2;
        outArcRect.bottom = (height - innerCircleRadius * 2) / 2 + innerCircleRadius * 2;
        outCirclePath.addArc(outArcRect,-80,340);

        midMarkRadius = innerCircleRadius * 3.0f;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread.setRun(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.setRun(false);
    }

    @Override
    public void run() {

    }

    /**
     * 画中环缺口线
     * @param canvas
     */
    private void drawMidNotchLine(Canvas canvas){
        canvas.drawPath(line1AnimPath,indexPaint);
        canvas.drawPath(line2AnimPath,indexPaint);

    }

    /**
     * 画中圆环与缺口刻度线
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas){
        outCirclePaint.setStrokeWidth(2);
        outCirclePaint.setColor(midCirlceColor);

        //画中圆环--带缺口
        canvas.drawPath(outCirclePath,outCirclePaint);

        //画中圆环中间标记弧线
        midMarkRect.left = (width - midMarkRadius * 2) / 2;
        midMarkRect.top = (height - midMarkRadius * 2) / 2;
        midMarkRect.right = (width - midMarkRadius * 2) / 2 + midMarkRadius * 2;
        midMarkRect.bottom = (height - midMarkRadius * 2) / 2 + midMarkRadius * 2;
        midCircleMarkPath.addArc(midMarkRect,120,45);
        outCirclePaint.setColor(Color.WHITE);
        outCirclePaint.setStrokeWidth(15);
        canvas.drawPath(midCircleMarkPath,outCirclePaint);

        midCircleMarkPath.addArc(midMarkRect,15,45);
        canvas.drawPath(midCircleMarkPath,outCirclePaint);


//        float startX = (float)(width / 2) + (float) Math.sin(arc2Angle(10)) * (innerCircleRadius + graduateLineLength / 2.0f);
//        float startY = (float)(height / 2) - (float) Math.cos(arc2Angle(10)) * (innerCircleRadius + graduateLineLength / 2.0f);
//
//        float stopX = (float)(width / 2) + (float) Math.sin(arc2Angle(10)) * (innerCircleRadius - graduateLineLength / 2.0f);
//        float stopY = (float)(height / 2) - (float) Math.cos(arc2Angle(10)) * (innerCircleRadius - graduateLineLength / 2.0f);
//        canvas.drawLine(startX, startY, stopX, stopY, animPaint);
//
//        float startX1 = (float)(width / 2) + (float) Math.sin(arc2Angle(350)) * (innerCircleRadius + graduateLineLength / 2.0f);
//        float startY1 = (float)(height / 2) - (float) Math.cos(arc2Angle(350)) * (innerCircleRadius + graduateLineLength / 2.0f);
//
//        float stopX1 = (float)(width / 2) + (float) Math.sin(arc2Angle(350)) * (innerCircleRadius - graduateLineLength / 2.0f);
//        float stopY1 = (float)(height / 2) - (float) Math.cos(arc2Angle(350)) * (innerCircleRadius - graduateLineLength / 2.0f);
//        canvas.drawLine(startX1, startY1, stopX1, stopY1, animPaint);
    }

    /**
     * 启动中间圆弧入场动画
     */
    private void startMidMarkArcInAnim(){
        ValueAnimator mAnimator=ValueAnimator.ofFloat(midMarkRadius,innerCircleRadius);
        mAnimator.setDuration(2000);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                midCircleMarkPath.reset();
                midMarkRadius = (float) mAnimator.getAnimatedValue();
                //invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 启动中圆环缺口线动画
     */
    private void startNotchLineAnim(){
        ValueAnimator mAnimator=ValueAnimator.ofFloat(0,line1Measure.getLength());
        mAnimator.setDuration(2000);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float start = (float) mAnimator.getAnimatedValue();
                float end = start + line1Measure.getLength() / 20;
                if(end > line1Measure.getLength()){
                    end= line1Measure.getLength();
                }
                if((line1Measure.getLength() - start) >= line1Measure.getLength() / 20){
                    line1AnimPath.reset();
                }
                //从 原始path中取出一段 放入目的path中（添加），并不会删除目的path中以前的数据
                line1Measure.getSegment(start,end,line1AnimPath,true);

            }
        });
        mAnimator.start();

        ValueAnimator mAnimator2=ValueAnimator.ofFloat(0,line2Measure.getLength());
        mAnimator2.setDuration(2000);
        mAnimator2.setInterpolator(new DecelerateInterpolator());
        mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float start = (float) mAnimator.getAnimatedValue();
                float end = start + line2Measure.getLength() / 20;
                if(end > line2Measure.getLength()){
                    end= line2Measure.getLength();
                }
                if((line2Measure.getLength() - start) >= line2Measure.getLength() / 20){
                    line2AnimPath.reset();
                }
                line2Measure.getSegment(start,end,line2AnimPath,true);

            }
        });
        mAnimator2.start();

    }


    /**
     * 画内外圆环的刻度线
     * @param canvas
     */
    private void drawCircleGraduate(Canvas canvas){

        outCirclePaint.setStrokeWidth(2);
        outCirclePaint.setStyle(Paint.Style.STROKE);
        //画外圆刻度圆环
        float evenryDegrees = arc2Angle(6.0f);
        outCirclePaint.setColor(outCircleKeduColor);
        for (int i = 0; i < 60; i++) {
            float degrees = i * evenryDegrees;
            if (i >= 0 && i < 3 ) {
                continue;
            }
            if(i > 57 && i < 60){
                continue;
            }
            float startX = width / 2.0f + (float) Math.sin(degrees) * outCirlceRadius;
            float startY = height / 2.0f - (float) Math.cos(degrees) * outCirlceRadius;

            float stopX = width / 2.0f + (float) Math.sin(degrees) * (outCirlceRadius + graduateLineLength);
            float stopY = height / 2.0f - (float) Math.cos(degrees) * (outCirlceRadius + graduateLineLength);

            canvas.drawLine(startX, startY, stopX, stopY, outCirclePaint);
        }

        //画内圆刻度圆环
        for (int i = 0; i < 90; i++) {
            float degrees = i * evenryDegrees;
            float startX = width / 2.0f + (float) Math.sin(degrees) * innreCirlceGraduateRadius;
            float startY = height / 2.0f - (float) Math.cos(degrees) * innreCirlceGraduateRadius;

            float stopX = width / 2.0f + (float) Math.sin(degrees) * (innreCirlceGraduateRadius + (int)(graduateLineLength / 1.5));
            float stopY = height / 2.0f - (float) Math.cos(degrees) * (innreCirlceGraduateRadius + (int)(graduateLineLength / 1.5));

            canvas.drawLine(startX, startY, stopX, stopY, outCirclePaint);
        }


    }

    /**
     * 画3个点的小点矩形
     * @param canvas
     */
    private void drawPointRect(Canvas canvas){

        //三个点的弧形矩形框
        outCirclePaint.setStyle(Paint.Style.FILL);
        outCirclePaint.setStrokeWidth(3);
        threePointRect.left = outArcRect.left + innerCircleRadius + (float)(innerCircleRadius * Math.cos(arc2Angle(threeAngle)));
        threePointRect.top = outArcRect.top + innerCircleRadius +(float)(innerCircleRadius * Math.sin(arc2Angle(threeAngle)));
        threePointRect.right = outArcRect.left + innerCircleRadius + (float)(innerCircleRadius * Math.cos(arc2Angle(threeAngle))) + innerCircleWidth;
        threePointRect.bottom = outArcRect.top + innerCircleRadius + (float)(innerCircleRadius * Math.sin(arc2Angle(threeAngle))) + innerCircleWidth;
        threePointPath.reset();
        threePointPath.addRect(threePointRect,Path.Direction.CCW);
        canvas.drawPath(threePointPath,outCirclePaint);



        threePointRect.left = outArcRect.left + innerCircleRadius + (float)(innerCircleRadius * Math.cos(arc2Angle(threeAngle2)));
        threePointRect.top = outArcRect.top + innerCircleRadius +(float)(innerCircleRadius * Math.sin(arc2Angle(threeAngle2)));
        threePointRect.right = outArcRect.left + innerCircleRadius + (float)(innerCircleRadius * Math.cos(arc2Angle(threeAngle2))) + (float)(innerCircleWidth / 1.5);
        threePointRect.bottom = outArcRect.top + innerCircleRadius + (float)(innerCircleRadius * Math.sin(arc2Angle(threeAngle2))) + (float)(innerCircleWidth / 1.5);
        threePointPath.reset();
        threePointPath.addRect(threePointRect,Path.Direction.CCW);
        canvas.drawPath(threePointPath,outCirclePaint);


        threePointRect.left = outArcRect.left + innerCircleRadius + (float)(innerCircleRadius * Math.cos(arc2Angle(threeAngle3)));
        threePointRect.top = outArcRect.top + innerCircleRadius +(float)(innerCircleRadius * Math.sin(arc2Angle(threeAngle3)));
        threePointRect.right = outArcRect.left + innerCircleRadius + (float)(innerCircleRadius * Math.cos(arc2Angle(threeAngle3))) + (float)(innerCircleWidth / 2);
        threePointRect.bottom = outArcRect.top + innerCircleRadius + (float)(innerCircleRadius * Math.sin(arc2Angle(threeAngle3))) + (float)(innerCircleWidth / 2);
        threePointPath.reset();
        threePointPath.addRect(threePointRect,Path.Direction.CCW);
        canvas.drawPath(threePointPath,outCirclePaint);
    }

    /**
     * 画横跨内中外三圆环的摆动圆弧
     * 第一段圆环半径与外环半径相等，第二段圆环半径与中环半径相等，第三段圆环半径与内环半径相等
     * @param canvas
     */
    private void drawIndexArc(Canvas canvas){

        //画在外环摆动的圆环
        indexPaint.setColor(outSwingArcColor);
        indexPaint.setStrokeWidth(Utils.dp2px(context,1));

        outSwingArcRect.left = (width - outIndexCircleRadius * 2) / 2;
        outSwingArcRect.top = (height - outIndexCircleRadius * 2) / 2;
        outSwingArcRect.right = (width - outIndexCircleRadius * 2) / 2 + outIndexCircleRadius * 2;
        outSwingArcRect.bottom = (height - outIndexCircleRadius * 2) / 2 + outIndexCircleRadius * 2;
        canvas.drawArc(outSwingArcRect,swingAngle,60,false,indexPaint);
        //canvas.drawPath(swingPath,indexPaint);


        outSwingArcRect.left = (width - (outIndexCircleRadius - 20) * 2) / 2;
        outSwingArcRect.top = (height - (outIndexCircleRadius - 20) * 2) / 2;
        outSwingArcRect.right = (width - (outIndexCircleRadius - 20) * 2) / 2 + (outIndexCircleRadius - 20) * 2;
        outSwingArcRect.bottom = (height - (outIndexCircleRadius - 20) * 2) / 2 + (outIndexCircleRadius - 20) * 2;

        indexPaint.setColor(Color.WHITE);
        indexPaint.setStrokeWidth(Utils.dp2px(context,5));

        //Path swingPath = new Path();
        outSwingPath.addArc(outSwingArcRect,swingAngle,60);
        canvas.drawArc(outSwingArcRect,swingAngle,10,false,indexPaint);

        //画在swingpath 滚动的路径动画
        outSwingPathMeasure.setPath(outSwingPath,false);  //设置需要测量的路径，即为外圈摆动的圆弧
        //canvas.drawPath(outCirclePointPath,indexPaint);
        if(isOriginStatus){
            float r = (outIndexCircleRadius - 20) ;
            outCirclePointPos[0] = (float)(width / 2) + (float)(r * Math.cos(arc2Angle(80)));
            outCirclePointPos[1] = (float)(height / 2) - (float)(r * Math.sin(arc2Angle(80)));
        }

        canvas.drawCircle(outCirclePointPos[0],outCirclePointPos[1],3,indexPaint);

        //画中两段式圆环弧线
        if(isOriginStatus){
            middleCirlceSwingRect = new RectF(
                    (width - innerCircleRadius * 2) / 2,
                    (height - innerCircleRadius * 2) / 2,
                    (width - innerCircleRadius * 2) / 2 + innerCircleRadius * 2,
                    (height - innerCircleRadius * 2) / 2 + innerCircleRadius * 2);
        }
        indexPaint.setColor(outMid2SegSwingArcColor);

        canvas.drawArc(middleCirlceSwingRect,swingAngle + 3.75f,10,false,indexPaint);
        canvas.drawArc(middleCirlceSwingRect,swingAngle + 48.75f,10,false,indexPaint);

        //画内环摆动圆弧
        float innreArcRadius = (innreCirlceGraduateRadius + graduateLineLength / 3.5f) * 2;
        if(isOriginStatus){
            innerCirlceSwingRect = new RectF(
                    (width - innreArcRadius) / 2,
                    (height - innreArcRadius) / 2,
                    (width - innreArcRadius) / 2 + innreArcRadius,
                    (height - innreArcRadius) / 2 + innreArcRadius);
        }
        indexPaint.setStrokeWidth(2);
        canvas.drawArc(innerCirlceSwingRect,swingAngle + 16.875f,30,false,indexPaint);

        //画内环与外环摆时的连接线
        float swingLineArc = arc2Angle(swingAngle + 80 + 16.875f + 22.5f); //先归原点再将线起始点移到弧形中间处
        float startX = width / 2.0f + (float) Math.sin(swingLineArc) * (innreArcRadius + swingLingOffSet) / 2;
        float startY = height / 2.0f - (float) Math.cos(swingLineArc) * (innreArcRadius + swingLingOffSet) / 2;

        float stopX = width / 2.0f + (float) Math.sin(swingLineArc) * (outIndexCircleRadius - swingLingOffSet / 2.0f);
        float stopY = height / 2.0f - (float) Math.cos(swingLineArc) * (outIndexCircleRadius - swingLingOffSet / 2.0f);
        indexPaint.setStrokeWidth(4);
        canvas.drawLine(startX, startY, stopX, stopY, indexPaint);

    }

    private void startThreePointAnim(){

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(60, 135);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                threeAngle = (float) animation.getAnimatedValue();
                //invalidate();
            }

        });
        valueAnimator.setDuration(800);
        //valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();

        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(50, 135);
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                threeAngle2 = (float) animation.getAnimatedValue();
                //invalidate();
            }

        });
        valueAnimator2.setDuration(800);
        //valueAnimator2.setInterpolator(new DecelerateInterpolator());
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.setRepeatMode(ValueAnimator.REVERSE);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                valueAnimator2.start();
            }
        },100);


        ValueAnimator valueAnimator3 = ValueAnimator.ofFloat(40, 135);
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                threeAngle3 = (float) animation.getAnimatedValue();
                //invalidate();
            }

        });
        valueAnimator3.setDuration(800);
        valueAnimator3.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator3.setRepeatMode(ValueAnimator.REVERSE);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                valueAnimator3.start();
            }
        },150);

    }

    /**
     * 启动外环刻度线动画
     */
    private void startOutCirlceGraduateLineAnim(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(outCircleOriginRadius, outCirlceScaleBigRadius);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                outCirlceRadius = (float) animation.getAnimatedValue();
                //invalidate();
            }

        });
        valueAnimator.setDuration(800);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }

    /**
     * 摆动圆弧动画
     */
    private void startSwingAnim(){

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(-80, 200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                swingAngle = (float) animation.getAnimatedValue();
                //invalidate();
            }

        });
        valueAnimator.setDuration(1200);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }

    /**
     * 启动外圆圈小点摆动动画
     */
    private void startOutCirclePointSwingAnim(){
        ValueAnimator mAnimator=ValueAnimator.ofFloat(0,outSwingPathMeasure.getLength());
        mAnimator.setDuration(1200);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                float start= (float) mAnimator.getAnimatedValue();
//                float end=start+(float)( outSwingPathMeasure.getLength() / 20);
//                if(end>outSwingPathMeasure.getLength()){
//                    end= outSwingPathMeasure.getLength();
//                }
//                outCirclePointPath.reset();
//                //从 原始path中取出一段 放入目的path中（添加），并不会删除目的path中以前的数据
//                outSwingPathMeasure.getSegment(start,end,outCirclePointPath,true);

                outSwingPath.reset();
                isOriginStatus = false;
                float distance = (float) animation.getAnimatedValue();
                //tan[0]是邻边 tan[1]是对边
                outSwingPathMeasure.getPosTan(distance, outCirclePointPos, tan);
                //postInvalidate();

            }
        });
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }


    /**
     * 弧度转角度
     * @param angle
     * @return
     */
    private float arc2Angle(float angle){

        return (float)(angle * 3.1415926 / 180);
    }


    private class DrawThread extends Thread{

        private SurfaceHolder mHolder;
        private boolean isRun = false;

        public void setRun(boolean run){
            this.isRun = run;
        }

        public DrawThread(SurfaceHolder holder){
            mHolder = holder;
        }

        @Override
        public void run() {
            super.run();
            while(isRun){
                Canvas canvas = null;
                synchronized (mHolder){
                    canvas = mHolder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);



                    drawOutCircle(canvas);
                    drawPointRect(canvas);
                    drawCircleGraduate(canvas);
                    drawIndexArc(canvas);
                    drawMidNotchLine(canvas);

                    if(canvas != null){
                        mHolder.unlockCanvasAndPost(canvas);
                    }

                    try {
                        /**用于控制绘制帧率*/
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
