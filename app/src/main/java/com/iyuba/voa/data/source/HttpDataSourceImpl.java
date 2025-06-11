package com.iyuba.voa.data.source;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iyuba.module.toolbox.EnDecodeUtils;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.app.Injection;
import com.iyuba.voa.data.entity.ExamRecord;
import com.iyuba.voa.data.entity.PronCorrect;
import com.iyuba.voa.data.entity.RecentRead;
import com.iyuba.voa.data.entity.StudyTime;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.entity.VoaScore;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlResponse;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.http.HttpService;
import com.iyuba.voa.utils.CipherUtils;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.voa.utils.DeviceUtils;
import com.mob.secverify.datatype.VerifyResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class HttpDataSourceImpl implements HttpDataSource {
    private final int appId = Constants.CONFIG.appId;

    private HttpService mHttpService;
    private volatile static HttpDataSourceImpl INSTANCE = null;
    private final String TOPIC = "voa";

    public static HttpDataSourceImpl getInstance(HttpService httpService) {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl(httpService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private HttpDataSourceImpl(HttpService httpService) {
        this.mHttpService = httpService;
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> titleApi(int maxid, int pages, int pageNum, int parentID) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("type", "android");
        map.put("format", "json");
        map.put("appId", String.valueOf(appId));
        map.put("maxid", String.valueOf(maxid));
        map.put("pages", String.valueOf(pages));
        map.put("pageNum", String.valueOf(pageNum));
        map.put("parentID", String.valueOf(parentID));
        return mHttpService.titleTed(map);
    }

    @Override
    public Observable<JsonObject> textExamApi(String voaid) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("format", "json");
        map.put("appId", String.valueOf(appId));
        map.put("voaid", String.valueOf(voaid));
        return mHttpService.textExamApi(map);
    }

    @Override
    public Observable<BaseResponse> feedback(int protocol, String uid, String content, String email) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("protocol", String.valueOf(protocol));
        map.put("format", "json");
        map.put("appid", String.valueOf(appId));
        map.put("uid", TextUtils.isEmpty(uid) ? "0" : uid);   //未登录uid传0
        try {
            map.put("content", URLEncoder.encode(content, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("app", TOPIC);
        map.put("email", email);
        map.put("platform", "android");
        return mHttpService.feedback(map);
    }

    @Override
    public Observable<UserInfo> login(String userName, String password, String protocol) {
        protocol = "11001";
        Map<String, String> map = Injection.provideMapInstance();
        String encode = userName;
        try {
            encode = URLEncoder.encode(userName, "UTF-8");
            map.put("username", encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String pwd = CipherUtils.md5(password);
        map.put("password", pwd);
        map.put("protocol", protocol);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append(userName);
        sb.append(pwd);
        sb.append("iyubaV2");
        map.put("sign", CipherUtils.md5(sb.toString()));
        map.put("format", "json");
        map.put("appid", String.valueOf(appId));
        map.put("token", "");
        map.put("app", TOPIC);
        map.put("platform", "android");
        return mHttpService.loginRegister(map);
    }

    @Override
    public Observable<UserInfo> register(String mobile, String userName, String password) {
        Map<String, String> map = Injection.provideMapInstance();
        String protocol = "11002";
        String encode = userName;
        try {
            encode = URLEncoder.encode(userName, "UTF-8");
            map.put("username", encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String pwd = CipherUtils.md5(password);
        map.put("password", pwd);
        map.put("protocol", protocol);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append(userName);
        sb.append(pwd);
        sb.append("iyubaV2");
        map.put("mobile", mobile);
        map.put("sign", CipherUtils.md5(sb.toString()));
        map.put("format", "json");
        map.put("appid", String.valueOf(appId));
        map.put("token", "");
        map.put("app", TOPIC);
        map.put("platform", "android");
        return mHttpService.loginRegister(map);
    }

    @Override
    public Observable<JsonObject> secVerify(VerifyResult data) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("protocol", "10010");
        try {
            map.put("token", URLEncoder.encode(data.getToken(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            map.put("token", data.getToken());
        }
        map.put("opToken", data.getOpToken());
        map.put("operator", data.getOperator());
        map.put("appId", String.valueOf(appId));
        map.put("appkey", BuildConfig.MOB_KEY);
        return mHttpService.loginRegisterByMob(map);
    }

    @Override
    public Observable<UserInfo> logout(String userName, String password) {
        Map<String, String> map = Injection.provideMapInstance();
        String protocol = "11005";
        String encode = userName;
        try {
            encode = URLEncoder.encode(userName, "UTF-8");
            map.put("username", encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String pwd = CipherUtils.md5(password);
        map.put("password", pwd);
        map.put("protocol", protocol);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append(userName);
        sb.append(pwd);
        sb.append("iyubaV2");
        map.put("sign", CipherUtils.md5(sb.toString()));
        map.put("format", "json");
        map.put("appid", String.valueOf(appId));
        map.put("app", TOPIC);
        map.put("platform", "android");
        return mHttpService.loginRegister(map);
    }

    @Override
    public Observable<BaseResponse> sendMessage3(String userphone) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("userphone", userphone);
        map.put("format", "json");
        return mHttpService.sendMessage3(map);
    }

    @Override
    public Observable<XmlResponse> updateCollect(String userId, String voaId, String type) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("userId", userId);
        map.put("appId", String.valueOf(appId));
        map.put("sentenceFlg", "0");
        map.put("format", "json");
        map.put("topic", TOPIC);
        map.put("sentenceId", "0");
        map.put("groupName", "Iyuba");
        map.put("voaId", voaId);
        map.put("type", type);
        return mHttpService.updateCollect(map);
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> getCollect(String uid, int pages, int pageCount) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("topic", TOPIC);
        map.put("app", TOPIC);
        map.put("userId", uid);
        map.put("appId", String.valueOf(appId));
        StringBuilder sb = new StringBuilder();
        sb.append("iyuba");
        sb.append(uid);
        sb.append(TOPIC);
        sb.append(appId);
        sb.append(DateUtil.getNowDay());
        map.put("sign", CipherUtils.md5(sb.toString()));
        map.put("pageNumber", String.valueOf(pages));
        map.put("pageCounts", String.valueOf(pageCount));
        return mHttpService.getCollect(map);
    }

    @Override
    public Observable<JsonObject> exportPDF(String voaId, int languageType) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("type", TOPIC);
        map.put("format", "json");
        map.put("isenglish", String.valueOf(languageType));
        map.put("voaid", voaId);
        return mHttpService.exportPDF(map);
    }

    @Override
    public Observable<XmlWord> apiWord(String word) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("q", word);
        return mHttpService.apiWord(map);
    }

    @Override
    public Observable<JsonObject> updateWord(String userId, String word, String type) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("userId", userId);
        map.put("platform", "android");
        map.put("appId", String.valueOf(appId));
        map.put("format", "json");
        map.put("groupName", "Iyuba");
        map.put("mod", type);
        map.put("word", word);
        return mHttpService.updateWord(map);

    }

    @Override
    public Observable<BaseResponse<List<XmlWord>>> wordListService(String userId, int page, int pageNum) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("platform", "android");
        map.put("appId", String.valueOf(appId));
        map.put("u", String.valueOf(userId));
        map.put("format", "json");
        map.put("pageNumber", String.valueOf(page));
        map.put("pageCounts", String.valueOf(pageNum));
        return mHttpService.wordListService(map);
    }

    @Override
    public Observable<BaseResponse<VoaScore>> uploadTesting(File file, String userId, String sentence, String newsId, String paraId, String IdIndex) {
        MediaType type = MediaType.parse("application/octet-stream");   //"text/xml;charset=utf-8"
//        File file = new File(recFilePath);
        RequestBody fileBody = RequestBody.create(file, type);
/*
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", topic)
                .addFormDataPart("userId", userId)
                .addFormDataPart("newsId", newsId)
                .addFormDataPart("paraId", paraId)
                .addFormDataPart("IdIndex", IdIndex)
                .addFormDataPart("sentence", sentence.replaceAll("\\+", "%20"))
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("appId", String.valueOf(appId))
                .build();*/
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", TOPIC)
                .addFormDataPart("userId", userId)
                .addFormDataPart("newsId", newsId)
                .addFormDataPart("paraId", paraId)
                .addFormDataPart("IdIndex", IdIndex)
                .addFormDataPart("sentence", sentence.replaceAll("\\+", "%20"))
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("wordId", "0")
                .addFormDataPart("flg", "0")
                .addFormDataPart("appId", String.valueOf(appId))
                .build();
        return mHttpService.uploadCorrectTesting(requestBody);
    }

    @Override
    public Observable<JsonObject> audioMerge(String audios) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("type", TOPIC);
        map.put("audios", String.valueOf(audios));
        return mHttpService.audioMerge(map);
    }

    @Override
    public Observable<JsonObject> releaseTesting(String voaid, String uid, String mergeUrl, String totalScore) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("platform", "android");
        map.put("topic", TOPIC);
        map.put("format", "json");
        map.put("userid", uid);
//        map.put("username", username);
        map.put("voaid", voaid);
        map.put("shuoshuotype", "4");
//        map.put("paraId", voaText.getParaId());
//        map.put("idIndex", voaText.getIdIndex());
        map.put("content", mergeUrl);
        map.put("score", totalScore);
        map.put("protocol", "60002");
        return mHttpService.releaseTesting(map);
    }

    @Override
    public Observable<JsonObject> releaseSingleTesting(String voaid, String username, String uid, VoaText voaText) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("platform", "android");
        map.put("topic", TOPIC);
        map.put("format", "json");
        map.put("userid", uid);
        map.put("voaid", voaid);
        map.put("shuoshuotype", "2");
        map.put("username", username);
        map.put("paraid", voaText.getParaId());
        map.put("idIndex", voaText.getIdIndex());
        map.put("content", voaText.getAudioUrl());
        map.put("score", voaText.getScore());
        map.put("protocol", "60002");
        return mHttpService.releaseTesting(map);
    }

    @Override
    public Observable<JsonObject> getTesting(String voaid, String uid) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("topic", TOPIC);
        map.put("topicId", voaid);
        map.put("shuoshuoType", "2,4");
        map.put("uid", uid);
        StringBuilder sb = new StringBuilder();
        sb.append(uid);
        sb.append("getWorksByUserId");
        sb.append(DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", CipherUtils.md5(sb.toString()));
        return mHttpService.getTesting(map);
    }

    @Override
    public Observable<JsonObject> getTopicRanking(String voaid, String uid, int start,
                                                  int total) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("type", "D");
        map.put("topic", TOPIC);
        map.put("uid", uid);
        map.put("topicid", voaid);
        map.put("start", String.valueOf(start));
        map.put("total", String.valueOf(total));
        StringBuilder sb = new StringBuilder();
        sb.append(uid);
        sb.append(TOPIC);
        sb.append(voaid);
        sb.append(start);
        sb.append(total);
        sb.append(DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", CipherUtils.md5(sb.toString()));
        return mHttpService.getTopicRanking(map);
    }


    @Override
    public Observable<UserInfo> getVipInfo(String id, String myid) {
        Map<String, String> map = Injection.provideMapInstance();
        int protocol = 20001;
        map.put("platform", "android");
        map.put("format", "json");
        map.put("protocol", String.valueOf(protocol));
        map.put("id", id);
        map.put("myid", myid);
        map.put("appid", String.valueOf(appId));

        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append(id);
        sb.append("iyubaV2");
        map.put("sign", CipherUtils.md5(sb.toString()));
        return mHttpService.getUserInfo(map);
    }

    @Override
    public Observable<JsonObject> alipay(String uid, String price, int month,
                                         int productId, String cate) {
        Map<String, String> map = Injection.provideMapInstance();
        int protocol = 20001;
        map.put("app_id", String.valueOf(appId));
        map.put("userId", uid);

        StringBuilder sb = new StringBuilder();
        sb.append(uid);
        sb.append("iyuba");
        sb.append(DateUtil.dateToString(null, DateUtil.YEAR_MONTH_DAY));
        map.put("code", CipherUtils.md5(sb.toString()));

        String WID_body = null;
        String WIDsubject = null;
        try {
            WID_body = URLEncoder.encode("花费" + price + "元购买" + cate, "utf-8");
            WIDsubject = URLEncoder.encode(String.valueOf(cate), "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("WIDtotal_fee", price);
        map.put("amount", String.valueOf(month));
        map.put("product_id", String.valueOf(productId));
        map.put("WIDbody", WID_body);
        map.put("WIDsubject", WIDsubject);
        return mHttpService.alipay(map);
    }

    @Override
    public Observable<JsonObject> weixinPay(String uid, String price, int month, int productId, String cate) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("format", "json");
        map.put("wxkey", "wx4c5842bc8b47f3e3");
        map.put("weixinApp", "wx4c5842bc8b47f3e3");
        map.put("appid", String.valueOf(BuildConfig.APPID_VALUE));
        map.put("uid", uid);
        map.put("money", price);
        map.put("amount", String.valueOf(month));
        map.put("productid", String.valueOf(productId));

        StringBuilder sb = new StringBuilder();
        sb.append(BuildConfig.APPID_VALUE);
        sb.append(uid);
        sb.append(price);
        sb.append(month);
        sb.append(DateUtil.dateToString(null, "yyyyMMdd"));
        map.put("sign", CipherUtils.md5(sb.toString()));
//        map.put("body", EnDecodeUtils.encode("花费" + price + "元购买" + cate));
        return mHttpService.weixinPay(map);
    }

    @Override
    public Observable<JsonObject> notifyAliNew(String data) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("data", data);
        return mHttpService.notifyAliNew(map);
    }

    @Override
    public Observable<JsonObject> updateDomain(String short1, String short2) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("appId", String.valueOf(appId));
        map.put("short1", short1);
        map.put("short2", short2);
        return mHttpService.updateDomain(map);
    }

    @Override
    public Observable<JsonObject> getWordToPDF(String uid, int page, int pageNum) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("u", uid);
        map.put("pageNumber", String.valueOf(page));
        map.put("pageCounts", String.valueOf(pageNum));
        return mHttpService.getWordToPDF(map);
    }

    @Override
    public Observable<JsonObject> updateStudyRecordNew(String beginTime, String endTime, String lessonId, String TestNumber, String TestWords, String uid, int endFlg) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("Lesson", TOPIC);
        map.put("appId", String.valueOf(appId));
        map.put("BeginTime", beginTime);
        map.put("EndTime", endTime);
        map.put("EndFlg", String.valueOf(endFlg));
        map.put("LessonId", String.valueOf(lessonId));
        map.put("TestNumber", String.valueOf(TestNumber));
        map.put("TestWords", String.valueOf(TestWords));
        map.put("uid", String.valueOf(uid));
        map.put("TestMode", String.valueOf(TestNumber));
        map.put("UserAnswer", String.valueOf(TestNumber));
        map.put("Score", String.valueOf(0));
        map.put("DeviceId", DeviceUtils.getDeviceId(Utils.getContext()));
        String sign = CipherUtils.md5(uid + beginTime + DateUtil.dateToString(null, DateUtil.YEAR_MONTH_DAY));
        map.put("sign", sign);
        return mHttpService.updateStudyRecordNew(map);
    }

    @Override
    public Observable<PronCorrect> correctPron(String word, String userPron, String oriPron) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("q", word);
        try {
            map.put("user_pron", URLEncoder.encode(userPron, "utf-8"));
            map.put("ori_pron", URLEncoder.encode(oriPron, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mHttpService.apiWordAi(map);
    }

    @Override
    public Observable<BaseResponse<VoaScore>> wordTesting(String userId, File file, VoaText voaText) {
        MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
//        File file = new File(recFilePath);
        RequestBody fileBody = RequestBody.create(file, type);
        String wordId = "0";
        for (VoaScore.Words wordScore : voaText.getWordScores()) {
            if (voaText.getSelectWord().equals(wordScore.getContent())) {
                wordId = String.valueOf(wordScore.getIndex());
                break;
            }
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", TOPIC)
                .addFormDataPart("userId", userId)
                .addFormDataPart("newsId", voaText.getVoaId())
                .addFormDataPart("paraId", voaText.getParaId())
                .addFormDataPart("IdIndex", voaText.getIdIndex())
                .addFormDataPart("sentence", voaText.getSelectWord().replaceAll("\\+", "%20"))
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("wordId", wordId)
                .addFormDataPart("flg", "2")
                .addFormDataPart("appId", String.valueOf(appId))
                .build();
        return mHttpService.uploadCorrectTesting(requestBody);
    }

    @Override
    public Observable<JsonArray> getAdEntryAll(String flag, String uid) {
        Map<String, String> map = Injection.provideMapInstance();
        // TODO: 2022/8/12
        map.put("appId", String.valueOf(appId));
        map.put("flag", flag);
        map.put("uid", uid);
        return mHttpService.getAdEntryAll(map);
    }

    @Override
    public Observable<StudyTime> getMyTime(String uid, String flag, String day) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("day", day);
        map.put("flg", flag);
        map.put("uid", uid);
        return mHttpService.getMyTime(map);
    }


    @Override
    public Observable<JsonObject> getReadRanking(String uid, String type, int start, int total) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("uid", uid);
        map.put("start", String.valueOf(start));
        map.put("total", String.valueOf(total));
        map.put("type", type);
        String sign = CipherUtils.md5(uid + type + start + total + DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", sign);
        return mHttpService.getNewsRanking(map);
    }


    @Override
    public Observable<JsonObject> getListenRanking(String uid, String type, int start, int total) {
        Map<String, String> map = Injection.provideMapInstance();

        map.put("uid", uid);
        map.put("start", String.valueOf(start));
        map.put("total", String.valueOf(total));
        map.put("type", type);
        String sign = CipherUtils.md5(uid + type + start + total + DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", sign);
        map.put("mode", "listening");
        return mHttpService.getStudyRanking(map);
    }

    @Override
    public Observable<JsonObject> getTalkRanking(String uid, String type, int start, int total) {
        Map<String, String> map = Injection.provideMapInstance();

        map.put("type", type);
        map.put("topic", TOPIC);
        map.put("uid", uid);
        map.put("topicid", "0");
        map.put("shuoshuotype", "4");
        map.put("start", String.valueOf(start));
        map.put("total", String.valueOf(total));
        StringBuilder sb = new StringBuilder();
        sb.append(uid);
        sb.append(TOPIC);
        sb.append("0");
        sb.append(start);
        sb.append(total);
        sb.append(DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", CipherUtils.md5(sb.toString()));
        return mHttpService.getTopicRanking(map);
    }

    @Override
    public Observable<JsonObject> getStudyRanking(String uid, String type, int start, int total) {
        Map<String, String> map = Injection.provideMapInstance();

        map.put("uid", uid);
        map.put("start", String.valueOf(start));
        map.put("total", String.valueOf(total));
        map.put("type", type);
        String sign = CipherUtils.md5(uid + type + start + total + DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", sign);
        map.put("mode", "all");
        return mHttpService.getStudyRanking(map);
    }

    @Override
    public Observable<JsonObject> getTestRanking(String uid, String type, int start, int total) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("uid", uid);
        map.put("start", String.valueOf(start));
        map.put("total", String.valueOf(total));
        map.put("type", type);
        String sign = CipherUtils.md5(uid + type + start + total + DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("sign", sign);
        return mHttpService.getTestRanking(map);
    }

    @Override
    public Observable<JsonObject> getShareInfoShow(String uid, String time) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("uid", uid);
        map.put("appId", String.valueOf(appId));
        map.put("time", time);
        return mHttpService.getShareInfoShow(map);
    }

    @Override
    public Observable<JsonObject> updateScore(String uid, String flag, String srid) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("uid", uid);
        map.put("appId", String.valueOf(appId));
        map.put("flag", flag);
        map.put("srid", srid);
        map.put("mobile", "1");
        return mHttpService.updateScore(map);
    }

    @Override
    public Observable<JsonObject> updateTestRecordNew(String uid, List<ExamRecord> examRecords) {
        Map<String, String> map = Injection.provideMapInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(uid);
        sb.append("iyubaTest");
        sb.append(DateUtil.dateToString(new Date(), DateUtil.YEAR_MONTH_DAY));
        map.put("uid", uid);
        map.put("sign", CipherUtils.md5(sb.toString()));
        map.put("appId", String.valueOf(appId));
        map.put("appName", Utils.getContext().getString(R.string.app_name));
        map.put("DeviceId", DeviceUtils.getDeviceId(Utils.getContext()));
        map.put("format", "json");
        JsonObject jo = new JsonObject();
        jo.add("datalist", GsonUtils.toJsonArray(examRecords));
        Map<String, String> map1 = Injection.provideMapInstance();
        map1.put("jsonStr", EnDecodeUtils.encode(jo.toString()));
//        map.put("jsonStr", EnDecodeUtils.encode(jo.toString()));
        return mHttpService.updateTestRecordNew(map, EnDecodeUtils.encode(jo.toString()));
    }

    @Override
    public Observable<JsonObject> searchApiNew(String key, int pages, int pageNum, int flg, String uid) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("key", key);
        map.put("format", "json");
        map.put("pages", String.valueOf(pages));
        map.put("pageNum", String.valueOf(pageNum));
        map.put("parentID", String.valueOf(0));
        map.put("userid", uid);
        map.put("type", TOPIC);
        map.put("flg", String.valueOf(flg));
        return mHttpService.searchApiNew(map);
    }

    @Override
    public Observable<JsonObject> recommend() {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("newstype", TOPIC);
        return mHttpService.recommend(map);
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> recommendByKeyword(String uid, String keyWord) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("newstype", TOPIC);
        map.put("userid", uid);
        map.put("keyword", keyWord);
        return mHttpService.recommendByKeyword(map);
    }

    @Override
    public Observable<BaseResponse<List<RecentRead>>> getRecentRV(String uid, int number) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("uid", uid);
        map.put("thisNumber", String.valueOf(number));
        return mHttpService.getRecentRV(map);
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> getNews(String newsIds) {
        Map<String, String> map = Injection.provideMapInstance();
        map.put("NewsIds", newsIds);
        map.put("format", "json");
        return mHttpService.getNews(map);
    }




}
