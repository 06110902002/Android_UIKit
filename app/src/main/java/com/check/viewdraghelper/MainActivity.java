package com.check.viewdraghelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.check.viewdraghelper.drag.DragActivity;

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

    }

    private class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_drag:
                    startActivity(new Intent(MainActivity.this, DragActivity.class));
                    break;

                case R.id.btn_elastic:
                    startActivity(new Intent(MainActivity.this, ElasticActivity.class));
                    break;

                case R.id.btn_load_tip:
                    startActivity(new Intent(MainActivity.this, RecycViewActivity.class));

                    break;

                case R.id.btn_tab_recyc:
                    startActivity(new Intent(MainActivity.this, TabLayoutRecycActivity.class));
                    break;
            }
        }
    }
}
