package com.check.viewdraghelper.recycview.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.check.viewdraghelper.R;
import com.check.viewdraghelper.recycview.ViewHolder;
import com.check.viewdraghelper.recycview.model.TipsEntity;
import com.check.viewdraghelper.utils.Utils;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des: 新手引导视图
 */
public class UserGuideView extends ViewHolder<TipsEntity> {

    private TextView txtTips;
    private ImageView imgTips;

    public UserGuideView(Context context, ViewGroup parent) {
        super(context, parent, R.layout.layout_user_guide_item);
        mContext = context;
    }

    @Override
    public void setData(TipsEntity data) {

        if(data != null){
            txtTips.setText(data.getTips());
            imgTips.setBackgroundResource(data.getImgResourceId());
            if(data.isCenter()){
                txtTips.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }else{
                int padding = Utils.dp2px(mContext,20);
                txtTips.setPadding(padding,0,0,0);
            }

            if(data.getLineSpace() != 0.0f){
                SpannableString msp = new SpannableString(data.getTips());
                //msp.setSpan(new AbsoluteSizeSpan(20,true), 34, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，同上。
                msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 34, data.getTips().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
                txtTips.setLineSpacing(0,data.getLineSpace());
                txtTips.setText(msp);

            }
        }
    }

    @Override
    public void findView(View itemView) {
        txtTips = itemView.findViewById(R.id.txt_tips);
        imgTips = itemView.findViewById(R.id.img_tips);
    }
}
