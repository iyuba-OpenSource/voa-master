package com.iyuba.voa.ui.main.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.ExoPlayer;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentSearchListBinding;
import com.iyuba.voa.utils.Constants;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/10/13
 * 邮箱：jxfengmtx@gmail.com
 */
public class SearchFragment extends BaseFragment<FragmentSearchListBinding, SearchViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_search_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
                .titleBar(R.id.toolbar)
                .statusBarColor(R.color.purple_500)
                .fitsSystemWindows(true)
                .init();

        viewModel.loadRecommend();
        viewModel.wordPlayer = new ExoPlayer.Builder(getContext()).build();

        Bundle arguments = getArguments();
        if (arguments != null) {
            String string = arguments.getString(Constants.BUNDLE.KEY);
            if (!TextUtils.isEmpty(string)) {
                viewModel.searchText.set(string);
                viewModel.searchClick.execute();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public SearchViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(SearchViewModel.class);
    }
}
