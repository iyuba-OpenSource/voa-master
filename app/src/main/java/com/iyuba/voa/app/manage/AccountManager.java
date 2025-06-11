package com.iyuba.voa.app.manage;

import android.content.Context;
import android.text.TextUtils;

import com.efs.sdk.pa.config.ConfigManager;
import com.iyuba.headlinelibrary.data.DataManager;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.voa.app.Injection;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;

import me.goldze.mvvmhabit.utils.RxUtils;

public class AccountManager {
    private DataManager mDataManager;
    private ConfigManager mConfigManager;
    private transient Context mContext;
    private static AppRepository appRepository;
    private AccountManager instanace;

    public static AccountManager getInstance() {
        appRepository = Injection.provideAppRepository();
        return new AccountManager();
    }

    public AccountManager() {
    }

    public static AccountManager getInstance(Context context, DataManager dataManager, ConfigManager configManager) {
        appRepository = Injection.provideAppRepository();
        return new AccountManager(context, dataManager, configManager);
    }

    AccountManager(Context context, DataManager dataManager, ConfigManager configManager) {
        this.mContext = context;
        this.mDataManager = dataManager;
        this.mConfigManager = configManager;
    }

    public void loadVIPInfo() {
        if (TextUtils.isEmpty(appRepository.spGetUid()) || appRepository.roomGetUserDataById(appRepository.spGetUid()) == null) {
            return;
        }
        appRepository.getVipInfo(appRepository.spGetUid(), appRepository.spGetUid())
//                .compose(RxUtils.bindToLifecycle(mcgetLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<UserInfo>() {
                    @Override
                    public void onResult(UserInfo data) {
                        appRepository.spSaveUserName(data.getUsername());
                        data.setUid(appRepository.spGetUid());
                        data.setImgSrc(appRepository.roomGetUserDataById(appRepository.spGetUid()).getImgSrc());
                        appRepository.roomUpdateUserData(data);
                        initMocUser(data);
                    }
                });
    }

    public void initMocUser() {
        UserInfo userInfo = appRepository.roomGetUserDataById(appRepository.spGetUid());
        appRepository.roomUpdateUserData(userInfo);
    }

    public void initMocUser(UserInfo data) {
        // 初始化微课及视频模块需要的一些信息
        //同步IyuUserManager

        User user = new User();
        user.uid = Integer.parseInt(data.getUid());
        user.nickname = data.getNickname();
        if (data.isVIP())
            user.vipExpireTime = data.getExpireTime();
        user.iyubiAmount = Integer.parseInt(data.getAmount());
        user.imgUrl = data.getImgSrc();
        user.credit = data.getCredits();
        user.name = data.getUsername();
        user.imgUrl = data.getImgSrc();
        user.email = data.getEmail();
        user.mobile = data.getMobile();
        if (TextUtils.isEmpty(appRepository.spGetUid()) || appRepository.roomGetUserDataById(appRepository.spGetUid()) == null)
            user.isTemp = false;
        else
            user.isTemp = true;
        user.vipStatus = data.getVipStatus();
        IyuUserManager.getInstance().setCurrentUser(user);
//        用户登录时配置！！！
//        PersonalHome.setSaveUserinfo(mocUser.uid, mocUser.name, mocUser.vipStatus);

    }


}
