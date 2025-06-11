package com.iyuba.voa.ui.main.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.imooclib.ui.mobclass.MobClassFragment;
import com.iyuba.voa.R;

import java.util.ArrayList;

import me.goldze.mvvmhabit.base.BaseFragment;

public class MicroClassFragment extends BaseFragment {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_micro;
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
        ArrayList<Integer> typeIdFilter = new ArrayList<>();
//        typeIdFilter.add(-2);//全部
//                        typeIdFilter.add(-1);//最新
//        typeIdFilter.add(2);//四级
        typeIdFilter.add(3);//VOA
//        typeIdFilter.add(27);// disney
//                        typeIdFilter.add(4);//六级
//                        typeIdFilter.add(7);//托福
//                        typeIdFilter.add(8);//考研
//                        typeIdFilter.add(9);//BBC
//        typeIdFilter.add(21);//新概念
//                        typeIdFilter.add(22);//走遍美国
//                        typeIdFilter.add(28);//学位
//                        typeIdFilter.add(52);//考研二
//                        typeIdFilter.add(61);//雅思
//                        typeIdFilter.add(91);//中职
//                        typeIdFilter.add(25);//小学
        //typeIdFilter.add(1);//N1
        //typeIdFilter.add(5);//N2
        //typeIdFilter.add(6);//N3
        Bundle bundle = MobClassFragment.buildArguments(3, false, typeIdFilter);
        MobClassFragment mobClassFragment = MobClassFragment.newInstance(bundle);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.micro_container, mobClassFragment).commit();
    }
}
