package com.iyuba.voa.ui.main.home.detail.content;

import static com.iyuba.voa.app.service.MusicService.CLOSE;
import static com.iyuba.voa.app.service.MusicService.NEXT;
import static com.iyuba.voa.app.service.MusicService.PAUSE;
import static com.iyuba.voa.app.service.MusicService.PLAY;
import static com.iyuba.voa.app.service.MusicService.PREV;
import static com.iyuba.voa.app.service.MusicService.PROGRESS;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.elvishew.xlog.XLog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.databinding.DialogWordBinding;
import com.iyuba.voa.databinding.FragmentDetailContentBinding;
import com.iyuba.voa.databinding.ViewContentPlayerBinding;
import com.iyuba.voa.ui.base.BaseTitleFragment;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.widget.TitlePopupDialog;
import com.iyuba.voa.utils.AdTimeCheck;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.widget.popmenu.ActionItem;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.utils.MaterialDialogUtil;
import me.goldze.mvvmhabit.utils.RxTimer;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class ContentFragment extends BaseTitleFragment<FragmentDetailContentBinding, ContentViewModel> {

    private static Disposable mDisposable;
    private ExoPlayer mPlayer;
    private ExoPlayer playerWord;
    private MenuItem menuItem;
    private Player.Listener playerListener;
    private TitleTed titleTed;
    private int clickPos;
    private TitlePopupDialog titlePopupDialog;
    private String adType;

    public ContentFragment() {
    }

    @Override
    public void initData() {
        super.initData();

        Bundle extras = getArguments();
        titleTed = extras.getParcelable(Constants.BUNDLE.KEY);
        clickPos = extras.getInt(Constants.BUNDLE.KEY_0);  //暂时没用到
        if (!TextUtils.isEmpty(titleTed.getVoaId())) {
            viewModel.voaid = titleTed.getVoaId();
//            titleTed = viewModel.loadTitleTed();
            viewModel.loadData();
            viewModel.getMessageLiu();
        }

        AppApplication application = (AppApplication) getActivity().getApplication();
        viewModel.musicService = application.getMusicBinder().getService();
        mPlayer = viewModel.musicService.mediaPlayer;
        mPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);  //单曲循环
//        mPlayer = new ExoPlayer.Builder(getContext()).build();

        binding.player.setPlayer(mPlayer);

        ViewContentPlayerBinding playerBinding = DataBindingUtil.bind(binding.player.getChildAt(0));
        playerBinding.setVariable(BR.viewModel, viewModel);
        //设置之后在一些双向绑定的场景下是没有用的，还需要调用viewStubBinding.lifecycleOwner = this绑定生命周期才能生效。踩坑记录下
        playerBinding.setLifecycleOwner(this);
      /*  if (NetworkUtil.isNetworkAvailable(getContext())) {
            MediaItem mediaItem = MediaItem.fromUri(titleTed.getSound());
            mPlayer.setMediaItem(mediaItem);
        } else {
            MediaItem mediaItem = MediaItem.fromUri(viewModel.loadMusic().getSound());  //本地音频
            mPlayer.setMediaItem(mediaItem);
        }*/
        playerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                XLog.i("ContentFragment-onIsPlayingChanged-" + isPlaying);
                if (!isPlaying) {  //暂停
                    viewModel.endTime = DateUtil.getNowTime();
                    viewModel.uploadRecord(0);
                    stopPlayerListener();
                    TitleTed titleTed0 = viewModel.loadTitleTed();
                    long lastTime = titleTed0 == null ? 0 : titleTed0.getPlayTime();
                    XLog.i("当前播放位置" + mPlayer.getCurrentPosition() + "----上次播放位置" + lastTime);
                    if (mPlayer.getCurrentPosition() > lastTime) {  //已播放位置大于保存的位置 则更新
//                        viewModel.updateTotalPosition(titleTed, (long) (viewModel.observableList.get(viewModel.observableList.size() - 1).entity.get().getEndTiming() * 1000));
                        viewModel.updatePlayPosition(ContentFragment.this.titleTed, mPlayer.getCurrentPosition());
                    }
                } else {
                    startPlayerProcessListener();
                }
                viewModel.musicService.updatePlayAndPause();
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            @Override
            public void onEvents(Player player, Player.Events events) {
                XLog.i("ContentFragment-onEvents-" + events);
                Player.Listener.super.onEvents(player, events);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                XLog.i("ContentFragment-onPlaybackStateChanged-" + playbackState);
                switch (playbackState) {
                    case Player.STATE_READY:
                        viewModel.musicService.beginPlayTime = DateUtil.getNowTime();
                        viewModel.updateTotalPosition(titleTed, mPlayer.getDuration());
                        break;
                    case Player.STATE_ENDED:  //播放结束
                        viewModel.endTime = DateUtil.getNowTime();
                        viewModel.currentPlayWords = viewModel.observableList.size();
                        viewModel.updateHotFlag(titleTed, 4);  //更新为阅读完成状态
                        viewModel.uploadRecord(1);
                        viewModel.updatePlayPosition(titleTed, mPlayer.getDuration());
                        break;
                }
                Player.Listener.super.onPlaybackStateChanged(playbackState);
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                XLog.i("ContentFragment-onPositionDiscontinuity-" + reason + "-" + oldPosition + "--" + newPosition);
                if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
                    viewModel.endTime = DateUtil.getNowTime();
                    viewModel.currentPlayWords = viewModel.observableList.size();
                    viewModel.updateHotFlag(titleTed, 4);  //更新为阅读完成状态
                    viewModel.uploadRecord(1);
                    viewModel.updatePlayPosition(titleTed, mPlayer.getDuration());
                }
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
            }
        };


        mPlayer.addListener(playerListener);
        viewModel.musicService.play(titleTed);
        viewModel.setServiceData();
     /*   mPlayer.prepare();
        mPlayer.play();*/
        binding.ivCloseAd.setOnClickListener(view -> binding.youdaoAd.setVisibility(View.GONE));
        binding.youdaoAd.setOnClickListener(view -> {
            if (!adType.equals("web"))
                return;
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, Utils.getContext().getString(R.string.app_name));
            bundle.putString(Constants.BUNDLE.KEY_0, viewModel.entity.get().getStartuppicUrl());
            startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
        });
        startPlayerProcessListener();


        setSpeed(playerBinding);

        setPlayRewind(playerBinding);
        setPlayForward(playerBinding);

//        initBottomDialog();
    }

    private void initBottomDialog() {
        titlePopupDialog = new TitlePopupDialog(getActivity());
        titlePopupDialog.addAction(new ActionItem(getActivity(), "倍速", R.drawable.vector_drawable_about));//
        titlePopupDialog.addAction(new ActionItem(getActivity(), "定时停止", R.drawable.vector_drawable_about));//headnews_pdf

        titlePopupDialog.setItemOnClickListener((item, position) -> {
            switch (position) {
                case 0:
                    break;
                case 1:
                    break;
            }
        });
    }

    private void setPlayForward(ViewContentPlayerBinding playerBinding) {
        playerBinding.playerForward.setOnClickListener(view -> {  //快进
            ObservableList<ContentItemViewModel> observableList = viewModel.observableList;
            for (int i = 0; i < observableList.size(); i++) {
                ContentItemViewModel contentItemViewModel = observableList.get(i);
                VoaText voaText = contentItemViewModel.entity.get();
                if (mPlayer == null) {
                    return;
                }
                double time = mPlayer.getCurrentPosition() / 1000.0;
                if (voaText.getTiming() <= time && time <= voaText.getEndTiming()) {  //落入播放区间
                    if (i + 1 < observableList.size()) {
                        voaText.setPlayCurrent(false);    //操作的数据会导致其他fragment 共用的改变
                        contentItemViewModel.entity.notifyChange();
                        ContentItemViewModel contentItemViewModel1 = observableList.get(i + 1);
                        VoaText voaText1 = contentItemViewModel1.entity.get();
                        voaText1.setPlayCurrent(true);
                        mPlayer.seekTo((long) (voaText1.getTiming() * 1000));
                        binding.rlDetailList.smoothScrollToPosition(i + 1);
                        break;
                    }
                } else voaText.setPlayCurrent(false);
                contentItemViewModel.entity.notifyChange();
            }
        });
    }

    private void setPlayRewind(ViewContentPlayerBinding playerBinding) {
        playerBinding.playerRewind.setOnClickListener(view -> {   //快退
            ObservableList<ContentItemViewModel> observableList = viewModel.observableList;
            for (int i = 0; i < observableList.size(); i++) {
                ContentItemViewModel contentItemViewModel = observableList.get(i);
                VoaText voaText = contentItemViewModel.entity.get();
                if (mPlayer == null) {
                    return;
                }
                double time = mPlayer.getCurrentPosition() / 1000.0;
                if (voaText.getTiming() <= time && time <= voaText.getEndTiming()) {  //落入播放区间
                    if (i - 1 >= 0) {
                        voaText.setPlayCurrent(false);
                        contentItemViewModel.entity.notifyChange();
                        ContentItemViewModel contentItemViewModel1 = observableList.get(i - 1);   //退一个
                        VoaText voaText1 = contentItemViewModel1.entity.get();
                        voaText1.setPlayCurrent(true);
                        mPlayer.seekTo((long) (voaText1.getTiming() * 1000));
                        binding.rlDetailList.smoothScrollToPosition(i - 1);
                        break;
                    }
                } else voaText.setPlayCurrent(false);

                contentItemViewModel.entity.notifyChange();
            }
        });
    }

    private void setSpeed(ViewContentPlayerBinding playerBinding) {
        playerBinding.playerSettings.setOnClickListener(view -> {
//            titlePopupDialog.show();
            if (!viewModel.checkIsVIP("该功能需要VIP权限, 是否开通vip?")) {
                return;
            }

            List<String> list = new ArrayList<>();
            for (int i = 6; i <= 20; i += 2) {
                list.add("x" + (i / 10.0));
            }
            MaterialDialogUtil.showListDialog((AppCompatActivity) getActivity(), "倍速播放", list, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                    viewModel.obSpeed.set(text.toString());
                    mPlayer.setPlaybackSpeed((float) Double.parseDouble(text.subSequence(1, text.length()).toString()));
                    ToastUtils.showShort("设置成功");
                }
            });
        });
    }

    //监听播放进度 字幕跟随滚动
    private void startPlayerProcessListener() {
        stopPlayerListener();
        XLog.i("开始监听");
        Observable.interval(500, 400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        ObservableList<ContentItemViewModel> observableList = viewModel.observableList;
                        double time = mPlayer.getCurrentPosition() / 1000.0;
                        XLog.i("监听进度" + time);

                        for (int i = 0; i < observableList.size(); i++) {
                            ContentItemViewModel contentItemViewModel = observableList.get(i);
                            VoaText voaText = contentItemViewModel.entity.get();
                            if (mPlayer == null) {
                                return;
                            }
                            if (voaText.getTiming() <= time && time <= voaText.getEndTiming()) {    //改变是activity 的数据
                                voaText.setPlayCurrent(true);
                                viewModel.currentPlayPara = i + 1;
//                    binding.rlDetailList. scrollToPosition(i);
                                binding.rlDetailList.smoothScrollToPosition(i);
                   /* LinearLayoutManager mLayoutManager =  (LinearLayoutManager) binding.rlDetailList.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(i, 0);*/   //位置置顶
                            } else voaText.setPlayCurrent(false);

                            contentItemViewModel.entity.notifyChange();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void stopPlayerListener() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            XLog.i("停止监听");
            mDisposable.dispose();
            mDisposable = null;
        }
    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ContentViewModel initViewModel() {

        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(ContentViewModel.class);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.UC.setPlayerProcess.observe(this, voaText -> mPlayer.seekTo((long) (voaText.getTiming() * 1000)));
        viewModel.UC.playerListener.observe(this, newState -> {
            XLog.i("滑动" + newState);
            switch (newState) {
                case 0:
                    RxTimer.timer(1000, number -> startPlayerProcessListener());  //滑动取词，延迟1s 防止返回过快
                    break;
                case 1:
                case 2:
                    stopPlayerListener();
                    break;
            }
        });

        viewModel.UC.showWordDialog.observe(this, xmlWord -> showWordDialog(xmlWord));
        viewModel.UC.serviceUpdate.observe(this, state -> {
            XLog.i(state);
            switch (state) {
                case PLAY:
                    XLog.d(state);
                    break;
                case PAUSE:
                case CLOSE:
//                            btnPlay.setIcon(getDrawable(R.mipmap.icon_play));
//                            btnPlay.setIconTint(getColorStateList(R.color.white));
//                            changeUI(musicService.getPlayPosition());
                    break;
                case PREV:
                    XLog.d("上一曲");
//                            changeUI(musicService.getPlayPosition());
                    break;
                case NEXT:
                    XLog.d("下一曲");
//                            changeUI(musicService.getPlayPosition());
                    break;
                case PROGRESS:
                    //播放进度发生改变时,只改变进度，不改变其他
                    break;
                default:
                    break;
            }
//                ToastUtils.showShort("服务返回" + state);
        });

        viewModel.UC.showAd.observe(this, data -> {
            adType = data.getType();
            switch (adType) {
                case "youdao":
                    initAd();
                    break;
                case "web":
                    binding.youdaoAd.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(data.getStartuppic())
                            .into(binding.ivAd);
                    break;
                default:
                    initAd();
                    break;
            }
        });
    }

   /* @Override
    public void onPause() {
        super.onPause();
        mPlayer.pause();
    }*/

    private void showWordDialog(XmlWord xmlWord) {
        playerWord = new ExoPlayer.Builder(getContext()).build();
        xmlWord.setCollect(viewModel.roomGetWord(xmlWord.getKey()) != null);

        DialogWordBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_word, null, false);
        ObservableField<XmlWord> field = new ObservableField<>();
        field.set(xmlWord);
        binding.setWordData(field);
        binding.tvPlay.setOnClickListener(view -> {
            if (TextUtils.isEmpty(xmlWord.getAudio())) {
                return;
            }
            mPlayer.pause();
            MediaItem mediaItem = MediaItem.fromUri(xmlWord.getAudio());
            playerWord.setMediaItem(mediaItem);
            playerWord.prepare();
            playerWord.play();
        });
        binding.ivCollectWord.setOnClickListener(view -> viewModel.updateWordCollect(field));
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(xmlWord.getKey())
                .customView(binding.getRoot(), true)
                .negativeText("取消")
                .cancelListener(dialogInterface -> {
                    viewModel.UC.dismissWordDialog.setValue(xmlWord);
                    if (null != playerWord) {
                        playerWord.stop();
                        playerWord.release();
                        playerWord = null;
                    }
                })
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();
        binding.tvPlay.callOnClick();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    // TODO: 2022/4/14
    //废弃 具体详情看viewpager2集成
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (null == mPlayer)
            return;
        if (isVisibleToUser) {
            mPlayer.play();
            startPlayerProcessListener();
        } else {
            mPlayer.pause();
            stopPlayerListener();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayerListener();
        if (null != mPlayer) {
            mPlayer.removeListener(playerListener);
        }
     /*   if (null != mPlayer) {
            mPlayer.stop();
            stopPlayerListener();
            mPlayer.release();
            mPlayer.removeListener(playerListener);
            mPlayer = null;
        }*/

    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_detail_content;
    }


    private void initAd() {
        XLog.i("getData: 初始化有道广告");
        //todo 有道广告不成功
        if (!viewModel.checkIsVIP(null) && AdTimeCheck.setAd()) {
            YouDaoNative youdaoNative = new YouDaoNative(getActivity(), "230d59b7c0a808d01b7041c2d127da95",
                    new YouDaoNative.YouDaoNativeNetworkListener() {
                        @Override
                        public void onNativeLoad(final NativeResponse nativeResponse) {
//                            binding.youdaoAd.setVisibility(View.VISIBLE);
                            List<String> imageUrls = new ArrayList<>();
                            imageUrls.add(nativeResponse.getMainImageUrl());
                            binding.youdaoAd.setOnClickListener(v -> nativeResponse.handleClick(binding.youdaoAd));
                            ImageService.get(getActivity(), imageUrls, new ImageService.ImageServiceListener() {
                                @TargetApi(Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(final Map<String, Bitmap> bitmaps) {
                                    if (nativeResponse.getMainImageUrl() != null) {
                                        Bitmap bitMap = bitmaps
                                                .get(nativeResponse.getMainImageUrl());
                                        if (bitMap != null) {
                                            binding.ivAd.setImageBitmap(bitMap);
                                            binding.ivAd.setVisibility(View.VISIBLE);
                                            nativeResponse.recordImpression(binding.ivAd);
                                        }
                                    }
                                }

                                @Override
                                public void onFail() {
                                    binding.youdaoAd.setVisibility(View.GONE);
                                }
                            });
                            binding.youdaoAd.setVisibility(View.VISIBLE);
                      /*      if (jsoad.getVisibility() == View.VISIBLE)
                                jsoad.setVisibility(View.GONE);*/
                        }

                        @Override
                        public void onNativeFail(NativeErrorCode nativeErrorCode) {
                            binding.youdaoAd.setVisibility(View.GONE);
                            Log.e("onNativeFail", "onNativeFail" + nativeErrorCode.toString());
                        }
                    });


            RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder().build();
            youdaoNative.makeRequest(requestParameters);
        }

        Log.d("测试", "getData: 有道广告末尾");
    }


}