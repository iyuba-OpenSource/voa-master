package com.iyuba.voa.ui.main.person.collect.word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;

import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
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
public class WordCollectListViewModel extends BaseTitleViewModel<AppRepository> {

    public UIChangeObservable UC = new UIChangeObservable();
    public static final int pageNum = 100;
    int mMorePageNumber = 1;
    int mPageIndex;
    public ObservableList<WordCollectListItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<WordCollectListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_word);

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


    public WordCollectListViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent<Integer> finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent<Integer> finishLoadMore = new SingleLiveEvent<>();
        public SingleLiveEvent<WordCollectListItemViewModel> playAudio = new SingleLiveEvent<>();
        public SingleLiveEvent<String> pdfSuccessDialog = new SingleLiveEvent<>();
    }

    public String getSort() {
        return model.spGetWordSortWay();
    }

    public void saveSort(String way) {
        model.spSaveWordSortWay(way);
    }

    public void syncData() {
        showLoadingDialog();
        model.wordListService(model.spGetUid(), 1, 100)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new SimpleDisposableObserver<BaseResponse<List<XmlWord>>>() {
                    @Override
                    public void onResult(BaseResponse<List<XmlWord>> baseResponse) {
                        List<XmlWord> datas = baseResponse.getData();
                        if (mMorePageNumber > baseResponse.getTotal()) {
                            ToastUtils.showShort("已是最新数据");
                            return;
                        }

                        if (datas == null || datas.size() == 0) {
                            return;
                        }
                        model.roomAddWords(datas.toArray(new XmlWord[datas.size()]));
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                    }
                });
    }

    public void loadData(int page, int pageNum) {
        if (page == 1) {
            mMorePageNumber = 1;
        }
        List<XmlWord> xmlWords = model.roomGetWordsByWhere(getSort(), page, pageNum);
        if (xmlWords != null && xmlWords.size() > 0) {
            if (page == 1) {  //下拉刷新需要清除全部
                observableList.clear();
            }
            for (XmlWord data : xmlWords) {
                observableList.add(new WordCollectListItemViewModel(WordCollectListViewModel.this, data));
            }
        }
//        dismissDialog();
        UC.finishLoadMore.call();
        UC.finishRefreshing.call();
    }


    public void updateWordCollect(ObservableField<XmlWord> word) {
        if (!checkIsLogin()) {
            return;
        }
        final XmlWord xmlWord = word.get();
        String type = "insert";

        if (xmlWord.isCollect()) {
            type = "delete";
        }
        model.updateWord(model.spGetUid(), xmlWord.getKey(), type)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<JsonObject>) jsonObject -> {
                    int result = jsonObject.get("result").getAsInt();
                    if (result == 1) {
                        xmlWord.setCollect(!xmlWord.isCollect());
                        word.notifyChange();
                        if (xmlWord.isCollect()) {
                            model.roomAddWords(xmlWord);
                        } else {
                            model.roomDeleteWordByWord(xmlWord.getKey());
                        }
                        ToastUtils.showShort((xmlWord.isCollect() ? "" : "取消") + "收藏成功");
                    }
                });
    }

    public void exportPdf() {
        showLoadingDialog();
        model.getWordToPDF(model.spGetUid(), 1, pageNum)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jsonObject -> {
                    dismissDialog();
                    if (jsonObject == null || null == jsonObject.get("filePath")) {
                        ToastUtils.showShort("导出失败");
                        return;
                    }
                    String path = jsonObject.get("filePath").getAsString();
                    if (StringUtils.isTrimEmpty(path)) {
                        ToastUtils.showShort("导出失败");
                        return;
                    }
//                    path = API_HOST_APPS + "/iyuba" + path;
                    UC.pdfSuccessDialog.setValue(path);
                });
    }

    public XmlWord roomGetWord(String word) {
        return model.roomGetWordByWord(word);
    }
}