package com.check.viewdraghelper.recycview.model;


import com.check.viewdraghelper.recycview.ViewDataType;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des:列表中的数据基类
 */
public class BaseData implements ViewDataType {

    @Override
    public int getItemType() {
        return 0;
    }
}
