package com.iyuba.voa.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.ui.vip.VipFragment;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/21
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class BaseTitleFragment<V extends ViewDataBinding, VM extends BaseTitleViewModel> extends BaseFragment<V, VM> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.view_top_title;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.showVipDialog.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String str) {
                showVipDialog(str);
            }
        });
    }

    public void showVipDialog(String msg) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(msg )
                .negativeText("取消")
                .positiveText("立即开通")
                .onPositive((dialog1, which) -> startContainerActivity(VipFragment.class.getCanonicalName()))
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }


}
