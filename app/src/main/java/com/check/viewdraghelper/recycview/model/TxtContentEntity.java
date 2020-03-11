package com.check.viewdraghelper.recycview.model;


import com.check.viewdraghelper.recycview.ListDataConfig;

/**
 * Create By 刘铁柱
 * Create Date 2019-12-27
 * Sensetime@Copyright
 * Des:
 */
public class TxtContentEntity extends BaseData {

    private String content;
    private int color;
    private int size;
    private boolean center; //文本是否居中

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    @Override
    public int getItemType() {
        return ListDataConfig.TXT_CONTENT;
    }
}
