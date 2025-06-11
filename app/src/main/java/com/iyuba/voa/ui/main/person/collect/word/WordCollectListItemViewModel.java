package com.iyuba.voa.ui.main.person.collect.word;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.XmlWord;

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
public class WordCollectListItemViewModel extends ItemViewModel<WordCollectListViewModel> {

    private int index;

    public ObservableField<XmlWord> entity = new ObservableField<>();

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {

    });

    public BindingCommand<Void> clickPlay = new BindingCommand<>(() -> {
        viewModel.UC.playAudio.setValue(WordCollectListItemViewModel.this);
    });

    public BindingCommand<Void> clickCollect = new BindingCommand<>(() -> {
        viewModel.updateWordCollect(entity);
    });

    public WordCollectListItemViewModel(@NonNull WordCollectListViewModel viewModel, XmlWord data) {
        super(viewModel);
        data.setCollect(viewModel.roomGetWord(data.getKey()) != null);
        entity.set(data);
    }

}