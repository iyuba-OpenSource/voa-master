package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;

public class VoaExam {

    private long voaid;

    @SerializedName("IndexId")
    private int indexId;
    @SerializedName("TestType")
    private int testType;
    @SerializedName("Question")
    private String question;
    @SerializedName("Answer1")
    private String answer1;
    @SerializedName("Answer2")
    private String answer2;
    @SerializedName("Answer3")
    private String answer3;
    @SerializedName("Answer4")
    private String answer4;
    @SerializedName("Answer")
    private String answer;
    private String def;
    private String words;

    private transient String topicNum;
    private transient String rightRate;
    private transient String speedTime;


    public long getVoaid() {
        return voaid;
    }

    public void setVoaid(long voaid) {
        this.voaid = voaid;
    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public int getTestType() {
        return testType;
    }

    public void setTestType(int testType) {
        this.testType = testType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(String topicNum) {
        this.topicNum = topicNum;
    }

    public String getRightRate() {
        return rightRate;
    }

    public void setRightRate(String rightRate) {
        this.rightRate = rightRate;
    }

    public String getSpeedTime() {
        return speedTime;
    }

    public void setSpeedTime(String speedTime) {
        this.speedTime = speedTime;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }
}
