package com.iyuba.voa.ui.main.home.detail.ranking.detail;

import android.app.Application;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.elvishew.xlog.XLog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.module.toolbox.EnDecodeUtils;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.RankDetail;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.voa.utils.JsonUtil;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
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
public class RankDetailViewModel extends BaseTitleViewModel<AppRepository> {
    final int pageNum = 20;
    public String headUrl;
    public ExoPlayer mPlayer;
    int mMorePageNumber = 1;
    public UIChangeObservable UC = new UIChangeObservable();


    public ObservableList<RankDetailItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<RankDetailItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_rank_detail);
    public String voaid;
    private String uid;
    private Player.Listener playerListener;

    public RankDetailViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);

    }

    public BindingCommand<Void> refreshCommand = new BindingCommand(() -> {
        loadData(voaid, uid);
    });

    public BindingCommand<Void> loadMoreCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
        }
    });

    public BindingCommand<Integer> onScrollStateChangedCommand = new BindingCommand<Integer>(integer -> {
        UC.playerListener.setValue(integer);
    });

    public class UIChangeObservable {
        public SingleLiveEvent<VoaText> setPlayerProcess = new SingleLiveEvent<>();
        public SingleLiveEvent<Integer> playerListener = new SingleLiveEvent<>();

        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();
        public SingleLiveEvent<RankDetailItemViewModel> showShareDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<String> loadDataSuccess = new SingleLiveEvent<>();

    }


    public void loadData(String voaid, String uid) {
        this.voaid = voaid;
        this.uid = uid;
        //获取评测列表
        model.getTesting(voaid, uid)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) data -> {
                    JsonElement resultCode = data.get("result");
                    if (resultCode != null && resultCode.getAsBoolean()) {
                        List<RankDetail> datas = JsonUtil.json2List(data.get("data").toString(), RankDetail.class);
                        observableList.clear();
                        for (RankDetail rank : datas) {
                            rank.setHeadUrl(headUrl);
                            observableList.add(new RankDetailItemViewModel(this, rank));
                        }
                    } else {
                        ToastUtils.showShort("加载失败");
                    }
                    UC.finishRefreshing.call();
                });


        playerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (!isPlaying) {
                    for (RankDetailItemViewModel testingItemViewModel : observableList) {
                        ObservableField<RankDetail> entity = testingItemViewModel.entity;
                        entity.get().setPlaying(false);
                        entity.notifyChange();
                    }
                }
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                XLog.i("onPlaybackStateChanged-" + playbackState);
                switch (playbackState) {
                    case Player.STATE_ENDED:  //播放结束
                        for (RankDetailItemViewModel itemViewModel : observableList) {
                            ObservableField<RankDetail> entity = itemViewModel.entity;
                            entity.get().setPlaying(false);
                            itemViewModel.playPosition = 0;
                            mPlayer.seekTo(0);
                            mPlayer.stop();
                            if (itemViewModel.animationDrawable != null)
                                itemViewModel.animationDrawable.stop();
                            entity.notifyChange();
                        }
                        break;
                }
                Player.Listener.super.onPlaybackStateChanged(playbackState);
            }
        };
        mPlayer.addListener(playerListener);
    }

    public void startInterfaceAddScore(int strId) {
        if (!checkIsLoginNoBreak()) {
            ToastUtils.showShort("登陆后分享可获取积分!");
            return;
        }
        String result = EnDecodeUtils.encode(DateUtil.getNowTime());
        String time = Base64.encodeToString(result.getBytes(), Base64.DEFAULT);
        model.updateScore(model.spGetUid(), time, String.valueOf(strId))
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) jo -> {
                    if (jo.get("result").getAsString().equals("200")) {
                        String addcredit = jo.get("addcredit").getAsString();
                        String totalcredit = jo.get("totalcredit").getAsString();
                        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数
                        float addmoney = Float.parseFloat(addcredit);
                        float allmoney = Float.parseFloat(totalcredit);
                        String f1 = df.format(addmoney * 0.01);
                        String f2 = df.format(allmoney * 0.01);
//                        String msg = "分享成功," + " 获得" + f1 + "元,总计: " + f2 + "元," + "满十元可在\"爱语吧\"公众号提现";
                        String msg = "分享成功," + " 获得" + f1 + "元,总计: " + f2 + "元," + "满十元可联系客服提现";
                        UC.loadDataSuccess.setValue(msg);
                    } else {
                        String t = "今日已分享，重复分享不能再次获取红包或积分哦！";
                        UC.loadDataSuccess.setValue(t);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mPlayer) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer.removeListener(playerListener);
            mPlayer = null;
        }
    }
}