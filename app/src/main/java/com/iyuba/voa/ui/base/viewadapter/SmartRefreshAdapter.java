package com.iyuba.voa.ui.base.viewadapter;

import androidx.databinding.BindingAdapter;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class SmartRefreshAdapter {
    @BindingAdapter(value = {"onSmartRefreshCommand", "onSmartLoadMoreCommand"}, requireAll = false)
    public static void onSmartRefreshAndLoadMoreCommand(SmartRefreshLayout layout, final BindingCommand onSmartRefreshCommand, final BindingCommand onSmartLoadMoreCommand) {
        //刷新的监听事件
        layout.setOnRefreshListener(refreshLayout -> {
            //请求数据
            if (onSmartRefreshCommand != null) {
                onSmartRefreshCommand.execute();
            }
        });
        //加载的监听事件
        layout.setOnLoadMoreListener(refreshLayout -> {
            if (onSmartLoadMoreCommand != null) {
                onSmartLoadMoreCommand.execute();
            }
        });
    }
}
