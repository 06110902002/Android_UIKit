package com.check.viewdraghelper.recycview.model;

import com.check.viewdraghelper.recycview.ListDataConfig;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des:
 */
public class EmptyEntity extends BaseData {

    private String emptyTips;

    public String getEmptyTips() {
        return emptyTips;
    }

    public void setEmptyTips(String emptyTips) {
        this.emptyTips = emptyTips;
    }

    @Override
    public int getItemType() {
        return ListDataConfig.EMPTY_TIPS;
    }
}
