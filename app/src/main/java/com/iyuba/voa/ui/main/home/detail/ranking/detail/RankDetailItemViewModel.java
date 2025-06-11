package com.iyuba.voa.ui.main.home.detail.ranking.detail;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.elvishew.xlog.XLog;
import com.google.android.exoplayer2.MediaItem;
import com.iyuba.voa.data.entity.RankDetail;
import com.iyuba.voa.utils.Constants;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankDetailItemViewModel extends ItemViewModel<RankDetailViewModel> {

    public long playPosition;
    private MediaItem mediaItem;
    public AnimationDrawable animationDrawable;

    private View imageView;
    public BindingCommand<View> getImageView = new BindingCommand<>(view -> {
        this.imageView = view;
        animationDrawable = (AnimationDrawable) imageView.getBackground();
    });

    public ObservableField<RankDetail> entity = new ObservableField<>();


    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {

    });
    public BindingCommand<Void> clickShare = new BindingCommand<>(() -> {
        viewModel.UC.showShareDialog.setValue(this);
    });


    public BindingCommand<Void> clickPlay = new BindingCommand<>(() -> {
      /*  for (RankDetailItemViewModel itemViewModel : viewModel.observableList) {
            if (itemViewModel.animationDrawable != null)
                itemViewModel.animationDrawable.stop();
        }*/
        viewModel.mPlayer.setMediaItem(mediaItem);
        RankDetail data = entity.get();
        if (data.isPlaying()) {   //已经播放
            data.setPlaying(false);
            viewModel.mPlayer.pause();
            animationDrawable.stop();
            playPosition = viewModel.mPlayer.getCurrentPosition();
        } else {
            viewModel.mPlayer.pause();
            data.setPlaying(true);
            animationDrawable.start();
            viewModel.mPlayer.setMediaItem(mediaItem, playPosition);
            viewModel.mPlayer.prepare();
            viewModel.mPlayer.play();
        }
        entity.notifyChange();
    });

    public RankDetailItemViewModel(@NonNull RankDetailViewModel viewModel, RankDetail data) {
        super(viewModel);
        entity.set(data);
        XLog.i("评测排名语音地址：" + Constants.CONFIG.USER_SPEECH_IP + entity.get().getShuoShuo());
        mediaItem = MediaItem.fromUri(Constants.CONFIG.USER_SPEECH_IP + entity.get().getShuoShuo());
    }

}