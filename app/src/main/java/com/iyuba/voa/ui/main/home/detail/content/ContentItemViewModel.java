package com.iyuba.voa.ui.main.home.detail.content;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.ui.widget.SelectWordTextView;

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
public class ContentItemViewModel extends ItemViewModel<ContentViewModel> {
    public ObservableField<VoaText> entity = new ObservableField<>();


    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        viewModel.UC.setPlayerProcess.setValue(entity.get());
    });

    public ContentItemViewModel(@NonNull ContentViewModel viewModel, VoaText data) {
        super(viewModel);
        entity.set(data);
    }

}