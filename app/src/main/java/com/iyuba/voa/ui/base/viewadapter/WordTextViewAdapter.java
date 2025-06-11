package com.iyuba.voa.ui.base.viewadapter;

import androidx.databinding.BindingAdapter;

import com.iyuba.voa.ui.widget.SelectWordTextView;

import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class WordTextViewAdapter {
    @BindingAdapter(value = {"onWordClickCommand"}, requireAll = false)
    public static void onWordClickCommand(SelectWordTextView layout, final BindingCommand onWordClickCommand) {
        layout.setOnClickWordListener(word -> {
            if (onWordClickCommand != null) {
                onWordClickCommand.execute(word);
            }
        });

    }
}
