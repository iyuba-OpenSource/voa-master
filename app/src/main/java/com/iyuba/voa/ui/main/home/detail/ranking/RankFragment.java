package com.iyuba.voa.ui.main.home.detail.ranking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.databinding.FragmentDetailRankBinding;
import com.iyuba.voa.utils.Constants;

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
public class RankFragment extends BaseFragment<FragmentDetailRankBinding, RankViewModel> {


    private ExoPlayer mPlayer;
    private ExoPlayer playerWord;
    private MenuItem menuItem;
    private TitleTed titleTed;

    public RankFragment() {
    }

    @Override
    public void initData() {
        super.initData();

        Bundle extras = getArguments();
        titleTed = extras.getParcelable(Constants.BUNDLE.KEY);

        if (TextUtils.isEmpty(viewModel.voaid)) {
            viewModel.titleTed = titleTed;
            viewModel.voaid = titleTed.getVoaId();
            viewModel.loadData(0, viewModel.pageNum);
        }

    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RankViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(RankViewModel.class);
    }


    @Override
    public void initViewObservable() {
        viewModel.UC.setPlayerProcess.observe(this, voaText -> mPlayer.seekTo((long) (voaText.getTiming() * 1000)));

        viewModel.UC.wordDialog.observe(this, xmlWord -> {
        });

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
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && titleTed != null) {
            viewModel.voaid = titleTed.getVoaId();
            viewModel.loadData(0, viewModel.pageNum);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_detail_rank;
    }

}