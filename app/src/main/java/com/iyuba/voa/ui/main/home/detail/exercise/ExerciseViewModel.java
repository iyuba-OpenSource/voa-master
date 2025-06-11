package com.iyuba.voa.ui.main.home.detail.exercise;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.ExamRecord;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaExam;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import timber.log.Timber;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/9/5
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class ExerciseViewModel extends BaseTitleViewModel<AppRepository> {
    final int totalTopic = 3;  //总题目
    public UIChangeObservable UC = new UIChangeObservable();
    public TitleTed titleTed;


    private long startTime;
    private long endTime;

    public ObservableField<VoaExam> entity = new ObservableField<>();
    public ObservableList<ExamRecord> entityAnswer = new ObservableArrayList<>();
    public ObservableInt topicIndex = new ObservableInt(1);   //题目索引
    public ObservableInt examProcess = new ObservableInt(-1);  //练习进度 -1 未开始练习 0开始做题  1做完1题  2做完2题  3做完3题  4提交完成,成绩显示  5显示成绩后查看原题

    public List<VoaExam> examList = new ArrayList<>();

    public ObservableList<ExerciseItemViewModel> observableList1 = new ObservableArrayList<>();
    public ObservableList<ExerciseItemViewModel> observableList2 = new ObservableArrayList<>();

    public ItemBinding<ExerciseItemViewModel> itemBinding1 = ItemBinding.of(BR.viewModel, R.layout.item_exam_answer);
    public ItemBinding<ExerciseItemViewModel> itemBinding2 = ItemBinding.of(BR.viewModel, R.layout.item_exam_answer);
    public String voaid;

    public BindingCommand<String> textChanged = new BindingCommand<>(s -> {
        int position = topicIndex.get() - 1;
        if (TextUtils.isEmpty(s)) {
            removeUserAnswer(position);
        } else {
            setUserAnswer(s, position);
        }
    });


    public BindingCommand<Void> clickPrevious = new BindingCommand<>(() -> {
        int i1 = topicIndex.get();
        int i = i1 <= 1 ? 1 : i1 - 1;
        entity.set(examList.get(i - 1));
        topicIndex.set(i);
        topicIndex.notifyChange();
        entity.notifyChange();
    });
    public BindingCommand<Void> clickNext = new BindingCommand<>(() -> {
        int i1 = topicIndex.get();
        int i = i1 >= totalTopic ? totalTopic : i1 + 1;
        entity.set(examList.get(i - 1));
        topicIndex.set(i);
        entity.notifyChange();
    });
    public BindingCommand<Void> clickStart = new BindingCommand<>(() -> {
        if (!checkIsLogin())
            return;
        if ("-1".equals(voaid)) {
            ToastUtils.showShort("本篇文章暂时没有练习题哟");
            return;
        }
        startTime = System.currentTimeMillis();
        examProcess.set(0);
    });

    public BindingCommand<Void> clickSubmit = new BindingCommand<>(() -> {
        endTime = System.currentTimeMillis();
        submitData();
    });

    public BindingCommand<Void> clickLookScore = new BindingCommand<>(() -> {
        examProcess.set(4);
    });

    public BindingCommand<Void> clickLookTopic = new BindingCommand<>(() -> {
        examProcess.set(5);
    });
    private int rightAnswerNumber;

    public void clickAnswer(String choice) {
        setUserAnswer(choice, topicIndex.get() - 1);
    }

    public ExerciseViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<VoaText> setPlayerProcess = new SingleLiveEvent<>();
        public SingleLiveEvent<Integer> playerListener = new SingleLiveEvent<>();
        public SingleLiveEvent<XmlWord> wordDialog = new SingleLiveEvent<>();

        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();
    }


    public void submitData() {
        model.updateTestRecordNew(model.spGetUid(), entityAnswer)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    if ("1".equals(jo.get("result").getAsString())) {
                        XLog.i("练习记录上传成功，积分=" + jo.get("jiFen").getAsString());
                        examProcess.set(4);
                        setExamResult();
                        buildRightData();
                        buildUserData();
                    }
                });

    }


    private String digPitInString(String source, String key) {
        if (source.contains("_")) {
            Timber.i("The source already have pits!!");
            return source;
        }
        if (!source.contains(key)) {
            Timber.i("keyword %s not found!!", key);
            return source;
        }
        String blank = "____";
        return source.replace(key, blank);
    }

    // this strange method is for some ugly exam data
    public List<VoaExam> setData(List<VoaExam> exams) {
        for (VoaExam exam : exams) {
            exam.setVoaid(Long.parseLong(voaid));
            switch (exam.getTestType()) {
                case 1: {
                    exam.setQuestion(exam.getQuestion().trim());
                    break;
                }
                case 2: {
                    String key = exam.getAnswer().trim();
                    exam.setQuestion(digPitInString(exam.getQuestion(), key).trim());
                    break;
                }
                case 3: {
                    String key;
                    switch (exam.getAnswer().trim()) {
                        case "A":
                        case "a":
                            key = exam.getAnswer1().trim();
                            break;
                        case "B":
                        case "b":
                            key = exam.getAnswer2().trim();
                            break;
                        case "C":
                        case "c":
                            key = exam.getAnswer3().trim();
                            break;
                        case "D":
                        case "d":
                            key = exam.getAnswer4().trim();
                            break;
                        default:
                            key = "|@_@|";
                            break;
                    }
                    exam.setQuestion(digPitInString(exam.getQuestion(), key).trim());
                    break;
                }
            }
        }
        entity.set(exams.get(topicIndex.get() - 1));
        examList.addAll(exams);
        return exams;
    }

    public void setUserAnswer(String answer, int position) {
        if (TextUtils.isEmpty(getUserAnswer(position)) && examProcess.get() < totalTopic) {
            examProcess.set(examProcess.get() + 1);
        }
        updateExerciseNum();
        boolean result = entityAnswer.get(position).getRightAnswer().trim()
                .equalsIgnoreCase(answer.trim());
        entityAnswer.get(position).setUserAnswer(answer);
        entityAnswer.get(position).setAnswerResult(result ? 1 : 0);
        entityAnswer.get(position).setTestTime(DateUtil.getNowTime());
    }

    public void updateExerciseNum() {
        if (examProcess.get() <= 0 || examProcess.get() > 3) {
            return;
        }
        TitleTed tt = model.roomGetTitleTedById(voaid);
        if (null != tt) {
            tt.setExerciseNum(examProcess.get());
        } else
            titleTed.setExerciseNum(examProcess.get());
        model.roomAddTitleTeds(titleTed);
    }

    public String getUserAnswer(int position) {
        return entityAnswer.get(position).getUserAnswer();
    }

    public void removeUserAnswer(int position) {
        entityAnswer.get(position).setUserAnswer("");
        entityAnswer.get(position).setAnswerResult(0);

    }


    private void setExamResult() {
        VoaExam voaExam = entity.get();
        StringBuilder sbNumber = new StringBuilder();
        StringBuilder sbRate = new StringBuilder();
        sbNumber.append(examList.size()).append("题");
        sbRate.append(calculateResult()).append("%");
        voaExam.setTopicNum(sbNumber.toString());
        voaExam.setRightRate(sbRate.toString());
        voaExam.setSpeedTime(DateUtil.transferTime(endTime - startTime));
        entity.notifyChange();
    }

    private int calculateResult() {
        for (int i = 0, length = entityAnswer.size(); i < length; i++) {
            if (entityAnswer.get(i).getAnswerResult() == 1)
                rightAnswerNumber++;
        }
        return (int) (((double) rightAnswerNumber / entityAnswer.size()) * 100);
    }


    private void buildUserData() {
        observableList1.clear();
        for (int i = 0, length = entityAnswer.size(); i < length; i++) {
            ExamRecord examRecord = entityAnswer.get(i);
            observableList1.add(new ExerciseItemViewModel(this, i, examRecord.getUserAnswer(), examRecord.getAnswerResult()));
        }
    }


    private void buildRightData() {
        observableList2.clear();
        for (int i = 0, length = entityAnswer.size(); i < length; i++) {
            observableList2.add(new ExerciseItemViewModel(this, i, entityAnswer.get(i).getRightAnswer(), 1));
        }
    }

}