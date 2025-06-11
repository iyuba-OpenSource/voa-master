package com.iyuba.voa.ui.main.person;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.module.favor.ui.BasicFavorActivity;
import com.iyuba.module.headlinetalk.ui.mytalk.MyTalkActivity;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.databinding.FragmentPersonBinding;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.MaterialDialogUtil;
import me.goldze.mvvmhabit.utils.ToastUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;
import personal.iyuba.personalhomelibrary.ui.search.SearchGroupActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity;
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/30
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
@RuntimePermissions
public class PersonFragment extends BaseFragment<FragmentPersonBinding, PersonViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_person;
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
                .fitsSystemWindows(true)
                .init();
        viewModel.setTitleText("我的");
//        viewModel.loadVIPInfo();
        viewModel.getData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.showLogoutDialog.observe(this, aBoolean -> {
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("提示")
                    .content(R.string.person_logout)
                    .positiveText("确定")
                    .negativeText("取消")
                    .onPositive((dialog1, which) -> MaterialDialogUtil.showInputDialog((AppCompatActivity) getActivity(), "您确定要注销账号吗？", "请输入登录密码", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog1, CharSequence input) {
                            viewModel.logout(input.toString().trim());
                        }
                    }))
                    .contentColor(getResources().getColor(R.color.black))
                    .canceledOnTouchOutside(false)//点击外部不取消对话框
                    .build();
            dialog.show();
        });

        viewModel.uc.showDomainDialog.observe(this, aBoolean -> {
            PersonFragmentPermissionsDispatcher.startLocationWithPermissionCheck(this);
          /*  ArrayList<String> list = new ArrayList<>();
            ArrayList<String> listValue = new ArrayList<>();
            list.add("爱语吧");
            list.add("珠穆朗玛");
            listValue.add("iyuba.cn");
            listValue.add("qomolama.cn");
            MaterialDialogUtil.showListDialog((AppCompatActivity) getActivity(), "切换域名", list, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                    viewModel.obSpeed.set(text.toString());
                    viewModel.updateDomain(listValue.get(position), Constants.CONFIG.COM_DOMAIN);
                    ToastUtils.showShort("切换成功");
                }
            });*/
        });

        viewModel.uc.startPersonalCenter.observe(this, userInfo -> {
            PersonalHome.setSaveUserinfo((userInfo.getUid() == null) ? 0 : Integer.parseInt(userInfo.getUid()), userInfo.getUsername(),
                    userInfo.getVipStatus());
            getContext().startActivity(PersonalHomeActivity.buildIntent(getContext(), Integer.parseInt(userInfo.getUid()), userInfo.getUsername(), 0));
        });
        viewModel.uc.startCollect.observe(this, o -> {
            startActivity(BasicFavorActivity.buildIntent(getContext()));
        });
        viewModel.uc.startVoice.observe(this, o -> {
            startActivity(MyTalkActivity.buildIntent(getContext(), HeadlineType.MEIYU));
        });
        viewModel.uc.startStudyReport.observe(this, userInfo -> {
            //StatisticsActivity.instance(mContext, Type.COURSE);
            String[] types = new String[]{
                    SummaryType.LISTEN,
                    SummaryType.EVALUATE,
                    //SummaryType.WORD,
                    SummaryType.TEST,
                    //SummaryType.MOOC,
//                    SummaryType.READ
            };
            startActivity(SummaryActivity.getIntent(getContext(), HeadlineType.VOA, types, 0));//10 PersonalType.NEWS
        });
        viewModel.uc.startMessage.observe(this, o -> {
            startActivity(MessageActivity.class);
        });
        viewModel.uc.startGroupQQ.observe(this, o -> {
            startActivity(SearchGroupActivity.class);
        });
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void locationPermissionFail() {
        ToastUtils.showShort("请开启位置权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PersonFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void startLocation() {
//        requestPermission();
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ToastUtils.showShort("请打开位置权限继续");
        } else {

        }
    }

    @Override
    public PersonViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(PersonViewModel.class);
    }


}
