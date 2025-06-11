package com.iyuba.voa.ui.main.person.rank;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;

import java.util.ArrayList;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

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
    public UIChangeObservable UC = new UIChangeObservable();

    private int pagerIndex;
    public ObservableBoolean isCollect = new ObservableBoolean(false);
    public ObservableBoolean isDownload = new ObservableBoolean(false);

    //ViewPager切换监听
    public BindingCommand<Integer> onPageSelectedCommand = new BindingCommand<>(index -> {
        pagerIndex = index;
    });
    public TitleTed titleTed;

    public RankViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<ArrayList<VoaText>> loadDataSuccess = new SingleLiveEvent<>();

        public SingleLiveEvent<String> pdfSuccessDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> collectSuccess = new SingleLiveEvent<>();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}