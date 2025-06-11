package com.iyuba.voa.data.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iyuba.voa.data.entity.PronCorrect;
import com.iyuba.voa.data.entity.RecentRead;
import com.iyuba.voa.data.entity.StudyTime;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.entity.VoaScore;
import com.iyuba.voa.data.entity.XmlResponse;
import com.iyuba.voa.data.entity.XmlWord;
import com.suzhou.concept.utils.ConverterFormat;
import com.suzhou.concept.utils.ResponseConverter;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public interface HttpService {
    /**
     * 获取首页慢速英语新闻列表数据
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @GET("/iyuba/titleApi.jsp")
    Observable<BaseResponse<List<TitleTed>>> titleTed(@QueryMap Map<String, String> params);

    /**
     * 详情页
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @GET("/iyuba/textExamApi.jsp")
    Observable<JsonObject> textExamApi(@QueryMap Map<String, String> params);


    /**
     * 反馈意见
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apis")
    @GET("/v2/api.iyuba")
    Observable<BaseResponse> feedback(@QueryMap Map<String, String> params);

    /**
     * 登录注册
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:api.")
    @GET("/v2/api.iyuba")
    Observable<UserInfo> loginRegister(@QueryMap Map<String, String> params);

    /**
     * 登录注册
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:api.")
    @GET("/v2/api.iyuba")
    Observable<JsonObject> loginRegisterByMob(@QueryMap Map<String, String> params);

    /**
     * 拉取会员信息
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:api.")
    @GET("/v2/api.iyuba")
    Observable<UserInfo> getUserInfo(@QueryMap Map<String, String> params);  //更新部分room字段

    /**
     * 发送验证码前(注册)验证手机号
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:api.")
    @GET("/sendMessage3.jsp")
    Observable<BaseResponse> sendMessage3(@QueryMap Map<String, String> params);

    /**
     * 收藏/取消收藏
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @ResponseConverter(format = ConverterFormat.XML)
    @GET("/iyuba/updateCollect.jsp")
    Observable<XmlResponse> updateCollect(@QueryMap Map<String, String> params);

    /**
     * 文章收藏列表
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:cms")
    @GET("/dataapi/jsp/getCollect.jsp")
    Observable<BaseResponse<List<TitleTed>>> getCollect(@QueryMap Map<String, String> params);

    /**
     * 导出pdf
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @GET("/iyuba/getVoapdfFile_new.jsp")
    Observable<JsonObject> exportPDF(@QueryMap Map<String, String> params);

    /**
     * 单词详情
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:word")
    @ResponseConverter(format = ConverterFormat.XML)
    @GET("/words/apiWord.jsp")
    Observable<XmlWord> apiWord(@QueryMap Map<String, String> params);

    /**
     * 单词收藏/取消收藏
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:word")
    @GET("/words/updateWord.jsp")
    Observable<JsonObject> updateWord(@QueryMap Map<String, String> params);

    /**
     * 生词本
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:word")
    @GET("/words/wordListService.jsp")
    Observable<BaseResponse<List<XmlWord>>> wordListService(@QueryMap Map<String, String> params);

    /**
     * 上传评测 无纠音
     *
     * @param body
     * @return
     */
    @Headers("urlPrefix:iuserspeech")
    @POST("/test/eval/")
    Observable<BaseResponse<VoaScore>> uploadTesting(@Body RequestBody body);

    /**
     * 上传评测 纠音\句子、单词
     *
     * @param
     * @return
     */
    @Headers("urlPrefix:iuserspeech")
//    @POST("/test/ai/")
    @POST("/test/ai10/")
    Observable<BaseResponse<VoaScore>> uploadCorrectTesting(@Body RequestBody body);

    /**
     * 评测语音合成
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:iuserspeech")
    @GET("/test/merge/")
    Observable<JsonObject> audioMerge(@QueryMap Map<String, String> params);

    /**
     * 获取用户评测数据列表
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:voa")
    @GET("/voa/getWorksByUserId.jsp")
    Observable<JsonObject> getTesting(@QueryMap Map<String, String> params);

    /**
     * 发布评测语音数据
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:voa")
    @GET("/voa/UnicomApi")
    Observable<JsonObject> releaseTesting(@QueryMap Map<String, String> params);

    /**
     * 获取评测排名
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:daxue")
    @GET("/ecollege/getTopicRanking.jsp")
    Observable<JsonObject> getTopicRanking(@QueryMap Map<String, String> params);

    /**
     * 获取阿里支付的orderInfo
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:vip")
    @GET("/alipay.jsp")
    Observable<JsonObject> alipay(@QueryMap Map<String, String> params);

    /**
     * 获取阿里支付的orderInfo
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:vip")
    @GET("/weixinPay.jsp?")
    Observable<JsonObject> weixinPay(@QueryMap Map<String, String> params);

    /**
     * 开通vip成功
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:vip")
    @GET("/notifyAliNew.jsp")
    Observable<JsonObject> notifyAliNew(@QueryMap Map<String, String> params);

    /**
     * 更新域名
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:updateDomain")
    @GET("/api/getDomain.jsp")
    Observable<JsonObject> updateDomain(@QueryMap Map<String, String> params);

    /**
     * 生词pdf导出
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:ai")
    @GET("/management/getWordToPDF.jsp")
    Observable<JsonObject> getWordToPDF(@QueryMap Map<String, String> params);


    /**
     * 记录上传
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:daxue")
    @GET("/ecollege/updateStudyRecordNew.jsp")
    Observable<JsonObject> updateStudyRecordNew(@QueryMap Map<String, String> params);

    /**
     * 纠音
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:word")
    @GET("/words/apiWordAi.jsp")
    Observable<PronCorrect> apiWordAi(@QueryMap Map<String, String> params);


    /**
     * 广告
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:dev")
    @GET("/getAdEntryAll.jsp")
    Observable<JsonArray> getAdEntryAll(@QueryMap Map<String, String> params);


    /**
     * 打卡
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:daxue")
    @GET("/ecollege/getMyTime.jsp")
    Observable<StudyTime> getMyTime(@QueryMap Map<String, String> params);

    /**
     * 新闻排行榜
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:cms")
    @GET("/newsApi/getNewsRanking.jsp")
    Observable<JsonObject> getNewsRanking(@QueryMap Map<String, String> params);


    /**
     * 获取评测排名
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:daxue")
    @GET("/ecollege/getStudyRanking.jsp")
    Observable<JsonObject> getStudyRanking(@QueryMap Map<String, String> params);

    /**
     * 获取练习排名
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:daxue")
    @GET("/ecollege/getTestRanking.jsp")
    Observable<JsonObject> getTestRanking(@QueryMap Map<String, String> params);

    /**
     * 获取打卡记录
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:app")
    @GET("/getShareInfoShow.jsp")
    Observable<JsonObject> getShareInfoShow(@QueryMap Map<String, String> params);

    /**
     * 获取打卡记录
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:api")
    @GET("/credits/updateScore.jsp")
    Observable<JsonObject> updateScore(@QueryMap Map<String, String> params);

    /**
     * 上传练习记录
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:daxue")
    @POST("/ecollege/updateTestRecordNew.jsp")
    @FormUrlEncoded
    Observable<JsonObject> updateTestRecordNew(@QueryMap Map<String, String> params, @Field("jsonStr") String jsonStr);


    /**
     * 新闻类的搜索
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @GET("/iyuba/searchApiNew.jsp")
    Observable<JsonObject> searchApiNew(@QueryMap Map<String, String> params);

    /**
     * 热词
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @GET("/voa/recommend.jsp")
    Observable<JsonObject> recommend(@QueryMap Map<String, String> params);

    /**
     * 热词搜索
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:apps")
    @GET("/voa/recommendByKeyword.jsp")
    Observable<BaseResponse<List<TitleTed>>> recommendByKeyword(@QueryMap Map<String, String> params);


    /** 每调用
     * 最近阅读历史id
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:cms")
    @GET("/newsApi/getRecentRV.jsp")
    Observable<BaseResponse<List<RecentRead>>> getRecentRV(@QueryMap Map<String, String> params);


    /** 每调用
     * 最近阅读历史id查询文章
     *
     * @param params
     * @return
     */
    @Headers("urlPrefix:cms")
    @GET("/cmsApi/getNews.jsp")
    Observable<BaseResponse<List<TitleTed>>> getNews(@QueryMap Map<String, String> params);

}
