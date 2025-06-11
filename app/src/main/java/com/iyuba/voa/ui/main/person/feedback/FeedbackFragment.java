package com.iyuba.voa.ui.main.person.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentFeedbackBinding;

import me.goldze.mvvmhabit.base.BaseFragment;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/9/30
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class FeedbackFragment extends BaseFragment<FragmentFeedbackBinding, FeedbackViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_feedback;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        viewModel.setIsShowBack(true);
        viewModel.setTitleText("反馈意见");
    }

    @Override
    public FeedbackViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(FeedbackViewModel.class);
    }
}
