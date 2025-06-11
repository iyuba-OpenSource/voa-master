package com.iyuba.voa.ui.main.home.detail.exercise;


import com.iyuba.voa.data.entity.ExamRecord;
import com.iyuba.voa.data.entity.VoaExam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoaExamManager {
    private static VoaExamManager instance = new VoaExamManager();

    private VoaExamManager() {
    }

    public static VoaExamManager getInstance() {
        return instance;
    }

    private List<VoaExam> exams;
    private List<ExamRecord> records;

    private HashMap<Integer, ExamRecord> userAnswers;
    private boolean isSubmitted;

    private int rightAnswerNumber;
    private int rightRate;

    public void setExams(List<VoaExam> exams) {
        this.exams = exams;
    }

    public int getVoaExamsAmount() {
        return exams.size();
    }

    public VoaExam getVoaExam(int position) {
        return (position >= 0 && position < exams.size()) ? exams.get(position) : null;
    }

    public boolean isSingleChoiceExam(int position) {
        return exams.get(position).getTestType() == 1;
    }

    public boolean isFillinExam(int position) {
        return exams.get(position).getTestType() == 2;
    }

    public boolean isFillChoiceExam(int position) {
        return exams.get(position).getTestType() == 3;
    }

    public boolean hasFormerExam(int position) {
        return position > 0;
    }

    public boolean hasNextExam(int position) {
        return position < exams.size() - 1;
    }

    public boolean hasVoaExams() {
        return exams != null && exams.size() > 0;
    }

    public List<ExamRecord> initExamRecords(String stime, long voaid) {
        records = new ArrayList<>();
        for (int i = 0; i < exams.size(); i++) {
            ExamRecord record = new ExamRecord();
            record.setBeginTime(stime);
            record.setLessonId(voaid);
            record.setTestNumber(exams.get(i).getIndexId());
            record.setRightAnswer(exams.get(i).getAnswer());
            record.setAnswerResult(0);
            records.add(record);
        }
        rightAnswerNumber = 0;
        rightRate = 0;

        isSubmitted = false;
        return records;
    }

    public void clearUserAnswer() {
        if (userAnswers == null) {
            userAnswers = new HashMap<>();
        } else {
            userAnswers.clear();
        }
    }

    public void destroy() {
        exams = null;
        records = null;
        userAnswers = null;
    }

    public boolean isInitialized() {
        return records != null && records.size() > 0 && userAnswers != null;
    }

    public void setStartTime(String stime, int position) {
        records.get(position).setBeginTime(stime);
    }

    public void setTestTime(String testtime, int position) {
        records.get(position).setTestTime(testtime);
    }

    public void setUserAnswer(String answer, int position) {
        boolean result = records.get(position).getRightAnswer().trim()
                .equalsIgnoreCase(answer.trim());
        records.get(position).setUserAnswer(answer);
        records.get(position).setAnswerResult(result ? 1 : 0);

        userAnswers.put(position, records.get(position));
    }

    public void removeUserAnswer(int position) {
        records.get(position).setUserAnswer("");
        records.get(position).setAnswerResult(0);

        userAnswers.remove(position);
    }

    public boolean isUserSubmitted() {
        return isSubmitted;
    }

    public boolean isAllFinished() {
        return records.size() == userAnswers.size();
    }

    public boolean hasUserAnswer(int position) {
        return getUserAnswer(position) != null;
    }

    public String getUserAnswer(int position) {
        return records.get(position).getUserAnswer();
    }

    public List<ExamRecord> getUserExamRecords() {
        if (userAnswers != null) {
            return new ArrayList<>(userAnswers.values());
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getUserAnswerList() {
        List<String> list = new ArrayList<>();
        for (int i = 0, length = records.size(); i < length; i++) {
            list.add(records.get(i).getUserAnswer());
        }
        return list;
    }

    public List<String> getRightAnswerList() {
        List<String> list = new ArrayList<>();
        for (int i = 0, length = records.size(); i < length; i++) {
            list.add(records.get(i).getRightAnswer());
        }
        return list;
    }

    public List<Integer> getAnswerResultList() {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0, length = records.size(); i < length; i++) {
            list.add(records.get(i).getAnswerResult());
        }
        return list;
    }

    public void finishTest(String etime) {
        isSubmitted = true;
        calculateResult();
    }

    private void calculateResult() {
        for (int i = 0, length = records.size(); i < length; i++) {
            if (records.get(i).getAnswerResult() == 1)
                rightAnswerNumber++;
        }
        rightRate = (int) (((double) rightAnswerNumber / records.size()) * 100);
    }

    public int getRightAnswerNumber() {
        return rightAnswerNumber;
    }

    public int getRightRate() {
        return rightRate;
    }

    public List<ExamRecord> getExamRecords() {
        return records;
    }

}
