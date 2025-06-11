package com.iyuba.voa.ui.main.person.rank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.databinding.FragmentRankListBinding;
import com.iyuba.voa.utils.Constants;

import java.util.Arrays;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.MaterialDialogUtil;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankListFragment extends BaseFragment<FragmentRankListBinding, RankListViewModel> {


    private TitleTed title;

    public RankListFragment() {
    }

    @Override
    public void initData() {
        super.initData();
        Bundle arguments = getArguments();
        viewModel.title = arguments.getString(Constants.BUNDLE.KEY);
        viewModel.loadData(0, viewModel.pageNum);
    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RankListViewModel initViewModel() {

        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(RankListViewModel.class);
    }


    @Override
    public void initViewObservable() {
        String[] dates = {"今天", "本周"};
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

        viewModel.UC.showFilterDialog.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                MaterialDialogUtil.showListDialog((AppCompatActivity) getActivity(), "请选择", Arrays.asList(dates), new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                viewModel.type.set("D");
                                break;
                            case 1:
                                viewModel.type.set("W");
                                break;
                        }
                        viewModel.loadData(0, viewModel.pageNum);
                    }
                });
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_rank_list;
    }

}