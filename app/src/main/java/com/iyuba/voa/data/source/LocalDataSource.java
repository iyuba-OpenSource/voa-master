package com.iyuba.voa.data.source;

import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;

import java.util.List;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2020/12/21
 * 邮箱：jxfengmtx@gmail.com
 */
public interface LocalDataSource {

    String spGetToken();

    void spSaveToken(String token);

    void spSaveUid(String data);

    String spGetUid();

    String spGetUserName();

    void spSaveUserName(String name);

    String spGetPassword();

    void spSavePassword(String password);

    String spGetBreakPage();

    void spSaveBreakPage(String breakPage);


    String[] spGetDomain();

    void spSaveDomain(String domain, String domain2);

    String spGetWordSortWay();

    void spSaveWordSortWay(String way);


    void roomSaveConfig(boolean config);

    boolean roomGetConfig();

    void roomAddUserDatas(UserInfo... data);

    void roomUpdateUserData(UserInfo data);

    void roomDeleteUserById(String uid);

    UserInfo roomGetUserDataById(String uid);

    List<UserInfo> roomGetUserDatas();

    void roomDeleteUserDatas();

    void roomAddTitleTeds(TitleTed... data);

    void roomDeleteTitleTedById(String id);

    TitleTed roomGetTitleTedById(String id);

    TitleTed roomGetTitleTedByIdFlag(String id, int flag);


    List<TitleTed> roomGetTitleTed();

    List<TitleTed> roomGetTitleTedByFlag(int flag);

    void roomDeleteTitleTeds();

    void roomAddWords(XmlWord... data);

    void roomDeleteWordByWord(String word);

    XmlWord roomGetWordByWord(String word);

    List<XmlWord> roomGetWords();

    List<XmlWord> roomGetWordsByWhere(String where, int page, int pageNum);

    void roomDeleteWords();

    void roomAddVoaTexts(VoaText... data);

    int roomGetVoaTestingCountByVoaId(String voaId);

    int roomGetVoaTextCountByVoaId(String voaId);


    void roomDeleteVoaTextByVoaIdAndIndex(String voaId, int index);

    VoaText roomGetVoaTextByVoaIdAndIndex(String voaId, int index);

    List<VoaText> roomGetVoaTexts();

    List<VoaText> roomGetVoaTextByVoaId(String voaId);

    void roomDeleteVoaTexts();

}
