package com.iyuba.voa.ui.main.splash;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.ActivityWelcomeBinding;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.main.MainActivity;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;
import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 引导页
 */
public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding, BaseTitleViewModel> {


    @Override
    public int initContentView(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_welcome;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public SplashViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(SplashViewModel.class);
    }

    @Override
    public void initData() {
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
// 设置数据源
        binding.bannerGuideBackground.setBackgroundResource(R.color.white);
        binding.bannerGuideForeground.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.mipmap.ic_guide1,
                R.mipmap.ic_guide2,
                R.mipmap.ic_guide3,
                R.mipmap.ic_guide4);
        binding.bannerGuideForeground.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, 0, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                startActivity(MainActivity.class);
                finish();
            }
        });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
