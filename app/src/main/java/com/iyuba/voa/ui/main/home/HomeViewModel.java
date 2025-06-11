package com.iyuba.voa.ui.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class HomeViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable UC = new UIChangeObservable();

    public ObservableField<TitleTed> titleTed = new ObservableField<>();
    public ObservableBoolean isPlaying = new ObservableBoolean();


    private int pagerIndex;
    //ViewPager切换监听
    public BindingCommand<Integer> onPageSelectedCommand = new BindingCommand<>(index -> {
        pagerIndex = index;
    });
    private Disposable mSubscription;

    public class UIChangeObservable {
        public SingleLiveEvent<String> serviceUpdate = new SingleLiveEvent<>();
    }

    public HomeViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(s -> {
                    loadVIPInfo();
                    UC.serviceUpdate.call();
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