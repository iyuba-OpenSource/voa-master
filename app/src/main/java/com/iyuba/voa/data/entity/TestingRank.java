package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/18
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class TestingRank {
    private int index;
    private String averScore;
    private transient String bottom1;
    private transient String bottom2;
    private transient String right;
    @SerializedName(value = "uid", alternate = {"myid"})
    private String uid;
    @SerializedName(value = "wpm", alternate = {"mywpm"})
    private String wpm;
    @SerializedName(value = "scores", alternate = {"myscores"})
    private int scores;
    @SerializedName(value = "name", alternate = {"myname"})
    private String name;
    @SerializedName(value = "count", alternate = {"mycount"})
    private int count;
    @SerializedName(value = "ranking", alternate = {"myranking"})
    private int ranking;
    @SerializedName("sort")
    private int sort;
    @SerializedName("vip")
    private String vip;
    @SerializedName(value = "imgSrc", alternate = {"myimgSrc"})
    private String imgSrc;

    @SerializedName("totalTime")
    private long totalTime;
    private long words;

    @SerializedName("totalWord")
    private long totalWord;
    @SerializedName("totalEssay")
    private long totalEssay;
    @SerializedName("totalRight")
    private long totalRight;

    @SerializedName("totalTest")
    private long totalTest;
    @SerializedName("cnt")
    private long cnt;


    public TestingRank() {
    }

    public TestingRank(String averScore, int scores, String name, int count, int ranking, int sort) {
        this.averScore = averScore;
        this.scores = scores;
        this.name = name;
        this.count = count;
        this.ranking = ranking;
        this.sort = sort;
    }

    public String getAverScore() {
        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数
        String format = df.format(scores / (count == 0 ? 1 : count));
        return format;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getBottom1() {
        return bottom1;
    }

    public void setBottom1(String bottom1) {
        this.bottom1 = bottom1;
    }

    public String getBottom2() {
        return bottom2;
    }

    public void setBottom2(String bottom2) {
        this.bottom2 = bottom2;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAverScore(String averScore) {
        this.averScore = averScore;
    }

    public String getWpm() {
        return wpm;
    }

    public void setWpm(String wpm) {
        this.wpm = wpm;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public long getWords() {
        return words;
    }

    public void setWords(long words) {
        this.words = words;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(long totalWord) {
        this.totalWord = totalWord;
    }

    public long getTotalEssay() {
        return totalEssay;
    }

    public void setTotalEssay(long totalEssay) {
        this.totalEssay = totalEssay;
    }

    public long getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(long totalRight) {
        this.totalRight = totalRight;
    }


    public long getTotalTest() {
        return totalTest;
    }

    public void setTotalTest(long totalTest) {
        this.totalTest = totalTest;
    }


    public long getCnt() {
        return cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }

}
