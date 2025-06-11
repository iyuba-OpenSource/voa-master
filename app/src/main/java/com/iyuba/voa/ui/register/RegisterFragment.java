package com.iyuba.voa.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.elvishew.xlog.XLog;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentRegisterBinding;
import com.iyuba.voa.ui.main.MainActivity;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.JsonUtil;
import com.iyuba.voa.utils.ThreadControl;

import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.wrapper.TokenVerifyResult;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.MaterialDialogUtil;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/06
 * 邮箱：jxfengmtx@gmail.com
 */
public class RegisterFragment extends BaseFragment<FragmentRegisterBinding, RegisterViewModel> {

    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            // TODO 此处为子线程！不可直接处理UI线程！处理后续操作需传到主线程中操作！
            ThreadControl.runUi(() -> {
                //成功回调
                if (result == SMSSDK.RESULT_COMPLETE) {
                    switch (event) {
                        //提交短信、语音验证码成功
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
//                            ToastUtils.showShort("提交短信成功");
//                            viewModel.isValidated = true;
                            viewModel.isValidatedSMS.set(true);
//                            viewModel.register();
                            break;
                        //获取短信验证码成功
                        case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                            ToastUtils.showShort("发送验证码成功");
                            viewModel.isValidatedSMS.set(false);
                            binding.tvCountDown.startCountDown(59);  //开启短信倒计时
                            break;
                        //获取语音验证码成功
                        case SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE:
                            //返回支持发送验证码的国家列表
                        case SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES:
                            //本机验证获取token成功
                        case SMSSDK.EVENT_GET_VERIFY_TOKEN_CODE:
                            TokenVerifyResult tokenVerifyResult = (TokenVerifyResult) data;
//                            SMSSDK.login(phoneNum,tokenVerifyResult);
                            //本机验证登陆成功
                        case SMSSDK.EVENT_VERIFY_LOGIN:

                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    viewModel.isValidatedSMS.set(false);
                    String message = ((Throwable) data).getMessage();
                    XLog.w(message);
                    String detail = JsonUtil.json2JsonObject(message).get("detail").getAsString();
                    ToastUtils.showShort(detail);
                    //失败回调
                } else {
                    //其他失败回调
                    viewModel.isValidatedSMS.set(false);
                    ((Throwable) data).printStackTrace();
                }
            });
        }
    };

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_register;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .init();
        viewModel.setTitleText("注册");
        viewModel.setIsShowBack(true);
        Bundle extras = getArguments();
        String phone = "";
        if (extras != null) {
            phone = extras.getString(Constants.BUNDLE.KEY, "");
            viewModel.isSecRegister = extras.getBoolean(Constants.BUNDLE.KEY_0, false);
            if (viewModel.isSecRegister) {
                ToastUtils.showShort("已为您生成默认用户名和密码");
                viewModel.setTitleText("快捷注册");
                viewModel.isValidatedSMS.set(true);
                viewModel.isAgree.set(true);
                viewModel.mobile.set(phone);
                viewModel.smsCode.set(String.valueOf(new Random().nextInt(999999)));
                viewModel.userName.set("user_" + new Random().nextInt(9999) + phone.substring(phone.length() - 4));
                viewModel.password.set(phone.substring(phone.length() - 6));
                viewModel.un = viewModel.userName.get();
                viewModel.pwd = viewModel.password.get();
//                viewModel.passwordShowSwitchOnClickCommand.execute();
            }
        }
        SMSSDK.registerEventHandler(eh); //注册短信回调

        binding.tvCountDown.setNormalText("获取验证码")
                .setCountDownText("重新获取(", ")")
                .setCloseKeepCountDown(true)//关闭页面保持倒计时开关
                .setCountDownClickable(false)//倒计时期间点击事件是否生效开关
                .setShowFormatTime(true)//是否格式化时间
//                .setOnCountDownFinishListener(() -> viewModel.isValidatedSMS.set(false))
                .setOnClickListener(v -> {
                    viewModel.checkIsRegister();
                });

    }

    @Override
    public RegisterViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(RegisterViewModel.class);
    }

    @Override
    public void initViewObservable() {
        //监听ViewModel中pSwitchObservable的变化, 当ViewModel中执行【uc.pSwitchObservable.set(!uc.pSwitchObservable.get());】时会回调该方法
        viewModel.uc.pSwitchEvent.observe(this, aBoolean -> {
            //pSwitchObservable是boolean类型的观察者,所以可以直接使用它的值改变密码开关的图标
            if (viewModel.uc.pSwitchEvent.getValue()) {
                //密码可见
                //在xml中定义id后,使用binding可以直接 拿到这个view的y引用,不再需要findViewById去找控件了
                binding.ivSwichPasswrod.setImageResource(R.mipmap.show_psw);
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                //密码不可见
                binding.ivSwichPasswrod.setImageResource(R.mipmap.show_psw_press);
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        viewModel.uc.startNewActivity.observe(this, bundle -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        viewModel.uc.countDownEvent.observe(this, registerViewModel -> {

        });
        viewModel.uc.showRegisterDialog.observe(this, registerViewModel -> {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("当前为您生成了默认的用户名及密码, 密码为手机号后六位,是否修改?")
                    .positiveText("否")
                    .negativeText("是")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            viewModel.register();
                        }
                    })
                    .canceledOnTouchOutside(false)//点击外部不取消对话框
                    .build();
            dialog.show();
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}