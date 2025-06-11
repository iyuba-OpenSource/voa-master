package com.iyuba.voa.ui.main.search;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.SearchContent;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.http.observer.ApiDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.JsonUtil;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/10/13
 * 邮箱：jxfengmtx@gmail.com
 */
public class SearchViewModel extends BaseTitleViewModel<AppRepository> {
    public ExoPlayer wordPlayer;
    public ObservableList<HotWordItemViewModel> recommendList = new ObservableArrayList<>();
    public ObservableList<SearchItemViewModel> observableList = new ObservableArrayList<>();

    public ObservableField<String> searchText = new ObservableField<>();

    public ItemBinding<HotWordItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_hot_word);
    //    public ItemBinding<HotWordItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_hot_word);

    public final OnItemBind<SearchItemViewModel> itemBinding2 = (itemBinding, position, item) -> {
        if (position == 0 && item.entity.get() != null) {
            itemBinding.set(BR.viewModel, R.layout.item_rv_word_search);
        } else if (position == 1 && observableList.get(0).entity.get() != null)
            itemBinding.set(BR.viewModel, R.layout.item_rv_search_header);
        else
            itemBinding.set(BR.viewModel, R.layout.item_rv_home_search);

    };

    public BindingCommand<Void> backOnClick = new BindingCommand<>(() -> finish());
    public BindingCommand<Void> clearText = new BindingCommand<>(() -> searchText.set(""));
    public BindingCommand<Void> searchClick = new BindingCommand<>(() -> searchData());

    public BindingCommand<String> textChanged = new BindingCommand<>(s -> {
        if (s.length() == 0) {
            observableList.clear();
        }
    });

    public BindingCommand<String> searchCall = new BindingCommand<>(s -> {
        searchData();
    });

    public SearchViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }


    public void searchData() {
        String key = searchText.get();
        if(TextUtils.isEmpty(key)){
            ToastUtils.showShort("暂无数据");
            return;
        }
        showLoadingDialog();
        model.searchApiNew(key, 1, 3, 0, model.spGetUid())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    if (jo.get("titleToal").getAsInt() > 0) {
                        observableList.clear();
                        if (jo.get("Word") != null) {
                            SearchContent searchContent = JsonUtil.json2Entity(jo.toString(), SearchContent.class);
                            searchContent.setCollect(roomGetWord(searchContent.getWord()) != null);
                            observableList.add(new SearchItemViewModel(SearchViewModel.this, searchContent));
                        }
                        if (observableList.size() > 0)   //添加精彩文章提示语
                            observableList.add(new SearchItemViewModel(SearchViewModel.this));

                        String titleData = jo.get("titleData").toString();
                        List<TitleTed> titleDataDTOS = JsonUtil.json2List(titleData, TitleTed.class);
                        for (TitleTed dataDTO : titleDataDTOS) {
                            observableList.add(new SearchItemViewModel(SearchViewModel.this, dataDTO));
                        }
                    } else ToastUtils.showShort("暂无数据");
                    dismissDialog();
                });
    }

    public void searchDataByHotWord(String word) {
        model.recommendByKeyword(model.spGetUid(), word)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new ApiDisposableObserver<List<TitleTed>>() {
                    @Override
                    public void onResult(List<TitleTed> datas) {
                        if (datas == null || datas.size() == 0) {
                            ToastUtils.showShort("无数据");
                            return;
                        }
                        observableList.clear();
                        for (TitleTed dataDTO : datas) {
                            observableList.add(new SearchItemViewModel(SearchViewModel.this, dataDTO));
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();

                    }
                });
    }

    public void loadRecommend() {
        model.recommend()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    String result = jo.get("result").getAsString();
                    if ("200".equals(result)) {
                        List<String> data = JsonUtil.jsonToList(jo.get("data").toString());
                        recommendList.clear();
                        for (String datum : data) {
                            recommendList.add(new HotWordItemViewModel(SearchViewModel.this, datum));
                        }
                    }
                });
    }


    public void updateWordCollect(ObservableField<SearchContent> word) {
        if (!checkIsLogin()) {
            return;
        }
        final SearchContent searchWord = word.get();
        String type = "insert";

        if (searchWord.isCollect()) {
            type = "delete";
        }
        model.updateWord(model.spGetUid(), word.get().getWord(), type)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<JsonObject>) jsonObject -> {
                    int result = jsonObject.get("result").getAsInt();
                    if (result == 1) {
                        searchWord.setCollect(!searchWord.isCollect());
                        word.notifyChange();
                        if (searchWord.isCollect()) {
                            XmlWord xmlWord = new XmlWord();
                            xmlWord.setKey(searchWord.getWord());
                            xmlWord.setDef(searchWord.getDef());
                            xmlWord.setPron(searchWord.getPhEn());
                            xmlWord.setAudio(searchWord.getPhEnMp3());
                            model.roomAddWords(xmlWord);
                        } else {
                            model.roomDeleteWordByWord(searchWord.getWord());
                        }
                        ToastUtils.showShort((searchWord.isCollect() ? "" : "取消") + "收藏成功");
                    }
                });
    }

    public XmlWord roomGetWord(String word) {
        return model.roomGetWordByWord(word);
    }

    /**
     * 获取条目下标
     *
     * @param listItemViewModel
     * @return
     */
    public int getListsItemPosition(SearchItemViewModel listItemViewModel) {
        return observableList.indexOf(listItemViewModel);
    }
}
