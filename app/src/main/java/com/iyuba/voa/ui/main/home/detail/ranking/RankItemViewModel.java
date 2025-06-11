package com.iyuba.voa.ui.main.home.detail.ranking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.TestingRank;
import com.iyuba.voa.ui.login.LoginActivity;
import com.iyuba.voa.ui.main.home.detail.ranking.detail.RankDetailFragment;
import com.iyuba.voa.utils.Constants;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankItemViewModel extends ItemViewModel<RankViewModel> {
    public ObservableField<TestingRank> entity = new ObservableField<>();


    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        if (entity.get().getUid().equals("-1")) {
            viewModel.startActivity(LoginActivity.class);
            return;
        }
        if (viewModel.checkIsLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, viewModel.voaid);
            bundle.putString(Constants.BUNDLE.KEY_0, String.valueOf(entity.get().getUid()));
            bundle.putString(Constants.BUNDLE.KEY_1, String.valueOf(entity.get().getImgSrc()));
            bundle.putString(Constants.BUNDLE.KEY_2, String.valueOf(entity.get().getName()));
            bundle.putParcelable(Constants.BUNDLE.KEY_3, viewModel.titleTed);
            viewModel.startContainerActivity(RankDetailFragment.class.getCanonicalName(), bundle);
        }
    });

    public RankItemViewModel(@NonNull RankViewModel viewModel, TestingRank data) {
        super(viewModel);
        entity.set(data);
    }

}