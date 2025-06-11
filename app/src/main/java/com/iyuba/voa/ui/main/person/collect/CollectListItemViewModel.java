package com.iyuba.voa.ui.main.person.collect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;
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
public class CollectListItemViewModel extends ItemViewModel<CollectListViewModel> {

    private int mPageIndex;


    public ObservableField<TitleTed> entity = new ObservableField<>();

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE.KEY, entity.get());
//        bundle.putString(Constants.BUNDLE.KEY_0, entity.get().getTitle());
//        bundle.putString(Constants.BUNDLE.KEY_1, entity.get().getSound());
        viewModel.startActivity(DetailActivity.class, bundle);
    });

    public CollectListItemViewModel(@NonNull CollectListViewModel viewModel, TitleTed data) {
        super(viewModel);
        entity.set(data);
    }

}