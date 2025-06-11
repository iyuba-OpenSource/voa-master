package com.iyuba.voa.ui.vip.order;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.CipherUtils;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class OrderViewModel extends BaseTitleViewModel<AppRepository> {

    private Disposable mSubscription;

    public UIChangeObservable uc = new UIChangeObservable();

    public ObservableInt payWay = new ObservableInt(1);  //0微信 1支付宝


    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> order = new ObservableField<>();
    public ObservableField<String> yuan = new ObservableField<>();


    public int productId;
    public int month;

    public BindingCommand clickSubmit = new BindingCommand(() -> {
        switch (payWay.get()) {
            case 0:
                if (BuildConfig.APPLICATION_ID.contains("voavoa"))  //voavoa包名用客户端支付
                    payForWx();
                else payForWxByWeb();
                break;
            case 1:
                payForZfb();
                break;
        }
    });


    public class UIChangeObservable {
        public SingleLiveEvent<String> alipayCallback = new SingleLiveEvent<>();
        public SingleLiveEvent<JsonObject> weixinPayCallback = new SingleLiveEvent<>();
        public SingleLiveEvent<String> startWeb = new SingleLiveEvent<>();
    }

    public OrderViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(WxPayEvent.class)
                .subscribe(wxPayEvent -> {
                    loadVIPInfo();
                });
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
    }

    public void payForWx() {
        model.weixinPay(model.spGetUid(), yuan.get(), month, productId, order.get())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    int code = jo.get("retcode").getAsInt();
                    if (code != 0) {
                        ToastUtils.showShort("支付失败");
                        return;
                    }
                    uc.weixinPayCallback.setValue(jo);
                });
    }

    public void payForWxByWeb() {
        String sign = CipherUtils.md5("iyubaPay" + yuan.get() + month + productId + DateUtil.getNowDay());
        String weixinUrl = "http://m." + Constants.CONFIG.DOMAIN + "/weixinPay/appWeiXinPay.html" +
                "?uid=" + model.spGetUid() + "" +
                "&money=" + yuan.get() +
                "&amount=" + month +
                "&appid=" + BuildConfig.APPID_VALUE +
                "&productid=" + productId +
                "&weichatId=wx5e29e754f5fb86d9" +
                "&sign=" + sign;
        uc.startWeb.setValue(weixinUrl);
     /*   Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, "微信支付");
        bundle.putString(Constants.BUNDLE.KEY_0, weixinUrl);
//        startContainerActivity(WebFragment.class.getCanonicalName(),bundle);
        startContainerActivity(WebFragment.class.getCanonicalName(), bundle);*/

    }

    public void payForZfb() {
        model.alipay(model.spGetUid(), yuan.get(), month, productId, order.get())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    JsonElement je = jo.get("alipayTradeStr");
                    if (null == je) {
                        ToastUtils.showShort("支付失败");
                        return;
                    }
                    String alipayTradeStr = je.getAsString();
                    if (!"".equals(alipayTradeStr)) {
                        uc.alipayCallback.setValue(alipayTradeStr);
                        XLog.i(alipayTradeStr);
                    }
                });
    }

    public String getUn() {
        return model.spGetUserName();
    }

    public void updateServer(String data) {
        model.notifyAliNew(data)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    if ("200".equals(jo.get("code").getAsString())) {
                        loadVIPInfo();
                        ToastUtils.showShort("开通成功！若未生效重新登陆即可");
                        finish();
                    } else {
                        ToastUtils.showShort("购买失败");
                    }
                });
    }

    public class WxPayEvent {

    }

}
