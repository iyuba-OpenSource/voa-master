package com.iyuba.voa.ui.main.home.detail;

import static com.iyuba.voa.utils.Constants.CONFIG.API_HOST_APPS;

import android.app.Application;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.module.toolbox.EnDecodeUtils;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlResponse;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.voa.utils.JsonUtil;
import com.iyuba.voa.utils.ThreadControl;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class DetailViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable UC = new UIChangeObservable();

    private int pagerIndex;
    public ObservableBoolean isCollect = new ObservableBoolean(false);
    public ObservableBoolean isDownload = new ObservableBoolean(false);

    //ViewPager切换监听
    public BindingCommand<Integer> onPageSelectedCommand = new BindingCommand<>(index -> {
        pagerIndex = index;
    });
    public TitleTed titleTed;

    public DetailViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<JsonObject> loadDataSuccess = new SingleLiveEvent<>();

        public SingleLiveEvent<String> pdfSuccessDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> collectSuccess = new SingleLiveEvent<>();
        public SingleLiveEvent<String> startDownloadArticle = new SingleLiveEvent<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        isCollect.set(model.roomGetTitleTedByIdFlag(titleTed.getVoaId(), 2) != null);
        isDownload.set(model.roomGetTitleTedByIdFlag(titleTed.getVoaId(), 3) != null);
    }

    public void saveVoaTexts(ArrayList<VoaText> voatexts) {
        model.roomAddVoaTexts(voatexts.toArray(new VoaText[voatexts.size()]));
    }

    public void saveTitleTed() {
        model.roomAddTitleTeds(titleTed);
    }

    public void loadData() {
        showLoadingDialog();
        model.textExamApi(titleTed.getVoaId())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new SimpleDisposableObserver<JsonObject>() {
                    @Override
                    public void onResult(JsonObject jo) {
                        dismissDialog();
                        JsonElement je = jo.get("voatext");
                        if (null == je) {
                            ToastUtils.showShort("无数据");
                            return;
                        }
                        List<VoaText> datas = JsonUtil.json2List(je.toString(), VoaText.class);
                        for (int i = 0; i < datas.size(); i++) {
                            VoaText voaText = datas.get(i);
                            voaText.setPlayCurrent(false);
                            voaText.setIndex(i + 1);
                            voaText.setVoaId(titleTed.getVoaId());
                        }
                        // TODO: 2022/8/8 有bug
                        List<VoaText> voaTexts = model.roomGetVoaTextByVoaId(titleTed.getVoaId());
                        if (voaTexts == null || voaTexts.size() == 0)
                            ThreadControl.EXECUTOR.submit(() -> {
                                model.roomAddVoaTexts(datas.toArray(new VoaText[datas.size()]));
                            });
                        UC.loadDataSuccess.setValue(jo);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissDialog();
                    }
                });
    }

    //菜单栏展开功能
    public void collect() {
        if (!checkIsLogin()) {
            return;
        }
        String type = "insert";
        if (isCollect.get()) {
            type = "del";
        }
        model.updateCollect(model.spGetUid(), titleTed.getVoaId(), type)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new Observer<XmlResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(XmlResponse xmlResponse) {
                        isCollect.set(!isCollect.get());
                        UC.collectSuccess.call();
//                        isCollect.notifyChange();
//                        TitleTed titleTed = new TitleTed();
//                        titleTed.setVoaId(voaid);
                        if (isCollect.get()) {
                                titleTed.setHotFlg(titleTed.getHotFlg() + ",2");
                            model.roomAddTitleTeds(titleTed);
                        } else {
                            titleTed.setHotFlg(titleTed.getHotFlg().replace("2", ""));
                            model.roomAddTitleTeds(titleTed);
                        }
                        ToastUtils.showShort((isCollect.get() ? "" : "取消") + "收藏成功");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void exportPdf(int type) {
        showLoadingDialog();
        model.exportPDF(titleTed.getVoaId(), type)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe((Consumer<JsonObject>) jsonObject -> {
                    dismissDialog();
                    if (jsonObject == null || jsonObject.get("path") == null) {
                        ToastUtils.showShort("导出失败");
                        return;
                    }
                    String path = jsonObject.get("path").getAsString();
                    if (StringUtils.isTrimEmpty(path)) {
                        ToastUtils.showShort("导出失败");
                        return;
                    }

                    path = API_HOST_APPS + "/iyuba" + path;
                    UC.pdfSuccessDialog.setValue(path);
                });
    }


    public void startInterfaceAddScore(int type, int strId) {
        String date = EnDecodeUtils.encode(DateUtil.getNowTime());
        String time = Base64.encodeToString(date.getBytes(), Base64.DEFAULT);
        if (TextUtils.isEmpty(model.spGetUid())) {
            return;
        }
        model.updateScore(model.spGetUid(), time, String.valueOf(strId))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) jo -> {
                    String result = jo.get("result").getAsString();
                    if (result.equals("200")) {
                        String addcredit = jo.get("addcredit").getAsString();
                        String totalcredit = jo.get("totalcredit").getAsString();
                        switch (type) {
                            case -1:
                                UC.startDownloadArticle.call();
                                break;
                            case -2:
                                ToastUtils.showShort("分享成功！+" + addcredit + "分！您当前总积分:" + totalcredit);
                                break;
                            default:
                                exportPdf(type);
                                break;
                        }
                    } else if (result.equals("201")) {
                        ToastUtils.showShort(jo.get("message").getAsString());
                    } else {
                        ToastUtils.showShort("积分剩余不足");
                    }
                });
    }

}