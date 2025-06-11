package com.iyuba.voa.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.ActivityLoginBinding;
import com.iyuba.voa.ui.main.MainActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
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
        viewModel.setTitleText("登录");
        viewModel.setIsShowBack(true);
    }

    @Override
    public LoginViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(LoginViewModel.class);
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        viewModel.uc.registerEvent.observe(this, loginViewModel -> {

        });


    }

}