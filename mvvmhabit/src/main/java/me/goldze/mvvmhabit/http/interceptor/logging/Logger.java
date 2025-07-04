package me.goldze.mvvmhabit.http.interceptor.logging;

import com.elvishew.xlog.XLog;

import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 11/07/2017.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface Logger {
    void log(int level, String tag, String msg);

    Logger DEFAULT = new Logger() {
        @Override
        public void log(int level, String tag, String message) {
            Platform.get().log(message, level, null);
            switch (level) {
                case Platform.INFO:
                    XLog.i(message);
                    break;
                default:
                    XLog.w(message);
                    break;
            }
        }
    };
}
