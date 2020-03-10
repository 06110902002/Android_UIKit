package com.check.viewdraghelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.check.viewdraghelper.loadanim.LoadAnimView;

/**
 * Create By 刘胡来
 * Create Date 2020-03-10
 * Sensetime@Copyright
 * Des:
 */
public class LoadAnimActivity extends Activity {

    private LoadAnimView progress;
    private Button btnLoading,btnSuccess,btnFail;

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

    }
}
