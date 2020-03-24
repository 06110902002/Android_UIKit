package com.check.viewdraghelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.check.viewdraghelper.customerboom.BoomActivity;
import com.check.viewdraghelper.drag.DragActivity;
import com.check.viewdraghelper.pathanim.PathAnimActivity;

import me.samlss.bloom.Bloom;
import me.samlss.bloom.effector.BloomEffector;
import me.samlss.bloom.shape.distributor.CircleShapeDistributor;
import tyrantgit.explosionfield.ExplosionField;


/**
 * 爆炸效果：https://github.com/tyrantgit/ExplosionField
 */
public class MainActivity extends AppCompatActivity {

    private Button btnScroller,btnElatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test1);
        initView();

    }

    private void initView(){
        btnElatic = findViewById(R.id.btn_elastic);
        btnScroller = findViewById(R.id.btn_drag);

        ClickListener onClick = new ClickListener();
        btnScroller.setOnClickListener(onClick);
        btnElatic.setOnClickListener(onClick);
        findViewById(R.id.btn_load_tip).setOnClickListener(onClick);
        findViewById(R.id.btn_tab_recyc).setOnClickListener(onClick);
        findViewById(R.id.btn_file_down_up).setOnClickListener(onClick);
        findViewById(R.id.btn_boom).setOnClickListener(onClick);
        findViewById(R.id.btn_path).setOnClickListener(onClick);

    }

    private class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_drag:
                    //startActivity(new Intent(MainActivity.this, DragActivity.class));
                    Bloom.with(MainActivity.this)
                            .setParticleRadius(5)
                            .setShapeDistributor(new CircleShapeDistributor())
                            .setEffector(new BloomEffector.Builder()
                                    .setDuration(800)
                                    .setAnchor(view.getWidth() / 2, view.getHeight() / 2)
                                    .build())
                            .boom(view);
                    break;

                case R.id.btn_elastic:
                    startActivity(new Intent(MainActivity.this, ElasticActivity.class));
                    break;

                case R.id.btn_load_tip:
                    startActivity(new Intent(MainActivity.this, LoadAnimActivity.class));
                    break;

                case R.id.btn_tab_recyc:
                    startActivity(new Intent(MainActivity.this, TabLayoutRecycActivity.class));
                    break;

                case R.id.btn_file_down_up:
                    startActivity(new Intent(MainActivity.this, FileUploadDownLoadActivity.class));
                    break;

                case R.id.btn_boom:
                    startActivity(new Intent(MainActivity.this, BoomActivity.class));

                    break;

                case R.id.btn_path:
                    startActivity(new Intent(MainActivity.this, PathAnimActivity.class));
                    break;

            }
        }
    }
}
