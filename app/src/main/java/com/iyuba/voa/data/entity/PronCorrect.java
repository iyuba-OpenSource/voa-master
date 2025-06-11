package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.goldze.mvvmhabit.http.BaseResponse;

public class PronCorrect extends BaseResponse {


    @SerializedName("user_pron")
    private String userPron;
    @SerializedName("ori_pron")
    private String oriPron;
    @SerializedName("key")
    private String key;
    @SerializedName("audio")
    private String audio;
    @SerializedName("pron")
    private String pron;
    @SerializedName("proncode")
    private String proncode;
    @SerializedName("def")
    private String def;
    @SerializedName("sent")
    private List<Sent> sent;


    public String getUserPron() {
        return userPron;
    }

    public void setUserPron(String userPron) {
        this.userPron = userPron;
    }

    public String getOriPron() {
        return oriPron;
    }

    public void setOriPron(String oriPron) {
        this.oriPron = oriPron;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getProncode() {
        return proncode;
    }

    public void setProncode(String proncode) {
        this.proncode = proncode;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public List<Sent> getSent() {
        return sent;
    }

    public void setSent(List<Sent> sent) {
        this.sent = sent;
    }

    public static class Sent {
        @SerializedName("number")
        private Integer number;
        @SerializedName("orig")
        private String orig;
        @SerializedName("trans")
        private String trans;

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public String getOrig() {
            return orig;
        }

        public void setOrig(String orig) {
            this.orig = orig;
        }

        public String getTrans() {
            return trans;
        }

        public void setTrans(String trans) {
            this.trans = trans;
        }
    }
}
