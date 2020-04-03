package com.check.viewdraghelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.arcloadview.SwingAnimation;

/**
 * Create By 刘胡来
 * Create Date 2020/4/2
 * Sensetime@Copyright
 * Des:
 */
public class ArcAnimActivity extends Activity {

    private ImageView imgView;
    private SwingAnimation swingAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_arcanim);
        initView();
    }

    private void initView(){
        imgView = findViewById(R.id.arc_view);

        swingAnimation = new SwingAnimation(
                0f, 60f, -60f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
        swingAnimation.setDuration(4000);     //动画持续时间
        swingAnimation.setRepeatCount(10);     //动画重播次数
        swingAnimation.setFillAfter(false);  //是否保持动画结束画面
        swingAnimation.setStartOffset(500);   //动画播放延迟

        imgView.startAnimation(swingAnimation);
    }
}
