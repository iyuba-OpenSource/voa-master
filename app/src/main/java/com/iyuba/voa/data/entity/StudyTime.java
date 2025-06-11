package com.iyuba.voa.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.text.DecimalFormat;

import me.goldze.mvvmhabit.http.BaseResponse;

public class StudyTime extends BaseResponse implements Parcelable {
    private String totalTime; //今日总学习时长,单位秒
    private String totalDaysTime;//总学习天数时长, 单位秒
    private String shareId;

    private String totalWords;//总学习单词数,单位各

    private String totalDays;//熊学习天数,单位天
    private String sentence;//返回的英文的名言警句

    private String totalUser;//总用户数

    private String ranking;//今日当前排名
    private String carry;//超越用户比

    private String totalWord;//今日总单词数

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDaysTime() {
        return totalDaysTime;
    }

    public void setTotalDaysTime(String totalDaysTime) {
        this.totalDaysTime = totalDaysTime;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(String totalWords) {
        this.totalWords = totalWords;
    }

    public String getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(String totalDays) {
        this.totalDays = totalDays;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(String totalUser) {
        this.totalUser = totalUser;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(String totalWord) {
        this.totalWord = totalWord;
    }

    public String getCarry() {
        if (TextUtils.isEmpty(ranking)||TextUtils.isEmpty(totalUser)) {
            return String.format("%s%%同学", carry);
        }
        int nowRank = Integer.parseInt(ranking);
        double allPerson = Double.parseDouble(totalUser);
        double d = 0.0;
        if (allPerson != 0) {
            d = 1 - nowRank / allPerson;
            DecimalFormat df = new DecimalFormat("0.00");
            carry = df.format(d).substring(2, 4);
        }
        return String.format("%s%%同学", carry);
    }

    public void setCarry(String carry) {
        this.carry = carry;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.totalTime);
        dest.writeString(this.totalDaysTime);
        dest.writeString(this.shareId);
        dest.writeString(this.totalWords);
        dest.writeString(this.totalDays);
        dest.writeString(this.sentence);
        dest.writeString(this.totalUser);
        dest.writeString(this.ranking);
        dest.writeString(this.totalWord);
    }

    public void readFromParcel(Parcel source) {
        this.totalTime = source.readString();
        this.totalDaysTime = source.readString();
        this.shareId = source.readString();
        this.totalWords = source.readString();
        this.totalDays = source.readString();
        this.sentence = source.readString();
        this.totalUser = source.readString();
        this.ranking = source.readString();
        this.totalWord = source.readString();
    }

    public StudyTime() {
    }

    protected StudyTime(Parcel in) {
        this.totalTime = in.readString();
        this.totalDaysTime = in.readString();
        this.shareId = in.readString();
        this.totalWords = in.readString();
        this.totalDays = in.readString();
        this.sentence = in.readString();
        this.totalUser = in.readString();
        this.ranking = in.readString();
        this.totalWord = in.readString();
    }

    public static final Parcelable.Creator<StudyTime> CREATOR = new Parcelable.Creator<StudyTime>() {
        @Override
        public StudyTime createFromParcel(Parcel source) {
            return new StudyTime(source);
        }

        @Override
        public StudyTime[] newArray(int size) {
            return new StudyTime[size];
        }
    };
}
