package com.check.viewdraghelper.recycview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.check.viewdraghelper.R;
import com.check.viewdraghelper.recycview.ListDataConfig;
import com.check.viewdraghelper.recycview.ViewHolder;
import com.check.viewdraghelper.recycview.model.BaseData;
import com.check.viewdraghelper.recycview.model.TipsEntity;
import com.check.viewdraghelper.recycview.view.TxtContentView;
import com.check.viewdraghelper.recycview.view.UserGuideView;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des: 新手引导适配器
 */
public class UserGuideAdapter extends RecyclerViewBaseAdapter {

    public UserGuideAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ListDataConfig.GUIDE){
            return new UserGuideView(this.context, parent);
        }else if(viewType == ListDataConfig.TXT_CONTENT){
            return new TxtContentView(this.context,parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int i = holder.getAdapterPosition();
        if(getDataList().get(i).getItemType() == ListDataConfig.GUIDE){
            String name =((TipsEntity)getDataList().get(i)).getTips();
            System.out.println("39------------onViewAttachedToWindow:"+i + " name:"+name);

            if(listener != null){
                listener.onVisiable(i,holder.itemView,getDataList().get(i));
            }
        }

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        int i = holder.getAdapterPosition();
        System.out.println("39------------onViewDetachedFromWindow:"+i);
        if(listener != null){
            listener.onInVisiable(i,holder.itemView,getDataList().get(i));
        }
    }


    private ItemStatusChangeListener listener;
    public void setItemStatusListener(ItemStatusChangeListener listener){
        this.listener = listener;
    }
    public interface ItemStatusChangeListener{

        void onVisiable(int position, View view, BaseData data);
        void onInVisiable(int position, View view, BaseData data);


    }
}
