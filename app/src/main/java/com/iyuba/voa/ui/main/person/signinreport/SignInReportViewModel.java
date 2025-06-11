package com.iyuba.voa.ui.main.person.signinreport;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.voa.data.entity.ShareInfoRecord;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.main.person.rank.RankListViewModel;
import com.iyuba.voa.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/30
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class SignInReportViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable UC = new UIChangeObservable();
    private String curTime = "";

    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();
    public BindingCommand<Void> clickLastMonth = new BindingCommand<>(() -> {
        UC.clickLast.call();
    });
    public BindingCommand<Void> clickNextMonth = new BindingCommand<>(() -> {
        UC.clickNext.call();
    });

    public SignInReportViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<List<ShareInfoRecord>> loadDataSuccess = new SingleLiveEvent<>();
        public SingleLiveEvent clickLast = new SingleLiveEvent<>();
        public SingleLiveEvent clickNext = new SingleLiveEvent<>();

    }

    public void loadData(int[] curDate) {
        if (curDate[1] < 10) {
            curTime = curDate[0] + "0" + curDate[1];
        } else {
            curTime = curDate[0] + "" + curDate[1];
        }
        model.getShareInfoShow(model.spGetUid(), curTime)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    JsonElement resultCode = jo.get("result");
                    if (resultCode != null && resultCode.getAsString().equals("200")) {
                        List<ShareInfoRecord> shareInfoRecords = JsonUtil.json2List(jo.get("record").toString(), ShareInfoRecord.class);
                        if (shareInfoRecords == null || shareInfoRecords.size() < 1) {
                            UC.loadDataSuccess.setValue(new ArrayList<>());
                        } else {
                            UC.loadDataSuccess.setValue(shareInfoRecords);
                            return;
                        }
                    }
                });
    }

}
