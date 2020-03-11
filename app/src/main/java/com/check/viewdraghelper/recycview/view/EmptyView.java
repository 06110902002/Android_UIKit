package com.check.viewdraghelper.recycview.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.check.viewdraghelper.R;
import com.check.viewdraghelper.recycview.ViewHolder;
import com.check.viewdraghelper.recycview.model.EmptyEntity;


/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des: 列表空数据占位视图
 */
public class EmptyView extends ViewHolder<EmptyEntity> {


    public EmptyView(Context context, ViewGroup parent) {
        super(context, parent, R.layout.layout_user_guide_item);
        mContext = context;
    }

    @Override
    public void setData(EmptyEntity data) {

    }

    @Override
    public void findView(View itemView) {
    }
}
