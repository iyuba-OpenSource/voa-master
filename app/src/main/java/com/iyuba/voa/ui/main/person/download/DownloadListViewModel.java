package com.iyuba.voa.ui.main.person.download;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.main.home.HomeListItemViewModel;
import com.iyuba.voa.utils.Constants;

import java.util.List;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class DownloadListViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable UC = new UIChangeObservable();
    final int pageNum = 10;
    int mMorePageNumber = 1;
    int mPageIndex;
    public ObservableList<DownloadListItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<DownloadListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_download);

    public ObservableField<TitleTed> entity = new ObservableField<>();
    public BindingCommand<Void> refreshCommand = new BindingCommand(() -> {
        loadData(1, pageNum * mMorePageNumber);
    });

    public BindingCommand<Void> loadMoreCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            loadData(++mMorePageNumber, pageNum);
        }
    });

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, entity.get().getVoaId());
        bundle.putString(Constants.BUNDLE.KEY_0, entity.get().getTitle());
        bundle.putString(Constants.BUNDLE.KEY_1, entity.get().getSound());
    });

    public DownloadListViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent<Integer> finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent<Integer> finishLoadMore = new SingleLiveEvent<>();
        public SingleLiveEvent<DownloadListItemViewModel> deleteDialog = new SingleLiveEvent<>();

    }

    public void loadData(int page, int pageNum) {
       /* if (page == 1)
            showLoadingDialog();*/
        List<TitleTed> titleTeds = model.roomGetTitleTedByFlag(3);
        if (titleTeds != null && titleTeds.size() > 0) {
            observableList.clear();
        }
        for (TitleTed data : titleTeds) {
            observableList.add(new DownloadListItemViewModel(DownloadListViewModel.this, data));
        }
        UC.finishLoadMore.call();
        UC.finishRefreshing.call();

    }

    //删除下载历史
    public void delDownData(TitleTed titleTed) {
        titleTed.setHotFlg(titleTed.getHotFlg().replace("3", ""));
        model.roomAddTitleTeds(titleTed);
    }

}