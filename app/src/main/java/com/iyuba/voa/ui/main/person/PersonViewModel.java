package com.iyuba.voa.ui.main.person;

import static com.iyuba.voa.utils.Constants.CONFIG.COIN_ADDRESS;
import static com.iyuba.voa.utils.Constants.CONFIG.COMPANY;
import static com.iyuba.voa.utils.Constants.CONFIG.PROTOCOL_IP;
import static com.iyuba.voa.utils.Constants.CONFIG.appId;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.module.toolbox.MD5;
import com.iyuba.trainingcamp.ui.GoldFragment;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.data.entity.StudyTime;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.main.home.HomeListFragment;
import com.iyuba.voa.ui.main.person.collect.word.WordCollectListFragment;
import com.iyuba.voa.ui.main.person.download.DownloadListFragment;
import com.iyuba.voa.ui.main.person.feedback.FeedbackFragment;
import com.iyuba.voa.ui.main.person.rank.RankActivity;
import com.iyuba.voa.ui.main.person.signin.SignInFragment;
import com.iyuba.voa.ui.main.person.signinreport.SignInReportFragment;
import com.iyuba.voa.ui.vip.VipFragment;
import com.iyuba.voa.utils.CacheUtil;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import personal.iyuba.personalhomelibrary.ui.my.MySpeechActivity;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/30
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class PersonViewModel extends BaseTitleViewModel<AppRepository> {
    private final String privacy = PROTOCOL_IP + "/protocolpri.jsp?company=" + COMPANY;
    private final String provision = PROTOCOL_IP + "/protocoluse.jsp?company=" + COMPANY;


    public UIChangeObservable uc = new UIChangeObservable();

    public ObservableField<UserInfo> entity = new ObservableField<>();

    public ObservableField<String> cacheSize = new ObservableField<>();

    public BindingCommand<Void> clickLogin = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.startPersonalCenter.setValue(entity.get());
//            ToastUtils.showShort("您已登录");
        }
    });
    public BindingCommand<Void> clickSignIn = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            SignIn();
//            ToastUtils.showShort("您已登录");
        }
    });

    public BindingCommand<Void> clickLogout = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.showLogoutDialog.call();
        }
    });
    public BindingCommand<Void> clickExitLogin = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            clearData();
        }
        ToastUtils.showShort("退出成功");
    });

    public BindingCommand<Void> clickPrivacy = new BindingCommand<>(() -> {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, "隐私协议");
        bundle.putString(Constants.BUNDLE.KEY_0, privacy + "&apptype=" + Utils.getContext().getString(R.string.app_name));
//        startContainerActivity(WebFragment.class.getCanonicalName(),bundle);
        startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
    });

    public BindingCommand<Void> clickTermsUse = new BindingCommand<>(() -> {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, "使用条款");
        bundle.putString(Constants.BUNDLE.KEY_0, provision + "&apptype=" + Utils.getContext().getString(R.string.app_name));
        startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
    });
    public BindingCommand<Void> clickUpdateDomain = new BindingCommand<>(() -> {
//        ToastUtils.showShort("尚在开发中");
//        uc.showDomainDialog.call();
        getNetDomain();
    });

    public BindingCommand<Void> clickCollect = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.startCollect.call();
//            startContainerActivity(CollectListFragment.class.getCanonicalName());
        }
    });
    public BindingCommand<Void> clickReadHistory = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE.KEY, 1);
            startContainerActivity(HomeListFragment.class.getCanonicalName(), bundle);
        }
    });
    public BindingCommand<Void> clickVoice = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.startVoice.call();
//            startContainerActivity(CollectListFragment.class.getCanonicalName());
        }
    });
    public BindingCommand<Void> clickCoinMall = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, "积分商城");
            bundle.putString(Constants.BUNDLE.KEY_0, String.format(COIN_ADDRESS + "uid=%s&sign=%s&username=%s&appid=%s&platform=android",
                    model.spGetUid(), MD5.getMD5ofStr("iyuba" + model.spGetUid() + "camstory"),
                    model.spGetUserName(),
                    appId));
            startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
        }
    });
    public BindingCommand<Void> clickWordCollect = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            startContainerActivity(WordCollectListFragment.class.getCanonicalName());
        }
    });
    public BindingCommand<Void> clickDownload = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            startContainerActivity(DownloadListFragment.class.getCanonicalName());
        }
    });
    public BindingCommand<Void> clickRanking = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            startActivity(RankActivity.class);
        }
    });
    public BindingCommand<Void> clickKouyuQ = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            startActivity(MySpeechActivity.class);
        }
    });
    public BindingCommand<Void> clickTrainCamp = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            startContainerActivity(GoldFragment.class.getCanonicalName());
        }
    });
    public BindingCommand<Void> clickStudyReport = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.startStudyReport.call();
        }
    });
    public BindingCommand<Void> clickSignInReport = new BindingCommand<>(() -> {
        startContainerActivity(SignInReportFragment.class.getCanonicalName());
    });
    public BindingCommand<Void> clickClearCache = new BindingCommand<>(() -> {
        CacheUtil.clearAllCache(Utils.getContext());
        ToastUtils.showShort("清除成功");
        getCache();
    });
    public BindingCommand<Void> clickVIP = new BindingCommand<>(() -> {
        startContainerActivity(VipFragment.class.getCanonicalName());
    });
    public BindingCommand<Void> clickFeedback = new BindingCommand<>(() -> {
        startContainerActivity(FeedbackFragment.class.getCanonicalName());
    });
    public BindingCommand<Void> clickMessage = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.startMessage.call();
        }
    });
    public BindingCommand<Void> clickGroupQQ = new BindingCommand<>(() -> {
        if (checkIsLogin()) {
            uc.startGroupQQ.call();
        }
    });
    private Disposable mSubscription;

    public PersonViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
        getCache();
    }


    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> showLogoutDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> showDomainDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<UserInfo> startPersonalCenter = new SingleLiveEvent<>();
        public SingleLiveEvent<UserInfo> startCollect = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> startVoice = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> startStudyReport = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> startMessage = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> startGroupQQ = new SingleLiveEvent<>();
    }


    private void getCache() {
        try {
            cacheSize.set(CacheUtil.getTotalCacheSize(Utils.getContext()));
        } catch (Exception e) {
            XLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearAllData() {
        model.roomDeleteWords();
        model.roomDeleteTitleTeds();
        model.roomDeleteUserDatas();
        model.roomDeleteVoaTexts();
        getData();
    }

    private void clearData() {
        //剩余2个可以同步
        model.roomDeleteWords();
        model.roomDeleteUserDatas();
//        model.roomDeleteTitleTeds();
        AppApplication application = (AppApplication) getApplication();
        MusicService.MusicBinder musicBinder = application.getMusicBinder();
        if (musicBinder != null) {
            MusicService musicService = musicBinder.getService();
            musicService.mediaPlayer.setPlaybackSpeed(1.0f);  //恢复音乐速度
        }
        getData();
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(s -> {
                    getData();
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mSubscription);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        UserInfo userInfo = model.roomGetUserDataById(model.spGetUid());
        entity.set(userInfo);
        entity.notifyChange();
    }

    public void logout(String pwd) {
        showDialog("正在注销");
        model.logout(entity.get().getUsername(), pwd)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver() {
                    @Override
                    public void onResult(Object o) {
                        clearAllData();
                        dismissDialog();
                        ToastUtils.showShort("注销成功");
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissDialog();
                    }
                });
    }

    public void getNetDomain() {
        showDialog("正在更新");
//        uc.showDomainDialog.call();  //伪装请求位置信息
        model.updateDomain(Constants.CONFIG.DOMAIN, Constants.CONFIG.COM_DOMAIN)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<JsonObject>() {
                    @Override
                    public void onResult(JsonObject jo) {
                        JsonElement je = jo.get("result");
                        if (null == je) {
                            ToastUtils.showShort("更新失败");
                            return;
                        }
                        int result = je.getAsInt();
                        if (result == 201) {
                            ToastUtils.showShort("更新成功");
                            if (jo.get("A").getAsInt() == 1) {
                                String short1 = jo.get("short1").getAsString();
                                String short2 = jo.get("short2").getAsString();
                                updateDomain(short1, short2);
                            }
                        } else {
                            ToastUtils.showShort("已更新");
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });

    }

    public void updateDomain(String domain1, String doamin2) {
        Constants.CONFIG.updateDomain(domain1, doamin2);
        model.spSaveDomain(domain1, doamin2);
    }

    //打卡
    public void SignIn() {
        model.getMyTime(model.spGetUid(), "1", String.valueOf(DateUtil.getNowDay()))
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new SimpleDisposableObserver<StudyTime>() {
                    @Override
                    public void onResult(StudyTime st) {
                        final int time = Integer.parseInt(st.getTotalTime());
                        int signStudyTime = 3 * 60;
                        if (time < signStudyTime) {
                            ToastUtils.showShort("打卡失败，当前已学习%d秒，\n满%d分钟可打卡", time, signStudyTime / 60);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Constants.BUNDLE.KEY, st);
                            startContainerActivity(SignInFragment.class.getCanonicalName(), bundle);
                        }
                    }
                });
    }
}
