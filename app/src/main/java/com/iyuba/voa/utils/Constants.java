package com.iyuba.voa.utils;


import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.voa.BuildConfig;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class Constants {
    public static class CONFIG {

        public static String DOMAI_IP = "111.198.52.105";
        public static int DOMAI_PORT = 8085;


        public static String DOMAIN = "iyuba.cn";
        public static String COM_DOMAIN = "iyuba.com.cn";


        public static String API_COM_DOMAIN = "api." + COM_DOMAIN;

        public static String IMAGE_URL = "http://staticvip." + DOMAIN + "/cms/news/image/";
        public static String IMAGE_AD_URL = "http://static3." + DOMAIN + "/dev/";

        public static String SOUND_IP = "http://staticvip." + DOMAIN + "/sounds/voa";
        public static String USER_SPEECH_IP = "http://iuserspeech." + DOMAIN + ":9001/voa/";
        public static String PROTOCOL_IP = "http://iuserspeech." + DOMAIN + ":9001/api";

        public static String API_HOST_APPS = "http://apps." + DOMAIN;
        public static String COIN_ADDRESS = "http://m." + DOMAIN + "/mall/index.jsp?";
        public static String AVATAR_ADDRESS = "http://" + API_COM_DOMAIN + "/v2/api.iyuba?protocol=10005&size=big&uid=";

        public static final int appId = BuildConfig.APPID_VALUE;
        public static int COMPANY = BuildConfig.PROTOCOL_VALUE;       // company:1北京爱语吧  2上海画笙  3爱语言  4山东爱语吧 //华为为4山东爱语吧  其他为3


        public static String EVALUATE_URL = "http://iuserspeech." + DOMAIN + ":9001/test/eval/"; //语音评测
        public static String MERGE_URL = "http://iuserspeech." + DOMAIN + ":9001/test/merge/"; //语音合成

        public static void updateDomain(String domain1, String domain2) {
            DOMAIN = domain1;
            COM_DOMAIN = domain2;
            API_COM_DOMAIN = "api." + domain2;

            AVATAR_ADDRESS = "http://" + API_COM_DOMAIN + "/v2/api.iyuba?protocol=10005&size=big&uid=";
            SOUND_IP = "http://staticvip." + domain1 + "/sounds/voa";
            USER_SPEECH_IP = "http://iuserspeech." + domain1 + ":9001/voa/";
            PROTOCOL_IP = "http://iuserspeech." + domain1 + ":9001/api";
//            PROTOCOL_IP = "http://www.bbe.net.cn";

            API_HOST_APPS = "http://apps." + domain1;
            EVALUATE_URL = "http://iuserspeech." + domain1 + ":9001/test/eval/";
            MERGE_URL = "http://iuserspeech." + domain1 + ":9001/test/merge/";

            CommonVars.domain = domain1;
            CommonVars.domainLong = domain2;

//        ITraining.resetMseUrl();
            String usageUrl = PROTOCOL_IP + "/protocoluse.jsp?company=" + COMPANY;
            String privacyUrl = PROTOCOL_IP + "/protocolpri.jsp?company=" + COMPANY;
            IPrivacy.setPrivacyUsageUrl(usageUrl, privacyUrl);

            String extraUrl = "http://iuserspeech." + domain1 + ":9001/test/eval/";
//            IHeadline.setExtraMseUrl(extraUr  l);
//            ITraining.setExtraEvaluateUrl(extraUrl);
            IHeadline.resetMseUrl();
            IHeadline.setExtraMseUrl(extraUrl);
            IHeadline.setExtraMergeAudioUrl(extraUrl);
        }
    }


    public static class SP {
        public static String IS_AGREE = "0x00X";
        public static String USER_TYPE = "user_type";
        public static String USER_NAME = "user_name";
        public static String PASSWORD = "password";
        public static String LOGIN_URL = "login_url";
        public static String TOKEN = "token";
        public static String UID = "uid";
        public static String DOMAIN = "domain";
        public static String COM_DOMAIN = "com_domain";
        public static String WORD_SORT = "WORD_SORT";

    }

    public static class BUNDLE {
        public static String KEY = "0X000X";
        public static String KEY_0 = "0X000";
        public static String KEY_1 = "0X001";
        public static String KEY_2 = "0X002";
        public static String KEY_3 = "0X003";
        public static String KEY_4 = "0X004";
        public static String KEY_5 = "0X005";
        public static String KEY_6 = "0X006";
        public static String KEY_X = "0XX";
    }

    public static class PERMISSION {
        public static boolean RECORD = false;
    }

    public static String BREAK_PAGE = "";
    public static boolean isPreVerifyDone = false;

}
