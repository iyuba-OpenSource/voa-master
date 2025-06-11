package com.iyuba.voa.ui.register;

import static com.iyuba.voa.utils.Constants.CONFIG.COMPANY;
import static com.iyuba.voa.utils.Constants.CONFIG.PROTOCOL_IP;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.login.LoginActivity;
import com.iyuba.voa.utils.CheckUtils;
import com.iyuba.voa.utils.Constants;

import cn.smssdk.SMSSDK;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/06
 * 邮箱：jxfengmtx@gmail.com
 */
public class RegisterViewModel extends BaseTitleViewModel<AppRepository> {
    private final String privacy = PROTOCOL_IP + "/protocolpri.jsp?company=" + COMPANY;

    public String un = "";
    public String pwd = "";

    public UIChangeObservable uc = new UIChangeObservable();
    public boolean isSecRegister = false;

    public BindingCommand clickLogin = new BindingCommand(() -> {
        finish();
        startActivity(LoginActivity.class);
    });

    public ObservableBoolean isValidatedSMS = new ObservableBoolean(false);

    public ObservableBoolean isAgree = new ObservableBoolean(false);
    public ObservableField<String> smsCode = new ObservableField<>("");
    public ObservableField<String> userName = new ObservableField<>("");
    public ObservableField<String> mobile = new ObservableField<>("");

    public ObservableField<String> password = new ObservableField<>("");
    //用户名清除按钮的显示隐藏绑定
    public ObservableInt clearBtnVisibility = new ObservableInt();
    //封装一个界面发生改变的观察者

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> pSwitchEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<RegisterViewModel> countDownEvent = new SingleLiveEvent<>();
        public SingleLiveEvent<Bundle> startNewActivity = new SingleLiveEvent<>();
        public SingleLiveEvent<String> showRegisterDialog = new SingleLiveEvent<>();
    }

    public RegisterViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public BindingCommand<Boolean> isAgreeCommand = new BindingCommand<>(new BindingConsumer<Boolean>() {
        @Override
        public void call(Boolean aBoolean) {
            isAgree.set(aBoolean);
        }
    });
    public BindingCommand clickPrivacy = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, "隐私协议");
            bundle.putString(Constants.BUNDLE.KEY_0, privacy + "&apptype=" + Utils.getContext().getString(R.string.app_name));
//        startContainerActivity(WebFragment.class.getCanonicalName(),bundle);
            startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
        }
    });
    public BindingCommand clearMobileOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            mobile.set("");
        }
    });
    public BindingCommand passwordShowSwitchOnClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
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
    //注册按钮的点击事件
    public BindingCommand registerOnClickCommand = new BindingCommand(() -> {
        if (StringUtils.isTrimEmpty(mobile.get()) || !CheckUtils.isMobileNumber(mobile.get())) {
            ToastUtils.showShort("手机号码格式不正确");
            return;
        }
        if (StringUtils.isTrimEmpty(smsCode.get()) || smsCode.get().length() < 4) {
            ToastUtils.showShort("请输入验证码");
            return;
        }
        if (!isAgree.get()) {
            ToastUtils.showShort("您尚未同意隐私协议");
            return;
        }
     /*   if (isSecRegister && un.equals(userName.get().trim()) && pwd.equals(password.get().trim())) {  //用户名或密码未修改
            uc.showRegisterDialog.call();
            return;
        }*/

        if (isValidatedSMS.get()) {  //验证码通过后
            if (StringUtils.isTrimEmpty(userName.get())) {
                ToastUtils.showShort("请输入用户名");
                return;
            }
            if (userName.get().trim().length() < 3 && userName.get().trim().length() > 15) {
                ToastUtils.showShort("用户名不合法");
                return;
            }
            if (password.get().trim().length() < 6 || password.get().length() > 15) {
                ToastUtils.showShort("请检查密码格式是否正确");
                return;
            }
            register();
        } else {
            SMSSDK.submitVerificationCode("86", mobile.get(), smsCode.get());
        }
    });

    public void checkIsRegister() {
        if (StringUtils.isTrimEmpty(mobile.get()) || !CheckUtils.isMobileNumber(mobile.get())) {
            ToastUtils.showShort("手机号码格式不正确");
            return;
        }
        model.sendMessage3(mobile.get())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<BaseResponse>() {
                    @Override
                    public void onResult(BaseResponse data) {
                        SMSSDK.getVerificationCode(BuildConfig.MOB_SMS_TEMPLATE, "86", mobile.get());  //获取成功之后回调到activity EventHandler
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void register() {
        if (StringUtils.isTrimEmpty(userName.get())) {
            ToastUtils.showShort("请输入用户名！");
            return;
        }
        if (StringUtils.isTrimEmpty(password.get())) {
            ToastUtils.showShort("请输入密码！");
            return;
        }

        showLoadingDialog();
        model.register(mobile.get(), userName.get(), password.get())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<UserInfo>() {
                    @Override
                    public void onResult(UserInfo data) {
                        model.spSaveUid(data.getUid());
                        model.roomAddUserDatas(data);
                     /*   AppManager.getAppManager().finishAllActivity();
                        uc.startNewActivity.call();*/
                        RxBus.getDefault().post("");
                        finish();
                        ToastUtils.showShort("注册成功");
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });
    }


}
