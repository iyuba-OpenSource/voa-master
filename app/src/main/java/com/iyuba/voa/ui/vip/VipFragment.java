package com.iyuba.voa.ui.vip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentVipBinding;
import com.iyuba.voa.ui.vip.iyubi.IyubiFragment;
import com.iyuba.voa.utils.Constants;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class VipFragment extends BaseFragment<FragmentVipBinding, VipViewModel> {


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_vip;
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
        viewModel.setTitleText("开通会员");
        viewModel.setRightText("购买爱语币");
        viewModel.setIsShowBack(true);
        viewModel.loadData();
        Bundle arguments = getArguments();
        int anInt = -1;
        if (arguments != null)
            anInt = arguments.getInt(Constants.BUNDLE.KEY, -1);
        if (anInt != -1) {
            if (anInt == 2) {
                viewModel.clickGoldenVIP.execute();
            }
        }
    }

    @Override
    public VipViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(VipViewModel.class);
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.setRightTextClick.observe(this, unused -> {
            if (!viewModel.checkIsLogin(VipFragment.class.getCanonicalName())) {
                return;
            }
            startContainerActivity(IyubiFragment.class.getCanonicalName());
        });

    }

}