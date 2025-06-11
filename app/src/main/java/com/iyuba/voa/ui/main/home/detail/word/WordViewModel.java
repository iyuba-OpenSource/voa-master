package com.iyuba.voa.ui.main.home.detail.word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaExam;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;

import java.util.List;

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
public class WordViewModel extends BaseTitleViewModel<AppRepository> {
    final int pageNum = 20;
    int mMorePageNumber = 1;
    public UIChangeObservable UC = new UIChangeObservable();


    public ObservableList<WordItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<WordItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_word_detail);
    public String voaid;
    public TitleTed titleTed;

    public WordViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);

    }


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


    public void loadData(List<VoaExam> exams) {
        observableList.clear();
        for (VoaExam exam : exams) {
            exam.setVoaid(Long.parseLong(voaid));
            observableList.add(new WordItemViewModel(this, exam));
        }
    }

}