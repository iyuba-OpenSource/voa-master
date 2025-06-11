package com.iyuba.voa.ui.main.person.feedback;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.iyuba.voa.R;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.AppUtils;
import com.iyuba.voa.utils.CheckUtils;
import com.iyuba.voa.utils.PhoneUtil;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/30
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class FeedbackViewModel extends BaseTitleViewModel<AppRepository> {
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();
    public BindingCommand<Void> clickSubmit = new BindingCommand<>(() -> {
        if (StringUtils.isTrimEmpty(email.get()) || !CheckUtils.isEmail(email.get())) {
            ToastUtils.showShort("请填写正确的邮箱地址");
            return;
        }
        if (StringUtils.isTrimEmpty(content.get())) {
            ToastUtils.showShort("内容不能为空");
            return;
        }
        submitData();
    });

    public FeedbackViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public void submitData() {
        StringBuilder sb = new StringBuilder();
        sb.append(content.get());
        sb.append("\n");
        sb.append("versionName:[").append(AppUtils.getVerName(Utils.getContext()));
        sb.append("]\n");
        sb.append("appName:[").append(Utils.getContext().getString(R.string.app_name));
        sb.append("]\n");
        sb.append("phoneModel:[").append(PhoneUtil.getMobileBrand());
        sb.append("-");
        sb.append(PhoneUtil.getMobileModel(Utils.getContext()));
        sb.append("]\n");
        sb.append("SDK:[").append(PhoneUtil.getSDKVersion(Utils.getContext()));
        sb.append("]\n");
        sb.append("sysVersion:[").append(PhoneUtil.getSystemVersion());
        sb.append("]\n");
        sb.append("system:[").append(PhoneUtil.isHarmonyOS() ? "HarmonyOS" : "android");
        sb.append("]");

        model.feedback(91001, model.spGetUid(), sb.toString(), email.get())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver() {
                    @Override
                    public void onResult(Object o) {
                        email.set("");
                        content.set("");
                        ToastUtils.showShort("反馈成功");
                    }
                });
    }
}
