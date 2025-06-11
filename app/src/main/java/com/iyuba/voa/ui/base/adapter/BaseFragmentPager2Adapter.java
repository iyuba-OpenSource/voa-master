package com.iyuba.voa.ui.base.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Created by goldze on 2017/7/17.
 * FragmentPager适配器
 */

public class BaseFragmentPager2Adapter extends FragmentStateAdapter {
    private List<Fragment> list;//ViewPager要填充的fragment列表
    private List<String> title;//tab中的title文字列表

    //使用构造方法来将数据传进去
    public BaseFragmentPager2Adapter(FragmentActivity fa, List<Fragment> list, List<String> title) {
        super(fa);
        this.list = list;
        this.title = title;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
