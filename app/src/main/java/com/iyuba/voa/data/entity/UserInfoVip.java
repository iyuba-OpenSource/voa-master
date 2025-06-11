package com.iyuba.voa.data.entity;

import com.google.gson.annotations.SerializedName;

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

public class UserInfoVip extends BaseResponse {

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
    @SerializedName("isteacher")
    private String isteacher;
    @SerializedName("credits")
    private String credits;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("email")
    private String email;
    @SerializedName("views")
    private String views;
    @SerializedName("amount")
    private int amount;
    @SerializedName("follower")
    private int follower;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("allThumbUp")
    private String allThumbUp;
    @SerializedName("icoins")
    private String icoins;
    @SerializedName("friends")
    private String friends;
    @SerializedName("doings")
    private String doings;
    @SerializedName("expireTime")
    private int expireTime;
    @SerializedName("money")
    private int money;
    @SerializedName("following")
    private int following;
    @SerializedName("sharings")
    private String sharings;
    @SerializedName("vipStatus")
    private String vipStatus;
    @SerializedName("username")
    private String username;

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

    public String getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(String isteacher) {
        this.isteacher = isteacher;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
