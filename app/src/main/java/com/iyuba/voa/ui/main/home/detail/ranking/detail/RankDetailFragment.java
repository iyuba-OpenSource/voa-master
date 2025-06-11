package com.iyuba.voa.ui.main.home.detail.ranking.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlayer;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.RankDetail;
import com.iyuba.voa.data.entity.TitleTed;
import com.iyuba.voa.databinding.FragmentDetailRankDetailBinding;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.ShareUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.MaterialDialogUtil;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankDetailFragment extends BaseFragment<FragmentDetailRankDetailBinding, RankDetailViewModel> {


    private ExoPlayer mPlayer;
    private String voaId;
    private ExoPlayer playerWord;
    private MenuItem menuItem;
    private String userName;
    private TitleTed titleTed;

    public RankDetailFragment() {
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();

        Bundle extras = getArguments();
        String voaId = extras.getString(Constants.BUNDLE.KEY);
        String uid = extras.getString(Constants.BUNDLE.KEY_0);
        viewModel.headUrl = extras.getString(Constants.BUNDLE.KEY_1);
        userName = extras.getString(Constants.BUNDLE.KEY_2);

        titleTed = extras.getParcelable(Constants.BUNDLE.KEY_3);
        viewModel.setIsShowBack(true);
        viewModel.setTitleText(userName + "的评测");

        viewModel.mPlayer = new ExoPlayer.Builder(getContext()).build();
//        binding.player.setPlayer(viewModel.mPlayer);
        if (!TextUtils.isEmpty(voaId)) {
            viewModel.voaid = voaId;
            viewModel.loadData(voaId, uid);
        }

    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RankDetailViewModel initViewModel() {

        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(RankDetailViewModel.class);
    }


    @Override
    public void initViewObservable() {
        viewModel.UC.setPlayerProcess.observe(this, voaText -> mPlayer.seekTo((long) (voaText.getTiming() * 1000)));

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

        viewModel.UC.showShareDialog.observe(this, ra -> {
            RankDetail rankDetail = ra.entity.get();
            String site = Constants.CONFIG.USER_SPEECH_IP + rankDetail.getShuoShuo();
            String title = "播音员:" + userName + "。标题:" + titleTed.getTitle();
            String desc = "@爱语吧 " + userName + "在语音评测中得了" + rankDetail.getScore() + "分。标题:" + titleTed.getTitle() + ":" +
                    titleTed.getDescCn() + " 下载地址:" + site;

            ShareUtils.showShare(getActivity(), rankDetail.getHeadUrl(), site, title, desc, new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    if (platform.getName().equals("QQ") || platform.getName().equals("Wechat") || platform.getName().equals("WechatFavorite")) {
                        viewModel.startInterfaceAddScore(45);
                    } else if (platform.getName().equals("QZone") || platform.getName().equals("WechatMoments") || platform.getName().equals("SinaWeibo") || platform.getName().equals("TencentWeibo")) {
                        viewModel.startInterfaceAddScore(46);
                    }
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {

                }

                @Override
                public void onCancel(Platform platform, int i) {

                }
            });
        });

        viewModel.UC.loadDataSuccess.observe(this, s -> MaterialDialogUtil.showMsgDialog((AppCompatActivity) getActivity(), s));

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_detail_rank_detail;
    }

}