package com.iyuba.voa.ui.vip.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.alipay.sdk.app.PayTask;
import com.elvishew.xlog.XLog;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.module.toolbox.MD5;
import com.iyuba.voa.BR;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.ActivityOrderBinding;
import com.iyuba.voa.ui.base.BaseTitleActivity;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.ThreadControl;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/04/02
 * 邮箱：jxfengmtx@gmail.com
 */
public class OrderActivity extends BaseTitleActivity<ActivityOrderBinding, OrderViewModel> {


    private IWXAPI msgApi;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_order;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .init();
        viewModel.setTitleText("支付");
        viewModel.setIsShowBack(true);

        Bundle arguments = getIntent().getExtras();
        viewModel.userName.set(viewModel.getUn());
        viewModel.order.set(arguments.getString(Constants.BUNDLE.KEY_0));
        viewModel.yuan.set(arguments.getString(Constants.BUNDLE.KEY_1));
        viewModel.productId = arguments.getInt(Constants.BUNDLE.KEY_2);
        viewModel.month = arguments.getInt(Constants.BUNDLE.KEY_3);
        if (BuildConfig.DEBUG)
            viewModel.yuan.set("0.01");
        msgApi = WXAPIFactory.createWXAPI(this, null);
    }

    @Override
    public OrderViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(OrderViewModel.class);
    }

    @Override
    public void initViewObservable() {
        viewModel.uc.alipayCallback.observe(this, str -> {
            ThreadControl.EXECUTOR.execute(() -> {
                PayTask alipay = new PayTask(OrderActivity.this);
                Map<String, String> result = alipay.payV2(str, true);
                ThreadControl.runUi(() -> {
                    viewModel.updateServer(result.toString());
                });
                XLog.i(result.toString());
            });
        });
        viewModel.uc.weixinPayCallback.observe(this, jo -> {
            ThreadControl.EXECUTOR.execute(() -> {
                PayReq req = new PayReq();
                req.appId = jo.get("appid").getAsString();
                req.partnerId = jo.get("mch_id").getAsString();
                req.prepayId = jo.get("prepayid").getAsString();
                req.nonceStr = jo.get("noncestr").getAsString();
                req.timeStamp = jo.get("timestamp").getAsString();
                req.packageValue = "Sign=WXPay";
                req.sign = buildWeixinSign(req, jo.get("mch_key").getAsString());
                msgApi.sendReq(req);
            });

        });
        viewModel.uc.startWeb.observe(this, str -> {
            Uri uri = Uri.parse(str);    //设置跳转的网站
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        binding.orderPayMethod1.setOnClickListener(view -> {
            viewModel.payWay.set(0);
            viewModel.payWay.notifyChange();
        });
        binding.orderPayMethod2.setOnClickListener(view -> {
            viewModel.payWay.set(1);
            viewModel.payWay.notifyChange();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadVIPInfo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewModel.loadVIPInfo();
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildWeixinStringA(payReq));
        sb.append("&key=").append(key);
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }

    private String buildWeixinStringA(PayReq payReq) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        return sb.toString();
    }
}