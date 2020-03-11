package com.check.viewdraghelper.recycview.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.check.viewdraghelper.R;

/**
 * Create By 刘胡来
 * Create Date 2020-03-10
 * Sensetime@Copyright
 * Des: 使用ItemDecoration 实现时间轴的
 */
public class TimeLineItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private Paint circlePaint;
    private Paint linePaint;
    private int radius;
    private int curPosition = 0;   // 当前进行中的位置

    public TimeLineItemDecoration(Context context) {
        this.context = context;
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        circlePaint.setStyle(Paint.Style.FILL);
        radius = dp2Px(8);
        circlePaint.setStrokeWidth(dp2Px(2));


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
        linePaint.setStrokeWidth(dp2Px(2));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = dp2Px(20);
        outRect.left = dp2Px(50);
        outRect.right = dp2Px(20);

    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        int childCount = parent.getChildCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int leftDecorationWidth = layoutManager.getLeftDecorationWidth(childView);
            int topDecorationHeight = layoutManager.getTopDecorationHeight(childView);
            // 获取当前 item 是 recyclerview 的第几个 childview
            int childLayoutPosition = parent.getChildLayoutPosition(childView);
            float startX = leftDecorationWidth / 2;
            float stopX = startX;
            // 圆顶部部分竖线，起点 Y
            float topStartY = childView.getTop() - topDecorationHeight;
            // 圆顶部部分竖线，终点 Y
            float topStopY = childView.getTop() + childView.getHeight() / 2 - radius;

            // 圆底部部分竖线，起点 Y
            float bottomStartY = childView.getTop() + childView.getHeight() / 2 + radius;
            // 圆底部部分竖线，终点 Y
            float bottomStopY = childView.getBottom();

            // 位置超过 curPosition 时，竖线颜色设置为浅色
            if (childLayoutPosition > curPosition) {
                linePaint.setColor(context.getResources().getColor(R.color.colorPrimaryTint));
                circlePaint.setColor(context.getResources().getColor(R.color.colorPrimaryTint));
                circlePaint.setStyle(Paint.Style.STROKE);
            } else {
                linePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
                circlePaint.setColor(context.getResources().getColor(R.color.colorPrimary));
                circlePaint.setStyle(Paint.Style.FILL);
            }

            // 绘制圆
            if (childLayoutPosition == curPosition) {
                circlePaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(leftDecorationWidth / 2, childView.getTop() + childView.getHeight() / 2, dp2Px(2), circlePaint);
            }
            canvas.drawCircle(leftDecorationWidth / 2, childView.getTop() + childView.getHeight() / 2, radius, circlePaint);

            // 绘制竖线 , 第 0 位置上只需绘制 下半部分
            if (childLayoutPosition == 0) {
                // 当前 item position = curPosition 时，绘制下半部分竖线时，颜色设置为浅色
                if (childLayoutPosition == curPosition) {
                    linePaint.setColor(context.getResources().getColor(R.color.colorPrimaryTint));
                }
                canvas.drawLine(startX, bottomStartY, startX, bottomStopY, linePaint);
                // 最后位置上，只需绘制上半部分
            } else if (childLayoutPosition == parent.getAdapter().getItemCount() - 1) {
                canvas.drawLine(startX, topStartY, startX, topStopY, linePaint);
            } else {
                // 都要绘制
                canvas.drawLine(startX, topStartY, startX, topStopY, linePaint);
                // 当前 item position = curPosition 时，绘制下半部分竖线时，颜色设置为浅色
                if (childLayoutPosition == curPosition) {
                    linePaint.setColor(context.getResources().getColor(R.color.colorPrimaryTint));
                }
                canvas.drawLine(startX, bottomStartY, startX, bottomStopY, linePaint);
            }
        }

    }

    /**
     * 设置进行中的位置
     *
     * @param recyclerView
     * @param position
     */
    public void setDoingPosition(RecyclerView recyclerView, int position) {
        if (recyclerView == null) {
            throw new IllegalArgumentException("RecyclerView can't be null");
        }
        if (recyclerView.getAdapter() == null) {
            throw new IllegalArgumentException("RecyclerView Adapter can't be null");
        }
        if (position < 0) {
            throw new IllegalArgumentException("position can't be less than 0");
        }
        recyclerView.getLayoutManager().getItemCount();
        if (position > recyclerView.getAdapter().getItemCount() - 1) {
            throw new IllegalArgumentException("position can't be greater than item count");
        }
        this.curPosition = position;
    }

    private int dp2Px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
}
