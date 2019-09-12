package com.check.viewdraghelper.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

/**
 * Create By 刘铁柱
 * Create Date 2019-09-12
 * Sensetime@Copyright
 * Des:
 */
public class MyDragLinearLayout extends LinearLayout {


    private ViewDragHelper mViewDragHelper;

    public MyDragLinearLayout(Context context) {
        this(context, null);
    }

    public MyDragLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDragLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        MyDragHelper dragHelper = new MyDragHelper();
        mViewDragHelper = ViewDragHelper.create(this, dragHelper);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    public class MyDragHelper extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //注意这里一定要返回true，否则后续的拖拽回调是不会生效的
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }
}
