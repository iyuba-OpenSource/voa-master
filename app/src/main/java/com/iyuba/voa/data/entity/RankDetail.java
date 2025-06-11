package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;
import com.iyuba.voa.utils.Constants;

import personal.iyuba.personalhomelibrary.data.model.Shareable;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/19
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankDetail {
    private boolean isPlaying;
    private String headUrl;
    @SerializedName("paraid")
    private String paraid;
    @SerializedName("score")
    private String score;
    @SerializedName("shuoshuotype")
    private int shuoshuotype;
    @SerializedName("againstCount")
    private String againstCount;
    @SerializedName("agreeCount")
    private String agreeCount;
    @SerializedName("TopicId")
    private String TopicId;
    @SerializedName("ShuoShuo")
    private String ShuoShuo;
    @SerializedName("id")
    private String id;
    @SerializedName("idIndex")
    private String idIndex;
    @SerializedName("CreateDate")
    private String CreateDate;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getParaid() {
        return paraid;
    }

    public void setParaid(String paraid) {
        this.paraid = paraid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getShuoshuotype() {
        return shuoshuotype;
    }

    public void setShuoshuotype(int shuoshuotype) {
        this.shuoshuotype = shuoshuotype;
    }

    public String getAgainstCount() {
        return againstCount;
    }

    public void setAgainstCount(String againstCount) {
        this.againstCount = againstCount;
    }

    public String getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(String agreeCount) {
        this.agreeCount = agreeCount;
    }

    public String getTopicId() {
        return TopicId;
    }

    public void setTopicId(String TopicId) {
        this.TopicId = TopicId;
    }

    public String getShuoShuo() {
        return ShuoShuo;
    }

    public void setShuoShuo(String ShuoShuo) {
        this.ShuoShuo = ShuoShuo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(String idIndex) {
        this.idIndex = idIndex;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

}
