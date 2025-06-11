package com.iyuba.voa.ui.main.home.detail.word;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.VoaExam;
import com.iyuba.voa.ui.main.search.SearchFragment;
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
public class WordItemViewModel extends ItemViewModel<WordViewModel> {
    public ObservableField<VoaExam> entity = new ObservableField<>();


    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        if (viewModel.checkIsLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, entity.get().getWords());
            viewModel.startContainerActivity(SearchFragment.class.getCanonicalName(), bundle);
        }
    });

    public WordItemViewModel(@NonNull WordViewModel viewModel, VoaExam data) {
        super(viewModel);
        entity.set(data);
    }

}