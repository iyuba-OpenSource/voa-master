package me.goldze.mvvmhabit.binding.viewadapter.recyclerview;

import androidx.annotation.NonNull;

import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/1
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class rcyAdapter extends BindingRecyclerViewAdapter {

    public rcyAdapter(@NonNull ItemBinding itemBinding) {
        super(itemBinding);
    }

    @NonNull
    @Override
    public ItemBinding getItemBinding() {
        return super.getItemBinding();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
