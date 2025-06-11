package com.iyuba.voa.ui.main.splash;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iyuba.voa.data.entity.Advertising;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.main.MainActivity;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.JsonUtil;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxTimer;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/11
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class SplashViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable uc = new UIChangeObservable();
    public ObservableField<Advertising> entity = new ObservableField<>();

    public BindingCommand<Advertising> clickSubmit = new BindingCommand<>(() -> {
        if (entity.get() != null) {
            uc.startMain.setValue(entity.get());
        }
//        uc.startMain.setValue(entity.get());
    });

    public SplashViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<Advertising> showYdAd = new SingleLiveEvent<>();
        public SingleLiveEvent<Advertising> startMain = new SingleLiveEvent<>();
    }

    public void getMessageLiu() {
        model.getAdEntryAll("1", model.spGetUid())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonArray>) array -> {
                    if (array.size() > 0) {
                        JsonObject jo = array.get(0).getAsJsonObject();
                        String result = jo.get("result").getAsString();
                        if (result.equals("1")) {
                            Advertising data = JsonUtil.json2Entity(jo.get("data").toString(), Advertising.class);
                            switch (data.getType()) {
                                case "youdao":
                                    uc.showYdAd.call();
                                    break;
                                case "web":
                                    entity.set(data);
                                    entity.notifyChange();
                                    break;
                                default:
                                    uc.showYdAd.call();
                            }
                        }
                    }
                });
    }
}
