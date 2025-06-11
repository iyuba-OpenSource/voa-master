package com.iyuba.voa.ui.main.video;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadlineManager;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.ITitleRefresh;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.app.manage.AccountManager;
import com.iyuba.voa.ui.event.VIpChangeEvent;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.goldze.mvvmhabit.base.BaseFragment;


/**
 * Created by iyuba on 2017/7/27.
 * 视频fragment
 */

public class VideoFragment extends BaseFragment {
    private TextView tv_title;
    private Context mContext;
    private DropdownTitleFragmentNew mExtraFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    protected boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_video;
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        isPrepared = true;
//        lazyLoad();
//        setUser();
        initview();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }

    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        initview();
        isFirst = false;
    }


    private void initview() {
        IHeadlineManager.appId = String.valueOf(Constants.CONFIG.appId);
        IHeadlineManager.appName = getResources().getString(R.string.app_name);

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

//        setUser();
        String[] types = new String[]{
//                HeadlineType.NEWS,
//                HeadlineType.VOA,
                HeadlineType.CSVOA,
//                HeadlineType.BBC,
                HeadlineType.HEADLINE,
                HeadlineType.MEIYU,
/*                HeadlineType.TED,
                HeadlineType.TOPVIDEOS,
                HeadlineType.BBCWORDVIDEO,
                HeadlineType.VOAVIDEO,
                HeadlineType.SMALLVIDEO*/
//                HeadlineType.JAPANVIDEOS
        };
        switch (BuildConfig.FLAVOR) {
            case "other":
                if (DateUtil.getNowTimeMM() >
                        DateUtil.stringToDate("2023-01-15", DateUtil.YEAR_MONTH_DAY).getTime()) {
                    types = new String[]{HeadlineType.VOA, HeadlineType.CSVOA};
                }
        }
        Bundle bundle = DropdownTitleFragmentNew.buildArguments(10, types, false);
        mExtraFragment = DropdownTitleFragmentNew.newInstance(bundle);
//        mExtraFragment.setSmallVideoFragment(VideoListFragment.newInstance());
        transaction.replace(R.id.video_container, mExtraFragment);
        transaction.show(mExtraFragment);
        transaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(VIpChangeEvent vIpChangeEvent) {
        setUser();
        if (mExtraFragment != null) {
            ((ITitleRefresh) mExtraFragment).refreshTitleContent();
        }
    }


    private void setUser() {
        AccountManager.getInstance().initMocUser();
    }


}
