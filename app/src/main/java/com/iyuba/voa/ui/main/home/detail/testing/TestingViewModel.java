package com.iyuba.voa.ui.main.home.detail.testing;

import static android.graphics.Color.argb;

import android.app.Application;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;

import com.elvishew.xlog.XLog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.data.entity.PronCorrect;
import com.iyuba.voa.data.entity.VoaScore;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.data.http.observer.ApiDisposableObserver;
import com.iyuba.voa.data.http.observer.SimpleDisposableObserver;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.ui.widget.SelectWordTextView;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.ThreadControl;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.RxTimer;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
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
public class TestingViewModel extends BaseTitleViewModel<AppRepository> {
    //标识文章状态 0：未合成/追加 ，1 已合成-暂停状态，2合成后播放 3----
    public ObservableInt contentStatus = new ObservableInt(0);

    public ObservableInt mergeScore = new ObservableInt(0);
    public ObservableBoolean isCycle = new ObservableBoolean(false);

    public UIChangeObservable UC = new UIChangeObservable();

    public ExoPlayer mPlayer;


    public ObservableList<TestingItemViewModel> observableList = new ObservableArrayList<>();

    public ItemBinding<TestingItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_rv_testing);
    public String voaid;
    public MediaItem.Builder mediaItemBuilder;
    private Player.Listener playerListener;
    private String mergeUrl;

    public TestingViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public BindingCommand<Integer> onScrollStateChangedCommand = new BindingCommand<Integer>(integer -> {
        UC.playerListener.setValue(integer);
    });

    public BindingCommand clickMerge = new BindingCommand(() -> {
        switch (contentStatus.get()) {
            case 0: //未合成
                mergeTesting();
                break;
            case 1: //暂停状态
                UC.closeTimeBar.setValue(false);
//                mPlayer.setMediaItem(MediaItem.fromUri(Constants.CONFIG.userSpeechIp + mergeUrl));
                mPlayer.prepare();
                mPlayer.play();
                contentStatus.set(2);
                break;
            case 2: //播放
                UC.closeTimeBar.setValue(false);
                mPlayer.pause();
                contentStatus.set(1);
                break;

        }

    });
    public BindingCommand clickRelease = new BindingCommand(() -> {
        // TODO: 2022/5/24 databinding有问题 事件已在activity处理
    });


    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> closeTimeBar = new SingleLiveEvent<>();
        public SingleLiveEvent<Integer> playerListener = new SingleLiveEvent<>();
        public SingleLiveEvent<ImageView> audioRecord = new SingleLiveEvent<>();
        public SingleLiveEvent<TestingItemViewModel> correctDialog = new SingleLiveEvent<>();
        public SingleLiveEvent<TestingItemViewModel> breakCourse = new SingleLiveEvent<>();
        public SingleLiveEvent<XmlWord> showWordDialog = new SingleLiveEvent<>();

    }


    public void loadData(String sound) {
        mediaItemBuilder = new MediaItem.Builder()
                .setUri(sound);
        ThreadControl.EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                List<VoaText> voaTexts = model.roomGetVoaTextByVoaId(voaid);
                for (int i = 0; i < voaTexts.size(); i++) {
                    VoaText voaText = voaTexts.get(i);
                    voaText.setTotalPara(voaTexts.size());
                    VoaText voaTextRoom = model.roomGetVoaTextByVoaIdAndIndex(voaid, voaText.getIndex());
                    if (null != voaTextRoom) {  //已经评测过
                        voaText.setAudioPath(voaTextRoom.getAudioPath());
                        voaText.setAudioUrl(voaTextRoom.getAudioUrl());
                        voaText.setScore(voaTextRoom.getScore());
                        voaText.setWordScores(voaTextRoom.getWordScores());
                    }
                    ThreadControl.runUi(() -> observableList.add(new TestingItemViewModel(TestingViewModel.this, voaText)));
                }
            }
        });

        playerListener = new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (!isPlaying) {
                    stopPlayerListener();
                    for (TestingItemViewModel testingItemViewModel : observableList) {
                        ObservableField<VoaText> entity = testingItemViewModel.entity;
                        entity.get().setPlayCurrent(false);
                        entity.get().setPlayRec(false);
                        if (contentStatus.get() == 2)  //仅当为播放状态时
                            contentStatus.set(1);
                        //                        mPlayer.stop();
                        entity.notifyChange();
                    }
                }
                Player.Listener.super.onIsPlayingChanged(isPlaying);
            }

            /*      @Override
                  public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                      XLog.i("onMediaItemTransition" + reason);
                      Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                  }

                    @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                XLog.i("onPlayWhenReadyChanged-" + reason);
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);
            }

                @Override
                  public void onTimelineChanged(Timeline timeline, int reason) {
                      XLog.i("onTimelineChanged" + reason);
                      Player.Listener.super.onTimelineChanged(timeline, reason);
                  }

                  @Override
                  public void onIsLoadingChanged(boolean isLoading) {
                      XLog.i("onIsLoadingChanged" + isLoading);
                      Player.Listener.super.onIsLoadingChanged(isLoading);
                  }

                  @Override
                  public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
                      XLog.i("onPlaylistMetadataChanged-" + mediaMetadata.albumArtist);
                      Player.Listener.super.onPlaylistMetadataChanged(mediaMetadata);
                  }*/


            @Override
            public void onPlaybackStateChanged(int playbackState) {
                XLog.i("onPlaybackStateChanged-" + playbackState);
                switch (playbackState) {
                    case Player.STATE_READY:
                        for (TestingItemViewModel testingItemViewModel : observableList) {
                            ObservableField<VoaText> entity = testingItemViewModel.entity;
                            testingItemViewModel.playPosition.set(0);
                            testingItemViewModel.playVoicePosition.set(0);
                            if (entity.get().isPlayRec()) {
                                testingItemViewModel.playVoiceTotalTime.set(mPlayer.getDuration());  //stackoverflow 大神解决方法
                            }
                        }
                        break;
                    case Player.STATE_ENDED:  //播放结束
                        stopPlayerListener();
                        for (TestingItemViewModel testingItemViewModel : observableList) {
                            ObservableField<VoaText> entity = testingItemViewModel.entity;
                            entity.get().setPlayCurrent(false);
                            entity.get().setPlayRec(false);
                            if (contentStatus.get() == 2)  //仅当为播放状态时
                                contentStatus.set(1);
                            testingItemViewModel.playPosition.set(0);
                            testingItemViewModel.playVoicePosition.set(0);
                            mPlayer.seekTo(0);
                            mPlayer.stop();
                            entity.notifyChange();
                        }
                        break;
                }
                Player.Listener.super.onPlaybackStateChanged(playbackState);
            }

        };

        mPlayer.addListener(playerListener);
    }

    public void startTestingPlayerProcessListener(TestingItemViewModel testingItemViewModel) {
        stopPlayerListener();
        RxTimer.listener(200, 100, getLifecycleProvider(), number -> {   //延迟800ms监听
            ThreadControl.runUi(() -> {
//                XLog.i(mPlayer.getCurrentPosition());
                long currentPosition = 0;
                if (mPlayer != null)
                    currentPosition = mPlayer.getCurrentPosition();
                ObservableField<VoaText> entity = testingItemViewModel.entity;
                if (entity.get().isPlayCurrent()) {
                    testingItemViewModel.playPosition.set(currentPosition);
                } else if (entity.get().isPlayRec()) {
                    testingItemViewModel.playVoicePosition.set(currentPosition);
                }
                entity.notifyChange();
            });
        });
    }


    public void stopPlayerListener() {
        RxTimer.cancel();
    }


    public void showCorrect(VoaScore.Words words, int position) {
        model.correctPron(words.getContent(), words.getUserPron(), words.getPron())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe(new SimpleDisposableObserver<PronCorrect>() {
                    @Override
                    public void onResult(PronCorrect correct) {
                        if (correct == null) {
                            ToastUtils.showShort("暂无该单词的详细信息");
                            return;
                        }
                        TestingItemViewModel itemViewModel = observableList.get(position);
                        itemViewModel.wordEntity.set(correct);
                        itemViewModel.wordEntity.notifyChange();
                    }
                });
    }

    //评测
    public void uploadTesting(File file, VoaText voaText) {
        showDialog("评测中");
        if (TextUtils.isEmpty(voaText.getSelectWord())) {
            model.uploadTesting(file, model.spGetUid(), voaText.getSentence(), voaid,
                            voaText.getParaId(), voaText.getIdIndex())
                    .compose(RxUtils.schedulersTransformer())
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                    .compose(RxUtils.exceptionTransformer())
                    .subscribe(new ApiDisposableObserver<VoaScore>() {
                        @Override
                        public void onResult(VoaScore data) {
//                        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数
                            double value = Double.parseDouble(data.getTotalScore());
                            voaText.setScore(String.valueOf((int) (value * 20)));
                            voaText.setAudioUrl(data.getUrl());
                            voaText.setWordScores(data.getWords());
                            voaText.setPlayCurrent(false);
                            setSpannable(voaText);
                            observableList.get(voaText.getIndex() - 1).entity.notifyChange();
                            model.roomAddVoaTexts(voaText);
                            contentStatus.set(0);
                            dismissDialog();
                            ToastUtils.showShort("评测成功");
                        }
                    });
        } else {
            model.wordTesting(model.spGetUid(), file, voaText)
                    .compose(RxUtils.schedulersTransformer())
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                    .compose(RxUtils.exceptionTransformer())
                    .subscribe(new ApiDisposableObserver<VoaScore>() {
                        @Override
                        public void onResult(VoaScore data) {
//                            double value = Double.parseDouble(data.getTotalScore());
//                            voaText.setScore(String.valueOf((int) (value * 20)));
                            voaText.setAudioWordUrl(data.getUrl());
                            voaText.setWordScore(data.getScores());
                            voaText.setPlayCurrent(false);
                            observableList.get(voaText.getIndex() - 1).entity.notifyChange();
                            dismissDialog();
                            ToastUtils.showShort("单词评测成功");
                        }
                    });
        }
    }

    public int getTestingCount() {
        int count = model.roomGetVoaTestingCountByVoaId(voaid);
        XLog.i(voaid + "-评测句子数：" + count);
        return count;
    }

    //评测合成
    public void mergeTesting() {
        StringBuilder sb = new StringBuilder();
        double totalScore = 0.0;
        int length = 0;
        for (TestingItemViewModel testingItemViewModel : observableList) {
            VoaText voaText = testingItemViewModel.entity.get();
            if (!StringUtils.isTrimEmpty(voaText.getAudioUrl())) {
                totalScore += Double.parseDouble(voaText.getScore());
                length++;
                sb.append(voaText.getAudioUrl());
                sb.append(",");
            }
        }
        String audios = sb.length() > 0 ? sb.deleteCharAt(sb.length() - 1).toString() : sb.toString();
        if (length < 2) {
            ToastUtils.showShort("评测至少2句才可以合成");
            return;
        }
        final int finalLength = length;
        final double finalTotalScore = totalScore;
        model.audioMerge(audios)

                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) data -> {
                    JsonElement result1 = data.get("result");
                    if (null == result1) {
                        ToastUtils.showShort("合成失败");
                        return;
                    }
                    String result = result1.getAsString();
                    if (result.equals("1")) {
                        mergeUrl = data.get("URL").getAsString();
                        mPlayer.setMediaItem(MediaItem.fromUri(Constants.CONFIG.USER_SPEECH_IP + mergeUrl));
                        mPlayer.prepare();
                        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数
                        double value = Double.parseDouble(df.format(finalTotalScore / finalLength));
                        mergeScore.set((int) value);  //上面分已*20
                        contentStatus.set(1);
                        UC.closeTimeBar.setValue(false);
                        ToastUtils.showShort("合成成功");
                    } else {
                        ToastUtils.showShort(data.get("message").getAsString());
                    }
                });
    }

    public void releaseSingleTesting(VoaText voaText) {
        showDialog("发布中……");
        model.releaseSingleTesting(voaid, model.spGetUserName()
                        , model.spGetUid(), voaText)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) data -> {
                    if (null == data) {
                        ToastUtils.showShort("发布失败");
                        return;
                    }
                    JsonElement resultCode = data.get("ResultCode");
                    if (null == resultCode) {
                        ToastUtils.showShort("发布失败");
                        return;
                    }
                    if (resultCode.getAsInt() == 501) {
                        ToastUtils.showShort("发布成功");
                    }
                    dismissDialog();
                });
    }

    public void releaseTesting() {
        if (StringUtils.isTrimEmpty(mergeUrl)) {
            ToastUtils.showShort("尚未合成语音");
            return;
        }
        showDialog("发布中……");
        model.releaseTesting(voaid,
                        model.spGetUid(), mergeUrl, String.valueOf((int) mergeScore.get()))
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe(new SimpleDisposableObserver<JsonObject>() {
                    @Override
                    public void onResult(JsonObject data) {
                        if (data.get("ResultCode").getAsInt() == 501) {
                            ToastUtils.showShort("发布成功");
                        }
                        dismissDialog();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dismissDialog();
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


    public void setSpannable(VoaText data) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(data.getSentence().replace("‘", "'"));
        List<VoaScore.Words> wordList = data.getWordScores();
        int beginIndex = 0;
        for (int i = 0; i < wordList.size(); i++) {
            if (beginIndex > data.getSentence().length())
                break;
            VoaScore.Words word = wordList.get(i);
            if (Double.parseDouble(word.getScore()) < 2 && Double.parseDouble(word.getScore()) != -1) {
                int tempEndIndex = beginIndex + word.getContent().length();
                sb.setSpan(new ForegroundColorSpan(argb(255, 255, 0, 0)), beginIndex, tempEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // System.out.println("-------------------->设置红色" + beginIndex + "---" + tempEndIndex + "---" + word.getContent());
            } else if (Double.parseDouble(word.getScore()) > 3) {
                int tempEndIndex = beginIndex + word.getContent().length();
                sb.setSpan(new ForegroundColorSpan(argb(255, 6, 142, 0)), beginIndex, tempEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            beginIndex += word.getContent().length() + 1;
        }
        TextView textView = observableList.get(data.getIndex() - 1).textView;
        SelectWordTextView selectWordTextView = observableList.get(data.getIndex() - 1).selectWordTextView;
        if (textView != null) {
            textView.setText(sb);
        }
        if (selectWordTextView != null) {
            selectWordTextView.setText(sb);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mPlayer) {
            mPlayer.stop();
            mPlayer.release();
            stopPlayerListener();
            mPlayer.removeListener(playerListener);
            mPlayer = null;
        }
    }
}