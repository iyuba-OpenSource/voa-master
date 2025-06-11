package com.iyuba.voa.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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
@Root(name = "data", strict = false)
public class XmlWord {

    private boolean isCollect;

    @SerializedName("createDate")
    private String createDate;

    @PrimaryKey
    @NonNull
    @SerializedName("Word")
    @Element(name = "key", required = false)
    private String key;
    @Element(name = "result", required = false)
    private String result;
    @Element(name = "audio", required = false)
    private String audio;
    @Element(name = "pron", required = false)
    private String pron;
    @Element(name = "proncode", required = false)
    private String proncode;
    @Element(name = "def", required = false)
    private String def;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
}
