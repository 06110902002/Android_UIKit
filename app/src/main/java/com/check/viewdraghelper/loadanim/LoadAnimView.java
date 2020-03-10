package com.check.viewdraghelper.loadanim;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.R;

/**
 * Create By 刘胡来
 * Create Date 2020-03-10
 * Sensetime@Copyright
 * Des: 加载成功与失败的动画主要总结android画线动画
 */
public class LoadAnimView extends View {

    private static final int PROGRESS_COLOR = Color.parseColor("#10a679");
    private static final int PROGRESS_WIDTH = 3;
    private static final int RADIUS = 30;

    private int mProgressColor = PROGRESS_COLOR;
    private int mProgressWidth = dp2px(PROGRESS_WIDTH);
    private int mRadius = dp2px(RADIUS);

    private Paint progressPaint;

    private int rotateDelta = 4;
    private int curAngle = 0;

    private int minAngle = -90;
    private int startAngle = -90;
    private int endAngle = 120;

    private Path path;
    private Status mStatus = Status.Loading;
    private float lineValueLeft;//左边对勾
    private float lineValueRight;//右边对勾
    private float failLineFirst;//叉号
    private float failLineSecond;

    public LoadAnimView(Context context) {
        this(context, null);
    }

    public LoadAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MdStyleProgress);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.MdStyleProgress_progress_color:
                    mProgressColor = typedArray.getColor(attr, PROGRESS_COLOR);
                    break;
                case R.styleable.MdStyleProgress_progress_width:
                    mProgressWidth = (int) typedArray.getDimension(attr, mProgressWidth);
                    break;
                case R.styleable.MdStyleProgress_tip_radius:
                    mRadius = (int) typedArray.getDimension(attr, mRadius);
                    break;
            }
        }
        typedArray.recycle();
        setPaint();

        path = new Path();
        path.moveTo(mRadius / 2, mRadius);
        path.lineTo(mRadius, mRadius + mRadius / 2);
        path.lineTo(mRadius + mRadius / 2, mRadius / 2);
    }

    private void setPaint() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setColor(mProgressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(mProgressWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize;
        int heightSize;
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = getPaddingLeft() + mProgressWidth + mRadius * 2 + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = getPaddingTop() + mProgressWidth + mRadius * 2 + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        if (mStatus == Status.Loading) {
            if (startAngle == minAngle) {
                endAngle += 6;
            }
            if (endAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (endAngle > 20) {
                    endAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                minAngle = startAngle;
                endAngle = 20;
            }
            canvas.rotate(curAngle += rotateDelta, mRadius, mRadius);//旋转rotateDelta=4的弧长
            canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), startAngle, endAngle, false, progressPaint);
            invalidate();
        } else if (mStatus == Status.LoadSuccess) {
            canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), startAngle, 360, false, progressPaint);
            //canvas.drawPath(path,progressPaint);
            //画圆圈中的对勾
            canvas.drawLine(mRadius / 2, mRadius, mRadius / 2 + lineValueLeft, mRadius + lineValueLeft, progressPaint);
            canvas.drawLine(mRadius, mRadius + mRadius / 2, mRadius + lineValueRight, mRadius + mRadius / 2 - 1.5f * lineValueRight, progressPaint);
        } else {
            canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), startAngle, 360, false, progressPaint);
            //画圆圈中的叉号(画右边叉号的时候，终止位置的x坐标为起始x位置减去failLineFirst，而failLineFirst在属性动画中的取值范围是0-mRadius)
            canvas.drawLine(mRadius + mRadius / 2, mRadius / 2, mRadius * 3 / 2 - failLineFirst, mRadius / 2 + failLineFirst, progressPaint);
            canvas.drawLine(mRadius / 2, mRadius / 2, mRadius / 2 + failLineSecond, mRadius / 2 + failLineSecond, progressPaint);
        }

        canvas.restore();
    }


    public enum Status {
        Loading,
        LoadSuccess,
        LoadFail
    }

    public void startAnima() {
        //对勾左边线的属性动画
        ValueAnimator animatorLeft = ValueAnimator.ofFloat(0f, mRadius / 2f);
        animatorLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineValueLeft = (float) animation.getAnimatedValue();
                Log.i("lineValueLeft", lineValueLeft + "");
                invalidate();//重绘，调onDraw()重绘
            }
        });
        //对勾右边线的属性动画
        ValueAnimator animatorRight = ValueAnimator.ofFloat(0f, mRadius / 2f);
        animatorRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineValueRight = (float) animation.getAnimatedValue();
                Log.i("lineValueRight", lineValueRight + "");
                invalidate();
            }
        });
        //将多个动画组合到一起需要借助AnimatorSet这个类
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorRight).after(animatorLeft);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    public void failAnima() {
        ValueAnimator failOne = ValueAnimator.ofFloat(0f, mRadius);
        failOne.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failLineFirst = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator failOther = ValueAnimator.ofFloat(0f, mRadius);
        failOther.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                failLineSecond = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(failOther).after(failOne);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
        invalidate();
    }

    /**
     * dp 2 px
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());
    }

}
