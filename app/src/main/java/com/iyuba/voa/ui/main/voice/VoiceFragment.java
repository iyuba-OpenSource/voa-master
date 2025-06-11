package com.iyuba.voa.ui.main.voice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.module.headlinetalk.ui.TalkStuffFragment;
import com.iyuba.voa.R;

import me.goldze.mvvmhabit.base.BaseFragment;


/**
 * Created by iyuba on 2017/7/27.
 * 视频fragment
 */

public class VoiceFragment extends BaseFragment {
    private Context mContext;

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
        initView();
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
        initView();
        isFirst = false;
    }

    private void initView() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Bundle bundle = TalkStuffFragment.buildArguments(HeadlineType.MEIYU);
        TalkStuffFragment fragment = TalkStuffFragment.newInstance(bundle);
//
        transaction.replace(R.id.video_container, fragment);
        transaction.show(fragment);
        transaction.commit();

//        fragment.getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    /*    RxTimer.timer(1000, new RxTimer.RxAction() {
            @Override
            public void action(long number) {
                try {
                    Class<? extends TalkStuffFragment> aClass = fragment.getClass();
                    Method mToolbar = aClass.getMethod("setNavigationIcon", Drawable.class);
                    mToolbar.setAccessible(true);
                    mToolbar.invoke(fragment, new Object[]{null});
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }


}
