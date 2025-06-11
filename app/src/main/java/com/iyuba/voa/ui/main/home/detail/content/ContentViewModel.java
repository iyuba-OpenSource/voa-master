package com.iyuba.voa.ui.main.home.detail.content;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.data.entity.Advertising;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.base.viewadapter.ContentDetailRecycleViewAdapter;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.JsonUtil;
import com.iyuba.voa.utils.ThreadControl;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class ContentViewModel extends BaseTitleViewModel<AppRepository> {
    public MusicService musicService;

    public ObservableField<Advertising> entity = new ObservableField<>();

    public ObservableField<String> obSpeed = new ObservableField<>("x1.0");
    public ObservableBoolean isShowCn = new ObservableBoolean(true);
    public UIChangeObservable UC = new UIChangeObservable();


    public ObservableList<ContentItemViewModel> observableList = new ObservableArrayList<>();
    public ContentDetailRecycleViewAdapter adapter = new ContentDetailRecycleViewAdapter(this);

    public ItemBinding<ContentItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_content);
    public String voaid;
    public String endTime;
    public int currentPlayPara;
    public int currentPlayWords;
    public int totalWordNum;
    private Disposable mNotificationSubscription;  //通知栏控制activity UI

    public ContentViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public BindingCommand<Integer> onScrollStateChangedCommand = new BindingCommand<Integer>(integer -> {
        UC.playerListener.setValue(integer);
    });
    public BindingCommand clickChangeEn = new BindingCommand(() -> {
        isShowCn.set(!isShowCn.get());
        for (ContentItemViewModel contentItemViewModel : observableList) {
            VoaText voaText = contentItemViewModel.entity.get();
            voaText.setShowCn(isShowCn.get());
            contentItemViewModel.entity.notifyChange();
        }
        isShowCn.notifyChange();
    });
    public BindingCommand clickAD = new BindingCommand(() -> {
        if (!entity.get().getType().equals("web"))
            return;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE.KEY, Utils.getContext().getString(R.string.app_name));
        bundle.putString(Constants.BUNDLE.KEY_0, entity.get().getStartuppicUrl());
        startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
    });
    public BindingCommand clickCloseAD = new BindingCommand(() -> {
        entity.get().setType("");
        entity.notifyChange();
    });


    public class UIChangeObservable {
        public SingleLiveEvent<VoaText> setPlayerProcess = new SingleLiveEvent<>();
        public SingleLiveEvent<Integer> playerListener = new SingleLiveEvent<>();
        public SingleLiveEvent<String> pdfSuccessDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> collectSuccess = new SingleLiveEvent<>();
        public SingleLiveEvent<XmlWord> showWordDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<XmlWord> dismissWordDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> serviceUpdate = new SingleLiveEvent<>();

        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();
        public SingleLiveEvent<Advertising> showAd = new SingleLiveEvent<>();


    }

    public TitleTed loadTitleTed() {
        return model.roomGetTitleTedById(voaid);
    }

    public void loadData() {
        ThreadControl.EXECUTOR.submit(() -> {
            int words = 0;
            List<VoaText> voaTexts = model.roomGetVoaTextByVoaId(voaid);
            if (observableList.size() == 0) {
                for (VoaText data :
                        voaTexts) {
                    totalWordNum += data.getWordNum();
                    data.setWordRecord(words += data.getWordNum());
                    ThreadControl.runUi(() -> observableList.add(new ContentItemViewModel(ContentViewModel.this, data)));
                }
            }
        });

    }

    public void searchWord(String word) {
        model.apiWord(word)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<XmlWord>) xmlWord -> {
                    if (xmlWord == null || "0".equals(xmlWord.getResult())) {
                        ToastUtils.showShort("暂无该单词的详细信息");
                        return;
                    }
                    xmlWord.setKey(word);
                    UC.showWordDialog.setValue(xmlWord);
                });
    }

    public void updateHotFlag(TitleTed titleTed, int flag) {
        titleTed.setHotFlg(titleTed.getHotFlg() + "," + flag);
        model.roomAddTitleTeds(titleTed);
    }

    public void updatePlayPosition(TitleTed titleTed, long time) {
        titleTed.setPlayTime(time);
        model.roomAddTitleTeds(titleTed);
    }

    public void updateTotalPosition(TitleTed titleTed, long time) {
        TitleTed titleTed2 = model.roomGetTitleTedById(voaid);
        if (titleTed2 == null || titleTed2.getTotalTime() == 0) {
            titleTed.setTotalTime(time);
            model.roomAddTitleTeds(titleTed);
        }
    }

    public void uploadRecord(int isEnd) {
        String uid = model.spGetUid();
        if (TextUtils.isEmpty(uid)) {
            XLog.i("未登录，听力记录不上传");
            return;
        }
        if (currentPlayPara == 0) {
            XLog.i("当前未播放段落");
            return;
        }
        int words = isEnd == 1 ? totalWordNum : observableList.get(currentPlayPara - 1).entity.get().getWordRecord();
        model.updateStudyRecordNew(musicService.beginPlayTime, endTime, voaid, String.valueOf(currentPlayPara), String.valueOf(words), uid, isEnd)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jo -> {
                    if (jo == null || null == jo.get("result")) {
                        ToastUtils.showShort("上传失败");
                        return;
                    }
                    if (jo.get("result").getAsString().equals("1")) {
                        XLog.i("听力记录上传成功，积分=" + jo.get("jifen").getAsString());
                        int jifen = jo.get("jifen").getAsInt();
                        if (jifen > 0) {
                            ToastUtils.showShort("恭喜您获得了%s积分", jifen);
                        }
                    }
                });
    }

    public void updateWordCollect(ObservableField<XmlWord> word) {
        if (!checkIsLogin()) {
            return;
        }
        final XmlWord xmlWord = word.get();
        String type = "insert";

        if (xmlWord.isCollect()) {
            type = "delete";
        }
        model.updateWord(model.spGetUid(), xmlWord.getKey(), type)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe((Consumer<JsonObject>) jsonObject -> {
                    int result = jsonObject.get("result").getAsInt();
                    if (result == 1) {
                        xmlWord.setCollect(!xmlWord.isCollect());
                        word.notifyChange();
                        if (xmlWord.isCollect()) {
                            model.roomAddWords(xmlWord);
                        } else {
                            model.roomDeleteWordByWord(xmlWord.getKey());
                        }
                        ToastUtils.showShort((xmlWord.isCollect() ? "" : "取消") + "收藏成功");
                    }
                });
    }

    public XmlWord roomGetWord(String word) {
        return model.roomGetWordByWord(word);
    }

    public void setServiceData() {
        MusicService.MusicBinder musicBinder = ((AppApplication) getApplication()).getMusicBinder();
        if (musicBinder != null) {
            musicBinder.getService().setNoticeIntent();
        }
    }

    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mNotificationSubscription = RxBus.getDefault().toObservable(String.class)
                .subscribe(state -> {
//                    loadVIPInfo();
                    UC.serviceUpdate.setValue(state);
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mNotificationSubscription);
    }

    @Override
    public void removeRxBus() {
        super.removeRxBus();
        RxSubscriptions.remove(mNotificationSubscription);
    }

    public void getMessageLiu() {
        model.getAdEntryAll("4", model.spGetUid())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonArray>) array -> {
                    if (array.size() > 0) {
                        JsonObject jo = array.get(0).getAsJsonObject();
                        String result = jo.get("result").getAsString();
                        if (result.equals("1")) {
                            Advertising data = JsonUtil.json2Entity(jo.get("data").toString(), Advertising.class);
                            UC.showAd.setValue(data);
                            entity.set(data);
                            entity.notifyChange();
                        }
                    }
                });
    }


}