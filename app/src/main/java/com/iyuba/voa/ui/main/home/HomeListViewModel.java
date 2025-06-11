package com.iyuba.voa.ui.main.home;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.data.entity.Advertising;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.http.observer.ApiDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.JsonUtil;
import com.iyuba.voa.utils.ThreadControl;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
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
public class HomeListViewModel extends BaseTitleViewModel<AppRepository> {


    public UIChangeObservable UC = new UIChangeObservable();
    final int pageNum = 10;
    int mMorePageNumber = 1;
    int mPageIndex;
    //    public ObservableList<HomeListItemViewModel> observableList = new ObservableArrayList<>();
    public List<HomeListItemViewModel> lists = new ArrayList<>();

    public ItemBinding<HomeListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_home);

    public String searchKey;
    public int bundleBreak = -1;  //0 查找  1阅读历史  其他 主页数据
    public ObservableField<TitleTed> entity = new ObservableField<>();
    public BindingCommand<Void> refreshCommand = new BindingCommand(() -> {
        switch (bundleBreak) {
            case 0:
                if (!TextUtils.isEmpty(searchKey)) {
                    loadSearchData(1, pageNum * mMorePageNumber);
                }
                break;
            case 1:
                loadReadHisData();
                break;
            default:
                loadData(1, pageNum * mMorePageNumber, mPageIndex);
        }

    });

    public BindingCommand<Void> loadMoreCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            switch (bundleBreak) {
                case 0:
                    if (!TextUtils.isEmpty(searchKey)) {
                        loadSearchData(++mMorePageNumber, pageNum);
                    }
                    break;
                case 1:
                    UC.finishLoadMore.call();
                    break;
                default:
                    loadData(++mMorePageNumber, pageNum, mPageIndex);
            }
        }
    });


    public HomeListViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent<Integer> finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent<Integer> finishLoadMore = new SingleLiveEvent<>();
        public SingleLiveEvent<Advertising> addAd = new SingleLiveEvent<>();
        public SingleLiveEvent<List<HomeListItemViewModel>> loadDataSuccess = new SingleLiveEvent<>();
        public SingleLiveEvent<HomeListItemViewModel> deleteDialog = new SingleLiveEvent<>();

    }

    //查找数据跳转所用接口
    public void loadSearchData(int page, int pageNum) {
        showLoadingDialog();
        model.searchApiNew(searchKey, page, pageNum, 1, model.spGetUid())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    if (jo.get("titleToal").getAsInt() > 0) {
                        String titleData = jo.get("titleData").toString();
                        List<TitleTed> titleDataDTOS = JsonUtil.json2List(titleData, TitleTed.class);
                        if (page == 1) {
                            lists.clear();
                        }

                        for (TitleTed dataDTO : titleDataDTOS) {
                            if (dataDTO.getSound().contains(Constants.CONFIG.SOUND_IP)) {
                                dataDTO.setSound(dataDTO.getSound().replace(Constants.CONFIG.SOUND_IP, ""));
                            }
                            lists.add(new HomeListItemViewModel(HomeListViewModel.this, dataDTO));
                        }
                        UC.loadDataSuccess.setValue(lists);


                    } else ToastUtils.showShort("暂无数据");
                    UC.finishLoadMore.call();
                    UC.finishRefreshing.call();
                    dismissDialog();
                });
    }

    //阅读历史跳转所用接口
    public TitleTed loadSingleReadHisData(String id) {
        return model.roomGetTitleTedByIdFlag(id, 4);
    }

    //删除阅读历史
    public void delReadHisData(TitleTed titleTed) {
        titleTed.setHotFlg(titleTed.getHotFlg().replace("4", ""));
        model.roomAddTitleTeds(titleTed);
    }

    public void loadReadHisData() {
        List<TitleTed> titleTeds = model.roomGetTitleTedByFlag(4);
        if (titleTeds.size() == 0) {
            UC.finishLoadMore.call();
            UC.finishRefreshing.call();
            ToastUtils.showShort("暂无数据");
            return;
        }
        ThreadControl.runUi(new Runnable() {
            @Override
            public void run() {
                lists.clear();
                for (TitleTed dataDTO : titleTeds) {
                    if (dataDTO.getSound().contains(Constants.CONFIG.SOUND_IP)) {
                        dataDTO.setSound(dataDTO.getSound().replace(Constants.CONFIG.SOUND_IP, ""));
                    }
                    lists.add(new HomeListItemViewModel(HomeListViewModel.this, dataDTO));
                }
                UC.loadDataSuccess.setValue(lists);
                UC.finishLoadMore.call();
                UC.finishRefreshing.call();
            }
        });
    }


    public int getTestingCount(String voaid) {
        int count = model.roomGetVoaTestingCountByVoaId(voaid);
        XLog.i(voaid + "-评测句子数：" + count);
        return count;
    }

    public int getTotalParaCount(String voaid) {
        int count = model.roomGetVoaTextCountByVoaId(voaid);
        XLog.i(voaid + "-总句子数：" + count);
        return count;
    }

    public TitleTed getTitleTed(String voaid) {
        return model.roomGetTitleTedById(voaid);
    }


    //主页数据所用
    public void loadData(int page, int pageNum, int parentId) {
        getMessageLiu();
        model.titleApi(0, page, pageNum, parentId)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new ApiDisposableObserver<List<TitleTed>>() {
                    @Override
                    public void onResult(List<TitleTed> datas) {
                        if (datas == null || datas.size() == 0) {
                            if (page == 1) {
                                ToastUtils.showShort("无数据");
                            } else ToastUtils.showShort("已是最新数据");
                            return;
                        }
                        if (page == 1) {
                            lists.clear();
                        }
                     /*   for (TitleTed data : datas) {
                            observableList.add(new HomeListItemViewModel(HomeListViewModel.this, data));
                        }*/

                        for (TitleTed data : datas) {
                            lists.add(new HomeListItemViewModel(HomeListViewModel.this, data));
                        }
                        UC.loadDataSuccess.setValue(lists);

                        MusicService.MusicBinder musicBinder = ((AppApplication) getApplication()).getMusicBinder();
                        if (musicBinder != null)
                            musicBinder.setValue(datas);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        UC.finishLoadMore.call();
                        UC.finishRefreshing.call();
                    }
                });
    }


    public void getMessageLiu() {
        model.getAdEntryAll("2", model.spGetUid())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonArray>) array -> {
                    if (array.size() > 0) {
                        JsonObject jo = array.get(0).getAsJsonObject();
                        String result = jo.get("result").getAsString();
                        if (result.equals("1")) {
                            Advertising data = JsonUtil.json2Entity(jo.get("data").toString(), Advertising.class);
                            UC.addAd.setValue(data);
                            return;
                        }
                    }

                });
        UC.addAd.setValue(null);
    }

    public boolean getIsVIP() {
        return model.roomGetUserDataById(model.spGetUid()) != null && !model.roomGetUserDataById(model.spGetUid()).getVipStatus().equals("0");
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCommand.execute();
    }

    /**
     * 获取条目下标
     *
     * @param listItemViewModel
     * @return
     */
    public int getListsItemPosition(HomeListItemViewModel listItemViewModel) {
        return lists.indexOf(listItemViewModel);
    }


}