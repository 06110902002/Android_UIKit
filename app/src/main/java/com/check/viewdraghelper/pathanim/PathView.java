package com.check.viewdraghelper.pathanim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Create By 刘胡来
 * Create Date 2020-03-24
 * Sensetime@Copyright
 * Des:  测试用的路径视图 ,使用贝塞尔曲线实现,真正使用的时候可以将本文件删除
 */
public class PathView extends View {

    private Paint paint;
    Point center = new Point((560 + 100) / 2,(560 + 100) / 2);
    int radius = (560 - 100) / 5;

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public PathView(Context context) {
        this(context, null);
        initView();
    }
    public PathView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);//防抖动
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        testPath2(canvas);

    }

    /**
     * 贝塞尔 曲线路径
     * @param canvas
     */
    private void testPath1(Canvas canvas){
        Path path = new Path();
        path.moveTo(60,60);
        path.lineTo(460,460);
        path.quadTo(660, 260, 860, 460);
        path.cubicTo(160,660,960,1060,260,1260);
        canvas.drawPath(path,paint);
    }

    private void testPath2(Canvas canvas){
        paint.setColor(Color.GREEN);
        Path path = new Path();

        //第一条贝塞尔曲线
        path.moveTo(center.x + radius * (float)Math.cos(50.0),center.y + radius * (float)Math.sin(50.0));  //从原点出发画一条贝塞尔曲线
        path.quadTo(660, 160, 760, 460);

        //第二条贝塞尔曲线
        path.moveTo(center.x + radius * (float)Math.cos(150.0),center.y + radius * (float)Math.sin(150.0));  //从原点出发画一条贝塞尔曲线
        path.quadTo(660, 260, 560, 60);


        //第三条贝塞尔曲线
        path.moveTo(center.x - radius * (float)Math.cos(220.0),center.y - radius * (float)Math.sin(220.0));  //从原点出发画一条贝塞尔曲线
        path.quadTo(60, 160, 60, 860);

        canvas.drawPath(path,paint);

        //画一个矩形框
        Path rectpath = new Path();
        Paint rectPaint = new Paint();
        rectpath.addRect(new RectF(100,100,560,560), Path.Direction.CCW);
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(3);
        canvas.drawPath(rectpath,rectPaint);


        Path circlepath = new Path();
        //画一个圆形
        circlepath.addCircle(center.x,center.y,(560 - 100) / 5, Path.Direction.CCW);
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);//防抖动
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.MAGENTA);
        circlePaint.setStrokeWidth(3);
        canvas.drawPath(circlepath,circlePaint);
    }

}
