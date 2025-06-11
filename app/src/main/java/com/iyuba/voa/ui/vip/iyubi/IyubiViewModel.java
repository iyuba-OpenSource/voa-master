package com.iyuba.voa.ui.vip.iyubi;

import static com.iyuba.voa.utils.Constants.BUNDLE.KEY;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_0;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_1;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_2;
import static com.iyuba.voa.utils.Constants.BUNDLE.KEY_3;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.vip.order.OrderActivity;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class IyubiViewModel extends BaseTitleViewModel<AppRepository> {

    public UIChangeObservable UC = new UIChangeObservable();

    public ObservableField<UserInfo> entity = new ObservableField<>();


    String orderName = "210爱语币";
    String yuan = "19.9";
    int month = 210;
    String cate = "购买爱语币";  //分类
    int productId = 1;  //爱语币购买

    public BindingCommand clickBi1 = new BindingCommand(new BindingAction() {  //点击本应用
        @Override
        public void call() {
            yuan = "19.9";
            month = 210;
            orderName = "210爱语币";
            startOrder();
        }
    });
    public BindingCommand clickBi2 = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            yuan = "59.9";
            month = 650;
            orderName = "650爱语币";
            startOrder();
        }
    });


    public BindingCommand clickBi3 = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            yuan = "99.9";
            month = 1100;
            orderName = "1100爱语币";
            startOrder();
        }
    });
    public BindingCommand clickBi4 = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            yuan = "599";
            month = 6600;
            orderName = "6600爱语币";
            startOrder();
        }
    });
    public BindingCommand clickBi5 = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            yuan = "999";
            month = 12000;
            orderName = "12000爱语币";
            startOrder();
        }
    });


    private void startOrder() {
        Bundle bundle = new Bundle();
        bundle.putString("cate", "爱语币");
        bundle.putString(KEY, model.spGetUserName());
        bundle.putString(KEY_0, orderName);
        bundle.putString(KEY_1, yuan);
        bundle.putInt(KEY_2, productId);
        bundle.putInt(KEY_3, month);
        startActivity(OrderActivity.class, bundle);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<IyubiViewModel> registerEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Bundle> startNewActivity = new SingleLiveEvent<>();
    }

    public IyubiViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);

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
