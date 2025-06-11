package com.iyuba.iyubalib;

import android.content.Context;

import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.trainingcamp.ITraining;
import com.iyuba.trainingcamp.TrainLessonType;
import com.iyuba.trainingcamp.TrainingManager;
import com.iyuba.trainingcamp.data.local.InfoHelper;
import com.iyuba.trainingcamp.data.local.db.TCDBManager;
import com.iyuba.trainingcamp.data.sys.TypefaceProvider;

import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.PersonalType;


public class Lib {
    public static void initIHeadline(Context context, String appid, String appName) {
        //视频模块
        IHeadlineManager.appId = String.valueOf(appid);
        IHeadlineManager.appName = appName;
        IHeadline.init(context, appid, appName, true); //视频
        IHeadline.setDebug(BuildConfig.DEBUG);
        //视频模块
//        IHeadline.setEnableShare(true);
        IHeadline.setEnableGoStore(false);
        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);

//        IHeadline.setListPlayMode();
        BasicDLDBManager.init(context);
        BasicFavorDBManager.init(context);
        BasicFavor.init(context, appid);    //收藏
        HLDBManager.init(context);
//                BasicFavor.setDebug(true);
        DLManager.init(context, 8);  //下载
    }

    public static void initIMOOC(Context context, String appid, String provision, String privacy) {
        //微课模块
        IPrivacy.init(context, provision, privacy);
        DLManager.init(context, 8);
        IMooc.init(context, String.valueOf(appid), context.getResources().getString(R.string.app_name));
        IMooc.setDebug(BuildConfig.DEBUG);
        IMooc.setEnableShare(true);
    }

    public static void initPersonalHome(Context context, String appid) {
        PersonalHome.init(context, appid, context.getResources().getString(R.string.app_name));
        PersonalHome.setEnableEditNickname(false);
        PersonalHome.setEnableShare(true);
        PersonalHome.setCategoryType(PersonalType.VOA);
        PrivacyInfoHelper.getInstance().putApproved(true);
    }

    public static void initTrainCamp(Context context, String appid) {
        TCDBManager.init(context);
//        InfoHelper.init(context);
        InfoHelper.init(context);
        TypefaceProvider.init(context);
        TrainingManager.productId = "3";  //黄金会员才能进训练营
        TrainingManager.appId = appid;
        TrainingManager.appName = context.getResources().getString(R.string.app_name);
        TrainingManager.lessonType = TrainLessonType.VOA;
        ITraining.setExtraEvaluateUrl("http://iuserspeech.iyuba.cn:9001/test/ai/");
    }
}
