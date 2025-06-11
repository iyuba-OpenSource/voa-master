package com.iyuba.voa.ui.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.ui.main.MainActivity;
import com.iyuba.voa.ui.vip.VipFragment;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/21
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class BaseTitleActivity<V extends ViewDataBinding, VM extends BaseTitleViewModel> extends BaseActivity<V, VM> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.view_top_title;
    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.showVipDialog.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String str) {
                showVipDialog(str);
            }
        });
        viewModel.uc.startNewActivity.observe(this, str -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    public void showVipDialog(String msg) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(msg)
                .negativeText("取消")
                .positiveText("立即开通")
                .onPositive((dialog1, which) -> startContainerActivity(VipFragment.class.getCanonicalName()))
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }

}
