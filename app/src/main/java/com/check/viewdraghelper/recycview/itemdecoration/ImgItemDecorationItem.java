package com.check.viewdraghelper.recycview.itemdecoration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
 * Des:
 */
public class ImgItemDecorationItem extends RecyclerView.ItemDecoration {

    private static final String TAG = "LinearItemDecoration";
    private Paint paint;
    private Paint dividerPaint;
    private Bitmap iconBitmap;

    public ImgItemDecorationItem(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(Color.parseColor("#e6e6e6"));
        dividerPaint.setStyle(Paint.Style.FILL);

        iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.location_icon);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = 5;
        outRect.left = 100;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            int leftDecorationWidth = layoutManager.getLeftDecorationWidth(childView);
            int bottomDecorationHeight = layoutManager.getBottomDecorationHeight(childView);
            int left = leftDecorationWidth / 2;
//            canvas.drawCircle(left, childView.getTop() + childView.getHeight() / 2, 20, paint);
//            canvas.drawCircle(leftDecorationWidth, childView.getTop() + childView.getHeight() / 2, 20, paint);

            // getItemOffsets()中的设置的是 bottom = 5px;所以在 drawRect 时，top 为 childView.getBottom,bottom为top+bottomDecorationHeight
            canvas.drawRect(new Rect(
                    leftDecorationWidth,
                    childView.getBottom(),
                    childView.getWidth() + leftDecorationWidth,
                    childView.getBottom() + bottomDecorationHeight
            ), dividerPaint);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            int leftDecorationWidth = layoutManager.getLeftDecorationWidth(childView);
            canvas.drawBitmap(iconBitmap, leftDecorationWidth - iconBitmap.getWidth() / 2,
                    childView.getTop() + childView.getHeight() / 2 - iconBitmap.getHeight() / 2, paint);
        }
    }
}
