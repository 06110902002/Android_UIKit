package com.check.viewdraghelper.scroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.LogUtils;
import com.check.viewdraghelper.R;

/**
 * Create By 刘铁柱
 * Create Date 2019-09-11
 * Sensetime@Copyright
 * Des: 使用Scroller实现 垂直可伸缩菜单
 */
public class ElasticLayout extends LinearLayout {

    LinearLayout bottomBar,bottomContent;
    private Scroller mScroller;
    public ElasticLayout(Context context) {
        super(context);
    }

    public ElasticLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public ElasticLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        bottomContent = findViewById(R.id.bottomContent);
        bottomBar = findViewById(R.id.bottombar);
        findViewById(R.id.bottom_btn).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                LogUtils.print(48,"");
            }
        });

        findViewById(R.id.bottom_btn2).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                LogUtils.print(56,"");
            }
        });

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        bottomBar.layout(0, getMeasuredHeight() - bottomBar.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        bottomContent.layout(0, getMeasuredHeight(), getMeasuredWidth(), bottomBar.getBottom() + bottomContent.getMeasuredHeight());
    }

    float downX,downY;
    int scrollOffset;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endY = (int) event.getY();
                int dy = (int) (endY - downY);
                int toScroll = getScrollY() - dy;
                if(toScroll < 0){
                    toScroll = 0;
                } else if(toScroll > bottomContent.getMeasuredHeight()){
                    toScroll = bottomContent.getMeasuredHeight();
                }
                scrollTo(0, toScroll);
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                scrollOffset = getScrollY();
                if(scrollOffset > bottomContent.getMeasuredHeight() / 2){
                    expendBottom();
                } else {
                    closeBottom();
                }
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) { // 计算新位置，并判断上一个滚动是否完成。
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();// 再次调用computeScroll。
        }
    }

    private void expendBottom(){
        int dy = bottomContent.getMeasuredHeight() - scrollOffset;
        mScroller.startScroll(getScrollX(), getScrollY(), 0, dy, 500);
        invalidate();
    }

    private void closeBottom(){
        int dy = 0 - scrollOffset;
        mScroller.startScroll(getScrollX(), getScrollY(), 0, dy, 500);
        invalidate();
    }
}
