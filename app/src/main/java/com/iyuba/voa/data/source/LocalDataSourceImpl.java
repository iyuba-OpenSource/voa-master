package com.iyuba.voa.data.source;


import android.text.TextUtils;

import com.iyuba.voa.app.AppDatabase;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.utils.Constants.SP;

import java.util.List;

import me.goldze.mvvmhabit.utils.SPUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2020/11/13
 * 邮箱：jxfengmtx@gmail.com
 */
public class LocalDataSourceImpl implements LocalDataSource {
    private volatile static LocalDataSourceImpl INSTANCE = null;
    private volatile static AppDatabase db = null;

    private SPUtils mSpUtils = SPUtils.getInstance();

    public static LocalDataSourceImpl getInstance(AppDatabase db) {
        if (INSTANCE == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSourceImpl(db);
                }
            }
        }
        return INSTANCE;
    }

    public LocalDataSourceImpl(AppDatabase db) {
        this.db = db;
    }


    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public String spGetToken() {
        return mSpUtils.getString(SP.TOKEN);
    }

    @Override
    public void spSaveToken(String token) {
        mSpUtils.put(SP.TOKEN, token);
    }

    @Override
    public void spSaveUid(String data) {
        mSpUtils.put(SP.UID, data);
    }

    @Override
    public String spGetUid() {
        return mSpUtils.getString(SP.UID);
    }

    @Override
    public String spGetUserName() {
        return mSpUtils.getString(SP.USER_NAME);
    }

    @Override
    public void spSaveUserName(String name) {
        mSpUtils.put(SP.USER_NAME, name);
    }

    @Override
    public String spGetPassword() {
        return mSpUtils.getString(SP.PASSWORD, "");
    }

    @Override
    public void spSavePassword(String password) {
        mSpUtils.put(SP.PASSWORD, password);
    }

    @Override
    public String spGetBreakPage() {
        return "";
//        return mSpUtils.getString(SP.BREAK_PAGE);
    }

    @Override
    public void spSaveBreakPage(String breakPage) {
//        mSpUtils.put(SP.BREAK_PAGE, breakPage);
    }

    @Override
    public String[] spGetDomain() {
        return new String[]{mSpUtils.getString(SP.DOMAIN), mSpUtils.getString(SP.COM_DOMAIN)};
    }

    @Override
    public void spSaveDomain(String domain, String domain2) {
        mSpUtils.put(SP.DOMAIN, domain);
        mSpUtils.put(SP.COM_DOMAIN, domain2);
    }

    @Override
    public String spGetWordSortWay() {
        return mSpUtils.getString(SP.WORD_SORT, "createDate");
    }

    @Override
    public void spSaveWordSortWay(String way) {
        mSpUtils.put(SP.WORD_SORT, way);
    }

    @Override
    public void roomSaveConfig(boolean config) {

    }

    @Override
    public boolean roomGetConfig() {
        return false;
    }

    @Override
    public void roomAddUserDatas(UserInfo... data) {
        db.userInfoDao().insertAll(data);
    }

    @Override
    public void roomUpdateUserData(UserInfo data) {
        db.userInfoDao().updateVip(data);
    }

    @Override
    public void roomDeleteUserById(String uid) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUid(uid);
        db.userInfoDao().delete(userInfo);
    }

    @Override
    public UserInfo roomGetUserDataById(String uid) {
        return db.userInfoDao().getUserById(uid);
    }

    @Override
    public List<UserInfo> roomGetUserDatas() {
        return db.userInfoDao().getAll();
    }

    @Override
    public void roomDeleteUserDatas() {
        db.userInfoDao().deleteAll();
    }

    @Override
    public void roomAddTitleTeds(TitleTed... data) {
        for (TitleTed titleTed : data) {
            titleTed.addUid(spGetUid());
        }
        db.titleTedDao().insertAll(data);
    }

    @Override
    public void roomDeleteTitleTedById(String id) {
        TitleTed titleTed = new TitleTed();
        titleTed.setVoaId(id);
        db.titleTedDao().delete(titleTed);
    }

    @Override
    public TitleTed roomGetTitleTedById(String id) {
        return db.titleTedDao().getTitleTedById(id, TextUtils.isEmpty(spGetUid()) ? "0" : spGetUid());
    }

    @Override
    public TitleTed roomGetTitleTedByIdFlag(String id, int flag) {
        return db.titleTedDao().getTitleTedByIdFlag(id, String.valueOf(flag),TextUtils.isEmpty(spGetUid()) ? "0" : spGetUid());
    }

    @Override
    public List<TitleTed> roomGetTitleTed() {
        return db.titleTedDao().getAll();
    }

    @Override
    public List<TitleTed> roomGetTitleTedByFlag(int flag) {
        return db.titleTedDao().getAllByFlag(String.valueOf(flag),TextUtils.isEmpty(spGetUid()) ? "0" : spGetUid());
    }

    @Override
    public void roomDeleteTitleTeds() {
        db.titleTedDao().deleteAll();
    }

    @Override
    public void roomAddWords(XmlWord... data) {
        db.wordDao().insertAll(data);
    }

    @Override
    public void roomDeleteWordByWord(String word) {
        XmlWord xmlWord = new XmlWord();
        xmlWord.setKey(word);
        db.wordDao().delete(xmlWord);
    }

    @Override
    public XmlWord roomGetWordByWord(String word) {
        return db.wordDao().getAllByTitle(word);
    }

    @Override
    public List<XmlWord> roomGetWords() {
        return db.wordDao().getAll();
    }

    @Override
    public List<XmlWord> roomGetWordsByWhere(String where, int page, int pageNum) {
        page = (page - 1) * pageNum;
        if (where.toLowerCase().contains("date"))
            return db.wordDao().getAllByDate(page, pageNum);
        else return db.wordDao().getAllByLetter(page, pageNum);
    }

    @Override
    public void roomDeleteWords() {
        db.wordDao().deleteAll();
    }

    @Override
    public void roomAddVoaTexts(VoaText... data) {
        for (VoaText voaText : data) {
            voaText.setUid(spGetUid());
        }
        db.voaTextDao().insertAll(data);
    }

    @Override
    public int roomGetVoaTestingCountByVoaId(String voaid) {
        return db.voaTextDao().getTestingCountByVoaid(voaid);
    }

    @Override
    public int roomGetVoaTextCountByVoaId(String voaId) {
        return db.voaTextDao().getCountByVoaid(voaId);
    }

    @Override
    public void roomDeleteVoaTextByVoaIdAndIndex(String voaId, int index) {
        VoaText voaText = new VoaText();
        voaText.setVoaId(voaId);
        voaText.setIndex(index);
        voaText.setUid(spGetUid());
        db.voaTextDao().delete(voaText);
    }


    @Override
    public VoaText roomGetVoaTextByVoaIdAndIndex(String voaId, int index) {
        return db.voaTextDao().getVoaTextById(voaId, index, spGetUid());
    }

    @Override
    public List<VoaText> roomGetVoaTexts() {
        return db.voaTextDao().getAll();
    }

    @Override
    public List<VoaText> roomGetVoaTextByVoaId(String voaId) {
        return db.voaTextDao().getVoaTextByVoaId(voaId);
    }

    @Override
    public void roomDeleteVoaTexts() {
        db.voaTextDao().deleteAll();
    }
}
