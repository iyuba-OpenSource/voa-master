
package com.iyuba.voa.ui.main.person.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentDownloadBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class DownloadListFragment extends BaseFragment<FragmentDownloadBinding, DownloadListViewModel> {
    private int mIndex;

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        //关闭动画效果 ,防止刷新闪烁
        SimpleItemAnimator sa = (SimpleItemAnimator) binding.rlList.getItemAnimator();
        sa.setSupportsChangeAnimations(false);
        binding.rlList.setItemAnimator(null);
        viewModel.setIsShowBack(true);
        viewModel.setTitleText("下载");
        viewModel.mPageIndex = mIndex;
        viewModel.loadData(1, 10);
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_download;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public DownloadListViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(DownloadListViewModel.class);
    }


    @Override
    public void initViewObservable() {
        viewModel.UC.finishRefreshing.observe(this, index -> {
            //结束刷新l
            binding.refresh.finishRefresh();
        });
        //监听上拉加载完成
        viewModel.UC.finishLoadMore.observe(this, index -> {
            //结束刷新
            binding.refresh.finishRefresh();
            binding.refresh.finishLoadMore();
        });

        viewModel.UC.deleteDialog.observe(this, listItemViewModel -> new MaterialDialog.Builder(getActivity())
                .title("提示")
                .content("您确定要删除这条数据吗?")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive((dialog1, which) -> {
                    viewModel.observableList.remove(listItemViewModel);
                    viewModel.delDownData(listItemViewModel.entity.get());
                })
                .contentColor(getResources().getColor(R.color.black))
                .build().show());
    }
}