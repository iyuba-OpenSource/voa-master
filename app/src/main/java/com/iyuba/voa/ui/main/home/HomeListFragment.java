
package com.iyuba.voa.ui.main.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.headlinelibrary.data.model.StreamType;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentVpHomeBinding;
import com.iyuba.voa.ui.widget.strategy.ContentMixStrategy;
import com.iyuba.voa.ui.widget.strategy.ContentNonVipStrategy;
import com.iyuba.voa.ui.widget.strategy.ContentStrategy;
import com.iyuba.voa.ui.widget.strategy.ContentVipStrategy;
import com.iyuba.voa.utils.Constants;
import com.youdao.sdk.nativeads.NativeAds;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoMultiNative;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import timber.log.Timber;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class HomeListFragment extends BaseFragment<FragmentVpHomeBinding, HomeListViewModel> implements YouDaoMultiNative.YouDaoMultiNativeNetworkListener {


    int[] mStreamTypes = new int[]{StreamType.YOUDAO, StreamType.YOUDAO, StreamType.YOUDAO};
    private List<NativeResponse> responses;
    private int mIndex;
    private ContentStrategy mContentStrategy;
    private int mStrategyCode;
    private RecyclerView.Adapter mWorkAdapter;

    private YouDaoMultiNative youDaoMultiNative;
    private RequestParameters mRequestParameters;
    private HomeListAdapter homeListAdapter;

    //在 Fragment 恢复保存状态的时候崩溃了。原来宿主 Activity 被销毁后重新恢复，它内部的 Fragment 也会通过反射的方式实例化。
    public HomeListFragment() {
    }

    public HomeListFragment(int index) {
        super();
        mIndex = index;
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        viewModel.mPageIndex = mIndex;
        Bundle arguments = getArguments();
        if (arguments != null) {
            switch (viewModel.bundleBreak = arguments.getInt(Constants.BUNDLE.KEY)) {
                case 0:  //搜索 更多
                    String string = arguments.getString(Constants.BUNDLE.KEY_X);
                    if (!TextUtils.isEmpty(string)) {
                        viewModel.searchKey = string;
                        viewModel.setIsShowBack(true);
                        viewModel.setTitleText(viewModel.searchKey);
                        viewModel.loadSearchData(1, 10);
                    }
                    break;
                case 1:  //阅读历史
                    viewModel.searchKey = "";
                    viewModel.loadReadHisData();
                    viewModel.setIsShowBack(true);
                    viewModel.setTitleText("阅读历史");
                    break;
            }

        } else
            viewModel.loadData(1, 10, mIndex);
        //关闭动画效果 ,防止刷新闪烁
        binding.rlList.setLayoutManager(new LinearLayoutManager(getActivity()));
        SimpleItemAnimator sa = (SimpleItemAnimator) binding.rlList.getItemAnimator();
        sa.setSupportsChangeAnimations(false);
        binding.rlList.setItemAnimator(null);
        String keywords = "";
        //有道广告请求
        try {
            youDaoMultiNative = new YouDaoMultiNative(getActivity(), "3438bae206978fec8995b280c49dae1e", this);
            mRequestParameters = new RequestParameters.RequestParametersBuilder().keywords(keywords).build();
            // 发起广告请求, 10代表一次获取的广告个数
            if (!viewModel.getIsVIP())
                youDaoMultiNative.makeRequest(mRequestParameters, 3);
        } catch (Exception var) {
        }
        homeListAdapter = new HomeListAdapter(viewModel.lists);
        binding.rlList.setAdapter(homeListAdapter);

    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_vp_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeListViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(HomeListViewModel.class);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.UC.loadDataSuccess.observe(this, ad -> {
            homeListAdapter.notifyDataSetChanged();
            //结束刷新l
        });
        viewModel.UC.finishRefreshing.observe(this, ad -> {
//            binding.rlList.notifyAll();
            //结束刷新l
            binding.refresh.finishRefresh();
        });
        //监听上拉加载完成
        viewModel.UC.finishLoadMore.observe(this, index -> {
            //结束刷新
            binding.refresh.finishRefresh();
            binding.refresh.finishLoadMore();
        });

        viewModel.UC.addAd.observe(this, data -> {
            if (data != null) {
                if (data.getFirstLevel().equals("1")) {
                    mStreamTypes[0] = StreamType.IYUBA;
                }
                if (data.getSecondLevel().equals("1")) {
                    mStreamTypes[1] = StreamType.IYUBA;
                }
                if (data.getThirdLevel().equals("1")) {
                    mStreamTypes[2] = StreamType.IYUBA;
                }
            }
            int code = getStrategyCode();
            if (mStrategyCode != code) {
                mStrategyCode = code;
                mContentStrategy = switchStrategy(mStrategyCode);
                mWorkAdapter = mContentStrategy.buildWorkAdapter(getActivity(), homeListAdapter);
                mContentStrategy.init(binding.rlList, mWorkAdapter);
                Log.e("string", "" + mStreamTypes[0] + mStreamTypes[1] + mStreamTypes[2]);
                if (mContentStrategy instanceof ContentNonVipStrategy) {
                    ((ContentNonVipStrategy) mContentStrategy).loadAd(mWorkAdapter);
                }
            } else {
                Timber.i("same strategy code");
            }
        });

        viewModel.UC.deleteDialog.observe(this, listItemViewModel -> new MaterialDialog.Builder(getActivity())
                .title("提示")
                .content("您确定要删除这条数据吗?")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive((dialog1, which) -> {
                    viewModel.lists.remove(listItemViewModel);
                    viewModel.delReadHisData(listItemViewModel.entity.get());
                    homeListAdapter.notifyDataSetChanged();
                })
                .contentColor(getResources().getColor(R.color.black))
                .build().show());
    }

    private int getStrategyCode() {
        if (viewModel.getIsVIP()) {
            return ContentStrategy.Strategy.VIP;
        } else {
            if (mStreamTypes != null) {
                return ContentStrategy.Strategy.MIX;
            } else {
                return ContentStrategy.Strategy.VIP;
            }
        }
    }

    private ContentStrategy switchStrategy(int strategyCode) {
        Log.e("=====", "=======3");
        switch (strategyCode) {
            case ContentStrategy.Strategy.VIP: {
                return new ContentVipStrategy();
            }
            case ContentStrategy.Strategy.MIX:
            default: {
                Log.e("=====", "=======4");
//                Log.e("string", "" + mStreamTypes[0] + mStreamTypes[1] + mStreamTypes[2]);
                return new ContentMixStrategy(2, 5, mStreamTypes, HolderType.SMALL);
            }
        }
    }


    @Override
    public void onNativeLoad(NativeAds nativeAds) {

    }

    @Override
    public void onNativeFail(NativeErrorCode nativeErrorCode) {

    }
}