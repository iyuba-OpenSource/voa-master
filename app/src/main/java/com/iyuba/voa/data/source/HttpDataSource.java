package com.iyuba.voa.data.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import com.mob.secverify.datatype.VerifyResult;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.QueryMap;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public interface HttpDataSource {
    Observable<BaseResponse<List<TitleTed>>> titleApi(int maxid, int pages, int pageNum, int parentID);

    Observable<JsonObject> textExamApi(String voaid);

    Observable<BaseResponse> feedback(int protocol, String uid, String content, String email);

    Observable<UserInfo> login(String userName, String password, String protocol);

    Observable<UserInfo> register(String mobile, String userName, String password);

    Observable<JsonObject> secVerify(VerifyResult verifyResult);

    Observable<UserInfo> logout(String userName, String password);

    Observable<BaseResponse> sendMessage3(String userphone);

    Observable<XmlResponse> updateCollect(String userId, String voaId, String type);

    Observable<BaseResponse<List<TitleTed>>> getCollect(String uid, int pages, int pageCount);

    Observable<JsonObject> exportPDF(String voaId, int languageType);

    Observable<XmlWord> apiWord(String word);

    Observable<JsonObject> updateWord(String userId, String word, String mod);

    Observable<BaseResponse<List<XmlWord>>> wordListService(String userId, int page, int pageNum);


    Observable<BaseResponse<VoaScore>> uploadTesting(File file, String userId, String sentence, String newsId
            , String paraId, String IdIndex);

    Observable<JsonObject> audioMerge(String audios);

    Observable<JsonObject> releaseTesting(String voaid, String uid, String mergeUrl, String totalScore);

    Observable<JsonObject> releaseSingleTesting(String voaid, String username, String uid, VoaText voaText);

    Observable<JsonObject> getTesting(String voaid, String uid);

    Observable<JsonObject> getTopicRanking(String voaid, String uid, int start, int total);

    Observable<UserInfo> getVipInfo(String id, String myid);

    Observable<JsonObject> alipay(String uid, String price, int month, int productId, String cate);

    Observable<JsonObject> weixinPay(String uid, String price, int month, int productId, String cate);

    Observable<JsonObject> notifyAliNew(String data);

    Observable<JsonObject> updateDomain(String short1, String short2);

    Observable<JsonObject> getWordToPDF(String uid, int page, int pageNum);

    Observable<JsonObject> updateStudyRecordNew(String beginTime, String endTime, String LessonId, String TestNumber, String TestWords, String uid, int endFlg);

    Observable<PronCorrect> correctPron(String word, String userPron, String oriPron);

    Observable<BaseResponse<VoaScore>> wordTesting(String userId, File file, VoaText voaText);

    Observable<JsonArray> getAdEntryAll(String flag, String uid);

    Observable<StudyTime> getMyTime(String uid, String flag, String day);

    Observable<JsonObject> getReadRanking(String uid, String type, int start, int total);

    Observable<JsonObject> getListenRanking(String uid, String type, int start, int total);

    Observable<JsonObject> getTalkRanking(String uid, String type, int start, int total);

    Observable<JsonObject> getStudyRanking(String uid, String type, int start, int total);

    Observable<JsonObject> getTestRanking(String uid, String type, int start, int total);

    Observable<JsonObject> getShareInfoShow(String uid, String time);

    Observable<JsonObject> updateScore(String uid, String flg, String srid);

    Observable<JsonObject> updateTestRecordNew(String uid, List<ExamRecord> examRecords);

    Observable<JsonObject> searchApiNew(String key, int pages, int pageNum, int flg, String uid);

    Observable<JsonObject> recommend();

    Observable<BaseResponse<List<TitleTed>>> recommendByKeyword(String uid, String keyWord);

    Observable<BaseResponse<List<RecentRead>>> getRecentRV(String uid,int number);

    Observable<BaseResponse<List<TitleTed>>> getNews(String newsIds);




}
