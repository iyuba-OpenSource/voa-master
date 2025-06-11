package com.iyuba.voa.ui.main.person.rank;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.TestingRank;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/17
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankListItemViewModel extends ItemViewModel<RankListViewModel> {
    public ObservableField<TestingRank> entity = new ObservableField<>();


    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {

    });

    public RankListItemViewModel(@NonNull RankListViewModel viewModel, TestingRank data) {
        super(viewModel);
        entity.set(data);
    }

}