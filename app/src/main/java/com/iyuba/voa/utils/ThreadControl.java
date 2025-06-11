package com.iyuba.voa.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/1/13
 * 邮箱：jxfengmtx@gmail.com
 */
public class ThreadControl {
    public static ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    static Handler uiThread = new Handler(Looper.getMainLooper());


    public static void runUi(Runnable runnable) {
        uiThread.post(runnable);
    }

    public static void destoryThread() {
        EXECUTOR.shutdownNow();
    }


}
