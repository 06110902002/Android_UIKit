package com.check.viewdraghelper.customerboom;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.R;
import com.check.viewdraghelper.arcanim.CircleMenuView;

/**
 * Create By 刘胡来
 * Create Date 2020-03-23
 * Sensetime@Copyright
 * Des:
 */
public class BoomActivity extends Activity {

    ExplosionField explosionField;
    CircleMenuView v;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_boom);

        explosionField = new ExplosionField(BoomActivity.this);
        // 绑定哪个控件哪个控件就有效果，如果需要整个layout，只要绑定根布局的id即可
        explosionField.addListener(findViewById(R.id.iv_round));


        initArcAnim();
    }

    private void initArcAnim(){
        v = findViewById(R.id.sample_text);
        v.setZOrderOnTop(true);
        v.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        v.setOnMenuClickListener(new CircleMenuView.OnMenuClickListener() {
            @Override
            public void onMenuClick(CircleMenuView.Which which) {
            }
        });

    }


    public void showme(View view) {
        v.appear();
    }

    public void disappear(View view) {
        v.disAppear();
    }

}
