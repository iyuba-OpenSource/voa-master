package com.iyuba.voa.ui.main.person.rank;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.ActivityPersonRankBinding;
import com.iyuba.voa.ui.base.BaseTitleActivity;
import com.iyuba.voa.ui.base.adapter.BaseFragmentPagerAdapter;
import com.iyuba.voa.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/16
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class RankActivity extends BaseTitleActivity<ActivityPersonRankBinding, RankViewModel> {


    private String[] stringArray;
    private List<Fragment> mFragments;
    private List<String> titlePager;

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();

        viewModel.setIsShowBack(true);
//        viewModel.setIsShowRightMenu(true);
        viewModel.setTitleText("排行榜");
        loadTitle();

        mFragments = pagerFragment();
        titlePager = pagerTitleString();

        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragments, titlePager);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(1);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));

    }


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_person_rank;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RankViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(RankViewModel.class);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.UC.loadDataSuccess.observe(this, voaTexts -> {

        });
    }


    protected List<Fragment> pagerFragment() {
        List<Fragment> list = new ArrayList<>();
        for (int i = 0; i < stringArray.length; i++) {
            RankListFragment rankListFragment = new RankListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BUNDLE.KEY, stringArray[i]);
            rankListFragment.setArguments(bundle);
            list.add(rankListFragment);
        }
        return list;
    }

    protected List<String> pagerTitleString() {
        return new ArrayList<>(Arrays.asList(stringArray));
    }


    public void loadTitle() {
        stringArray = Utils.getContext().getResources().getStringArray(R.array.person_rank);
    }
}