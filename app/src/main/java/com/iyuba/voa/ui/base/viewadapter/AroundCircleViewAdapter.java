package com.iyuba.voa.ui.base.viewadapter;

import androidx.databinding.BindingAdapter;

import com.iyuba.voa.ui.widget.AroundCircleView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/22
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class AroundCircleViewAdapter {

    @BindingAdapter(value = {"onProcess", "totalProcess"}, requireAll = false)
    public static void onProcess(AroundCircleView layout, long process, long totalProcess) {
        layout.setTotalProcess(totalProcess);
        layout.setProgress(process);
    }
}
