package com.iyuba.voa.ui.base;

import static com.iyuba.voa.utils.Constants.CONFIG.AVATAR_ADDRESS;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.login.LoginActivity;
import com.iyuba.voa.ui.register.RegisterFragment;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.JsonUtil;
import com.mob.secverify.OAuthPageEventCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;
import com.mob.secverify.ui.component.CommonProgressDialog;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class BaseTitleViewModel<M extends BaseModel> extends BaseViewModel<M> {


    public ObservableField<String> titleText = new ObservableField<>("标题");
    public ObservableField<String> rightText = new ObservableField<>();
    public ObservableBoolean isShowBack = new ObservableBoolean(false);
    public ObservableBoolean isShowRightText = new ObservableBoolean(true);
    public ObservableBoolean isShowRightMenu = new ObservableBoolean(false);

    //兼容databinding，去泛型化
    public BaseTitleViewModel mBaseTitleViewModel;

    public UIChangeObservable uc = new UIChangeObservable();
    private Disposable mShowSetDialogSubscription;

    public class UIChangeObservable {
        public SingleLiveEvent<View> getRightView = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> setRightClick = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> setRightTextClick = new SingleLiveEvent<>();
        public SingleLiveEvent<String> showVipDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> startNewActivity = new SingleLiveEvent<>();
    }

    public BaseTitleViewModel(@NonNull Application application) {
        this(application, null);
    }

    public BaseTitleViewModel(@NonNull Application application, M model) {
        super(application, model);
        mBaseTitleViewModel = this;
    }

    public BindingCommand<Void> backOnClick = new BindingCommand(() -> finish());
    public BindingCommand<Void> rightTextOnClick = new BindingCommand(() -> uc.setRightTextClick.call()
    );

    public BindingCommand<View> getRightView = new BindingCommand<View>(view -> uc.getRightView.setValue(view));
    public BindingCommand<View> righMenutOnClick = new BindingCommand<View>(() -> {
        uc.setRightClick.call();
    });

    @Override
    public void registerRxBus() {
        super.registerRxBus();

    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
    }


    public void setIsShowBack(boolean isShowBack) {
        this.isShowBack.set(isShowBack);
    }

    public void setIsShowRightMenu(boolean isShowRightMenu) {
        this.isShowRightMenu.set(isShowRightMenu);
    }


    /**
     * 设置标题
     *
     * @param text 标题文字
     */
    public void setTitleText(String text) {
        titleText.set(text);
    }

    /**
     * 设置右侧文本
     *
     * @param text 文字
     */
    public void setRightText(String text) {
        rightText.set(text);
    }

    public boolean checkIsLogin() {
        return checkIsLogin(null);
    }

    public boolean checkIsLoginNoBreak() {
        AppRepository model = (AppRepository) this.model;
        if (model == null) {
            ToastUtils.showShort("您尚未登录");
            return false;
        }
        if (TextUtils.isEmpty(model.spGetUid()) || model.roomGetUserDataById(model.spGetUid()) == null) {  //数据库更新sp不会删除
            ToastUtils.showShort("您尚未登录");
            Constants.BREAK_PAGE = "";
            return false;
        }
        return true;
    }

    public boolean checkIsLogin(String loginReturnPage) {
        AppRepository model = (AppRepository) this.model;
        if (model == null) {
            return false;
        }
        if (TextUtils.isEmpty(model.spGetUid()) || model.roomGetUserDataById(model.spGetUid()) == null) {  //数据库更新sp不会删除
            if (!TextUtils.isEmpty(loginReturnPage))
                Constants.BREAK_PAGE = loginReturnPage;
            else Constants.BREAK_PAGE = "";
            startLogin();
            return false;
        }
        return true;
    }

    public boolean checkIsVIP(String msg) {
        AppRepository model = (AppRepository) this.model;
        if (!checkIsLoginNoBreak()) {
            ToastUtils.showShort("您尚未登录");
            if (!TextUtils.isEmpty(msg))
                showVipDialog(msg);
            return false;
        }
        if (model == null) {
            return false;
        }
        if ("0".equals(model.roomGetUserDataById(model.spGetUid()).getVipStatus())) {  //数据库更新sp不会删除
//            ToastUtils.showShort("您尚未开通vip");
            if (!TextUtils.isEmpty(msg))
                showVipDialog(msg);
            return false;
        }
        return true;
    }

    public void startLogin() {
//        startActivity(LoginActivity.class);
//1.4暂时隐藏 以下
        boolean isVerifySupport = SecVerify.isVerifySupport();
        XLog.i("SecVerify.isVerifySupport()  " + isVerifySupport);
        if (isVerifySupport && Constants.isPreVerifyDone) {
            verify();
        } else {
            startActivity(LoginActivity.class);
        }
    }

    /**
     * 免密登录
     */
    private void verify() {
        XLog.i("Verify called");
        //需要在verify之前设置
        SecVerify.OtherOAuthPageCallBack(cb -> {
            cb.pageOpenCallback(new OAuthPageEventCallback.PageOpenedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " pageOpened");
                }
            });
            cb.loginBtnClickedCallback(new OAuthPageEventCallback.LoginBtnClickedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " loginBtnClicked");
                }
            });
            cb.agreementPageClosedCallback(new OAuthPageEventCallback.AgreementPageClosedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " agreementPageClosed");
                }
            });
            cb.agreementPageOpenedCallback(new OAuthPageEventCallback.AgreementClickedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " agreementPageOpened");
                }
            });
            cb.cusAgreement1ClickedCallback(new OAuthPageEventCallback.CusAgreement1ClickedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " cusAgreement1ClickedCallback");
                }
            });
            cb.cusAgreement2ClickedCallback(new OAuthPageEventCallback.CusAgreement2ClickedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " cusAgreement2ClickedCallback");
                }
            });
            cb.checkboxStatusChangedCallback(new OAuthPageEventCallback.CheckboxStatusChangedCallback() {
                @Override
                public void handle(boolean b) {
                    XLog.i(System.currentTimeMillis() + " current status is " + b);
                }
            });
            cb.pageCloseCallback(new OAuthPageEventCallback.PageClosedCallback() {
                @Override
                public void handle() {
                    XLog.i(System.currentTimeMillis() + " pageClosed");
                    CommonProgressDialog.dismissProgressDialog();
                }
            });
        });
        SecVerify.verify(new VerifyCallback() {
            @Override
            public void onOtherLogin() {
                CommonProgressDialog.dismissProgressDialog();
                // 用户点击“其他登录方式”，处理自己的逻辑
                XLog.i("onOtherLogin called");
                startActivity(LoginActivity.class);
            }

            @Override
            public void onUserCanceled() {
                CommonProgressDialog.dismissProgressDialog();
                SecVerify.finishOAuthPage();
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
                XLog.i("onUserCanceled called");
            }

            @Override
            public void onComplete(VerifyResult data) {
                // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                XLog.i("onComplete called");
                tokenToPhone(data);
            }

            @Override
            public void onFailure(VerifyException e) {
                XLog.i("onFailure called");
            }
        });
    }


    private void tokenToPhone(VerifyResult verifyResult) {
        if (verifyResult != null) {
            XLog.i("verifyResult.getOperator()  " + verifyResult.getOperator());
            XLog.i("verifyResult.getOpToken()  " + verifyResult.getOpToken());
            XLog.i("verifyResult.getToken()  " + verifyResult.getToken());
            AppRepository model = (AppRepository) this.model;
            model.secVerify(verifyResult)
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                    .compose(RxUtils.exceptionTransformer())
                    .compose(RxUtils.schedulersTransformer())
                    .subscribe((Consumer<JsonObject>) jo -> {
                        JsonElement resultCode = jo.get("isLogin");
                        if (resultCode != null && resultCode.getAsString().equals("1")) {
                            UserInfo userInfo = JsonUtil.json2Entity(jo.get("userinfo").toString(), UserInfo.class);
                            model.spSaveUid(userInfo.getUid());
                            userInfo.setImgSrc(Constants.CONFIG.AVATAR_ADDRESS + userInfo.getUid());
                            model.roomAddUserDatas(userInfo);
//                            AppManager.getAppManager().removeActivity(AppManag    er.getAppManager().currentActivity());
//                            finish();
//                            AppManager.getAppManager().finishAllActivity();
//                            uc.startNewActivity.call();
//                            startActivity(MainActivity.class);
                            RxBus.getDefault().post("");
                            ToastUtils.showShort("登录成功");
                        } else {
                            ToastUtils.showShort("验证失败，账号未注册");
                            JsonElement res = jo.get("res");
                            if (res != null) {
                                JsonObject asJsonObject = res.getAsJsonObject();
                                JsonElement phone = asJsonObject.get("phone");
                                if (phone != null) {
                                    String asString = phone.getAsString();
                                    if (!TextUtils.isEmpty(asString)) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Constants.BUNDLE.KEY, asString);
                                        bundle.putBoolean(Constants.BUNDLE.KEY_0, true);
                                        startContainerActivity(RegisterFragment.class.getCanonicalName(), bundle);
                                        return;
                                    }
                                }
                            }
                            startContainerActivity(RegisterFragment.class.getCanonicalName());
                        }


                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtils.showShort("服务器错误+");
                        }
                    });
        } else {
            XLog.i("tokenToPhone verifyResult is null.");
            startActivity(LoginActivity.class);
        }
    }

    public void showVipDialog(String msg) {
        uc.showVipDialog.setValue(msg);
    }

    public void loadVIPInfo() {
        AppRepository model = (AppRepository) this.model;
        if (TextUtils.isEmpty(model.spGetUid()) || model.roomGetUserDataById(model.spGetUid()) == null) {
            return;
        }
        model.getVipInfo(model.spGetUid(), model.spGetUid())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<UserInfo>() {
                    @Override
                    public void onResult(UserInfo data) {
                        model.spSaveUserName(data.getUsername());
                        data.setUid(model.spGetUid());
                        data.setImgSrc(AVATAR_ADDRESS + data.getUid());
                        model.roomUpdateUserData(data);
                        initMicoCourse(data);
                    }
                });
    }

    private void initMicoCourse(UserInfo data) {
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
        if (checkIsLogin())
            user.isTemp = false;
        else
            user.isTemp = true;
        user.vipStatus = data.getVipStatus();
        IyuUserManager.getInstance().setCurrentUser(user);
    }
}
