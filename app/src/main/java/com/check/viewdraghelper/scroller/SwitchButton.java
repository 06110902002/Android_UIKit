package com.check.viewdraghelper.scroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

/**
 * Create By 刘铁柱
 * Create Date 2019-09-16
 * Sensetime@Copyright
 * Des: 仿ios 竖向的switch开关
 */
public class SwitchButton extends LinearLayout {


    //view默认的高,view默认的宽是高的两倍(单位:dp)
    public static final int VIEW_HEIGHT = 20;
    //椭圆的边框宽度
    private static final int strokeLineWidth = 3;
    //圆的边框宽度
    private static final int circleStrokeWidth = 3;

    //椭圆边框颜色
    private String StrokeLineColor = "#bebfc1";
    //椭圆填充颜色
    private String StrokeSolidColor = "#00ffffff";
    //圆形边框颜色
    private String CircleStrokeColor = "#abacaf";
    //圆形checked填充颜色
    private String CircleCheckedColor = "#ff5555";
    //圆形非checked填充颜色
    private String CircleNoCheckedColor = "#bebfc1";

    //控件内边距
    private static int PADDING = 20;
    //移动的判定距离
    private static int MOVE_DISTANCE = 50;

    //圆的x轴圆心
    private float circle_x;
    private float circle_y;

    //是否是大圆
    private boolean isBigCircle = false;

    //圆角矩形的高
    private int strokeHeight;
    //圆角矩形的半径
    private float strokeCircleRadius;
    //内部圆的半径
    private float circleRadius;
    private Scroller mScroller;
    //当前按钮的开关状态
    private boolean isChecked = false;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private float circleStartY;
    private float circleEndY;
    private int centerX;
    private float centerY;
    private float preY = 0;
    private boolean isMove;
    private int view_height_int;
    private int strokeLineColor_int;
    private int strokeCheckedSolidColor_int;
    private int strokeNoCheckedSolidColor_int;
    private int circleStrokeColor_int;
    private int circleChecked_int;
    private int circleNoCheckedColor_int;


    public SwitchButton(Context context) {
        super(context);
        init(context);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setEnabled(true);
        setClickable(true);
        mPaint = new Paint();
        mScroller = new Scroller(context);
        view_height_int = dip2px(context, VIEW_HEIGHT);
        strokeLineColor_int = Color.parseColor(StrokeLineColor);
        strokeNoCheckedSolidColor_int = Color.parseColor(StrokeSolidColor);
        circleStrokeColor_int = Color.parseColor(CircleStrokeColor);
        circleChecked_int = Color.parseColor(CircleCheckedColor);
        circleNoCheckedColor_int = Color.parseColor(CircleNoCheckedColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            //如果是wrap_content
            heightSize = view_height_int;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = heightSize * 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (isBigCircle) {
            PADDING = w / 10;
        } else {
            PADDING = w / 15;
        }
        MOVE_DISTANCE = mHeight / 100;
        //圆角椭圆的高
        strokeHeight = w - PADDING * 2;


        //外部圆角矩形的半径
        strokeCircleRadius = w / 2;



        //内部圆的半径
        if (isBigCircle) {
            circleRadius = strokeCircleRadius + PADDING;
        } else {
            circleRadius = strokeCircleRadius - PADDING * 2;
        }

        circle_x = w / 2;

        circleStartY = PADDING * 8;
        circleEndY = mHeight - circleStartY;


        if (isChecked) {
            circle_y = circleEndY;
        } else {
            circle_y = circleStartY;
        }

        //控件的中线
        centerX = mWidth / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
        drawCircle(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = event.getY();
                isMove = false;
                if (!isChecked) {
                    circle_y = PADDING + strokeCircleRadius;
                } else {
                    circle_y = mHeight - PADDING - strokeCircleRadius;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float move_y = event.getY();

                if (Math.abs(move_y - preY) > MOVE_DISTANCE) {
                    isMove = true;
                    if (move_y < circleStartY) {

                        circle_y = circleStartY;
                        isChecked = false;
                    } else if (move_y > circleEndY) {
                        circle_y = circleEndY;
                        isChecked = true;
                    } else {
                        circle_y = move_y;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    if (circle_y >= centerY) {
                        //关闭(执行开启)
                        mScroller.startScroll(0, (int)circle_y, 0,(int) (circleEndY - circle_y));
                        isChecked = true;
                    } else {
                        //开启（执行关闭）
                        mScroller.startScroll(0, (int)circle_y,0, (int) (circleStartY - circle_y));
                        isChecked = false;
                    }
                } else {
                    if (!isChecked) {
                        //关闭(执行开启)
                        mScroller.startScroll(0,(int) circle_y, 0,(int) (circleEndY - circle_y));
                        isChecked = true;
                    } else {
                        //开启（执行关闭）
                        mScroller.startScroll(0, (int) circle_y, 0,(int) (circleStartY - circle_y));
                        isChecked = false;
                    }
                }
//                if (mListener != null) {
//                    mListener.onCheckedChangeListener(isChecked);
//                }
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    //画圆角矩形
    private void drawRect(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        if (isBigCircle && isChecked) {
            mPaint.setColor(strokeCheckedSolidColor_int);
        } else {
            mPaint.setColor(strokeNoCheckedSolidColor_int);
        }
        //画填充
        canvas.drawRoundRect(PADDING, PADDING, mWidth - PADDING, mHeight - PADDING, strokeCircleRadius, strokeCircleRadius, mPaint);

        //画边框
        mPaint.setStrokeWidth(strokeLineWidth);
        mPaint.setColor(strokeLineColor_int);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(PADDING, PADDING, mWidth - PADDING, mHeight - PADDING, strokeCircleRadius, strokeCircleRadius, mPaint);
    }

    //画里面的圆
    private void drawCircle(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        float circleRadiusNew = circleRadius;
        if (isBigCircle) {
            circleRadiusNew -= circleStrokeWidth;
        }
        if (isChecked) {
            mPaint.setColor(circleChecked_int);
        } else {
            mPaint.setColor(circleNoCheckedColor_int);
        }
        canvas.drawCircle(circle_x, circle_y, circleRadiusNew, mPaint);

        if (isBigCircle) {
            //画圆的边
            mPaint.setColor(circleStrokeColor_int);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(circleStrokeWidth);
            canvas.drawCircle(circle_x, circle_y, circleRadiusNew, mPaint);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            circle_y = mScroller.getCurrY();
            invalidate();
        }
    }



    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
