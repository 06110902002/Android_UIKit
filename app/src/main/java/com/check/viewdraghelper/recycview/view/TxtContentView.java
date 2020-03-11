package com.check.viewdraghelper.recycview.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.check.viewdraghelper.R;
import com.check.viewdraghelper.recycview.model.TxtContentEntity;
import com.check.viewdraghelper.recycview.ViewHolder;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-27
 * Sensetime@Copyright
 * Des:
 */
public class TxtContentView  extends ViewHolder<TxtContentEntity> {

    private TextView txtContent;

    public TxtContentView(Context context, ViewGroup parent) {
        super(context, parent, R.layout.layout_txt_content_item);
        mContext = context;
    }

    @Override
    public void setData(TxtContentEntity data) {
        if(data != null){
            txtContent.setText(data.getContent());
        }
    }

    @Override
    public void findView(View itemView) {
        txtContent = itemView.findViewById(R.id.txt_content);
    }
}