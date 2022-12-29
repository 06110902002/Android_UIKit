package com.check.viewdraghelper;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.check.viewdraghelper.loadanim.LoadAnimView;
import com.check.viewdraghelper.loadanim.SaleProgressView;

/**
 * Create By 刘胡来
 * Create Date 2020-03-10
 * Sensetime@Copyright
 * Des:
 */
public class LoadAnimActivity extends Activity {

    private LoadAnimView progress;
    private Button btnLoading,btnSuccess,btnFail;
    private SaleProgressView saleProgressView;
    int  i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loadanim);
        progress = findViewById(R.id.progress);

        findViewById(R.id.success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setStatus(LoadAnimView.Status.LoadSuccess);
                progress.startAnima();
            }
        });

        findViewById(R.id.fail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setStatus(LoadAnimView.Status.LoadFail);
                progress.failAnima();
            }
        });


        saleProgressView = findViewById(R.id.txt_pro);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(i <= 100) {

                    LoadAnimActivity.this.runOnUiThread((new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {

                            float pro = i / 100.f;
                            saleProgressView.setCurPercentage(i / 100.f);
                            saleProgressView.setText(pro * 100 + "%");

                            LinearGradient linearGradient = new LinearGradient(0, 0, saleProgressView.getWidth(), 0,
                                    getColor(R.color.color_00B8CC), getColor(R.color.color_00D7F0),
                                    Shader.TileMode.CLAMP);
                            saleProgressView.setProgressShader(linearGradient);
                        }
                    }));
                    i ++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
