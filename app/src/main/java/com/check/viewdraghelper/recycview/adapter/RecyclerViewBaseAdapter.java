package com.check.viewdraghelper.recycview.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.check.viewdraghelper.recycview.ListDataConfig;
import com.check.viewdraghelper.recycview.ViewHolder;
import com.check.viewdraghelper.recycview.model.BaseData;
import com.check.viewdraghelper.recycview.view.EmptyView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des:
 */
public class RecyclerViewBaseAdapter extends RecyclerView.Adapter<ViewHolder> {


    protected Context context;
    private List<BaseData> dataList;

    public RecyclerViewBaseAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<? extends BaseData> append) {
        getDataList().addAll(append);
    }

    public void appendData(List<? extends BaseData> append){
        getDataList().addAll(union(getDataList(),append));
    }

    public void appendData(BaseData BaseData){
        getDataList().add(BaseData);
    }

    /**
     * 获取列表并集
     *
     * @param list1
     * @param list2
     * @return
     */
    private List<BaseData> union(List<? extends BaseData> list1, List<? extends BaseData> list2){
        Set<BaseData> mTmp = new HashSet<>();
        mTmp.addAll(list1);
        mTmp.addAll(list2);

        List<BaseData> newList = new ArrayList<>();
        newList.addAll(mTmp);
        return newList;
    }

    public void addData(BaseData vhModel) {
        List<BaseData> list = new ArrayList<>();
        list.add(vhModel);
        addData(list);
    }

    public void clean() {
        getDataList().clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ListDataConfig.EMPTY_TIPS || getDataList().size() == 0) {
            return new EmptyView(context, parent);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(getDataList().get(position));
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    /**
     * 获取视图类型，从数据中获取
     * 即从数据中设定一个类型用来区分不同的视图
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return getDataList().get(position).getItemType();
    }

    public List<BaseData> getDataList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

}

