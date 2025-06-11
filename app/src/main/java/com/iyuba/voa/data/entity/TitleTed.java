package com.iyuba.voa.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
@Entity
public class TitleTed implements Parcelable {

    @SerializedName(value = "CreatTime", alternate = {"CreateTime"})
    private String creatTime;
    @SerializedName("Category")
    private String category;
    @SerializedName("Title")
    private String title;
    @SerializedName("Texts")
    private Integer texts;
    @SerializedName("Sound")
    private String sound;
    @SerializedName("Pic")
    private String pic;
    @SerializedName(value = "VoaId", alternate = {"voaid", "Voaid", "NewsId"})
    @PrimaryKey
    @NonNull
    private String voaId;
    private String uids;  //登录需向此字段添加（A1001A,A100002A）
    @SerializedName("Url")
    private String url;
    @Ignore
    @SerializedName("Pagetitle")
    private String pageTitle;
    @SerializedName("DescCn")
    private String descCn;
    @SerializedName(value = "Title_cn", alternate = {"Title_Cn"})
    private String title_cn;
    @SerializedName("PublishTime")
    private String publishTime;
    @SerializedName("HotFlg")
    private String hotFlg;   //1为初始态，未收藏  2已收藏 3.已下载, 4.已阅读完成 2,3收藏下载都包含
    @SerializedName("ReadCount")
    private String readCount;

    private long totalTime;
    private long playTime;
    private int exerciseNum;

    public String getCreatTime() {
        if (TextUtils.isEmpty(creatTime)) {
            return "";
        }
        String[] s = creatTime.split(" ");
        if (s.length > 1)
            try {
                return DateUtil.isToday(s[0]) ? "今天 " + s[1].split(":00\\.0")[0] : s[0];
            } catch (Exception e) {
                return creatTime;
            }
        return creatTime;
    }

    public void addUid(String uid) {
        if (TextUtils.isEmpty(getUids())) {
            uid = "0";
            String s = "A" + uid + "A";
            setUids(s);
            return;
        }
        String s = "A" + uid + "A";
        if (!getUids().contains(uid))
            setUids(getUids() + "," + s);
    }

    public void deleteUid(String uid) {
        setUids(getUids().replace("A" + uid + "A", ""));
    }

    public String getUids() {
        return uids;
    }

    public void setUids(String uids) {
        this.uids = uids;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getSound() {
        return Constants.CONFIG.SOUND_IP + sound;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescCn() {
        return descCn;
    }

    public void setDescCn(String descCn) {
        this.descCn = descCn;
    }

    public String getTitle_cn() {
        return title_cn;
    }

    public void setTitle_cn(String title_cn) {
        this.title_cn = title_cn;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getHotFlg() {
        return hotFlg;
    }

    public void setHotFlg(String hotFlg) {
        this.hotFlg = hotFlg;
    }

    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public int getExerciseNum() {
        return exerciseNum;
    }

    public void setExerciseNum(int exerciseNum) {
        this.exerciseNum = exerciseNum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.creatTime);
        dest.writeString(this.category);
        dest.writeString(this.title);
        dest.writeValue(this.texts);
        dest.writeString(this.sound);
        dest.writeString(this.pic);
        dest.writeString(this.voaId);
        dest.writeString(this.url);
        dest.writeString(this.pageTitle);
        dest.writeString(this.descCn);
        dest.writeString(this.title_cn);
        dest.writeString(this.publishTime);
        dest.writeString(this.hotFlg);
        dest.writeString(this.readCount);
        dest.writeLong(this.totalTime);
        dest.writeLong(this.playTime);
        dest.writeInt(this.exerciseNum);
    }

    public void readFromParcel(Parcel source) {
        this.creatTime = source.readString();
        this.category = source.readString();
        this.title = source.readString();
        this.texts = (Integer) source.readValue(Integer.class.getClassLoader());
        this.sound = source.readString();
        this.pic = source.readString();
        this.voaId = source.readString();
        this.url = source.readString();
        this.pageTitle = source.readString();
        this.descCn = source.readString();
        this.title_cn = source.readString();
        this.publishTime = source.readString();
        this.hotFlg = source.readString();
        this.readCount = source.readString();
        this.totalTime = source.readLong();
        this.playTime = source.readLong();
        this.exerciseNum = source.readInt();
    }

    public TitleTed() {
    }

    protected TitleTed(Parcel in) {
        this.creatTime = in.readString();
        this.category = in.readString();
        this.title = in.readString();
        this.texts = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sound = in.readString();
        this.pic = in.readString();
        this.voaId = in.readString();
        this.url = in.readString();
        this.pageTitle = in.readString();
        this.descCn = in.readString();
        this.title_cn = in.readString();
        this.publishTime = in.readString();
        this.hotFlg = in.readString();
        this.readCount = in.readString();
        this.totalTime = in.readLong();
        this.playTime = in.readLong();
        this.exerciseNum = in.readInt();
    }

    public static final Creator<TitleTed> CREATOR = new Creator<TitleTed>() {
        @Override
        public TitleTed createFromParcel(Parcel source) {
            return new TitleTed(source);
        }

        @Override
        public TitleTed[] newArray(int size) {
            return new TitleTed[size];
        }
    };
}
