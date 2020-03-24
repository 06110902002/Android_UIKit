package com.check.viewdraghelper.arcanim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.check.viewdraghelper.R;

import java.lang.ref.WeakReference;
import java.util.Arrays;


/**
 * Create By 刘胡来
 * Create Date 2020-03-23
 * Sensetime@Copyright
 * Des:  @android:color/transparent  透明色
 */
public class CircleMenuView extends SurfaceView implements SurfaceHolder.Callback{


    private static final String TAG = CircleMenuView.class.getSimpleName();

    private static class InnerHandler extends Handler {
        WeakReference<CircleMenuView> weakReference;

        public InnerHandler(CircleMenuView view) {
            weakReference = new WeakReference<CircleMenuView>(view);
        }
    }

    private static CircleMenuView.InnerHandler mHandler = null;

    private static final int DEFAULT_TIMEOUT = 1500;
    private static int DEFAULT_RADIUS = 40;
    public static final int WIDTH_HEIGHT_RATIO = 14;


    //当前的状态
    private State mCurrentState = State.APPEAR;
    private Bitmap mBp = null;
    private Canvas mBPCanvas = null;
    /*
        出现的值动画
     */
    private ValueAnimator mAppearAnim = null;
    /*
        点击然后变大的值动画
     */
    private ValueAnimator mGrowBigAnim = null;

    /*
       出现涟漪的值动画
    */
    private ValueAnimator mDisappearAnim = null;

    /*
        点击然后渐渐透明的动画
     */
    private ValueAnimator mAlphaAnim = null;


    TextPaint textPaint = new TextPaint();

    StaticLayout staticLayout = null;

    private int mRadius;

    private int mTextSize;

    private P mP = null;

    private long downTime = System.currentTimeMillis();

    private SurfaceHolder mHolder;

    private Canvas mCanvas;
    private long mTimeOut;

    private Paint mPaint;

    public volatile int mBigAlpha = 255;
    private Which mWhich = Which.NONE;
    private Rect mRect = new Rect();


    /**
     * 主要的颜色，指的是扩展为王八的颜色以及按钮线条的颜色，按钮的颜色和王八的颜色一致
     */
    int mMajorColor = Color.WHITE;
    /**
     * 按钮内圆的颜色
     */
    int mBackColor = Color.TRANSPARENT;
    /**
     * 文字的颜色
     */
    int mTextColor = Color.WHITE;


    int mStrokeWidth = 2;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = WIDTH_HEIGHT_RATIO * mRadius;
        int desiredHeight = WIDTH_HEIGHT_RATIO * mRadius;
        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    public CircleMenuView(Context context) {
        this(context, null);
    }

    public CircleMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircleMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleMenuView);
        DEFAULT_RADIUS = (int) ta.getDimension(R.styleable.CircleMenuView_arcradius, DEFAULT_RADIUS);
        mTextSize = (int) ta.getDimension(R.styleable.CircleMenuView_textSize, DEFAULT_RADIUS / 5);
        mTextSize = (int) sp2px(context, mTextSize);
        mRadius = dip2px(context, DEFAULT_RADIUS);
        mTimeOut = ta.getInteger(R.styleable.CircleMenuView_timeOut, DEFAULT_TIMEOUT);
        mTextTop = ta.getString(R.styleable.CircleMenuView_topText);
        mTextRightTop = ta.getString(R.styleable.CircleMenuView_rightTopText);
        mTextRightBottom = ta.getString(R.styleable.CircleMenuView_rightBottomText);
        mTextLeftBottom = ta.getString(R.styleable.CircleMenuView_leftBottomText);
        mTextLeftTop = ta.getString(R.styleable.CircleMenuView_leftTopText);
        mTextColor = ta.getColor(R.styleable.CircleMenuView_textColor, mTextColor);
        mBackColor = ta.getColor(R.styleable.CircleMenuView_backColor, mBackColor);
        mMajorColor = ta.getColor(R.styleable.CircleMenuView_majorColor, mMajorColor);
        mStrokeWidth = dip2px(context, ta.getDimension(R.styleable.CircleMenuView_strokeWidth, mStrokeWidth));
        Which.TOP.setText(mTextTop);
        Which.RIGHTTOP.setText(mTextRightTop);
        Which.RIGHTBOTTOM.setText(mTextRightBottom);
        Which.LEFTBOTTOM.setText(mTextLeftBottom);
        Which.LEFTTOP.setText(mTextLeftTop);
        ta.recycle();
        init();

    }

    PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    PorterDuffXfermode srcMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    PorterDuffXfermode blurMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mHandler = new InnerHandler(this);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mBp == null) {
            mBp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        }
        if (mBPCanvas == null) {
            mBPCanvas = new Canvas(mBp);
        }
    }

    private void initAppearAnim() {
        if (mAppearAnim == null) {
            long duration = 3000;
            mAppearAnim = ValueAnimator.ofInt(0, State.APPEAR.maxProgress);
            mAppearAnim.setDuration(duration).setInterpolator(new OvershootInterpolator(0.5f));
            mAppearAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    State.APPEAR.progress = (int) animation.getAnimatedValue();
                    drawAppear();
                    update();
                }
            });
            mAppearAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mCurrentState == State.APPEAR) {
                        mCurrentState = State.NORMAL;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }
    }


    private void clear() {
        mPaint.setXfermode(srcMode);
        mBPCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        update();

    }

    public void appear() {
        clear();
        cancelAnim(mAppearAnim, mDisappearAnim, mGrowBigAnim, mAlphaAnim);
        mCurrentState = State.APPEAR;
        State.APPEAR.progress = 0;
        State.NORMAL.progress = 0;
        State.GROWBIG.progress = 0;
        State.ALPHA.progress = 0;
        State.DISAPPEAR.progress = 0;
        mBigAlpha = 255;
        mWhich = Which.NONE;
        initAppearAnim();
        mAppearAnim.setStartDelay(50);
        mAppearAnim.start();
    }

    private void cancelAnim(ValueAnimator... anims) {
        if (anims == null || anims.length <= 0) {
            return;
        }
        for (ValueAnimator anim : anims) {
            if (anim != null && anim.isRunning()) {
                anim.pause();
                anim.cancel();
                anim = null;
            }
        }

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


    //1r -- 1.2r -- 1.4r repead
    ValueAnimator mRadiusAnimator1 = null;

    public static final int DISAPPEAR_DURATION = 1600;
    private void initDisappearAnimators() {
        if (mRadiusAnimator1 == null) {
            mRadiusAnimator1 = ValueAnimator.ofFloat(mRadius, 3.5f * mRadius);
            mRadiusAnimator1.setDuration(DISAPPEAR_DURATION);
            mRadiusAnimator1.setInterpolator(new LinearInterpolator());
            mRadiusAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mPath1Radius = (float) animation.getAnimatedValue();
                }
            });
        }


    }


    private volatile float mPath1Radius = 0;
    private volatile float mPath1Alpha = 0;

    private float mWaveItemInterval;

    int[] as = new int[]{15, 35, 37, 37, 34, 35, 38, 37, 35, 36};

    /**
     * 点击后出现的画涟漪的动画
     */
    private void drawDisAppear() {

        mBPCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPaint.setXfermode(srcMode);

        log("mPath1Radius = " + mPath1Radius);
        float r = mPath1Radius;
        int waveCount = 0;
        mWaveItemInterval = mRadius / 3;
        //按照动画进度，生成对应半径的path
        while (r > mRadius) {
            //按照动画进度，生成对应半径的path
            Path path = createPathByAngles(mP.x, mP.y, r, (int) (mRadius / 10 + mRadius / 10 * (State.DISAPPEAR.progress * 1.0f / 100)), true, as);
            mPaint.setColor(mMajorColor);
            mPaint.setStyle(Paint.Style.STROKE);

            //按照进度设置alpha
            if (mCurrentState == State.ALPHA) {
                textPaint.setAlpha(mBigAlpha);

            } else {
                //绘制波纹
                if (waveCount < 3) {
                    mPaint.setAlpha((int) (255 * (1 - ((r - mRadius) / mRadius))));
                    mPaint.setStrokeWidth(mStrokeWidth);

                    if (r <= 2 * mRadius) {
                        mBPCanvas.drawPath(path, mPaint);

                    }
                    waveCount++;
                }
            }

            r = r - mWaveItemInterval;

        }


        if (mCurrentState == State.ALPHA) {
            textPaint.setAlpha(mBigAlpha);

        } else {
            mPaint.setAlpha(255);
        }
        Path pathWhite = null;

        if (State.DISAPPEAR.progress < State.DISAPPEAR.maxProgress) {
            pathWhite = createPathByAngles(mP.x, mP.y, mRadius / 10 * 9, mRadius / 10, true, as);

        } else {
            pathWhite = createPathByAngles(mP.x, mP.y, mRadius / 10 * 9, mRadius / 10, true);
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mMajorColor);
        mBPCanvas.drawPath(pathWhite, mPaint);
        int textSize = (int) (mTextSize + mTextSize / 10 * (State.GROWBIG.progress * 1.0f / State.GROWBIG.maxProgress));
        drawText(mWhich.text, mP, mTextColor, 255, textSize);
        int left = (int) (mP.x - mRadius - mRadius);
        int top = (int) (mP.y - mRadius - mRadius);
        int right = (int) (mP.x + mRadius + mRadius);
        int bottom = (int) (mP.y + mRadius + mRadius);
        mRect.left = left;
        mRect.top = top;
        mRect.right = right;
        mRect.bottom = bottom;
        updatePart(mRect);

    }


    /**
     * 画出现的动画
     * 中心位置逐渐变大的无规则曲线圆 逐渐变成王八模型
     * 1.createPathByAngles 产生曲线点 逐一画贝塞尔曲线
     * <p>
     * <p>
     * <p>
     * 王八模型 分离出来四个圆 带有粘连效果
     * 四个圆从无规则曲线圆 渐变为圆 同时伴有弹性动画
     * <p>
     * 园中黑色小圆逐渐变大 文字内容逐渐出现 伴有弹性动画
     */
    private void drawAppear() {
        drawAppearFirst();
        drawAppearSecond();
    }

    private Paint canvaspaint = new Paint();

    private void update() {
        try {
            synchronized (mHolder) {
                mCanvas = mHolder.lockCanvas();
                if (mCanvas != null) {
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvaspaint.setXfermode(srcMode);
                    mCanvas.drawPaint(canvaspaint);
                    mCanvas.drawBitmap(mBp, 0, 0, canvaspaint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mHolder != null && mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public void updatePart(Rect rect) {
        try {
            synchronized (mHolder) {
                mCanvas = mHolder.lockCanvas(rect);
                if (mCanvas != null) {
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    canvaspaint.setXfermode(srcMode);
                    mCanvas.drawPaint(canvaspaint);
                    mCanvas.drawBitmap(mBp, 0, 0, canvaspaint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mHolder != null && mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }


    /**
     * 按下时 按钮变大
     * 松手 缩小曲线 加涟漪
     * 最后消失
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentState != State.NORMAL) {
                    return super.onTouchEvent(e);
                }
                Which which = processDownEvent(e);
                if (which != Which.NONE) {
                    mCurrentState = State.GROWBIG;
                    mP = getPByWhich(which);
                    initGrowBigAnim();
                    mWhich = which;
                    mGrowBigAnim.start();
                    downTime = System.currentTimeMillis();
                    return true;
                } else {
                    super.onTouchEvent(e);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentState != State.GROWBIG) {
                    return super.onTouchEvent(e);
                }

                cancelAnim(mGrowBigAnim);
                if (System.currentTimeMillis() - downTime > mTimeOut && distance(new P(e.getX(), e.getY()), getPByWhich(mWhich)) <= mRadius) {
                    //触发点击事件
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onMenuClick(mWhich);
                    }
                    startDisappearAnim();
                } else {
                    State.APPEAR.progress = State.APPEAR.maxProgress;
                    drawAppearSecond();
                    update();
                    mCurrentState = State.NORMAL;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                State.APPEAR.progress = State.APPEAR.maxProgress;
                drawAppearSecond();
                update();
                mCurrentState = State.NORMAL;
                break;
            default:
                break;
        }
        return super.onTouchEvent(e);
    }


    private void initGrowBigAnim() {
        if (mGrowBigAnim == null) {
            mGrowBigAnim = ValueAnimator.ofInt(0, State.GROWBIG.maxProgress);
            mGrowBigAnim.setDuration(mTimeOut);
            mGrowBigAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mGrowBigAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    State.GROWBIG.progress = (int) animation.getAnimatedValue();
                    drawGrowup();
                }
            });
        }

    }


    private void startDisappearAnim() {
        if (mCurrentState == State.DISAPPEAR) {
            return;
        }
        mCurrentState = State.DISAPPEAR;
        if (mDisappearAnim == null) {
            mDisappearAnim = ValueAnimator.ofInt(2, State.DISAPPEAR.maxProgress);
            mDisappearAnim.setDuration(DISAPPEAR_DURATION);
            mDisappearAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    State.DISAPPEAR.progress = (int) animation.getAnimatedValue();
                    drawDisAppear();
                }
            });
            mDisappearAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startAlphaAnim();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }
        initDisappearAnimators();

        cancelAnim(mRadiusAnimator1);

        mRadiusAnimator1.start();
        mDisappearAnim.start();


    }


    private void startAlphaAnim() {
        if (mAlphaAnim == null) {
            mAlphaAnim = ValueAnimator.ofInt(State.ALPHA.maxProgress / 10, State.ALPHA.maxProgress);
            mAlphaAnim.setDuration(800);
            mAlphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mAlphaAnim.isRunning()) {
                        State.ALPHA.progress = (int) animation.getAnimatedValue();
                        drawAlpha();
                    }
                }
            });
        }
        mCurrentState = State.ALPHA;
        mAlphaAnim.start();

    }


    private void drawAlpha() {
        mBigAlpha = (int) (255 - 255 * (State.ALPHA.progress * 1.0f / State.ALPHA.maxProgress));
        mPaint.setAlpha(mBigAlpha);
        State.APPEAR.progress = State.APPEAR.maxProgress;
        if(mBigAlpha < 250) {
            drawAppearSecond();
            update();

        }
    }


    private void drawGrowup() {
        if (mP != null) {
            int left = (int) (mP.x - mRadius - mRadius);
            int top = (int) (mP.y - mRadius - mRadius);
            int right = (int) (mP.x + mRadius + mRadius);
            int bottom = (int) (mP.y + mRadius + mRadius);
            mRect.left = left;
            mRect.top = top;
            mRect.right = right;
            mRect.bottom = bottom;
            float r = mRadius + mRadius / 3 * (State.GROWBIG.progress * 1.0f / State.GROWBIG.maxProgress);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setXfermode(srcMode);
            mPaint.setColor(mMajorColor);

            //画外围圆
            mBPCanvas.drawCircle(mP.x, mP.y, r, mPaint);


            mPaint.setXfermode(mode);
            mPaint.setColor(mBackColor);

            mBPCanvas.drawCircle(mP.x, mP.y, r * 0.9f, mPaint);

            int textSize = (int) (mTextSize + mTextSize / 4 * (State.GROWBIG.progress * 1.0f / State.GROWBIG.maxProgress));
            drawText(mWhich.text, mP, mTextColor, 255, textSize);
            updatePart(mRect);
        }
    }

    /**
     * 判断down事件的下落点是否在五个按钮内，如果是返回true，否则返回false表示不消费该事件
     *
     * @param event
     * @return 中心 0
     * 上方 1
     * 下方 2
     * 左侧 3
     * 右侧 4
     * 其他 -1
     */
    private Which processDownEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        P nowP = new P(x, y);
        float dstx = getWidth() / 2;
        float dstY = 3 * mRadius;
        P dstP = new P(dstx, dstY);
        P sourceP = new P(getWidth() / 2, getHeight() / 2);
        //距离中心圆心的坐标
        float disTop = distance(getRotationPoint(sourceP, dstP, 0), nowP);
        float disRightTop = distance(getRotationPoint(sourceP, dstP, 72), nowP);
        float disRightBottom = distance(getRotationPoint(sourceP, dstP, 72 * 2), nowP);
        float disLeftBottom = distance(getRotationPoint(sourceP, dstP, 72 * 3), nowP);
        float disLeftTop = distance(getRotationPoint(sourceP, dstP, 72 * 4), nowP);
        if (disTop <= mRadius) {
            return Which.TOP;
        }
        if (disRightTop <= mRadius) {
            return Which.RIGHTTOP;
        }
        if (disRightBottom <= mRadius) {
            return Which.RIGHTBOTTOM;
        }
        if (disLeftBottom <= mRadius) {
            return Which.LEFTBOTTOM;
        }
        if (disLeftTop <= mRadius) {
            return Which.LEFTTOP;
        }
        return Which.NONE;
    }

    public void disAppear() {
        cancelAnim(mAlphaAnim, mRadiusAnimator1, mGrowBigAnim, mAppearAnim, mDisappearAnim);
        startAlphaAnim();
    }

    /**
     * 绘制第二阶段
     * 具有粘连效果 高斯模糊的 五圆分离效果 四个圆 产生path后不变，但是需要旋转
     * 画五个不规则圆
     * 画粘连效果
     */
    private void drawAppearSecond() {

        int secondMinProgress = State.APPEAR.maxProgress / 5 * 3;
        int secondMaxProgress = State.APPEAR.maxProgress;
        int offProgress = (secondMaxProgress - secondMinProgress);
        float ratio = (State.APPEAR.progress - secondMinProgress) * 1.0f / offProgress;
        if (State.APPEAR.progress < secondMinProgress) {
            return;
        }
        //清屏
        mBPCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        float r = mRadius / 4 + mRadius / 4 * 3 * ratio;

        float nowy = 6.5f * mRadius - 3.5f * mRadius * ratio;
        int d = (int) (mRadius / 15  + mRadius / 10  * ratio);

        //画5个圆
        for (int index = 0; index < 5; index++) {
            mBPCanvas.save();
            mBPCanvas.rotate(72 * index, getWidth() / 2, getHeight() / 2);


            Path p;
            if (ratio <= 0.8) {
                p = createPathByAngles(getWidth() / 2, nowy, r, d, true, as);
            } else {
                p = createPathByAngles(getWidth() / 2, nowy, r, d, true);
            }


            if (mCurrentState == State.ALPHA) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(r * 0.1f);
                mPaint.setColor(mMajorColor);
                mPaint.setXfermode(srcMode);
                mPaint.setAlpha(mBigAlpha);
            } else {
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mPaint.setXfermode(srcMode);
                mPaint.setColor(mMajorColor);
            }

            //画按钮外围圆
            mBPCanvas.drawPath(p, mPaint);
            if (ratio > 0.8 && mCurrentState != State.ALPHA) {
                //求出ratio从0.8 变到1 的基于0-1的ratio
                float newRatio = (ratio - 0.8f) * (1 / 0.2f);
                if (mCurrentState == State.ALPHA) {
                    mPaint.setColor(mBackColor);

                    mPaint.setAlpha(mBigAlpha);
                    mPaint.setXfermode(mode);
                } else {
                    mPaint.setXfermode(mode);
                    mPaint.setColor(mBackColor);
                }
                //画按钮内圆
                mBPCanvas.drawCircle(getWidth() / 2, nowy, r * 0.9f * ((newRatio <= 1 ? newRatio : 1 + (newRatio - 1) * 0.1f)), mPaint);
            }
            mBPCanvas.restore();
        }


        if (ratio > 0.8) {
            //求出ratio从0.8 变到1 的基于0-1的ratio
            float newRatio = (ratio - 0.8f) * (1 / 0.2f);
            float textSize = mTextSize * (newRatio <= 1 ? newRatio : (1 + (newRatio - 1) * 0.15f));
            //画字
            int textColor = mTextColor;
            drawText(Which.TOP.text, getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, nowy, 0), textColor, 255, textSize);
            drawText(Which.RIGHTTOP.text, getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, nowy, 72), textColor, 255, textSize);
            drawText(Which.RIGHTBOTTOM.text, getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, nowy, 72 * 2), textColor, 255, textSize);
            drawText(Which.LEFTBOTTOM.text, getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, nowy, 72 * 3), textColor, 255, textSize);
            drawText(Which.LEFTTOP.text, getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, nowy, 72 * 4), textColor, 255, textSize);
        }

        if (ratio < 0.6) {
            //0--1f
            float newr = ratio / 0.6f;
            Path cp = createPathByAngles(getWidth() / 2, getHeight() / 2, r, d, true, as);
            mPaint.setColor(mMajorColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setXfermode(srcMode);
            mPaint.setAlpha((int) (255));

            mBPCanvas.drawPath(cp, mPaint);
            //画粘连效果
            for (int index = 0; index < 5; index++) {
                cp.reset();
                mBPCanvas.save();
                mBPCanvas.rotate(72 * index, getWidth() / 2, getHeight() / 2);
                P rp1 = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - r, 90 + 180 + 20);
                P rp2 = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight() / 2 - r, 70);


                cp.moveTo(rp1.x, rp1.y);

                cp.quadTo(getWidth() / 2, getHeight() / 2 - r - 2 * r * (1 - newr), rp2.x, rp2.y);

                cp.close();
                mBPCanvas.drawPath(cp, mPaint);

                cp.reset();
                rp1 = getRotationPoint(getWidth() / 2, nowy, getWidth() / 2, nowy - r, 90 + 20 + 140);
                rp2 = getRotationPoint(getWidth() / 2, nowy, getWidth() / 2, nowy - r, 90 + 20 );


                cp.moveTo(rp1.x, rp1.y);

                cp.quadTo(getWidth() / 2, nowy+ r + 2 * r * (1 - newr), rp2.x, rp2.y);
                cp.close();
                mBPCanvas.drawPath(cp, mPaint);


                mBPCanvas.restore();

            }


        }
        else if (ratio < 0.9f) {//0.6 -- 0.9
            float newratio = (ratio - 0.6f) / 0.3f; // 0 -- 1
            Path cp = createPathByAngles(getWidth() / 2, getHeight() / 2, r * (1 - newratio), d, true);
            mPaint.setColor(mMajorColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setXfermode(srcMode);
            float newRatio = ratio / 0.8f; // 0 - 1
            mPaint.setAlpha((int) (255));
            mBPCanvas.drawPath(cp, mPaint);
        }

    }


    /**
     * 绘制展开的第一阶段
     */
    private void drawAppearFirst() {
        int innerPathMaxProgress = State.APPEAR.maxProgress / 5;
        int outterPathMaxProgress = State.APPEAR.maxProgress / 5 * 3;
        if (State.APPEAR.progress <= outterPathMaxProgress) {
            mBPCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            Path path = null;
            //从小到王八
            if (State.APPEAR.progress <= innerPathMaxProgress) {
                //d  从 mRadius / 3 * 2 - 0
                float ratio = (State.APPEAR.progress * 1.0f / outterPathMaxProgress);
                float innerR = mRadius / 4 * 3 * ratio;
                if (path == null) {
                    path = new Path();
                }
                path.addCircle(getWidth() / 2, getHeight() / 2, innerR, Path.Direction.CW);

            } else if (State.APPEAR.progress > innerPathMaxProgress && State.APPEAR.progress <= outterPathMaxProgress) {
                //d 从0 到 30
                int outterD = (int) (mRadius / 5 + mRadius / 5 * 2 * ((State.APPEAR.progress - innerPathMaxProgress) * 1.0f / (outterPathMaxProgress - innerPathMaxProgress)));
                float r = mRadius / 5 * 2 + mRadius / 5 * (State.APPEAR.progress * 1.0f / outterPathMaxProgress);
                path = createPathByAngles(getWidth() / 2, getHeight() / 2, r, outterD, true, 36, 72, 72, 72, 72);
//                cancelAnim(mAppearAnim);
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mMajorColor);
            mBPCanvas.drawPath(path, mPaint);

        }
    }


    //状态枚举类
    public enum State {
        APPEAR(0, 1000), //展开阶段，不可操作，为一个状态
        BLACKCIRCLE_TEXT(0, 1000),//画黑色圆圈以及文字的阶段
        NORMAL(0, 0),
        GROWBIG(0, 100),
        DISAPPEAR(0, 100),//回收阶段 不接受触控
        ALPHA(0, 100),
        NONE(-1, -1);
        public int progress;
        public int maxProgress;

        State(int progress, int maxProgress) {
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

    }


    public P getPByWhich(Which w) {
        P p = new P();
        switch (w) {
            case TOP://中间
                p = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, 3 * mRadius, 0);
                break;
            case LEFTTOP:
                p = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, 3 * mRadius, 72 * 4);
                break;
            case RIGHTTOP:
                p = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, 3 * mRadius, 72);
                break;
            case LEFTBOTTOM:
                p = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, 3 * mRadius, 72 * 3);

                break;
            case RIGHTBOTTOM:
                p = getRotationPoint(getWidth() / 2, getHeight() / 2, getWidth() / 2, 3 * mRadius, 72 * 2);

                break;
            default:
                break;
        }
        return p;
    }


    public Path createPathByAngles(float cx, float cy, float r, MP... mps) {
        Path path = new Path();
        if (mps == null || mps.length <= 0) {
            path.addCircle(cx, cy, r, Path.Direction.CW);
            return path;
        }
        int lastAngle = 0;

        for (MP i : mps) {
            lastAngle += i.degree;
        }
        lastAngle = 0;
        P[] ps = new P[mps.length];
        for (int dex = 0; dex < ps.length; dex++) {
            int angle = mps[dex].degree;
            lastAngle += angle;
            float x = (float) (cx + Math.sin(lastAngle * 1.0 / 180 * Math.PI) * r);
            float y = (float) (cy - Math.cos(lastAngle * 1.0 / 180 * Math.PI) * r);
            ps[dex] = new P(x, y);
        }
        //开始画path
        lastAngle = 0;
        float prex, prey;
        prex = ps[ps.length - 1].x;
        prey = ps[ps.length - 1].y;
        path.moveTo(prex, prey);
        for (int dex = 0; dex < mps.length; dex++) {
            int angle = mps[dex].degree;
            lastAngle += angle;
            float d = mps[dex].d;
            boolean isOut = mps[dex].isOut;
            float x = ps[dex].x;
            float y = ps[dex].y;
            //获取x，y与前一个点中间纯直线距离d的坐标点位置 起点相对在左 终点相对在右 d为正数 往上 d为负数 往下走
            float k = (y - prey) / (x - prex);

            float mk = k == 0 ? 0 : -1 / k;
            float mx = (prex + x) / 2;
            float my = (prey + y) / 2;

            float mb = my - mk * mx;
            float t = mb - my;
            float a = 1 + mk * mk;
            float b = 2 * t * mk - 2 * mx;
            float c = t * t + mx * mx - d * d;
            float x1, y1, x2, y2;
            if (mk == 0) {
                x1 = mx;
                y1 = my + d;
                x2 = mx;
                y2 = my - d;
            } else {
                x1 = ((float) (-1 * b + Math.sqrt(b * b - 4 * a * c))) / (2 * a);
                y1 = mk * x1 + mb;


                x2 = ((float) (-1 * b - Math.sqrt(b * b - 4 * a * c))) / (2 * a);
                y2 = mk * x2 + mb;

            }

            float controlX, controlY;//控制点坐标
            //判断两个点到中心的距离是否大于radius
            if (isOut) {//采用外面的点
                if (distance(cx, cy, x1, y1) > distance(cx, cy, mx, my)) {
                    controlX = x1;
                    controlY = y1;
                } else {
                    controlX = x2;
                    controlY = y2;
                }
            } else {//才有里面的点
                if (distance(cx, cy, x1, y1) < distance(cx, cy, mx, my)) {
                    controlX = x1;
                    controlY = y1;
                } else {
                    controlX = x2;
                    controlY = y2;
                }
            }
            path.quadTo(controlX, controlY, x, y);
            prey = y;
            prex = x;
        }
        path.close();


        return path;
    }

    /**
     * 按照指定的角度（相对角度）产生一个曲线圆类似于水波的Path
     *
     * @param cx    圆心坐标x
     * @param cy    圆心坐标y
     * @param r     基准半径
     * @param d     控制端距离中垂线的距离
     * @param isOut 控制点的位置，true表示在两点连线的外面
     * @param as    点数组系列。
     * @return 返回产生的Path
     */
    private Path createPathByAngles(float cx, float cy, float r, int d, boolean isOut, int... as) {
        Path path = new Path();
        if (as == null || as.length <= 0) {
            path.addCircle(cx, cy, r, Path.Direction.CW);
            return path;
        }
        int lastAngle = 0;

//        for (int i : as) {
//            lastAngle += i;
//        }

//        lastAngle = 0;
        P[] ps = new P[as.length];
        for (int dex = 0; dex < as.length; dex++) {
            int angle = as[dex];
            lastAngle += angle;
            float x = (float) (cx + Math.sin(lastAngle * 1.0 / 180 * Math.PI) * r);
            float y = (float) (cy - Math.cos(lastAngle * 1.0 / 180 * Math.PI) * r);
            ps[dex] = new P(x, y);
        }

        //开始画path
        lastAngle = 0;
        float prex, prey;
        prex = ps[ps.length - 1].x;
        prey = ps[ps.length - 1].y;
        path.moveTo(prex, prey);
        for (int dex = 0; dex < as.length; dex++) {
            int angle = as[dex];
            lastAngle += angle;

            float x = ps[dex].x;
            float y = ps[dex].y;


            //获取x，y与前一个点中间纯直线距离d的坐标点位置 起点相对在左 终点相对在右 d为正数 往上 d为负数 往下走
            float k = (y - prey) / (x - prex);

            float mk = k == 0 ? 0 : -1 / k;
            float mx = (prex + x) / 2;
            float my = (prey + y) / 2;


            float mb = my - mk * mx;
            float t = mb - my;
            float a = 1 + mk * mk;
            float b = 2 * t * mk - 2 * mx;
            float c = t * t + mx * mx - d * d;
            double x1, y1, x2, y2;
            if (mk == 0) {
                x1 = mx;
                y1 = my + d;
                x2 = mx;
                y2 = my - d;
            } else {
                x1 = ((double) (-1 * b + Math.sqrt(b * b - 4 * a * c))) / (2 * a);
                y1 = mk * x1 + mb;


                x2 = ((double) (-1 * b - Math.sqrt(b * b - 4 * a * c))) / (2 * a);
                y2 = mk * x2 + mb;

            }

            double controlX, controlY;//控制点坐标
            //判断两个点到中心的距离是否大于radius
            if (isOut) {//采用外面的点
                if (distance(cx, cy, x1, y1) > distance(cx, cy, mx, my)) {
                    controlX = x1;
                    controlY = y1;
                } else {
                    controlX = x2;
                    controlY = y2;
                }
            } else {//才有里面的点
                if (distance(cx, cy, x1, y1) < distance(cx, cy, mx, my)) {
                    controlX = x1;
                    controlY = y1;
                } else {
                    controlX = x2;
                    controlY = y2;
                }
            }

            path.quadTo((float) controlX, (float) controlY, x, y);
            prey = y;
            prex = x;
        }
        path.close();


        return path;


    }

    /**
     * 求出两个坐标之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private float distance(double x1, double y1, double x2, double y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private float distance(P p1, P p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);

    }

    /**
     * dst与source连接线按照source顺时针旋转rotation角度之后 dst的新坐标
     *
     * @param sourceX
     * @param sourceY
     * @param dstX
     * @param dstY
     * @param rotation
     * @return
     */
    private P getRotationPoint(float sourceX, float sourceY, float dstX, float dstY, int rotation) {
        double angle = rotation * 1.0f / 180 * Math.PI;
        P p = new P();
        float distance = distance(sourceX, sourceY, dstX, dstY);
        p.x = (float) (sourceX + Math.sin(angle) * distance);
        p.y = (float) (sourceY - Math.cos(angle) * distance);
        return p;
    }

    private P getRotationPoint(P source, P dst, int rotation) {
        double angle = rotation * 1.0f / 180 * Math.PI;
        P p = new P();
        float distance = distance(source.x, source.y, dst.x, dst.y);
        p.x = (float) (source.x + Math.sin(angle) * distance);
        p.y = (float) (source.y - Math.cos(angle) * distance);
        return p;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float spVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }


    public static void log(String log) {
        Log.d(TAG, log);
    }

    public static class P {
        public float x;
        public float y;

        public P() {
        }

        public P(float x, float y) {
            this.x = x;
            this.y = y;

        }
    }


    public static class MP {
        int degree;
        float d;
        boolean isOut;

        public MP(int degree, float d, boolean isOut) {
            this.degree = degree;
            this.d = d;
            this.isOut = isOut;
        }
    }

    public interface OnMenuClickListener {
        void onMenuClick(Which which);
    }

    public OnMenuClickListener onMenuClickListener = null;

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        onMenuClickListener = listener;
    }


    public String mTextTop;
    public String mTextLeftTop;
    public String mTextRightTop;
    public String mTextLeftBottom;
    public String mTextRightBottom;

    public String getTextTop() {
        return mTextTop;
    }

    public void setTextTop(String mTextTop) {
        this.mTextTop = mTextTop;
        Which.TOP.setText(mTextTop);

    }

    public String getTextLeftTop() {
        return mTextLeftTop;
    }

    public void setTextLeftTop(String textLeftTop) {
        this.mTextLeftTop = mTextLeftTop;
        Which.LEFTTOP.setText(textLeftTop);
    }

    public String getTextRightTop() {
        return mTextRightTop;
    }

    public void setTextRightTop(String textRightTop) {
        this.mTextRightTop = mTextRightTop;
        Which.RIGHTTOP.setText(textRightTop);
    }

    public String getTextLeftBottom() {
        return mTextLeftBottom;
    }

    public void setTextLeftBottom(String textLeftBottom) {
        this.mTextLeftBottom = mTextLeftBottom;
        Which.LEFTBOTTOM.setText(textLeftBottom);
    }

    public String getTextRightBottom() {
        return mTextRightBottom;
    }

    public void setTextRightBottom(String textRightBottom) {
        this.mTextRightBottom = mTextRightBottom;
        Which.RIGHTBOTTOM.setText(textRightBottom);
    }


    /**
     * @param text 要画的内容
     */
    public void drawText(String text, P p, int textcolor, int alpha, float textsize) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        float x = p.x;
        float y = p.y;
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setDither(true);
        textPaint.setTextSize(textsize);
        textPaint.setColor(textcolor);
        if (mCurrentState == State.ALPHA) {
            textPaint.setAlpha(mBigAlpha);

        } else {
            textPaint.setAlpha(alpha);
        }
        int l = text.length();
        if (l <= 3) {
            mBPCanvas.save();
            staticLayout = new StaticLayout(text, textPaint, (int) (2 * mRadius), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, true);
            mBPCanvas.translate(x - textPaint.measureText(text) / 2, y - staticLayout.getHeight() / 2);
            staticLayout.draw(mBPCanvas);
            mBPCanvas.restore();
        } else {
            String s1 = "";
            String s2 = "";
            switch (l) {
                case 4: {
                    s1 = text.substring(0, 2);
                    s2 = text.substring(2, 4);
                    break;
                }
                case 5: {
                    s1 = text.substring(0, 3);
                    s2 = text.substring(3, 5);
                    break;
                }
                case 6:
                    s1 = text.substring(0, 3);
                    s2 = text.substring(3, 6);
                    break;
                default:
                    break;
            }
            mBPCanvas.save();
            staticLayout = new StaticLayout(s1, textPaint, (int) (4 * mRadius), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, true);
            mBPCanvas.translate(x - textPaint.measureText(s1) / 2, y - staticLayout.getHeight());
            staticLayout.draw(mBPCanvas);
            mBPCanvas.restore();
            mBPCanvas.save();
            staticLayout = new StaticLayout(s2, textPaint, (int) (4 * mRadius), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, true);
            mBPCanvas.translate(x - textPaint.measureText(s2) / 2, y);
            staticLayout.draw(mBPCanvas);
            mBPCanvas.restore();
        }
    }

    public enum Which {
        TOP(""),
        LEFTTOP(""),
        RIGHTTOP(""),
        LEFTBOTTOM(""),
        RIGHTBOTTOM(""),
        NONE("");
        public String text;

        public void setText(String text) {
            this.text = text;
        }

        Which(String text) {
            this.text = text;
        }

    }
}
