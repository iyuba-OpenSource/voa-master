package com.iyuba.voa.ui.login;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.register.RegisterFragment;
import com.iyuba.voa.utils.Constants;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class LoginViewModel extends BaseTitleViewModel<AppRepository> {

    public UIChangeObservable uc = new UIChangeObservable();

    public BindingCommand clickRegister = new BindingCommand(() -> {
        finish();
      /*  Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, "19862136666");
        bundle.putBoolean(Constants.BUNDLE.KEY_0, true);
        startContainerActivity(RegisterFragment.class.getCanonicalName(), bundle);*/
        startContainerActivity(RegisterFragment.class.getCanonicalName());
    });
    public BindingCommand clickForgetPwd = new BindingCommand(() -> {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, "忘记密码");
        bundle.putString(Constants.BUNDLE.KEY_0, "http://m." + Constants.CONFIG.DOMAIN + "/m_login/inputPhonefp.jsp");
//        startContainerActivity(WebFragment.class.getCanonicalName(),bundle);
        startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
    });


    public ObservableField<String> userName = new ObservableField<>("");

    public ObservableField<String> password = new ObservableField<>("");
    //用户名清除按钮的显示隐藏绑定
    public ObservableInt clearBtnVisibility = new ObservableInt();
    //封装一个界面发生改变的观察者

    public class UIChangeObservable {
        //密码开关观察者
        public SingleLiveEvent<Boolean> pSwitchEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<LoginViewModel> registerEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Bundle> startNewActivity = new SingleLiveEvent<>();
    }

    public LoginViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
      /*  userName.set(model.spGetUserName());
        password.set(model.spGetPassword());

        ipObFiled.set(model._getIp());
        portObFiled.set(model._getPort());
        isIntranet = model._getConfig();
        saveIpAndPort();
       */
    }


    //清除用户名的点击事件, 逻辑从View层转换到ViewModel层
    public BindingCommand clearUserNameOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            userName.set("");
        }
    });
    //密码显示开关  (你可以尝试着狂按这个按钮,会发现它有防多次点击的功能)
    public BindingCommand passwordShowSwitchOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //让观察者的数据改变,逻辑从ViewModel层转到View层，在View层的监听则会被调用
            uc.pSwitchEvent.setValue(uc.pSwitchEvent.getValue() == null || !uc.pSwitchEvent.getValue());
        }
    });
    //用户名输入框焦点改变的回调事件
    public BindingCommand<Boolean> onFocusChangeCommand = new BindingCommand<>(new BindingConsumer<Boolean>() {
        @Override
        public void call(Boolean hasFocus) {
            if (hasFocus) {
                clearBtnVisibility.set(View.VISIBLE);
            } else {
                clearBtnVisibility.set(View.INVISIBLE);
            }
        }
    });
    //登录按钮的点击事件
    public BindingCommand loginOnClickCommand = new BindingCommand(() -> login());

    /**
     * 网络模拟一个登陆操作
     **/
    private void login() {
        if (StringUtils.isTrimEmpty(userName.get())) {
            ToastUtils.showShort("请输入账号！");
            return;
        }
        if (StringUtils.isTrimEmpty(password.get())) {
            ToastUtils.showShort("请输入密码！");
            return;
        }
        showLoadingDialog();
        model.login(userName.get(), password.get(), "11001")
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<UserInfo>() {
                    @Override
                    public void onResult(UserInfo data) {
                        model.spSaveUid(data.getUid());
                        data.setImgSrc(Constants.CONFIG.AVATAR_ADDRESS + data.getUid());
                        model.roomAddUserDatas(data);
                      /*  AppManager.getAppManager().finishAllActivity();
                        uc.startNewActivity.call();*/
                        RxBus.getDefault().post("");
                        finish();
                        ToastUtils.showShort("登录成功  ");
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });
    }


}
