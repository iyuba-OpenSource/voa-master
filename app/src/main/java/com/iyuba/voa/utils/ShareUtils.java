package com.iyuba.voa.utils;

import static com.iyuba.share.SharePlatform.SinaWeibo;

import android.content.Context;
import android.util.Log;

import com.iyuba.voa.R;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareUtils {

    public static void showShare(Context mContext, String imageUrl, String siteUrl, String title, String content, PlatformActionListener platformActionListener) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(siteUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        oks.setImageUrl(imageUrl);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(siteUrl);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mContext.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(siteUrl);

        oks.setCallback(platformActionListener);
        // 启动分享GUI
        oks.show(mContext);
    }

    public static void showQrShare(Context mContext, String imagePath,PlatformActionListener platformActionListener) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.addHiddenPlatform(QQ.NAME);
        oks.addHiddenPlatform(QZone.NAME);
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setPlatform(WechatMoments.NAME);
        oks.setImagePath(imagePath);
        // url仅在微信（包括好友和朋友圈）中使用
        ShareSDK.getPlatform(Wechat.NAME);
        oks.setSilent(true);
        oks.setCallback(platformActionListener);
        // 启动分享GUI
        oks.show(mContext);
    }


}
