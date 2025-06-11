package com.iyuba.voa.data.entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.EnDecodeUtils;
import com.iyuba.voa.utils.DateUtil;

import java.util.List;

public class SearchContent {

    @SerializedName("WordId")
    private String wordId;
    @SerializedName("Word")
    private String word;
    @SerializedName("def")
    private String def;
    @SerializedName("ph_am")
    private String phAm;
    @SerializedName("ph_am_mp3")
    private String phAmMp3;
    @SerializedName("titleData")
    private List<TitleDataDTO> titleData;
    @SerializedName("ph_en")
    private String phEn;
    @SerializedName("titleToal")
    private Integer titleToal;
    @SerializedName("ph_en_mp3")
    private String phEnMp3;
    @SerializedName("textData")
    private List<TextDataDTO> textData;
    @SerializedName("English")
    private Boolean english;
    @SerializedName("WordCn")
    private String wordCn;
    @SerializedName("textToal")
    private Integer textToal;
    private boolean isCollect;

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getPhAm() {
        return phAm;
    }

    public void setPhAm(String phAm) {
        this.phAm = phAm;
    }

    public String getPhAmMp3() {
        return phAmMp3;
    }

    public void setPhAmMp3(String phAmMp3) {
        this.phAmMp3 = phAmMp3;
    }

    public List<TitleDataDTO> getTitleData() {
        return titleData;
    }

    public void setTitleData(List<TitleDataDTO> titleData) {
        this.titleData = titleData;
    }

    public String getPhEn() {
        return EnDecodeUtils.decode(phEn);
    }

    public void setPhEn(String phEn) {
        this.phEn = phEn;
    }

    public Integer getTitleToal() {
        return titleToal;
    }

    public void setTitleToal(Integer titleToal) {
        this.titleToal = titleToal;
    }

    public String getPhEnMp3() {
        return phEnMp3;
    }

    public void setPhEnMp3(String phEnMp3) {
        this.phEnMp3 = phEnMp3;
    }

    public List<TextDataDTO> getTextData() {
        return textData;
    }

    public void setTextData(List<TextDataDTO> textData) {
        this.textData = textData;
    }

    public Boolean getEnglish() {
        return english;
    }

    public void setEnglish(Boolean english) {
        this.english = english;
    }

    public String getWordCn() {
        return wordCn;
    }

    public void setWordCn(String wordCn) {
        this.wordCn = wordCn;
    }

    public Integer getTextToal() {
        return textToal;
    }

    public void setTextToal(Integer textToal) {
        this.textToal = textToal;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public static class TitleDataDTO {
        @SerializedName("Category")
        private String category;
        @SerializedName("Title_Cn")
        private String titleCn;
        @SerializedName("CreateTime")
        private String createTime;
        @SerializedName("Title")
        private String title;
        @SerializedName("Texts")
        private Integer texts;
        @SerializedName("Sound")
        private String sound;
        @SerializedName("Pic")
        private String pic;
        @SerializedName("VoaId")
        private String voaId;
        @SerializedName("ReadCount")
        private String readCount;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTitleCn() {
            return titleCn;
        }

        public void setTitleCn(String titleCn) {
            this.titleCn = titleCn;
        }

        public String getCreateTime() {
            if (TextUtils.isEmpty(createTime)) {
                return "";
            }
            String[] s = createTime.split(" ");
            if (s.length > 1)
                try {
                    return DateUtil.isToday(s[0]) ? "今天 " + s[1].split(":00\\.0")[0] : s[0];
                } catch (Exception e) {
                    return createTime;
                }
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getTexts() {
            return texts;
        }

        public void setTexts(Integer texts) {
            this.texts = texts;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getVoaId() {
            return voaId;
        }

        public void setVoaId(String voaId) {
            this.voaId = voaId;
        }

        public String getReadCount() {
            return readCount;
        }

        public void setReadCount(String readCount) {
            this.readCount = readCount;
        }
    }

    public static class TextDataDTO {
        @SerializedName("EndTiming")
        private String endTiming;
        @SerializedName("ParaId")
        private String paraId;
        @SerializedName("IdIndex")
        private String idIndex;
        @SerializedName("SoundText")
        private String soundText;
        @SerializedName("Sentence_cn")
        private String sentenceCn;
        @SerializedName("Timing")
        private String timing;
        @SerializedName("VoaId")
        private String voaId;
        @SerializedName("Sentence")
        private String sentence;

        public String getEndTiming() {
            return endTiming;
        }

        public void setEndTiming(String endTiming) {
            this.endTiming = endTiming;
        }

        public String getParaId() {
            return paraId;
        }

        public void setParaId(String paraId) {
            this.paraId = paraId;
        }

        public String getIdIndex() {
            return idIndex;
        }

        public void setIdIndex(String idIndex) {
            this.idIndex = idIndex;
        }

        public String getSoundText() {
            return soundText;
        }

        public void setSoundText(String soundText) {
            this.soundText = soundText;
        }

        public String getSentenceCn() {
            return sentenceCn;
        }

        public void setSentenceCn(String sentenceCn) {
            this.sentenceCn = sentenceCn;
        }

        public String getTiming() {
            return timing;
        }

        public void setTiming(String timing) {
            this.timing = timing;
        }

        public String getVoaId() {
            return voaId;
        }

        public void setVoaId(String voaId) {
            this.voaId = voaId;
        }

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }
    }


}
