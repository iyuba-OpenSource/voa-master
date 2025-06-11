package com.iyuba.voa.app;


import static com.iyuba.voa.utils.Constants.CONFIG.COM_DOMAIN;
import static com.iyuba.voa.utils.Constants.CONFIG.DOMAIN;

import android.text.TextUtils;

import com.bumptech.glide.request.target.ViewTarget;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.ui.login.LoginActivity;
import com.iyuba.voa.utils.AppUtils;
import com.iyuba.voa.utils.Constants;
import com.umeng.commonsdk.UMConfigure;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.SPUtils;
import timber.log.Timber;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class AppApplication extends BaseApplication {
    private MusicService.MusicBinder musicBinder;

    public MusicService.MusicBinder getMusicBinder() {
        return musicBinder;
    }

    public void setMusicBinder(MusicService.MusicBinder musicBinder) {
        this.musicBinder = musicBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);   // 解决问题： You must not call setTag() on a view Glide is targeting
        initCrash();
        // 友盟预初始化
        UMConfigure.preInit(this, "59ba44f5f29d984d3d000057", AppUtils.getChannel());

        //域名初始化
        String domain = SPUtils.getInstance().getString(Constants.SP.DOMAIN);
        String domain2 = SPUtils.getInstance().getString(Constants.SP.COM_DOMAIN);
        if (!TextUtils.isEmpty(domain) && !TextUtils.isEmpty(domain2)) {
            Constants.CONFIG.updateDomain(domain, domain2);
        } else {
            Constants.CONFIG.updateDomain(DOMAIN, COM_DOMAIN);
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(LoginActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        musicBinder.getService().closeNotification();
        musicBinder.getService().closeMusic();
    }
}
