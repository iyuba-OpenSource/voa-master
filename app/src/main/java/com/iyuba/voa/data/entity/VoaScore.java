package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/15
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class VoaScore {

    @SerializedName("sentence")
    private String sentence;
    @SerializedName("words")
    private List<Words> words;
    @SerializedName("scores")
    private String scores;
    @SerializedName("total_score")
    private String totalScore;
    @SerializedName("filepath")
    private String filepath;
    @SerializedName("URL")
    private String url;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<Words> getWords() {
        return words;
    }

    public void setWords(List<Words> words) {
        this.words = words;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Words {
        @SerializedName("index")
        private String index;
        @SerializedName("content")
        private String content;
        @SerializedName("pron")
        private String pron;
        @SerializedName("pron2")
        private String pron2;
        @SerializedName("user_pron")
        private String userPron;
        @SerializedName("user_pron2")
        private String userPron2;
        @SerializedName("score")
        private String score;
        @SerializedName("insert")
        private String insert;
        @SerializedName("delete")
        private String delete;
        @SerializedName("substitute_orgi")
        private String substituteOrgi;
        @SerializedName("substitute_user")
        private String substituteUser;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPron() {
            return pron;
        }

        public void setPron(String pron) {
            this.pron = pron;
        }

        public String getPron2() {
            return pron2;
        }

        public void setPron2(String pron2) {
            this.pron2 = pron2;
        }

        public String getUserPron() {
            return userPron;
        }

        public void setUserPron(String userPron) {
            this.userPron = userPron;
        }

        public String getUserPron2() {
            return userPron2;
        }

        public void setUserPron2(String userPron2) {
            this.userPron2 = userPron2;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getInsert() {
            return insert;
        }

        public void setInsert(String insert) {
            this.insert = insert;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public String getSubstituteOrgi() {
            return substituteOrgi;
        }

        public void setSubstituteOrgi(String substituteOrgi) {
            this.substituteOrgi = substituteOrgi;
        }

        public String getSubstituteUser() {
            return substituteUser;
        }

        public void setSubstituteUser(String substituteUser) {
            this.substituteUser = substituteUser;
        }
    }
}
