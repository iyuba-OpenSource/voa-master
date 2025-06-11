package com.iyuba.voa.ui.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppApplication;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.app.service.MusicService;
import com.iyuba.voa.databinding.FragmentHomeBinding;
import com.iyuba.voa.ui.base.adapter.BaseFragmentPagerAdapter;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;
import com.iyuba.voa.ui.main.search.SearchFragment;
import com.iyuba.voa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.RxTimer;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {


    private List<Fragment> mFragments;
    private List<String> titlePager;
    private String[] stringArray;
    private MusicService musicService;
    private MusicService.MusicBinder musicBinder;
    private RotateAnimation rotate;

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .fitsSystemWindows(true)
                .init();
        viewModel.setTitleText(getResources().getString(R.string.app_name));
        viewModel.setIsShowRightMenu(true);
        loadTitle();
        viewModel.loadVIPInfo();
        mFragments = pagerFragment();
        titlePager = pagerTitleString();
        //设置Adapter
        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), mFragments, titlePager);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(1);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));

        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(3000);//设置动画持续周期
        rotate.setRepeatCount(-1);//设置重复次数
        rotate.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        binding.imageQuick.setAnimation(rotate);
//        binding.llQuickContainer.setAnimation(rotate);
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initViewObservable() {
        viewModel.UC.serviceUpdate.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                setMusicControl();
            }
        });

        viewModel.uc.getRightView.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageResource(R.drawable.ic_baseline_search_24);
                }
            }
        });
        viewModel.uc.setRightClick.observe(this, new Observer<Void>() {  //搜索查找
            @Override
            public void onChanged(Void unused) {
                startContainerActivity(SearchFragment.class.getCanonicalName());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setMusicControl();
    }

    private void setMusicControl() {
        AppApplication application = (AppApplication) getActivity().getApplication();
        musicBinder = application.getMusicBinder();
        if (musicBinder != null)
            musicService = musicBinder.getService();
        if (musicService != null && musicService.getPlayPosition() != -1) {  //已有歌曲播放
            binding.llQuickContainer.setVisibility(View.VISIBLE);
            viewModel.isPlaying.set(musicService.mediaPlayer.isPlaying());
            if (viewModel.isPlaying.get()) {
                RxTimer.timer(700, number -> binding.imageQuick.startAnimation(rotate));
            } else binding.imageQuick.clearAnimation();
            viewModel.titleTed.set(musicBinder.getIsPlaySource());
            binding.llQuickContainer.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BUNDLE.KEY, viewModel.titleTed.get());
                bundle.putInt(Constants.BUNDLE.KEY_0, musicService.getPlayPosition());
                startActivity(DetailActivity.class, bundle);
            });
            binding.imageToggle.setOnClickListener(view -> {
                musicService.pauseOrContinueMusic();
                viewModel.isPlaying.set(musicService.mediaPlayer.isPlaying());
                if (viewModel.isPlaying.get())
                    binding.imageQuick.startAnimation(rotate);
                else binding.imageQuick.clearAnimation();
            });
        } else binding.llQuickContainer.setVisibility(View.GONE);
    }

    @Override
    public HomeViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(HomeViewModel.class);
    }


    protected List<Fragment> pagerFragment() {
        List<Fragment> list = new ArrayList<>();
        for (int i = 0; i < stringArray.length; i++) {
            list.add(new HomeListFragment(i));
        }
        return list;
    }

    protected List<String> pagerTitleString() {
        List<String> list = new ArrayList<>();
        for (String s : stringArray) {
            list.add(s);
        }
        return list;
    }


    public void loadTitle() {
        stringArray = Utils.getContext().getResources().getStringArray(R.array.home);
    }

}