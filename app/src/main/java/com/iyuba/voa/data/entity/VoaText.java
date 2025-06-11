package com.iyuba.voa.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.iyuba.voa.data.entity.converter.WordListConverter;

import java.util.List;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
@Entity(primaryKeys = {"voaId", "index"})
@TypeConverters(WordListConverter.class)
public class VoaText implements Parcelable {
    @NonNull
    private String voaId;
    private String uid;
    private int index;
    private String score;
    @Ignore
    private String wordScore;
    private String audioPath;   //评测之后，此路径会修改，算是是否评测过的标志
    private String audioUrl;
    private transient String audioWordUrl;
    @Ignore
    private boolean isRecording;  //是否正在录制
    @Ignore
    private boolean isPlayCurrent;
    @Ignore
    private boolean isPlayRec;  //是否播放录音
    @Ignore
    private boolean isShowCn = true;  //默认中文

    @SerializedName("ImgPath")
    private String imgPath;
    @SerializedName("EndTiming")
    private Double endTiming;
    @SerializedName("ParaId")
    private String paraId;
    @SerializedName("IdIndex")
    private String idIndex;
    private String sentence_cn;
    @SerializedName("ImgWords")
    private String imgWords;
    @SerializedName("Sentence")
    private String sentence;
    @SerializedName("Timing")
    private Double timing;
    private transient int wordNum;   //单词数量
    private transient int wordRecord;  //听到的单词总数量
    private transient String selectWord;  //点击选择的单词
    private transient int totalPara;  //点击选择的单词

    private List<VoaScore.Words> wordScores;


    public String getVoaId() {
        return voaId;
    }

    public void setVoaId(String voaId) {
        this.voaId = voaId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScore() {
        return score == null ? "0" : score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getWordScore() {
        return wordScore == null ? "0" : wordScore;
    }

    public void setWordScore(String wordScore) {
        this.wordScore = wordScore;
    }

    public int getWordNum() {
        if (sentence == null) {
            wordNum = 0;
            return wordNum;
        }
        String[] s = sentence.split(" ");
        wordNum = s.length;
        return wordNum;
    }

    public void setWordNum(int wordNum) {
        this.wordNum = wordNum;
    }

    public String getSelectWord() {
        return selectWord == null ? "" : selectWord;
    }

    public void setSelectWord(String selectWord) {
        this.selectWord = selectWord;
    }

    public int getWordRecord() {
        return wordRecord;
    }

    public void setWordRecord(int wordRecord) {
        this.wordRecord = wordRecord;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getAudioUrl() {
        return TextUtils.isEmpty(audioUrl) ? "" : audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getAudioWordUrl() {
        return TextUtils.isEmpty(audioWordUrl) ? "" : audioWordUrl;
    }

    public void setAudioWordUrl(String audioWordUrl) {
        this.audioWordUrl = audioWordUrl;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isPlayCurrent() {
        return isPlayCurrent;
    }

    public void setPlayCurrent(boolean playCurrent) {
        isPlayCurrent = playCurrent;
    }

    public boolean isPlayRec() {
        return isPlayRec;
    }

    public void setPlayRec(boolean playRec) {
        isPlayRec = playRec;
    }

    public boolean isShowCn() {
        return isShowCn;
    }

    public void setShowCn(boolean showCn) {
        isShowCn = showCn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public Double getEndTiming() {
        return endTiming;
    }

    public void setEndTiming(Double endTiming) {
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

    public String getSentence_cn() {
        return sentence_cn;
    }

    public void setSentence_cn(String sentence_cn) {
        this.sentence_cn = sentence_cn;
    }

    public String getImgWords() {
        return imgWords;
    }

    public void setImgWords(String imgWords) {
        this.imgWords = imgWords;
    }


    public Double getTiming() {
        return timing;
    }

    public void setTiming(Double timing) {
        this.timing = timing;
    }


    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getTotalPara() {
        return totalPara;
    }

    public void setTotalPara(int totalPara) {
        this.totalPara = totalPara;
    }

    public List<VoaScore.Words> getWordScores() {
        return wordScores;
    }

    public void setWordScores(List<VoaScore.Words> wordScores) {
        this.wordScores = wordScores;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeByte(this.isPlayCurrent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowCn ? (byte) 1 : (byte) 0);
        dest.writeString(this.imgPath);
        dest.writeValue(this.endTiming);
        dest.writeString(this.paraId);
        dest.writeString(this.idIndex);
        dest.writeString(this.sentence_cn);
        dest.writeString(this.imgWords);
        dest.writeValue(this.timing);
        dest.writeString(this.sentence);
    }

    public void readFromParcel(Parcel source) {
        this.index = source.readInt();
        this.isPlayCurrent = source.readByte() != 0;
        this.isShowCn = source.readByte() != 0;
        this.imgPath = source.readString();
        this.endTiming = (Double) source.readValue(Double.class.getClassLoader());
        this.paraId = source.readString();
        this.idIndex = source.readString();
        this.sentence_cn = source.readString();
        this.imgWords = source.readString();

        this.timing = (Double) source.readValue(Double.class.getClassLoader());
        this.sentence = source.readString();
    }

    public VoaText() {
    }

    protected VoaText(Parcel in) {
        this.index = in.readInt();
        this.isPlayCurrent = in.readByte() != 0;
        this.isShowCn = in.readByte() != 0;
        this.imgPath = in.readString();
        this.endTiming = (Double) in.readValue(Double.class.getClassLoader());
        this.paraId = in.readString();
        this.idIndex = in.readString();
        this.sentence_cn = in.readString();
        this.imgWords = in.readString();
        this.timing = (Double) in.readValue(Double.class.getClassLoader());
        this.sentence = in.readString();
    }

    public static final Parcelable.Creator<VoaText> CREATOR = new Parcelable.Creator<VoaText>() {
        @Override
        public VoaText createFromParcel(Parcel source) {
            return new VoaText(source);
        }

        @Override
        public VoaText[] newArray(int size) {
            return new VoaText[size];
        }
    };
}
