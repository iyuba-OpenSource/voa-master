package com.iyuba.voa.ui.main.home.detail.exercise;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlayer;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.ExamRecord;
import com.iyuba.voa.data.entity.VoaExam;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.databinding.FragmentDetailExerciseBinding;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.voa.utils.JsonUtil;

import java.util.List;

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
public class ExerciseFragment extends BaseFragment<FragmentDetailExerciseBinding, ExerciseViewModel> {


    private ExoPlayer mPlayer;
    private ExoPlayer playerWord;
    private MenuItem menuItem;

    public ExerciseFragment() {
    }

    @Override
    public void initData() {
        super.initData();
        Bundle extras = getArguments();
        if (extras == null) {
            viewModel.voaid = "-1";
            viewModel.entityAnswer.clear();
            for (int i = 0; i < 3; i++) {
                viewModel.entityAnswer.add(new ExamRecord());
            }
            return;
        }
        viewModel.titleTed = extras.getParcelable(Constants.BUNDLE.KEY);

        if (TextUtils.isEmpty(viewModel.voaid)) {
            viewModel.voaid = viewModel.titleTed.getVoaId();
            String json = extras.getString(Constants.BUNDLE.KEY_3);
            List<VoaExam> voaExams = JsonUtil.json2List(json, VoaExam.class);
            viewModel.setData(voaExams);
            VoaExamManager.getInstance().setExams(voaExams);
            VoaExamManager.getInstance().clearUserAnswer();
            List<ExamRecord> examRecords = VoaExamManager.getInstance().initExamRecords(DateUtil.getNowTime(), Long.parseLong(viewModel.voaid));
            viewModel.entityAnswer.clear();
            viewModel.entityAnswer.addAll(examRecords);
        }
//        viewModel.loadData();
    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ExerciseViewModel initViewModel() {

        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(ExerciseViewModel.class);
    }


    @Override
    public void initViewObservable() {
        viewModel.UC.setPlayerProcess.observe(this, voaText -> {

        });

        viewModel.UC.wordDialog.observe(this, new Observer<XmlWord>() {
            @Override
            public void onChanged(XmlWord xmlWord) {
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
        return R.layout.fragment_detail_exercise;
    }


}