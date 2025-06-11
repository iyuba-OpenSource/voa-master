package com.iyuba.voa.ui.main.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableLong;

import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;
import com.iyuba.voa.utils.Constants;

import java.text.DecimalFormat;

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
public class HomeListItemViewModel extends ItemViewModel<HomeListViewModel> {

    private int mPageIndex;

    public ObservableBoolean isRead = new ObservableBoolean();
    public ObservableLong testingPosition = new ObservableLong(0);
    public ObservableLong testingTotal = new ObservableLong(0);
    public ObservableLong exercisePosition = new ObservableLong(0);
    public ObservableLong listenPercent = new ObservableLong(0);


    public ObservableField<TitleTed> entity = new ObservableField<>();

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE.KEY, entity.get());
        int itemPosition = viewModel.getListsItemPosition(this);
        bundle.putInt(Constants.BUNDLE.KEY_0, itemPosition);
        viewModel.startActivity(DetailActivity.class, bundle);
    });

    public BindingCommand<Void> longClickItem = new BindingCommand<>(() -> {
        switch (viewModel.bundleBreak) {
            case 1:
                viewModel.UC.deleteDialog.setValue(this);
        }
    });

    public HomeListItemViewModel(@NonNull HomeListViewModel viewModel, TitleTed data) {
        super(viewModel);
        entity.set(data);
        isRead.set(viewModel.loadSingleReadHisData(data.getVoaId()) != null);
        testingPosition.set(viewModel.getTestingCount(data.getVoaId()));
        testingTotal.set(viewModel.getTotalParaCount(data.getVoaId()));
        TitleTed titleTed = viewModel.getTitleTed(data.getVoaId());

        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数

        long l = titleTed == null ? 0 : titleTed.getPlayTime();
        long l1 = titleTed == null || titleTed.getTotalTime() == 0 ? 1 : titleTed.getTotalTime();
        double v = Double.parseDouble(df.format(l * 1.0 / l1));
        double value = Math.min(v, 1.0);
        listenPercent.set((long) (value * 100));
        long ex = titleTed == null ? 0 : titleTed.getExerciseNum();
        exercisePosition.set(ex);
    }

}