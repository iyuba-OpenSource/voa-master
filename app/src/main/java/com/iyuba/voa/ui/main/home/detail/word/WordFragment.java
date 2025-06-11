package com.iyuba.voa.ui.main.home.detail.word;

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
import com.iyuba.voa.data.entity.VoaExam;
import com.iyuba.voa.databinding.FragmentDetailWordBinding;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.JsonUtil;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class WordFragment extends BaseFragment<FragmentDetailWordBinding, WordViewModel> {


    private ExoPlayer mPlayer;
    private ExoPlayer playerWord;
    private MenuItem menuItem;
    private TitleTed titleTed;

    public WordFragment() {
    }

    @Override
    public void initData() {
        super.initData();

        Bundle extras = getArguments();
        if (extras == null) {
//            ToastUtils.showShort("本篇没有重点单词哟");
            return;
        }
        titleTed = extras.getParcelable(Constants.BUNDLE.KEY);

        if (TextUtils.isEmpty(viewModel.voaid)) {
            viewModel.titleTed = titleTed;
            viewModel.voaid = titleTed.getVoaId();
            String json = extras.getString(Constants.BUNDLE.KEY_4);
            List<VoaExam> voaExams = JsonUtil.json2List(json, VoaExam.class);
            viewModel.loadData(voaExams);
        }

    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public WordViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(WordViewModel.class);
    }


    @Override
    public void initViewObservable() {
        viewModel.UC.setPlayerProcess.observe(this, voaText -> mPlayer.seekTo((long) (voaText.getTiming() * 1000)));

        viewModel.UC.wordDialog.observe(this, xmlWord -> {
        });

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Bundle extras = getArguments();
            if (extras == null) {
                ToastUtils.showShort("本篇没有重点单词哟");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_detail_word;
    }

}