package com.iyuba.voa.data.repository;

import androidx.annotation.VisibleForTesting;

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
import com.iyuba.voa.data.source.HttpDataSource;
import com.iyuba.voa.data.source.LocalDataSource;
import com.mob.secverify.datatype.VerifyResult;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */

public class AppRepository extends BaseModel implements HttpDataSource, LocalDataSource {
    private volatile static AppRepository INSTANCE = null;
    private final HttpDataSource mHttpDataSource;
    private final LocalDataSource mLocalDataSource;

    public AppRepository(HttpDataSource httpDataSource, LocalDataSource localDataSource) {
        mHttpDataSource = httpDataSource;
        mLocalDataSource = localDataSource;

    }

    public static AppRepository getInstance(HttpDataSource httpDataSource, LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (AppRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppRepository(httpDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> titleApi(int maxid, int pages, int pageNum, int parentID) {
        return mHttpDataSource.titleApi(maxid, pages, pageNum, parentID);
    }

    @Override
    public Observable<JsonObject> textExamApi(String voaid) {
        return mHttpDataSource.textExamApi(voaid);
    }

    @Override
    public Observable<BaseResponse> feedback(int protocol, String uid, String content, String email) {
        return mHttpDataSource.feedback(protocol, uid, content, email);
    }

    @Override
    public Observable<UserInfo> login(String userName, String password, String protocol) {
        return mHttpDataSource.login(userName, password, protocol);
    }

    @Override
    public Observable<UserInfo> register(String mobile, String userName, String password) {
        return mHttpDataSource.register(mobile, userName, password);
    }

    @Override
    public Observable<JsonObject> secVerify(VerifyResult verifyResult) {
        return mHttpDataSource.secVerify(verifyResult);
    }

    @Override
    public Observable<UserInfo> logout(String userName, String password) {
        return mHttpDataSource.logout(userName, password);
    }

    @Override
    public Observable<BaseResponse> sendMessage3(String userphone) {
        return mHttpDataSource.sendMessage3(userphone);
    }

    @Override
    public Observable<XmlResponse> updateCollect(String userId, String voaId, String type) {
        return mHttpDataSource.updateCollect(userId, voaId, type);
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> getCollect(String uid, int pages, int pageCount) {
        return mHttpDataSource.getCollect(uid, pages, pageCount);
    }

    @Override
    public Observable<JsonObject> exportPDF(String voaId, int languageType) {
        return mHttpDataSource.exportPDF(voaId, languageType);
    }

    @Override
    public Observable<XmlWord> apiWord(String word) {
        return mHttpDataSource.apiWord(word);
    }

    @Override
    public Observable<JsonObject> updateWord(String userId, String word, String mod) {
        return mHttpDataSource.updateWord(userId, word, mod);
    }

    @Override
    public Observable<BaseResponse<List<XmlWord>>> wordListService(String userId, int page, int pageNum) {
        return mHttpDataSource.wordListService(userId, page, pageNum);
    }

    @Override
    public Observable<BaseResponse<VoaScore>> uploadTesting(File file, String userId, String sentence, String newsId, String paraId, String IdIndex) {
        return mHttpDataSource.uploadTesting(file, userId, sentence, newsId, paraId, IdIndex);
    }

    @Override
    public Observable<JsonObject> audioMerge(String audios) {
        return mHttpDataSource.audioMerge(audios);
    }

    @Override
    public Observable<JsonObject> releaseTesting(String voaid, String uid, String mergeUrl, String totalScore) {
        return mHttpDataSource.releaseTesting(voaid, uid, mergeUrl, totalScore);
    }

    @Override
    public Observable<JsonObject> releaseSingleTesting(String voaid, String username, String uid, VoaText voaText) {
        return mHttpDataSource.releaseSingleTesting(voaid, username, uid, voaText);

    }


    @Override
    public Observable<JsonObject> getTesting(String voaid, String uid) {
        return mHttpDataSource.getTesting(voaid, uid);
    }

    @Override
    public Observable<JsonObject> getTopicRanking(String voaid, String uid, int start, int total) {
        return mHttpDataSource.getTopicRanking(voaid, uid, start, total);
    }

    @Override
    public Observable<UserInfo> getVipInfo(String id, String myid) {
        return mHttpDataSource.getVipInfo(id, myid);
    }

    @Override
    public Observable<JsonObject> alipay(String uid, String price, int month, int productId, String cate) {
        return mHttpDataSource.alipay(uid, price, month, productId, cate);
    }

    @Override
    public Observable<JsonObject> weixinPay(String uid, String price, int month, int productId, String cate) {
        return mHttpDataSource.weixinPay(uid, price, month, productId, cate);
    }

    @Override
    public Observable<JsonObject> notifyAliNew(String data) {
        return mHttpDataSource.notifyAliNew(data);
    }

    @Override
    public Observable<JsonObject> updateDomain(String short1, String short2) {
        return mHttpDataSource.updateDomain(short1, short2);
    }

    @Override
    public Observable<JsonObject> getWordToPDF(String uid, int page, int pageNum) {
        return mHttpDataSource.getWordToPDF(uid, page, pageNum);
    }

    @Override
    public Observable<JsonObject> updateStudyRecordNew(String beginTime, String endTime, String LessonId, String TestNumber, String TestWords, String uid, int endFlg) {
        return mHttpDataSource.updateStudyRecordNew(beginTime, endTime, LessonId, TestNumber, TestWords, uid, endFlg);
    }

    @Override
    public Observable<PronCorrect> correctPron(String word, String userPron, String oriPron) {
        return mHttpDataSource.correctPron(word, userPron, oriPron);
    }

    @Override
    public Observable<BaseResponse<VoaScore>> wordTesting(String userId, File file, VoaText voaText) {
        return mHttpDataSource.wordTesting(userId, file, voaText);
    }

    @Override
    public Observable<JsonArray> getAdEntryAll(String flag, String uid) {
        return mHttpDataSource.getAdEntryAll(flag, uid);
    }

    @Override
    public Observable<StudyTime> getMyTime(String uid, String flag, String day) {
        return mHttpDataSource.getMyTime(uid, flag, day);
    }

    @Override
    public Observable<JsonObject> getReadRanking(String uid, String type, int start, int total) {
        return mHttpDataSource.getReadRanking(uid, type, start, total);
    }

    @Override
    public Observable<JsonObject> getListenRanking(String uid, String type, int start, int total) {
        return mHttpDataSource.getListenRanking(uid, type, start, total);
    }

    @Override
    public Observable<JsonObject> getTalkRanking(String uid, String type, int start, int total) {
        return mHttpDataSource.getTalkRanking(uid, type, start, total);
    }

    @Override
    public Observable<JsonObject> getStudyRanking(String uid, String type, int start, int total) {
        return mHttpDataSource.getStudyRanking(uid, type, start, total);
    }

    @Override
    public Observable<JsonObject> getTestRanking(String uid, String type, int start, int total) {
        return mHttpDataSource.getTestRanking(uid, type, start, total);
    }

    @Override
    public Observable<JsonObject> getShareInfoShow(String uid, String time) {
        return mHttpDataSource.getShareInfoShow(uid, time);
    }

    @Override
    public Observable<JsonObject> updateScore(String uid, String flg, String srid) {
        return mHttpDataSource.updateScore(uid, flg, srid);
    }

    @Override
    public Observable<JsonObject> updateTestRecordNew(String uid, List<ExamRecord> examRecords) {
        return mHttpDataSource.updateTestRecordNew(uid, examRecords);
    }

    @Override
    public Observable<JsonObject> searchApiNew(String key, int pages, int pageNum, int flg, String uid) {
        return mHttpDataSource.searchApiNew(key, pages, pageNum, flg, uid);
    }

    @Override
    public Observable<JsonObject> recommend() {
        return mHttpDataSource.recommend();
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> recommendByKeyword(String uid, String keyWord) {
        return mHttpDataSource.recommendByKeyword(uid, keyWord);
    }

    @Override
    public Observable<BaseResponse<List<RecentRead>>> getRecentRV(String uid, int number) {
        return mHttpDataSource.getRecentRV(uid, number);
    }

    @Override
    public Observable<BaseResponse<List<TitleTed>>> getNews(String newsIds) {
        return mHttpDataSource.getNews(newsIds);
    }


    @Override
    public String spGetToken() {
        return mLocalDataSource.spGetToken();
    }

    @Override
    public void spSaveToken(String token) {
        mLocalDataSource.spSaveToken(token);
    }

    @Override
    public void spSaveUid(String data) {
        mLocalDataSource.spSaveUid(data);
    }

    @Override
    public String spGetUid() {
        return mLocalDataSource.spGetUid();
    }

    @Override
    public String spGetUserName() {
        return mLocalDataSource.spGetUserName();
    }

    @Override
    public void spSaveUserName(String name) {
        mLocalDataSource.spSaveUserName(name);
    }

    @Override
    public String spGetPassword() {
        return mLocalDataSource.spGetPassword();
    }

    @Override
    public void spSavePassword(String password) {
        mLocalDataSource.spSavePassword(password);
    }

    @Override
    public String spGetBreakPage() {
        return mLocalDataSource.spGetBreakPage();
    }

    @Override
    public void spSaveBreakPage(String breakPage) {
        mLocalDataSource.spSaveBreakPage(breakPage);
    }

    @Override
    public String[] spGetDomain() {
        return mLocalDataSource.spGetDomain();
    }

    @Override
    public void spSaveDomain(String domain, String domain2) {
        mLocalDataSource.spSaveDomain(domain, domain2);
    }

    @Override
    public String spGetWordSortWay() {
        return mLocalDataSource.spGetWordSortWay();
    }

    @Override
    public void spSaveWordSortWay(String way) {
        mLocalDataSource.spSaveWordSortWay(way);
    }

    @Override
    public void roomSaveConfig(boolean config) {
        mLocalDataSource.roomSaveConfig(config);
    }

    @Override
    public boolean roomGetConfig() {
        return mLocalDataSource.roomGetConfig();
    }

    @Override
    public void roomAddUserDatas(UserInfo... data) {
        mLocalDataSource.roomAddUserDatas(data);
    }

    @Override
    public void roomUpdateUserData(UserInfo data) {
        mLocalDataSource.roomUpdateUserData(data);
    }

    @Override
    public void roomDeleteUserById(String uid) {
        mLocalDataSource.roomDeleteUserById(uid);
    }

    @Override
    public UserInfo roomGetUserDataById(String uid) {
        return mLocalDataSource.roomGetUserDataById(uid);
    }

    @Override
    public List<UserInfo> roomGetUserDatas() {
        return mLocalDataSource.roomGetUserDatas();
    }

    @Override
    public void roomDeleteUserDatas() {
        mLocalDataSource.spSaveUid("");
        mLocalDataSource.roomDeleteUserDatas();
    }

    @Override
    public void roomAddTitleTeds(TitleTed... data) {
        mLocalDataSource.roomAddTitleTeds(data);
    }

    @Override
    public void roomDeleteTitleTedById(String id) {
        mLocalDataSource.roomDeleteTitleTedById(id);
    }

    @Override
    public TitleTed roomGetTitleTedById(String id) {
        return mLocalDataSource.roomGetTitleTedById(id);
    }

    @Override
    public TitleTed roomGetTitleTedByIdFlag(String id, int flag) {
        return mLocalDataSource.roomGetTitleTedByIdFlag(id, flag);
    }

    @Override
    public List<TitleTed> roomGetTitleTed() {
        return mLocalDataSource.roomGetTitleTed();
    }

    @Override
    public List<TitleTed> roomGetTitleTedByFlag(int flag) {
        return mLocalDataSource.roomGetTitleTedByFlag(flag);
    }

    @Override
    public void roomDeleteTitleTeds() {
        mLocalDataSource.roomDeleteTitleTeds();
    }

    @Override
    public void roomAddWords(XmlWord... data) {
        mLocalDataSource.roomAddWords(data);
    }

    @Override
    public void roomDeleteWordByWord(String word) {
        mLocalDataSource.roomDeleteWordByWord(word);
    }

    @Override
    public XmlWord roomGetWordByWord(String word) {
        return mLocalDataSource.roomGetWordByWord(word);
    }

    @Override
    public List<XmlWord> roomGetWords() {
        return mLocalDataSource.roomGetWords();
    }

    @Override
    public List<XmlWord> roomGetWordsByWhere(String where, int page, int pageNum) {
        return mLocalDataSource.roomGetWordsByWhere(where, page, pageNum);
    }

    @Override
    public void roomDeleteWords() {
        mLocalDataSource.roomGetWords();
    }

    @Override
    public void roomAddVoaTexts(VoaText... data) {
        mLocalDataSource.roomAddVoaTexts(data);
    }

    @Override
    public int roomGetVoaTestingCountByVoaId(String voaId) {
        return mLocalDataSource.roomGetVoaTestingCountByVoaId(voaId);
    }

    @Override
    public int roomGetVoaTextCountByVoaId(String voaId) {
        return mLocalDataSource.roomGetVoaTextCountByVoaId(voaId);
    }

    @Override
    public void roomDeleteVoaTextByVoaIdAndIndex(String voaId, int index) {
        mLocalDataSource.roomDeleteVoaTextByVoaIdAndIndex(voaId, index);
    }

    @Override
    public VoaText roomGetVoaTextByVoaIdAndIndex(String voaId, int index) {
        return mLocalDataSource.roomGetVoaTextByVoaIdAndIndex(voaId, index);
    }

    @Override
    public List<VoaText> roomGetVoaTexts() {
        return mLocalDataSource.roomGetVoaTexts();
    }

    @Override
    public List<VoaText> roomGetVoaTextByVoaId(String voaId) {
        return mLocalDataSource.roomGetVoaTextByVoaId(voaId);
    }

    @Override
    public void roomDeleteVoaTexts() {
        mLocalDataSource.roomDeleteVoaTexts();
    }
}
