package com.check.viewdraghelper.okhttpmgr;

/**
 * Create By 刘胡来
 * Create Date 2020-03-19
 * Sensetime@Copyright
 * Des:
 */
public interface ReqProgressCallBack<T>  extends ReqCallBack<T>{
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
}
