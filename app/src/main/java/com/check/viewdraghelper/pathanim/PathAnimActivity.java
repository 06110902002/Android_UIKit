package com.check.viewdraghelper.pathanim;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.R;
import com.check.viewdraghelper.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Create By 刘胡来
 * Create Date 2020-03-24
 * Sensetime@Copyright
 * Des:
 */
public class PathAnimActivity extends Activity {

    private Button fab;
    private AnimatorPath path;//声明动画集合
    private AnimatorPath path2;//声明动画集合
    private AnimatorPath path3;//声明动画集合

    private BubbleView bubbleView;
    private BubbleView bubbleView1;
    private BubbleView bubbleView2;
    private FrameLayout layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pathanim);
        layout = findViewById(R.id.layout_parent);
        fab = findViewById(R.id.fab);
        bubbleView = findViewById(R.id.bubble);
        bubbleView.setTextContent("5g");
        buildBubbleView();



        setPath();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimatorPath(bubbleView, "fab", path);
                startAnimatorPath(bubbleView1, "fab2", path2);
                startAnimatorPath(bubbleView2, "fab3", path3);
            }
        });
    }

    private void buildBubbleView(){

        int width = Utils.dp2px(this,100);
        bubbleView1 = new BubbleView(this,"6g");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,width);
        bubbleView1.setLayoutParams(params);
        layout.addView(bubbleView1);

        bubbleView2 = new BubbleView(this,"7g");
        bubbleView2.setLayoutParams(params);
        layout.addView(bubbleView2);

    }

    /*设置动画路径*/
    public void setPath(){
        path = new AnimatorPath();
        //path.moveTo(0,0);
//        path.lineTo(400,400);
//        path.secondBesselCurveTo(600, 200, 800, 400); //订单
//        path.thirdBesselCurveTo(100,600,900,1000,200,1200);

        Point center = new Point((560 + 100) / 2,(560 + 100) / 2);
        int radius = (560 - 100) / 5;
        path.moveTo(center.x + radius * (float)Math.cos(50.0),center.y + radius * (float)Math.sin(50.0));  //从原点出发画一条贝塞尔曲线
        path.secondBesselCurveTo(660, 160, 760, 460);

        path2 = new AnimatorPath();
        path2.moveTo(center.x + radius * (float)Math.cos(150.0),center.y + radius * (float)Math.sin(150.0));  //从原点出发画一条贝塞尔曲线
        path2.secondBesselCurveTo(660, 260, 560, 60);

        path3 = new AnimatorPath();
        path3.moveTo(center.x - radius * (float)Math.cos(220.0),center.y - radius * (float)Math.sin(220.0));  //从原点出发画一条贝塞尔曲线
        path3.secondBesselCurveTo(60, 160, 60, 860);


    }

    /**
     * 设置动画
     * @param view 使用动画的View
     * @param propertyName 属性名字
     * @param path 动画路径集合
     */
    private void startAnimatorPath( BubbleView view, String propertyName, AnimatorPath path) {
        ObjectAnimator anim = ObjectAnimator.ofObject(this, propertyName, new PathEvaluator(), path.getPoints().toArray());
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(800);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.startWaveAnim(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    /**
     * 设置View的属性通过ObjectAnimator.ofObject()的反射机制来调用
     * @param newLoc
     */
    public void setFab(PathPoint newLoc) {
//        fab.setTranslationX(newLoc.mX);
//        fab.setTranslationY(newLoc.mY);

        bubbleView.setTranslationX(newLoc.mX);
        bubbleView.setTranslationY(newLoc.mY);
    }

    public void setFab2(PathPoint newLoc){
        bubbleView1.setTranslationX(newLoc.mX);
        bubbleView1.setTranslationY(newLoc.mY);
    }

    public void setFab3(PathPoint newLoc){
        bubbleView2.setTranslationX(newLoc.mX);
        bubbleView2.setTranslationY(newLoc.mY);
    }

}
