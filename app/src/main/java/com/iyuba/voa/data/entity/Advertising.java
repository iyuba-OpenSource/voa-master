package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;
import com.iyuba.voa.utils.Constants;

public class Advertising {

    @SerializedName("id")
    private String id;
    @SerializedName("adId")
    private String adId;
    private String firstLevel;
    private String secondLevel;
    private String thirdLevel;
    @SerializedName("startuppic_StartDate")
    private String startuppicStartdate;
    @SerializedName("startuppic_EndDate")
    private String startuppicEnddate;
    @SerializedName("startuppic")
    private String startuppic;
    @SerializedName("type")
    private String type;
    @SerializedName("startuppic_Url")
    private String startuppicUrl;
    @SerializedName("classNum")
    private String classNum;
    @SerializedName("title")
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(String firstLevel) {
        this.firstLevel = firstLevel;
    }

    public String getSecondLevel() {
        return secondLevel;
    }

    public void setSecondLevel(String secondLevel) {
        this.secondLevel = secondLevel;
    }

    public String getThirdLevel() {
        return thirdLevel;
    }

    public void setThirdLevel(String thirdLevel) {
        this.thirdLevel = thirdLevel;
    }

    public String getStartuppicStartdate() {
        return startuppicStartdate;
    }

    public void setStartuppicStartdate(String startuppicStartdate) {
        this.startuppicStartdate = startuppicStartdate;
    }

    public String getStartuppicEnddate() {
        return startuppicEnddate;
    }

    public void setStartuppicEnddate(String startuppicEnddate) {
        this.startuppicEnddate = startuppicEnddate;
    }

    public String getStartuppic() {
        return Constants.CONFIG.IMAGE_AD_URL + startuppic;
//        return "http://dev.iyuba.cn/" + startuppic;
    }

    public void setStartuppic(String startuppic) {
        this.startuppic = startuppic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartuppicUrl() {
        return startuppicUrl;
    }

    public void setStartuppicUrl(String startuppicUrl) {
        this.startuppicUrl = startuppicUrl;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
