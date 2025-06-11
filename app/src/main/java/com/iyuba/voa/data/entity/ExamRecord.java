package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;

public class ExamRecord {

    @SerializedName("BeginTime")
    private String beginTime;
    @SerializedName("LessonId")
    private long lessonId; // voaid
    @SerializedName("TestNumber")
    private int testNumber; // 题号
    @SerializedName("UserAnswer")
    private String userAnswer = ""; // 用户答案
    @SerializedName("RightAnswer")
    private String rightAnswer; // 正确答案
    @SerializedName("AnswerResut")
    private int answerResult; // 正确与否：0：错误；1：正确
    @SerializedName("TestTime")
    private String testTime;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    public int getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(int testNumber) {
        this.testNumber = testNumber;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public int getAnswerResult() {
        return answerResult;
    }

    public void setAnswerResult(int answerResult) {
        this.answerResult = answerResult;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }
}
