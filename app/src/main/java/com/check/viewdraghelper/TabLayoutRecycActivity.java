package com.check.viewdraghelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.check.viewdraghelper.recycview.adapter.UserGuideAdapter;
import com.check.viewdraghelper.recycview.model.BaseData;
import com.check.viewdraghelper.recycview.model.TipsEntity;
import com.check.viewdraghelper.recycview.model.TxtContentEntity;
import com.google.android.material.tabs.TabLayout;

/**
 * Create By 刘胡来
 * Create Date 2020-03-11
 * Sensetime@Copyright
 * Des: Tablayout +ReyclerView实现 联动滚动
 */
public class TabLayoutRecycActivity extends Activity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private UserGuideAdapter userGuideAdapter;
    private LinearLayoutManager manager;
    private String[] tabTxt = {"客厅", "卧室", "餐厅", "书房", "阳台", "儿童房"};
    //判读是否是recyclerView主动引起的滑动，true- 是，false- 否，由tablayout引起的
    private boolean isRecyclerScroll;
    //记录上一次位置，防止在同一内容块里滑动 重复定位到tablayout
    private int lastPos;
    //用于recyclerView滑动到指定的位置
    private boolean canScroll;
    private int scrollToPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recyc_tablayout);

        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tablayout);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        userGuideAdapter = new UserGuideAdapter(this);
        buidData();
        recyclerView.setAdapter(userGuideAdapter);

        for (int i = 0; i < tabTxt.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTxt[i]));
        }



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //点击标签，使recyclerView滑动，isRecyclerScroll置false
                int pos = tab.getPosition();
                isRecyclerScroll = false;
                moveToPosition(manager, recyclerView, pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当滑动由recyclerView触发时，isRecyclerScroll 置true
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isRecyclerScroll = true;
                }
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (canScroll) {
                    canScroll = false;
                    // moveToPosition(manager, recyclerView, scrollToPosition);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isRecyclerScroll) {
                    //第一个可见的view的位置，即tablayou需定位的位置
                    int position = manager.findFirstVisibleItemPosition();
                    if (lastPos != position) {
                        tabLayout.setScrollPosition(position, 0, true);
                    }
                    lastPos = position;
                }
            }
        });


    }


    public void moveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int position) {
        // 第一个可见的view的位置
        int firstItem = manager.findFirstVisibleItemPosition();
        // 最后一个可见的view的位置
        int lastItem = manager.findLastVisibleItemPosition();
        if (position <= firstItem) {
            // 如果跳转位置firstItem 之前(滑出屏幕的情况)，就smoothScrollToPosition可以直接跳转，
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 跳转位置在firstItem 之后，lastItem 之间（显示在当前屏幕），smoothScrollBy来滑动到指定位置
            int top = mRecyclerView.getChildAt(position - firstItem).getTop();
            mRecyclerView.smoothScrollBy(0, top);
        } else {
            // 如果要跳转的位置在lastItem 之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用当前moveToPosition方法，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            scrollToPosition = position;
            canScroll = true;
        }
    }

    private void buidData(){
        for(int i = 0; i < tabTxt.length; i ++){
            TxtContentEntity tipsEntity = new TxtContentEntity();
            tipsEntity.setContent(tabTxt[i]);
            userGuideAdapter.addData(tipsEntity);
        }
    }
}
