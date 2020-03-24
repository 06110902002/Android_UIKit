package com.check.viewdraghelper.okhttpmgr;

/**
 * Create By 刘胡来
 * Create Date 2020-03-19
 * Sensetime@Copyright
 * Des:
 */
public interface ReqCallBack<T> {
    /**
     * 响应成功
     */
    void onReqSuccess(T result);

    /**
     * 响应失败
     */
    void onReqFailed(String errorMsg);
}