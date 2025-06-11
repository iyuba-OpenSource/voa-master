package com.iyuba.voa.ui.main.home.detail.testing;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableLong;

import com.elvishew.xlog.XLog;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.voa.data.entity.PronCorrect;
import com.iyuba.voa.data.entity.VoaScore;
import com.iyuba.voa.data.entity.VoaText;
import com.iyuba.voa.ui.widget.SelectWordTextView;
import com.iyuba.voa.utils.Constants;

import java.util.Objects;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
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
public class TestingItemViewModel extends ItemViewModel<TestingViewModel> {
    public ObservableLong playTotalTime = new ObservableLong(0);

    public ObservableLong playPosition = new ObservableLong(0);

    public ObservableLong playVoiceTotalTime = new ObservableLong(0);
    public ObservableLong playVoicePosition = new ObservableLong(0);

    public TextView textView;// TODO: 2022/4/15 可优化binding CharSequence
    public SelectWordTextView selectWordTextView;
    public SelectWordTextView selectContentWordTextView;
    private MediaItem mediaItem;  //原音
    private MediaItem mediaItem2; //录音
    private ImageView imageView;

    public ObservableBoolean isClickItem = new ObservableBoolean(false);

    public ObservableField<VoaText> entity = new ObservableField<>();
    public ObservableField<VoaScore> wordScoreEntity = new ObservableField<>();
    public ObservableField<PronCorrect> wordEntity = new ObservableField<>();

    public BindingCommand<View> getRecordView = new BindingCommand<View>(view -> {
        imageView = (ImageView) view;
    });
    public BindingCommand<View> getTextView = new BindingCommand<>(view -> {
        textView = (TextView) view;
        if (entity.get().getWordScores() != null && entity.get().getWordScores().size() > 0) {
            viewModel.setSpannable(entity.get());   //必须在添加item之后
        }
        selectContentWordTextView = (SelectWordTextView) view;
        selectContentWordTextView.setOnClickWordListener(word -> {
//                clickItem.execute();
            viewModel.searchWord(word);
        });
    });
    public BindingCommand<View> getDialogSelectWordTextView = new BindingCommand<>(view -> {
        selectWordTextView = (SelectWordTextView) view;
        VoaText voaText = entity.get();
        if (null != entity.get().getWordScores() && entity.get().getWordScores().size() > 0) {
            viewModel.setSpannable(entity.get());   //必须在添加item之后
//            VoaScore.Words words = voaText.getWordScores().get(0);
            for (VoaScore.Words word : voaText.getWordScores()) {   //选中第一个读错的
                if (Double.parseDouble(word.getScore()) < 2) {
                    voaText.setSelectWord(word.getContent());
                    viewModel.showCorrect(word, entity.get().getIndex() - 1);
                    break;
                }
            }
        }
        selectWordTextView.setOnClickWordListener(word -> {
            voaText.setSelectWord(word);
            VoaScore.Words words = null;
            for (VoaScore.Words wordScore : voaText.getWordScores()) {
                // TODO: 2022/5/26 需要改可能
                if (word.equals(wordScore.getContent().replaceAll("\\p{P}", ""))) {
                    wordScore.setContent(word);
                    words = wordScore;
                    break;
                }
            }
            if (words != null) {
                double value = Double.parseDouble(words.getScore());
                voaText.setWordScore(String.valueOf((int) (value * 20)));
                entity.notifyChange();
                viewModel.showCorrect(words, entity.get().getIndex() - 1);
            } else {
                ToastUtils.showShort("暂无该单词的详细信息");
            }
        });
    });

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
//        if (viewModel.checkIsLoginNoBreak()) {
        for (TestingItemViewModel itemViewModel : viewModel.observableList) {
            itemViewModel.isClickItem.set(false);
        }
        viewModel.mPlayer.stop();
        playPosition.set(0);
        playVoicePosition.set(0);
        isClickItem.set(true);
    });

    //录音句子评测
    public BindingCommand<Void> clickRecord = new BindingCommand<>(() -> {
      /*  if (!viewModel.checkIsLogin()) {  //未登录可以评测
            return;
        }*/
        if (viewModel.getTestingCount() > 2 && !viewModel.checkIsVIP("非会员用户每一篇可以免费评测三句，是否开通会员解锁？")) {
            XLog.i("超过3句");
            return;
        }
        viewModel.UC.closeTimeBar.setValue(true);
        VoaText data = entity.get();
        data.setSelectWord("");  //区分句子、单词评测
        if (data.isRecording()) {
            data.setRecording(false);
            viewModel.UC.audioRecord.setValue(null);
        } else {
            data.setPlayCurrent(true);
            imageView.setTag(entity.get());
            viewModel.UC.audioRecord.setValue(imageView);
        }
    });

    //录音单词测
    public BindingCommand<Void> clickWordRecord = new BindingCommand<>(() -> {
        if (!viewModel.checkIsLogin()) {
            return;
        }
        if (viewModel.getTestingCount() > 2 && !viewModel.checkIsVIP("非会员用户每一篇可以免费评测三句，是否开通会员解锁？")) {
            XLog.i("超过3句");
            return;
        }
        viewModel.UC.closeTimeBar.setValue(true);
        VoaText data = entity.get();
        if (data.isRecording()) {
            data.setRecording(false);
            viewModel.UC.audioRecord.setValue(null);
        } else {
            data.setPlayCurrent(true);
            imageView.setTag(entity.get());
            viewModel.UC.audioRecord.setValue(imageView);
        }
    });

    public BindingCommand<Void> clickHorn = new BindingCommand<>(() -> {  //播放句子录音
        viewModel.UC.closeTimeBar.setValue(true);
        VoaText data = entity.get();
        data.setPlayCurrent(false);
        if (data.isPlayRec()) {
            data.setPlayRec(false);
            viewModel.mPlayer.pause();
            playVoicePosition.set(viewModel.mPlayer.getCurrentPosition());
            viewModel.stopPlayerListener();
        } else {
            if (!TextUtils.isEmpty(data.getAudioPath())) {
                mediaItem2 = MediaItem.fromUri(data.getAudioPath());
            } else if (!TextUtils.isEmpty(data.getAudioUrl())) {
                mediaItem2 = MediaItem.fromUri(Constants.CONFIG.USER_SPEECH_IP + data.getAudioUrl());
            } else {
                ToastUtils.showShort("无音频数据");
                return;
            }
            viewModel.mPlayer.pause();
            data.setPlayRec(true);

            viewModel.mPlayer.setMediaItem(mediaItem2, playVoicePosition.get());
            viewModel.startTestingPlayerProcessListener(TestingItemViewModel.this);
            viewModel.mPlayer.prepare();
            viewModel.mPlayer.play();

        }
        entity.notifyChange();
    });

    public BindingCommand<Void> clickPron = new BindingCommand<>(() -> {  //播放单词发音
        PronCorrect data = wordEntity.get();
        if (!TextUtils.isEmpty(data.getAudio())) {
            mediaItem2 = MediaItem.fromUri(data.getAudio());
        } else {
            ToastUtils.showShort("无音频数据");
            return;
        }
        viewModel.mPlayer.setMediaItem(mediaItem2, playVoicePosition.get());
//        viewModel.startTestingPlayerProcessListener(TestingItemViewModel.this);
        viewModel.mPlayer.prepare();
        viewModel.mPlayer.play();
    });
    public BindingCommand<Void> clickWordHorn = new BindingCommand<>(() -> {  //播放单词录音
        viewModel.UC.closeTimeBar.setValue(true);
        VoaText data = entity.get();
        data.setPlayCurrent(false);
        if (data.isPlayRec()) {
            data.setPlayRec(false);
            viewModel.mPlayer.pause();
            playVoicePosition.set(viewModel.mPlayer.getCurrentPosition());
            viewModel.stopPlayerListener();
        } else {
            if (!TextUtils.isEmpty(data.getAudioWordUrl())) {
                mediaItem2 = MediaItem.fromUri(Constants.CONFIG.USER_SPEECH_IP + data.getAudioWordUrl());
            } else {
                ToastUtils.showShort("无音频数据");
                return;
            }
            viewModel.mPlayer.pause();
            data.setPlayRec(true);

            viewModel.mPlayer.setMediaItem(mediaItem2, playVoicePosition.get());
            viewModel.startTestingPlayerProcessListener(TestingItemViewModel.this);
            viewModel.mPlayer.prepare();
            viewModel.mPlayer.play();

        }
        entity.notifyChange();
    });

    public BindingCommand<Void> clickSingleRelease = new BindingCommand<>(() -> {
        viewModel.releaseSingleTesting(entity.get());
    });
    public BindingCommand<Void> clickCorrect = new BindingCommand<>(() -> {  //纠音
        viewModel.UC.correctDialog.setValue(this);
    });
    public BindingCommand<Void> clickBreakCourse = new BindingCommand<>(() -> {  //微课
        viewModel.UC.breakCourse.call();
    });

    public BindingCommand<Void> clickPlay = new BindingCommand<>(() -> {  //播放句子
        viewModel.UC.closeTimeBar.setValue(true);
        VoaText data = entity.get();
        data.setPlayRec(false);
        if (data.isPlayCurrent()) {   //已经播放
            data.setPlayCurrent(false);
            viewModel.mPlayer.pause();
            viewModel.stopPlayerListener();
            playPosition.set(viewModel.mPlayer.getCurrentPosition());
        } else {
            viewModel.mPlayer.pause();
            viewModel.startTestingPlayerProcessListener(TestingItemViewModel.this);
            data.setPlayCurrent(true);
            viewModel.mPlayer.setMediaItem(mediaItem, playPosition.get());
            viewModel.mPlayer.prepare();
            viewModel.mPlayer.play();
        }
        entity.notifyChange();

//        setSpannable(data);
//        holder.tvSen.setText(sb);

    });

    public TestingItemViewModel(@NonNull TestingViewModel viewModel, VoaText data) {
        super(viewModel);
        entity.set(data);
        long startPositionMs = (long) (data.getTiming() * 1000);
        long endPositionMs = (long) (data.getEndTiming() * 1001);
        mediaItem = viewModel.mediaItemBuilder
                .setClippingConfiguration(
                        new MediaItem.ClippingConfiguration.Builder()
                                .setStartPositionMs(startPositionMs)
                                .setEndPositionMs(endPositionMs)
                                .build())
                .build();
        playTotalTime.set(endPositionMs - startPositionMs);
        if (Objects.requireNonNull(entity.get()).getIndex() == 1) {
            isClickItem.set(true);  //默认显示第一个按钮
        }
    }

}