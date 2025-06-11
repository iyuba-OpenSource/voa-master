package com.iyuba.voa.ui.main.search;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.google.android.exoplayer2.MediaItem;
import com.iyuba.voa.data.entity.SearchContent;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.ui.main.home.HomeListFragment;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;
import com.iyuba.voa.utils.Constants;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/10/13
 * 邮箱：jxfengmtx@gmail.com
 */
public class SearchItemViewModel extends ItemViewModel<SearchViewModel> {
    public ObservableField<SearchContent> entity = new ObservableField<>();
    public ObservableField<TitleTed> entityArticle = new ObservableField<>();


    public SearchItemViewModel(@NonNull SearchViewModel viewModel) {
        super(viewModel);
    }

    public SearchItemViewModel(@NonNull SearchViewModel viewModel, SearchContent content) {
        super(viewModel);
        entity.set(content);
    }

    public SearchItemViewModel(@NonNull SearchViewModel viewModel, TitleTed content) {
        super(viewModel);
        entityArticle.set(content);
    }

    public BindingCommand<Void> clickItem = new BindingCommand<>(() -> {
        TitleTed titleTed = entityArticle.get();
        if (null != titleTed) {
            if (titleTed.getSound().contains(Constants.CONFIG.SOUND_IP)) {
                titleTed.setSound(titleTed.getSound().replace(Constants.CONFIG.SOUND_IP, ""));
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE.KEY, titleTed);
            viewModel.startActivity(DetailActivity.class, bundle);
        }
    });

    public BindingCommand<Void> clickMore = new BindingCommand<>(() -> {
        if (viewModel.observableList.size() > 2) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE.KEY, 0);
            bundle.putString(Constants.BUNDLE.KEY_X, viewModel.searchText.get());
            viewModel.startContainerActivity(HomeListFragment.class.getCanonicalName(), bundle);
        }
    });

    public BindingCommand<Void> clickPlay = new BindingCommand<>(() -> {
        SearchContent searchContent = entity.get();
        if (null != searchContent) {
            if (TextUtils.isEmpty(searchContent.getPhEnMp3())) {
                return;
            }
            MediaItem mediaItem = MediaItem.fromUri(searchContent.getPhEnMp3());
            viewModel.wordPlayer.setMediaItem(mediaItem);
            viewModel.wordPlayer.prepare();
            viewModel.wordPlayer.play();
        }
    });

    public BindingCommand<Void> clickCollect = new BindingCommand<>(() -> {
        if (null != entity.get()) {
            viewModel.updateWordCollect(entity);
        }
    });


}
