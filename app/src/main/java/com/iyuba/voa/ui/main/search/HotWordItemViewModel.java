package com.iyuba.voa.ui.main.search;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.data.entity.SearchContent;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.XmlWord;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/10/13
 * 邮箱：jxfengmtx@gmail.com
 */
public class HotWordItemViewModel extends ItemViewModel<SearchViewModel> {
    public ObservableField<String> entity = new ObservableField<>();

    public HotWordItemViewModel(@NonNull SearchViewModel viewModel, String word) {
        super(viewModel);
        entity.set(word);
    }

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        viewModel.searchText.set(entity.get());
        viewModel.searchDataByHotWord(entity.get());
    });

}
