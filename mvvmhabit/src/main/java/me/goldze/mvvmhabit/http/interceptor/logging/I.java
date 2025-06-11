package me.goldze.mvvmhabit.http.interceptor.logging;


import com.elvishew.xlog.XLog;

import java.util.logging.Level;

import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 10/02/2017.
 */
class I {

    protected I() {
        throw new UnsupportedOperationException();
    }

    static void log(int type, String tag, String msg) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(tag);
        switch (type) {
            case Platform.INFO:
                XLog.tag(tag).i(msg);
//                logger.log(Level.INFO, msg);
                break;
            default:
                XLog.w(tag + "-%s", msg);
//                logger.log(Level.WARNING, msg);
                break;
        }
    }
}
