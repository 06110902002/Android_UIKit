package com.check.viewdraghelper;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.check.viewdraghelper.recycview.ListDataConfig;
import com.check.viewdraghelper.recycview.adapter.UserGuideAdapter;
import com.check.viewdraghelper.recycview.itemdecoration.BottomFloatItemDecoration;
import com.check.viewdraghelper.recycview.itemdecoration.ImgItemDecorationItem;
import com.check.viewdraghelper.recycview.itemdecoration.TimeLineItemDecoration;
import com.check.viewdraghelper.recycview.model.BaseData;
import com.check.viewdraghelper.recycview.model.TipsEntity;

/**
 * Create By 刘胡来
 * Create Date 2020-03-10
 * Sensetime@Copyright
 * Des:
 */
public class RecycViewActivity extends Activity {

    private RecyclerView recyclerView;
    private UserGuideAdapter userGuideAdapter;
    private ImageView imgTips;
    private int curInvisiableIndex = -1;
    private int curVisiable = -1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_user_guide);
        recyclerView = findViewById(R.id.recyc_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userGuideAdapter = new UserGuideAdapter(this);
        buidData();
        recyclerView.setAdapter(userGuideAdapter);
        imgTips = findViewById(R.id.img_tips2);



        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)imgTips.getLayoutParams();

                v.getId();
                int dy = scrollY - oldScrollY;
                if(dy > 0){
                    System.out.println("43----------up:"+dy);
                    layoutParams.topMargin --;
                }else if(dy < 0){
                    layoutParams.topMargin ++;
                    System.out.println("43----------down:"+dy);

                }

                imgTips.setLayoutParams(layoutParams);

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int childCount = recyclerView.getChildCount();
                for (int i = 0; i < childCount; i++){
                    View view = recyclerView.getChildAt(i);
                    int position = recyclerView.getChildAdapterPosition(view);
                    if(userGuideAdapter.getDataList().get(position).getItemType() == ListDataConfig.GUIDE){
                        TipsEntity tipsEntity = (TipsEntity)userGuideAdapter.getDataList().get(position);

                        System.out.println("86----------:"+tipsEntity.getTips());

                    }
                }

            }
        });


    }

    private void buidData(){
        for(int i = 0; i < 20; i ++){
            TipsEntity tipsEntity = new TipsEntity();
            tipsEntity.setTips(""+i);
            tipsEntity.setImgResourceId(R.mipmap.location_icon);
            tipsEntity.setCenter(false);
            userGuideAdapter.addData(tipsEntity);
        }

        userGuideAdapter.setItemStatusListener(new UserGuideAdapter.ItemStatusChangeListener() {
            @Override
            public void onVisiable(int position, View view, BaseData data) {
                curVisiable = position;
            }

            @Override
            public void onInVisiable(int position, View view, BaseData data) {
                curInvisiableIndex = position;

            }
        });
    }

    private void drawTimeLine(){
        TimeLineItemDecoration decoration = new TimeLineItemDecoration(this);
        decoration.setDoingPosition(recyclerView, 0);
        recyclerView.addItemDecoration(decoration);
    }

    private void drawImg(){
        ImgItemDecorationItem decoration = new ImgItemDecorationItem(this);
        recyclerView.addItemDecoration(decoration);
    }

    private void drawBottomNav(){
        BottomFloatItemDecoration decoration = new BottomFloatItemDecoration(this);
        recyclerView.addItemDecoration(decoration);
    }
}
