package com.check.viewdraghelper.recycview.model;

import com.check.viewdraghelper.recycview.ListDataConfig;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-24
 * Sensetime@Copyright
 * Des:
 */
public class TipsEntity extends BaseData {

    private String tips;
    private int imgResourceId;
    private boolean center;
    private float lineSpace;

    public float getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
    }



    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getImgResourceId() {
        return imgResourceId;
    }

    public void setImgResourceId(int imgResourceId) {
        this.imgResourceId = imgResourceId;
    }

    @Override
    public int getItemType() {
        return ListDataConfig.GUIDE;
    }
}
