package com.check.viewdraghelper.loadanim;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.R;

/**
 * Copyright@NIO Since 2014
 * CreateTime  : 2022/12/29 15:41
 * Author      : rambo.liu
 * Description :参考 https://www.jianshu.com/p/eab4564c8382
 */
public class SaleProgressView extends View {
    private int mWidth;
    private int mHeight;
    private float mRadius;
    private RectF mRectFBackground;

    private Paint mBgPaint;
    private Paint mContentPaint;
    private Paint mTextPaint;

    private float mBaseLineY;

    private PorterDuffXfermode mContentMode;
    private PorterDuffXfermode mTextMode;

    private Bitmap mTextBitmap;
    private Canvas mTextCanvas;

    private float mCurPercentage;

    private String mText = "下载中";

    private float mTextSize = 0;
    private int mProgressMixTextColor = Color.WHITE;
    private int mTextColor = Color.WHITE;
    private int mBgColor = Color.GRAY;

    public SaleProgressView(Context context) {
        this(context, null);
        init(context,null);
    }

    public SaleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init(context,attrs);
    }

    public SaleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);

        try {
            mTextSize = attr.getDimension(R.styleable.ProgressView_text_Size, 20);
            mProgressMixTextColor = attr.getColor(R.styleable.ProgressView_progressMixTextColor, Color.WHITE);
            mTextColor = attr.getColor(R.styleable.ProgressView_text_Color, Color.WHITE);
            mBgColor = attr.getColor(R.styleable.ProgressView_bgColor, Color.GRAY);
        } finally {
            attr.recycle();
        }




        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mContentMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mTextMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);

        mContentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mContentPaint.setColor(Color.GREEN);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setCurPercentage(float curPercentage) {
        mCurPercentage = curPercentage;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        // 圆的半径
        mRadius = mHeight / 3.958f;
        if (mRectFBackground == null) {
            mRectFBackground = new RectF(0, 0,
                    mWidth, mHeight);
        }
        if (mBaseLineY == 0.0f) {
            Paint.FontMetricsInt fm = mTextPaint.getFontMetricsInt();
            mBaseLineY = mHeight / 2.0f - (fm.descent / 2.0f + fm.ascent / 2.0f);
        }

        mTextBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mTextCanvas = new Canvas(mTextBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawContent(canvas);
        drawText(canvas);
    }

    private void drawContent(Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, mWidth, mHeight, null);
        canvas.drawRoundRect(mRectFBackground, mRadius, mRadius, mBgPaint);
        mContentPaint.setXfermode(mContentMode);
        canvas.drawRoundRect(new RectF(0, 0, mWidth * mCurPercentage, mHeight),
                mRadius, mRadius, mContentPaint);
        mContentPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setProgressShader(Shader shader) {
        if (shader != null && mContentPaint != null) {
            mContentPaint.setShader(shader);
            invalidate();
        }
    }

    private void drawText(Canvas canvas) {
        mTextCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mTextPaint.setColor(mTextColor); //设置初始文本颜色
        mTextCanvas.drawText(mText, mWidth / 2.0f, mBaseLineY, mTextPaint);

        mTextPaint.setXfermode(mTextMode);

        mTextPaint.setColor(mProgressMixTextColor); //设置相交时的颜色
        mTextCanvas.drawRoundRect(new RectF(0, 0, mWidth * mCurPercentage, mHeight),
                mRadius, mRadius, mTextPaint);

        canvas.drawBitmap(mTextBitmap, 0, 0, null);

        mTextPaint.setXfermode(null);
    }
}