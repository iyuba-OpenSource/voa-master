package com.iyuba.voa.data.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.iyuba.voa.utils.DateUtil;

import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/2
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
@Entity(ignoredColumns = {"data"})
public class UserInfo extends BaseResponse {


    @PrimaryKey
    @SerializedName("uid")
    @NonNull
    private String uid;

    @SerializedName(value = "amount", alternate = {"Amount"})
    private String amount;
    @SerializedName("mobile")
    private String mobile;

    @SerializedName("isteacher")
    private String isteacher;
    @SerializedName("expireTime")
    private long expireTime;
    @SerializedName("money")
    private String money;
    @SerializedName("credits")
    private Integer credits;
    @SerializedName("jiFen")
    private Integer jiFen;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("vipStatus")
    private String vipStatus;
    @SerializedName("imgSrc")
    private String imgSrc;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("albums")
    private String albums;
    @SerializedName("gender")
    private String gender;
    @SerializedName("distance")
    private String distance;
    @SerializedName("blogs")
    private String blogs;
    @SerializedName("middle_url")
    private String middleUrl;
    @SerializedName("contribute")
    private String contribute;
    @SerializedName("shengwang")
    private String shengwang;
    @SerializedName("bio")
    private String bio;
    @SerializedName("posts")
    private String posts;
    @SerializedName("relation")
    private int relation;
    @SerializedName("views")
    private String views;

    @SerializedName("follower")
    private int follower;
    @SerializedName("allThumbUp")
    private String allThumbUp;
    @SerializedName("icoins")
    private String icoins;
    @SerializedName("friends")
    private String friends;
    @SerializedName("doings")
    private String doings;
    @SerializedName("following")
    private int following;
    @SerializedName("sharings")
    private String sharings;
    private boolean isVIP;
    private String expireTimeDate;


    public boolean isVIP() {
        isVIP = false;
        if (Integer.parseInt(TextUtils.isEmpty(vipStatus) ? "0" : vipStatus) > 0 && DateUtil.getNowTimeS() < Long.valueOf(expireTime)) {
            isVIP = true;
            return true;
        }
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public void setExpireTimeDate(String expireTimeDate) {
        this.expireTimeDate = expireTimeDate;
    }

    public String getExpireTimeDate() {
        expireTimeDate = DateUtil.secondsToStrDate(expireTime, DateUtil.YEAR_MONTH_DAY);
        return expireTimeDate;
    }


    public String getAmount() {
        return TextUtils.isEmpty(amount) ? "0" : amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(String isteacher) {
        this.isteacher = isteacher;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getJiFen() {
        return jiFen;
    }

    public void setJiFen(Integer jiFen) {
        this.jiFen = jiFen;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBlogs() {
        return blogs;
    }

    public void setBlogs(String blogs) {
        this.blogs = blogs;
    }

    public String getMiddleUrl() {
        return middleUrl;
    }

    public void setMiddleUrl(String middleUrl) {
        this.middleUrl = middleUrl;
    }

    public String getContribute() {
        return contribute;
    }

    public void setContribute(String contribute) {
        this.contribute = contribute;
    }

    public String getShengwang() {
        return shengwang;
    }

    public void setShengwang(String shengwang) {
        this.shengwang = shengwang;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }


    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public String getAllThumbUp() {
        return allThumbUp;
    }

    public void setAllThumbUp(String allThumbUp) {
        this.allThumbUp = allThumbUp;
    }

    public String getIcoins() {
        return icoins;
    }

    public void setIcoins(String icoins) {
        this.icoins = icoins;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getDoings() {
        return doings;
    }

    public void setDoings(String doings) {
        this.doings = doings;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public String getSharings() {
        return sharings;
    }

    public void setSharings(String sharings) {
        this.sharings = sharings;
    }
}
