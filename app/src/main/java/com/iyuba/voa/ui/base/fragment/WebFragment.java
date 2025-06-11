package com.iyuba.voa.ui.base.fragment;

import static com.iyuba.voa.utils.Constants.CONFIG.COMPANY;
import static com.iyuba.voa.utils.Constants.CONFIG.PROTOCOL_IP;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.databinding.FragmentWebBinding;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.vip.order.OrderActivity;
import com.iyuba.voa.ui.vip.order.OrderViewModel;
import com.iyuba.voa.utils.Constants;
import com.just.agentweb.AgentWeb;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/30
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class WebFragment extends BaseFragment<FragmentWebBinding, BaseTitleViewModel> {


    private AgentWeb mAgentWeb;
    // company:1爱语吧  2上海画笙  3爱语言  //华为为爱语吧  其他为3
    private final String privacy = PROTOCOL_IP + "/protocolpri.jsp?company=" + COMPANY;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_web;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        Bundle extras = getArguments();
        String title = "";
        String url = "";
        if (extras != null) {
            title = extras.getString(Constants.BUNDLE.KEY);
            url = extras.getString(Constants.BUNDLE.KEY_0);
        }
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        viewModel.setIsShowBack(true);
        viewModel.setTitleText(title);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) binding.llWeb, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().post(OrderViewModel.WxPayEvent.class);
    }
}
