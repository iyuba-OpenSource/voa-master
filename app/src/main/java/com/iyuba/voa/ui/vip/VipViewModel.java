package com.iyuba.voa.ui.vip;

import static com.iyuba.voa.utils.Constants.BUNDLE.KEY;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_0;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_1;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_2;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_3;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableMap;

import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.vip.order.OrderActivity;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class VipViewModel extends BaseTitleViewModel<AppRepository> {

    public UIChangeObservable UC = new UIChangeObservable();

    public ObservableField<UserInfo> entity = new ObservableField<>();

    public ObservableInt vipType = new ObservableInt(0);  // 0本应用vip 1全站 vip 2黄金vip
    public ObservableInt vipSelect = new ObservableInt(0);  // 0：1月
    public ObservableMap<String, String> vipText1 = new ObservableArrayMap<>();


    String orderName = "本应用一月会员";
    String yuan = "25";
    int month = 1;
    int productId = 10;  //本应用会员

    //点击本应用
    public BindingCommand clickThisVIP = new BindingCommand(() -> {
        orderName = Utils.getContext().getString(R.string.this_vip_time1);
        yuan = "25";
        productId = 10;
        vipType.set(0);
        vipSelect.set(0);
        vipSelect.notifyChange();
    });
    //点击全站
    public BindingCommand clickAllVIP = new BindingCommand(() -> {
        orderName = Utils.getContext().getString(R.string.all_vip_time1);
        productId = 0;
        vipType.set(1);
        yuan = "50";
        vipSelect.set(0);
        vipSelect.notifyChange();
    });
    //点击黄金vip
    public BindingCommand clickGoldenVIP = new BindingCommand(() -> {
        orderName = Utils.getContext().getString(R.string.gold_vip_time1);
        productId = 3;
        vipType.set(2);
        yuan = "98";
        vipSelect.set(0);
        vipSelect.notifyChange();
    });
    public BindingCommand clickRadio = new BindingCommand((BindingConsumer<String>) str -> {
        orderName = str;
        if (str.startsWith("本应用一月")) {
            yuan = "25";
            month = 1;
        } else if (str.startsWith("本应用半年")) {
            yuan = "69";
            month = 6;
        } else if (str.startsWith("本应用一年")) {
            yuan = "99";
            month = 12;
        } else if (str.startsWith("本应用三年")) {
            yuan = "199";
            month = 36;
        } else if (str.startsWith(Utils.getContext().getString(R.string.all_vip_time1))) {
            yuan = "50";
            month = 1;
        } else if (str.startsWith(Utils.getContext().getString(R.string.all_vip_time2))) {
            yuan = "198";
            month = 6;
        } else if (str.startsWith(Utils.getContext().getString(R.string.all_vip_time3))) {
            yuan = "298";
            month = 12;
        } else if (str.startsWith(Utils.getContext().getString(R.string.all_vip_time4))) {
            yuan = "588";
            month = 36;
        } else if (str.startsWith(Utils.getContext().getString(R.string.gold_vip_time1))) {
            yuan = "98";
            month = 1;
        } else if (str.startsWith(Utils.getContext().getString(R.string.gold_vip_time2))) {
            yuan = "288";
            month = 3;
        } else if (str.startsWith(Utils.getContext().getString(R.string.gold_vip_time3))) {
            yuan = "518";
            month = 6;
        } else if (str.startsWith(Utils.getContext().getString(R.string.gold_vip_time4))) {
            yuan = "998";
            month = 12;
        }

    });

    public BindingCommand clickSubmit = new BindingCommand(() -> {
        if (!checkIsLogin(VipFragment.class.getCanonicalName())) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(KEY, entity.get().getUsername());
        bundle.putString(KEY_0, orderName);
        bundle.putString(KEY_1, yuan);
        bundle.putInt(KEY_2, productId);
        bundle.putInt(KEY_3, month);
        startActivity(OrderActivity.class, bundle);
    });
    private Disposable mSubscription;


    public class UIChangeObservable {
        public SingleLiveEvent<VipViewModel> registerEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Bundle> startNewActivity = new SingleLiveEvent<>();
    }

    public VipViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);

    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(s -> {
                    loadData();
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
    }

    public void loadData() {
        UserInfo userInfo = model.roomGetUserDataById(model.spGetUid());
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setUsername("未登录");
        }
        entity.set(userInfo);
        entity.notifyChange();
    }

}
