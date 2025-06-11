package com.iyuba.voa.ui.main;

import static com.iyuba.voa.utils.Constants.CONFIG.AVATAR_ADDRESS;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class MainViewModel extends BaseTitleViewModel<AppRepository> {

    private Disposable mSubscription;

    public MainViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(s -> {
                    loadVIPInfo();
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
    }

}