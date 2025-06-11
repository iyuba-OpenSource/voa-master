
package com.iyuba.voa.ui.main.person.collect.word;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.XmlWord;
import com.iyuba.voa.databinding.FragmentWordCollectBinding;
import com.iyuba.voa.ui.base.BaseTitleFragment;
import com.iyuba.voa.utils.FileUtils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/11
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class WordCollectListFragment extends BaseTitleFragment<FragmentWordCollectBinding, WordCollectListViewModel> {
    private int mIndex;
    private ExoPlayer playerWord;

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        //关闭动画效果 ,防止刷新闪烁
        SimpleItemAnimator sa = (SimpleItemAnimator) binding.rlList.getItemAnimator();
        sa.setSupportsChangeAnimations(false);
        binding.rlList.setItemAnimator(null);
        viewModel.setIsShowBack(true);
        viewModel.setIsShowRightMenu(true);
        viewModel.setTitleText("生词本");
        viewModel.mPageIndex = mIndex;
        viewModel.syncData();
        viewModel.loadData(1, WordCollectListViewModel.pageNum);
        playerWord = new ExoPlayer.Builder(getContext()).build();
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_word_collect;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public WordCollectListViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(WordCollectListViewModel.class);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.UC.finishRefreshing.observe(this, index -> {
            //结束刷新l
            binding.refresh.finishRefresh();
        });
        //监听上拉加载完成
        viewModel.UC.finishLoadMore.observe(this, index -> {
            //结束刷新
            binding.refresh.finishRefresh();
            binding.refresh.finishLoadMore();
        });
        viewModel.UC.playAudio.observe(this, wordCollectListItemViewModel -> {
            XmlWord xmlWord = wordCollectListItemViewModel.entity.get();
            if (TextUtils.isEmpty(xmlWord.getAudio())) {
                return;
            }
            MediaItem mediaItem = MediaItem.fromUri(xmlWord.getAudio());
            playerWord.setMediaItem(mediaItem);
            playerWord.prepare();
            playerWord.play();
        });
        viewModel.uc.getRightView.observe(this, view -> view.setOnClickListener(view1 -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);//1.实例化PopupMenu
            getActivity().getMenuInflater().inflate(R.menu.menu_word, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_exportpdf:
                        if (!viewModel.checkIsVIP("该功能需要VIP权限, 是否开通vip?")) {
                            break;
                        }
                        viewModel.exportPdf();
                        break;
                    case R.id.menu_sorting:
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                .title("排序方式")
                                .items(R.array.word_sort)
                                .itemsCallback((dialog1, itemView, position, text) -> {
                                    switch (position) {
                                        case 0:  //首字母
                                            viewModel.saveSort("key");
                                            break;
                                        case 1:  //收藏时间
                                            viewModel.saveSort("createDate");
                                            break;
                                    }
                                    viewModel.refreshCommand.execute();
                                })
                                .canceledOnTouchOutside(true)//点击外部取消对话框
                                .build();
                        dialog.show();
                        break;
                }
                return false;
            });
            popupMenu.show();
        }));
        viewModel.UC.pdfSuccessDialog.observe(this, url -> {
            showPdfDialog(url);
        });
    }

    private void showPdfDialog(String path) {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("已为您导出PDF链接")
                .content(path)
                .positiveText("复制")
                .negativeText("取消")
                .onPositive((dialog1, which) -> FileUtils.copyStr(getContext(), path))
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != playerWord) {
            playerWord.stop();
            playerWord.release();
            playerWord = null;
        }
    }
}