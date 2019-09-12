package com.check.viewdraghelper.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.check.viewdraghelper.R;

/**
 * Create By 刘铁柱
 * Create Date 2019-09-12
 * Sensetime@Copyright
 * Des: ViewDragHelper 实现拖动伸缩菜单
 */
public class MyDragViewLayout extends ViewGroup {
    public ViewDragHelper mViewDragHelper;
    private boolean isOpen = true;
    private View mMenuView;
    private View mContentView;
    private int mCurrentTop = 0;
    public MyDragViewLayout(Context context) {
        super(context);
        init();
    }

    public MyDragViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyDragViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        //ViewDragHelper静态方法传入ViewDragHelperCallBack创建
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
//          mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
    }
    //实现ViewDragHelper.Callback相关方法
    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //返回ture则表示可以捕获该view
           return child == mContentView;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            //setEdgeTrackingEnabled设置的边界滑动时触发
            //通过captureChildView对其进行捕获，该方法可以绕过tryCaptureView

            //mViewDragHelper.captureChildView(mContentView, pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //手指触摸移动时回调, left表示要到的x坐标
            return super.clampViewPositionHorizontal(child, left, dx);//
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //手指触摸移动时回调 top表示要到达的y坐标
            return Math.max(Math.min(top, mMenuView.getHeight()), 0);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //手指抬起释放时回调
            int finalTop = mMenuView.getHeight();
            if(yvel <= 0){
                if(releasedChild.getTop()< mMenuView.getHeight()/2){
                    finalTop = 0;
                }else{
                    finalTop = mMenuView.getHeight();
                }
            }else{
                if(releasedChild.getTop() > mMenuView.getHeight()/2){
                    finalTop = mMenuView.getHeight();
                }else{
                    finalTop = 0;
                }
            }
            mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), finalTop);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //mDrawerView完全覆盖屏幕则防止过度绘制
            mMenuView.setVisibility((changedView.getHeight() - top == getHeight()) ? View.GONE : View.VISIBLE);
            mCurrentTop +=dy;
            requestLayout();
        }
        @Override
        public int getViewVerticalDragRange(View child) {
            if (mMenuView == null) return 0;
            return (mContentView == child) ? mMenuView.getHeight() : 0;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                isOpen = (mContentView.getTop() == mMenuView.getHeight());
            }
        }
    }
    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
    public boolean isDrawerOpened() {
        return isOpen;
    }
    //onInterceptTouchEvent方法调用ViewDragHelper.shouldInterceptTouchEvent
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    //onTouchEvent方法中调用ViewDragHelper.processTouchEvent方法并返回true
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView = getChildAt(0);
        mContentView = getChildAt(1);

        findViewById(R.id.txt_content).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (mViewDragHelper.smoothSlideViewTo(mContentView, getPaddingLeft(), 0)) {
                    ViewCompat.postInvalidateOnAnimation(MyDragViewLayout.this);
                }
            }
        });

        findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mViewDragHelper.smoothSlideViewTo(mContentView, getPaddingLeft(), mMenuView.getHeight())) {
                    ViewCompat.postInvalidateOnAnimation(MyDragViewLayout.this);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mMenuView.layout(0, 0,
                mMenuView.getMeasuredWidth(),
                mMenuView.getMeasuredHeight());
        System.out.println("175---------:"+mCurrentTop);
        mContentView.layout(0, mCurrentTop + mMenuView.getHeight(),
                mContentView.getMeasuredWidth(),
                mCurrentTop + mContentView.getMeasuredHeight() + mMenuView.getHeight());

    }
}
