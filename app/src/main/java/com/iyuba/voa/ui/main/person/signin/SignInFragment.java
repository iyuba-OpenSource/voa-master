package com.iyuba.voa.ui.main.person.signin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;
import com.gyf.immersionbar.ImmersionBar;
import com.iyuba.voa.BR;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.StudyTime;
import com.iyuba.voa.databinding.FragmentSignBinding;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.ShareUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.MaterialDialogUtil;
import me.goldze.mvvmhabit.utils.RxTimer;
import me.goldze.mvvmhabit.utils.SDCardUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/16
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class SignInFragment extends BaseFragment<FragmentSignBinding, SignInViewModel> {

    private StudyTime studyTime;
    private String signPath;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_sign;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        ImmersionBar.with(this)
//                .titleBar(R.id.toolbar)
//                .fitsSystemWindows(true)
                .fullScreen(true)
                .init();
        viewModel.setIsShowBack(true);
        viewModel.setTitleText("打卡");
        viewModel.setRightText("分享");

        Bundle arguments = getArguments();
        studyTime = arguments.getParcelable(Constants.BUNDLE.KEY);
        viewModel.entity.set(studyTime);
        viewModel.loadData(studyTime.getShareId());

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.setRightTextClick.observe(this, unused -> {
            ToastUtils.showShort("正在分享");
            if (!viewModel.isClickDaka.get()) {
                ToastUtils.showShort("请先点击打卡生成二维码");
                return;
            }
            RxTimer.timer(200, a -> {
                writeBitmapToFile();
                ShareUtils.showQrShare(getContext(), signPath, new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        viewModel.startInterfaceAddScore();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
            });
        });

        viewModel.UC.loadDataSuccess.observe(this, s -> MaterialDialogUtil.showMsgDialog((AppCompatActivity) getActivity(), s));
    }

    @Override
    public SignInViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return new ViewModelProvider(this, factory).get(SignInViewModel.class);
    }

    public void writeBitmapToFile() {
        View view = getActivity().getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            return;
        }
        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();

        signPath = SDCardUtils.getFileCachePath() + System.currentTimeMillis() + ".png";
        XLog.i("writeBitmapToFile " + signPath);
        File newpngfile = new File(signPath);
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newpngfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
