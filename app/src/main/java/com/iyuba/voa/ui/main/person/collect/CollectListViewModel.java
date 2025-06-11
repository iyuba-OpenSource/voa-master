package com.iyuba.voa.ui.main.person.collect;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.http.observer.ApiDisposableObserver;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.Constants;

import java.util.List;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
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
public class CollectListViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable UC = new UIChangeObservable();
    final int pageNum = 10;
    int mMorePageNumber = 1;
    int mPageIndex;
    public ObservableList<CollectListItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<CollectListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_collect);

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

    public CollectListViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent<Integer> finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent<Integer> finishLoadMore = new SingleLiveEvent<>();

    }

    public void loadData(int page, int pageNum) {
        if (page == 1)
            showLoadingDialog();
        model.getCollect(model.spGetUid(), page, pageNum)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new SimpleDisposableObserver<BaseResponse<List<TitleTed>>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResult(BaseResponse<List<TitleTed>> baseResponse) {
                        List<TitleTed> datas = baseResponse.getData();
                        if (mMorePageNumber > baseResponse.getTotal()) {
                            ToastUtils.showShort("已是最新数据");
                            return;
                        }
                        if (datas == null || datas.size() == 0) {
                            if (page == 1) {
                                ToastUtils.showShort("无数据");
                            }
                            return;
                        }
                        if (page == 1) {  //下拉刷新需要清除全部
                            observableList.clear();
                        }
                        model.roomAddTitleTeds(datas.stream().toArray(TitleTed[]::new));
                        for (TitleTed data : datas) {
                            observableList.add(new CollectListItemViewModel(CollectListViewModel.this, data));
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissDialog();
                        UC.finishLoadMore.call();
                        UC.finishRefreshing.call();
                    }
                });
    }

}