package com.iyuba.voa.ui.main.person.rank;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.TestingRank;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.JsonUtil;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/17
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankListViewModel extends BaseTitleViewModel<AppRepository> {
    final int pageNum = 20;
    int mMorePageNumber = 1;
    public String title;
    public ObservableField<String> type = new ObservableField<>("D");
    public UIChangeObservable UC = new UIChangeObservable();


    public ObservableList<RankListItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<RankListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_person_rank);

    public RankListViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);

    }

    public BindingCommand<Void> clickFilter = new BindingCommand<>(() -> {
        UC.showFilterDialog.call();
    });
    public BindingCommand<Void> refreshCommand = new BindingCommand(() -> {
        loadData(0, pageNum * mMorePageNumber);
    });

    public BindingCommand<Void> loadMoreCommand = new BindingCommand(() -> loadData(++mMorePageNumber, pageNum));


    public BindingCommand<Integer> onScrollStateChangedCommand = new BindingCommand<Integer>(integer -> {
    });

    public class UIChangeObservable {
        public SingleLiveEvent showFilterDialog = new SingleLiveEvent<>();
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();

    }

    public void loadData(int start, int pageNum) {
        if (!TextUtils.isEmpty(title)) {
            switch (title) {
                case "阅读":
                    loadReadData(start, pageNum);
                    break;
                case "听力":
                    loadListenData(start, pageNum);
                    break;
                case "口语":
                    loadTalkData(start, pageNum);
                    break;
                case "学习":
                    loadStudyData(start, pageNum);
                    break;
                case "测试":
                    loadTestData(start, pageNum);
                    break;
            }
        }
    }

    public void loadReadData(int start, int pageNum) {
        model.getReadRanking(model.spGetUid(), type.get(), start, pageNum)
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
                        testingRank.setBottom1("单词数:" + testingRank.getWords());
                        testingRank.setBottom2("文章数:" + testingRank.getCnt());
                        testingRank.setRight("WPM:" + testingRank.getWpm());
                        observableList.add(new RankListItemViewModel(this, testingRank));
                        List<TestingRank> datas = JsonUtil.json2List(data.get("data").toString(), TestingRank.class);

                        int i = 1;
                        for (TestingRank rank : datas) {
                            rank.setIndex(i++);
                            rank.setBottom1("单词数:" + rank.getWords());
                            rank.setBottom2("文章数:" + rank.getCnt());
                            rank.setRight("WPM:" + rank.getWpm());
                            observableList.add(new RankListItemViewModel(this, rank));
                        }
                    } else {
                        XLog.i("无排名信息");
                    }
                    UC.finishRefreshing.call();
                    UC.finishLoadMore.call();

                    dismissDialog();
                });
    }

    public void loadListenData(int start, int pageNum) {
        model.getListenRanking(model.spGetUid(), type.get(), start, pageNum)
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
                        testingRank.setBottom1("单词数:" + testingRank.getTotalWord());
                        testingRank.setBottom2("文章数:" + testingRank.getTotalEssay());
                        long min = testingRank.getTotalTime() / 60;
                        testingRank.setRight(min + "分钟");
                        observableList.add(new RankListItemViewModel(this, testingRank));
                        List<TestingRank> datas = JsonUtil.json2List(data.get("data").toString(), TestingRank.class);

                        int i = 1;
                        for (TestingRank rank : datas) {
                            rank.setIndex(i++);
                            rank.setBottom1("单词数:" + rank.getTotalWord());
                            rank.setBottom2("文章数:" + rank.getTotalEssay());
                            min = rank.getTotalTime() / 60;
                            rank.setRight(min + "分钟");
                            observableList.add(new RankListItemViewModel(this, rank));
                        }
                    } else {
                        XLog.i("无排名信息");
                    }
                    UC.finishRefreshing.call();
                    UC.finishLoadMore.call();

                    dismissDialog();
                });
    }

    public void loadTalkData(int start, int pageNum) {
        model.getTalkRanking(model.spGetUid(), type.get(), start, pageNum)
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
                        testingRank.setBottom1("句子数:" + testingRank.getCount());
                        testingRank.setBottom2("平均分:" + testingRank.getAverScore());
                        testingRank.setRight("总分:" + testingRank.getScores());
                        observableList.add(new RankListItemViewModel(this, testingRank));
                        List<TestingRank> datas = JsonUtil.json2List(data.get("data").toString(), TestingRank.class);

                        int i = 1;
                        for (TestingRank rank : datas) {
                            rank.setIndex(i++);
                            rank.setBottom1("句子数:" + rank.getCount());
                            rank.setBottom2("平均分:" + rank.getAverScore());
                            rank.setRight("总分:" + rank.getScores());
                            observableList.add(new RankListItemViewModel(this, rank));
                        }
                    } else {
                        XLog.i("无排名信息");
                    }
                    UC.finishRefreshing.call();
                    UC.finishLoadMore.call();

                    dismissDialog();
                });
    }

    public void loadStudyData(int start, int pageNum) {
        model.getStudyRanking(model.spGetUid(), type.get(), start, pageNum)
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
                        testingRank.setBottom1("单词数:" + testingRank.getTotalWord());
                        testingRank.setBottom2("文章数:" + testingRank.getTotalEssay());
                        double hour = testingRank.getTotalTime() / 3600;
                        testingRank.setRight(hour + "小时");
                        observableList.add(new RankListItemViewModel(this, testingRank));
                        List<TestingRank> datas = JsonUtil.json2List(data.get("data").toString(), TestingRank.class);

                        int i = 1;
                        for (TestingRank rank : datas) {
                            rank.setIndex(i++);
                            rank.setBottom1("单词数:" + rank.getTotalWord());
                            rank.setBottom2("文章数:" + rank.getTotalEssay());
                            hour = rank.getTotalTime() / 3600;
                            rank.setRight(hour + "小时");
                            observableList.add(new RankListItemViewModel(this, rank));
                        }
                    } else {
                        XLog.i("无排名信息");
                    }
                    UC.finishRefreshing.call();
                    UC.finishLoadMore.call();

                    dismissDialog();
                });
    }

    public void loadTestData(int start, int pageNum) {
        model.getTestRanking(model.spGetUid(), type.get(), start, pageNum)
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
                        testingRank.setBottom1("总题数:" + testingRank.getTotalTest());
                        testingRank.setBottom2("正确数:" + testingRank.getTotalRight());
                        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数
                        String format = testingRank.getTotalTest() == 0 ? "0.00" : df.format(1.0 * testingRank.getTotalRight() / testingRank.getTotalTest());
                        testingRank.setRight("正确率:" + format);
                        observableList.add(new RankListItemViewModel(this, testingRank));
                        List<TestingRank> datas = JsonUtil.json2List(data.get("data").toString(), TestingRank.class);

                        int i = 1;
                        for (TestingRank rank : datas) {
                            rank.setIndex(i++);
                            rank.setBottom1("总题数:" + rank.getTotalTest());
                            rank.setBottom2("正确数:" + rank.getTotalRight());
                            format = rank.getTotalTest() == 0 ? "0.00" : df.format(1.0 * rank.getTotalRight() / rank.getTotalTest());
                            rank.setRight("正确率:" + format);
                            observableList.add(new RankListItemViewModel(this, rank));
                        }
                    } else {
                        XLog.i("无排名信息");
                    }
                    UC.finishRefreshing.call();
                    UC.finishLoadMore.call();

                    dismissDialog();
                });
    }


}