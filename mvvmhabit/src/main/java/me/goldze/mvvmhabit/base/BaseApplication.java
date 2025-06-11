package me.goldze.mvvmhabit.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.io.File;

import me.goldze.mvvmhabit.BuildConfig;
import me.goldze.mvvmhabit.utils.SDCardUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * Created by goldze on 2017/6/15.
 */

public class BaseApplication extends Application {
    private static Application sInstance;
    private static final long MAX_TIME = 1000 * 60 * 60 * 24; // 1 days


    @Override
    public void onCreate() {
        super.onCreate();

        setApplication(this);
        initXLog();
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        //初始化工具类
        Utils.init(application);
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getAppManager().removeActivity(activity);
            }
        });

    }


    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }


    public void initXLog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                        : LogLevel.NONE)
//                .enableStackTrace(2)
                .build();
        Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log

        Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                .Builder(new File(SDCardUtils.getFileCachePath(), "log").getPath())       // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // Default: NeverCleanStrategy()
                .flattener(new ClassicFlattener())                     // Default: DefaultFlattener
                .build();

        XLog.init(config, androidPrinter, filePrinter);
//        LibCat.config(true, filePrinter);
    }
}
