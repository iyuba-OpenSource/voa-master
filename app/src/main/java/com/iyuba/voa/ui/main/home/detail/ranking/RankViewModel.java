package com.iyuba.voa.ui.main.home.detail.ranking;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.TestingRank;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.JsonUtil;

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
public class RankViewModel extends BaseTitleViewModel<AppRepository> {
    final int pageNum = 20;
    int mMorePageNumber = 1;
    public UIChangeObservable UC = new UIChangeObservable();


    public ObservableList<RankItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<RankItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_rank);
    public String voaid;
    public TitleTed titleTed;

    public RankViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
        if (TextUtils.isEmpty(model.spGetUid()) || model.roomGetUserDataById(model.spGetUid()) == null) {
            TestingRank testingRank = new TestingRank();
            testingRank.setName("尚未登录");
            testingRank.setUid("-1");
            observableList.add(0, new RankItemViewModel(this, testingRank));
        }
    }

    public BindingCommand<Void> refreshCommand = new BindingCommand(() -> {
        loadData(0, pageNum * mMorePageNumber);
    });

    public BindingCommand<Void> loadMoreCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            loadData(++mMorePageNumber, pageNum);
        }
    });


    public BindingCommand<Integer> onScrollStateChangedCommand = new BindingCommand<Integer>(integer -> {
        UC.playerListener.setValue(integer);
    });

    public class UIChangeObservable {
        public SingleLiveEvent<VoaText> setPlayerProcess = new SingleLiveEvent<>();
        public SingleLiveEvent<Integer> playerListener = new SingleLiveEvent<>();
        public SingleLiveEvent<String> pdfSuccessDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> collectSuccess = new SingleLiveEvent<>();
        public SingleLiveEvent<XmlWord> wordDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<XmlWord> dismissWordDialog = new SingleLiveEvent<>();

        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();

    }


    public void loadData(int start, int pageNum) {
        model.getTopicRanking(voaid, model.spGetUid(), start, pageNum)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) data -> {
                    JsonElement resultCode = data.get("result");
                    if (resultCode != null && resultCode.getAsInt() > 0) {
                        if (start == 0) {
                            observableList.clear();
                        }
                        //加入我的排名
                        TestingRank testingRank = JsonUtil.json2Entity(data.toString(), TestingRank.class);
                        observableList.add(new RankItemViewModel(this, testingRank));
                        List<TestingRank> datas = JsonUtil.json2List(data.get("data").toString(), TestingRank.class);

                        int i = 1;
                        for (TestingRank rank : datas) {
                            rank.setIndex(i++);
                            observableList.add(new RankItemViewModel(this, rank));
                        }

                    } else {
                        ToastUtils.showShort("无排名信息");
                        XLog.i("无排名信息");
                    }
                    UC.finishRefreshing.call();
                    UC.finishLoadMore.call();

                    dismissDialog();
                });

    }


}