package com.check.viewdraghelper.pathanim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.check.viewdraghelper.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create By 刘胡来
 * Create Date 2020-03-24
 * Sensetime@Copyright
 * Des:
 */
public class BubbleView extends View {

    private Paint paint;
    private ObjectAnimator mAnimator;

    /**
     * 文字颜色
     */
    private int textColor = Color.parseColor("#69c78e");
    /**
     * 水滴填充颜色
     */
    private int waterColor = Color.parseColor("#c3f593");
    /**
     * 球描边颜色
     */
    private int storkeColor = Color.parseColor("#69c78e");
    /**
     * 描边线条宽度
     */
    private float strokeWidth = 0.5f;
    /**
     * 文字字体大小
     */
    private float textSize = 36;
    /**
     * 水滴球半径
     */
    private int mRadius = 30;

    private float waterWaveRadius;
    private ValueAnimator waterAnim;
    private Point center;
    private List<Circle> mRipples;
    private Paint mPaint;
    // View宽
    private float mWidth;

    // View高
    private float mHeight;
    private int mSpeed;
    private Context mContext;
    private float mDensity;
    private boolean startWave;


    private String textContent="";

    public void setTextContent(String text){
        this.textContent = text;
    }

    public BubbleView(Context context,String textContent) {
        super(context);
        this.textContent = textContent;
        init(context);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        waterWaveRadius = mRadius;

        center = new Point();
        mRipples = new ArrayList<>();
        Circle c = new Circle((int)waterWaveRadius, 255);
        mRipples.add(c);

        //画水波画笔
        startWave = false;
        mPaint = new Paint();
        mPaint.setColor(Color.MAGENTA);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mSpeed = 1;
        mDensity = 8;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawWaterWave(canvas);
        drawCircleView(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //setMeasuredDimension(Utils.dp2px(getContext(), (int) (2 * (mRadius+strokeWidth))),Utils.dp2px(getContext(), (int) (2 * (mRadius+strokeWidth))));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        center.x = getWidth() / 2;
        center.y = getHeight() / 2;
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    private void drawCircleView(Canvas canvas){
        //圆球
        paint.setColor(waterColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(center.x, center.y, Utils.dp2px(getContext(), mRadius), paint);

        //描边
        paint.setColor(storkeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Utils.dp2px(getContext(), (int) strokeWidth));
        canvas.drawCircle(center.x, center.y, Utils.dp2px(getContext(), (int) (mRadius+strokeWidth)) , paint);

        //圆球文字
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        drawVerticalText(canvas, center.x, center.y, textContent);
    }

    /**
     * 画水波涟漪圆，用动画进行驱动
     * @param canvas
     */
    private void drawWaterWave(Canvas canvas){
        if(!startWave) return;

        for (int i = 0; i < mRipples.size(); i++) {
            Circle c = mRipples.get(i);
            mPaint.setAlpha(c.alpha);// （透明）0~255（不透明）
            canvas.drawCircle(center.x, center.y, c.width, mPaint);

            if (c.width > mWidth) {
                mRipples.remove(i);
            } else {
                double alpha = 255 - c.width * (255 / ((double) mWidth / 2));
                c.alpha = (int) alpha;
                c.width += mSpeed;
            }
        }


        // 里面添加圆
        if (mRipples.size() > 0) {
            // 控制第二个圆出来的间距
            if (mRipples.get(mRipples.size() - 1).width > Utils.dp2px(mContext,mDensity)) {
                mRipples.add(new Circle(0, 255));
            }

        }
        invalidate();


    }

    private void drawVerticalText(Canvas canvas, float centerX, float centerY, String text) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float baseLine = -(fontMetrics.ascent + fontMetrics.descent) / 2;
        float textWidth = paint.measureText(text);
        float startX = centerX - textWidth / 2;
        float endY = centerY + baseLine;
        canvas.drawText(text, startX, endY, paint);
    }


    /**
     * 上下摆动的动画
     */
    public void start() {
        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofFloat(this, "translationY", -6.0f, 6.0f, -6.0f);
            mAnimator.setDuration(3500);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.start();
        } else if (!mAnimator.isStarted()) {
            mAnimator.start();
        }
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    public void startWaveAnim(boolean start){
        this.startWave = start;
        invalidate();
    }


    private class Circle {
        Circle(int width, int alpha) {
            this.width = width;
            this.alpha = alpha;
        }

        int width;
        int alpha;
    }


}
