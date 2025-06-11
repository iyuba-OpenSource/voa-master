package com.iyuba.voa.ui.main.home.detail.exercise;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/9/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class ExerciseItemViewModel extends ItemViewModel<ExerciseViewModel> {
    public ObservableField<String> entityContent = new ObservableField<>();
    public ObservableInt entityResult = new ObservableInt();
    public ObservableInt entityIndex = new ObservableInt();


    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {

    });

    public ExerciseItemViewModel(@NonNull ExerciseViewModel viewModel, int index, String content, int result) {
        super(viewModel);
        entityContent.set(content);
        entityResult.set(result);
        entityIndex.set(index);
    }

}