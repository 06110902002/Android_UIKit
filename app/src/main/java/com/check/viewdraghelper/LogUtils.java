package com.check.viewdraghelper;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Create By 刘铁柱
 * Create Date 2019-09-05
 * Sensetime@Copyright
 * Des: 日志助手
 */
public class LogUtils {

    public static  boolean isDebug = true;

    public static final int VERBOSE = 5;

    public static final int DEBUG = 4;

    public static final int INFO = 3;

    public static final int WARN = 2;

    public static final int ERROR = 1;

    /**
     * 把异常用来输出日志的综合方法
     *
     * @param @param tag 日志标识
     * @param @param throwable 抛出的异常
     * @param @param type 日志类型
     * @return void 返回类型
     * @throws
     */
    public static void log(String tag, Throwable throwable, int type) {
        log(tag, exToString(throwable), type);
    }

    /**
     * 用来输出日志的综合方法（文本内容）
     *
     * @param @param tag 日志标识
     * @param @param msg 要输出的内容
     * @param @param type 日志类型
     * @return void 返回类型
     * @throws
     */
    public static void log(String tag, String msg, int type) {
        switch (type) {
            case VERBOSE:
                v(tag, msg);
                break;
            case DEBUG:
                d(tag, msg);
                break;
            case INFO:
                i(tag, msg);
                break;
            case WARN:
                w(tag, msg);
                break;
            case ERROR:
                e(tag, msg);
                break;
            default:
                break;
        }
    }

    /**
     * verbose等级的日志输出
     *
     * @param tag
     *            日志标识
     * @param msg
     *            要输出的内容
     * @return void 返回类型
     * @throws
     */
    public static void v(String tag, String msg) {
        // 是否开启日志输出
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    /**
     * debug等级的日志输出
     *
     * @param tag
     *            标识
     * @param msg
     *            内容
     * @return void 返回类型
     * @throws
     */
    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }

    }

    /**
     * info等级的日志输出
     *
     * @param  tag 标识
     * @param  msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    /**
     * warn等级的日志输出
     *
     * @param tag 标识
     * @param msg 内容
     * @return void 返回类型
     * @throws
     */
    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    /**
     * error等级的日志输出
     *
     * @param  tag 标识
     * @param  msg 内容
     * @return void 返回类型
     */
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void print(int line,String log){
        if(isDebug){
            System.out.println(line+"---------:"+log);
        }
    }

    /**
     * 把异常信息转化为字符串
     *
     * @param ex 异常信息
     * @return 异常信息字符串
     */
    private static String exToString(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String result = writer.toString();
        return result;
    }
    
}
