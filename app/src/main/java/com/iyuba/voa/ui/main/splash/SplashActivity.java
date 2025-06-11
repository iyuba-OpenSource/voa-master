package com.iyuba.voa.ui.main.splash;

import static com.iyuba.voa.utils.Constants.CONFIG.COMPANY;
import static com.iyuba.voa.utils.Constants.CONFIG.PROTOCOL_IP;
import static com.iyuba.voa.utils.Constants.CONFIG.appId;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.elvishew.xlog.XLog;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.iyubalib.Lib;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.voa.BR;
import com.iyuba.voa.BuildConfig;
import com.iyuba.voa.R;
import com.iyuba.voa.app.AppViewModelFactory;
import com.iyuba.voa.data.entity.Advertising;
import com.iyuba.voa.databinding.ActivitySplashBinding;
import com.iyuba.voa.ui.base.fragment.WebFragment;
import com.iyuba.voa.ui.main.MainActivity;
import com.iyuba.voa.ui.widget.AroundCircleView;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;
import com.mob.MobSDK;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;
import com.umeng.commonsdk.UMConfigure;
import com.youdao.sdk.common.NativeIndividualDownloadOptions;
import com.youdao.sdk.common.OAIDHelper;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.ContainerActivity;
import me.goldze.mvvmhabit.http.ExceptionHandle;
import me.goldze.mvvmhabit.utils.RxTimer;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import personal.iyuba.personalhomelibrary.PersonalHome;
import timber.log.Timber;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {

    private final String provision = PROTOCOL_IP + "/protocoluse.jsp?company=" + COMPANY;
    private final String privacy = PROTOCOL_IP + "/protocolpri.jsp?company=" + COMPANY;
    private AlertDialog dialog;

    private long totalTime = 6000;
    private long intervalTime = 200;
    private YouDaoNative youdaoNative;
    private AroundCircleView aroundCircleView;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public SplashViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, factory).get(SplashViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
    /*    if (BuildConfig.DEBUG)
            SPUtils.getInstance().put(Constants.SP.IS_AGREE, false);*/
        aroundCircleView = binding.acvSkip;
        aroundCircleView.setTotalProcess(totalTime);

        if (!SPUtils.getInstance().getBoolean(Constants.SP.IS_AGREE, false)) {
            startDialog();
        } else {
            initComponent();
            initThisComo();
        }


        aroundCircleView.setOnClickListener(view -> {
            RxTimer.cancel();
            finish();
            startActivity(MainActivity.class);
            intervalTime = -1;
        });


    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.showYdAd.observe(this, new Observer<Advertising>() {
            @Override
            public void onChanged(Advertising advertising) {
                showYoudaoAd();
            }
        });

        viewModel.uc.startMain.observe(this, new Observer<Advertising>() {
            @Override
            public void onChanged(Advertising ad) {
                //进入广告页面 ，返回之后直接进入主页
                RxTimer.cancel();
                finish();
                Intent intent1 = new Intent(SplashActivity.this, MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addCategory(Intent.CATEGORY_LAUNCHER);

                Intent intent2 = new Intent(SplashActivity.this, ContainerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE.KEY, getString(R.string.app_name));
                bundle.putString(Constants.BUNDLE.KEY_0, ad.getStartuppicUrl());
                intent2.putExtra(ContainerActivity.FRAGMENT, WebFragment.class.getCanonicalName());
                intent2.putExtra(ContainerActivity.BUNDLE, bundle);
                Intent[] intents = {intent1, intent2};
                SplashActivity.this.startActivities(intents);
            }
        });
    }


    private void showYoudaoAd() {
        XLog.i("开屏为%s", "有道");
        if (youdaoNative == null) {
            try {
                initYouDao();
            } catch (Exception var2) {
            }
        }
        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .build();
        if (AdBlocker.getInstance().shouldBlockAd()) {
            XLog.i("showYoudaoAd " + AdBlocker.getInstance().getBlockStartDate());
        } else {
            if (youdaoNative != null) {
                NativeIndividualDownloadOptions nativeDownloadOptions = new NativeIndividualDownloadOptions();
                nativeDownloadOptions.setConfirmDialogEnabled(true);
//                OAIDHelper.getInstance().getOAID();
                youdaoNative.setmNativeIndividualDownloadOptions(nativeDownloadOptions);
                youdaoNative.makeRequest(requestParameters);
            }
        }

    }


    private void initYouDao() {
        youdaoNative = new YouDaoNative(SplashActivity.this, "a710131df1638d888ff85698f0203b46",
                new YouDaoNative.YouDaoNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(final NativeResponse nativeResponse) {
                        List<String> imageUrls = new ArrayList<>();
                        imageUrls.add(nativeResponse.getMainImageUrl());
                        binding.welcomeChangepic.setOnClickListener(v -> {
                            nativeResponse.handleClick(binding.welcomeChangepic);
                            XLog.i("有道开屏广告点击了");
//                                isclickad = true;
                        });
                        ImageService.get(SplashActivity.this, imageUrls, new ImageService.ImageServiceListener() {
                            @TargetApi(Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onSuccess(final Map<String, Bitmap> bitmaps) {
                                if (nativeResponse.getMainImageUrl() != null) {
                                    Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
                                    if (bitMap != null) {
                                        nativeResponse.recordImpression(binding.welcomeChangepic);
                                        binding.welcomeChangepic.setImageBitmap(bitMap);
                                    }
                                }
                            }

                            @Override
                            public void onFail() {
                                XLog.i("ImageService-onfail");
                            }
                        });
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        XLog.i("onNativeFail" + nativeErrorCode.toString());
                    }
                });

    }

    private void startDialog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        //对话框弹出后点击或按返回键不消失;
        dialog.setCancelable(false);

        final Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_agreement);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            TextView textView = window.findViewById(R.id.tv_1);
            TextView tvCancel = window.findViewById(R.id.tv_cancel);
            TextView tvAgree = window.findViewById(R.id.tv_agree);
            tvCancel.setOnClickListener(view -> {
                AppManager.getAppManager().AppExit();
            });
            tvAgree.setOnClickListener(view -> {   //同意
//                initThisComo();
                initComponent();
                SPUtils.getInstance().put(Constants.SP.IS_AGREE, true);
                dialog.cancel();
//                startActivity(WelcomeActivity.class);
                startActivity(MainActivity.class);

            });
            String str = Utils.getContext().getString(R.string.protocol_content);
            textView.setText(str);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(str);
            final int start = str.indexOf("《");//第一个出现的位置
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE.KEY, "隐私协议");
                    bundle.putString(Constants.BUNDLE.KEY_0, privacy + "&apptype=" + Utils.getContext().getString(R.string.app_name));
//        startContainerActivity(WebFragment.class.getCanonicalName(),bundle);
                    startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(SplashActivity.this.getResources().getColor(R.color.highlight));       //设置文件颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, start, start + 6, 0);

            final int end = str.lastIndexOf("《");//最后一个出现的位置

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE.KEY, "隐私协议");
                    bundle.putString(Constants.BUNDLE.KEY_0, provision + "&apptype=" + Utils.getContext().getString(R.string.app_name));
//        startContainerActivity(WebFragment.class.getCanonicalName(),bundle);
                    startContainerActivity(WebFragment.class.getCanonicalName(), bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(SplashActivity.this.getResources().getColor(R.color.highlight));       //设置文件颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, end, end + 6, 0);

            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(ssb, TextView.BufferType.SPANNABLE);
        }
    }

    private void initThisComo() {
        intervalTime = 0;
        if (DateUtil.getNowTimeMM() >=
                DateUtil.stringToDate("2023-01-15", DateUtil.YEAR_MONTH_DAY).getTime())
            viewModel.getMessageLiu();
        RxTimer.interval(100, 50, new RxTimer.RxAction() {
            @Override
            public void action(long number) {
                aroundCircleView.setProgress(intervalTime += 50);
                XLog.i(number + "剩余跳过-" + intervalTime);
                if (intervalTime >= totalTime) {
                    finish();
                    RxTimer.cancel();
                    startActivity(MainActivity.class);
                }
            }
        });
    }

    private void initComponent() {
        // 初始化MobSDK(需用户同意隐私政策后调用)
        MobSDK.init(this, BuildConfig.MOB_KEY, BuildConfig.MOB_SECRET);

        MobSDK.submitPolicyGrantResult(true);
        viewModel.loadVIPInfo();

        String string = Constants.BREAK_PAGE;
        if (!TextUtils.isEmpty(string)) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BUNDLE.KEY, 2);  //2 默认选中黄金vip
            startContainerActivity(string, bundle);
        }
        UMConfigure.init(SplashActivity.this, UMConfigure.DEVICE_TYPE_PHONE, "");             // 初始化友盟SDK (需用户同意隐私政策后调用)

        preVerify(); // 1.4版本无秒验

        RxJavaPlugins.setErrorHandler(throwable -> Observable.error(ExceptionHandle.handleException(throwable)));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Timber.e(e));

        try {
            OAIDHelper.getInstance().init(getApplicationContext());
            YoudaoSDK.init(getApplicationContext());
            YouDaoAd.getYouDaoOptions().setAllowSubmitInstalledPackageInfo(false);
            YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(false);
//            YouDaoAd.getYouDaoOptions().setAllowSubmitInstalledPackageInfo();
//            Date compileDate = AdTimeCheck.sdf.parse(BuildConfig.COMPILE_DATETIME);
//            AdBlocker.getInstance().setBlockStartDate(compileDate);
        } catch (Exception arg1) {
            arg1.printStackTrace();
        }
        /*CommonVars.domain = "aivoa.cn";
        CommonVars.domainLong = "aivoa.cn";*/
        //微课
        Lib.initIMOOC(getApplicationContext(), String.valueOf(appId), provision, privacy);

        Lib.initPersonalHome(getApplicationContext(), String.valueOf(appId));
        PersonalHome.setMainPath(MainActivity.class.getName());

        Lib.initTrainCamp(getApplicationContext(),String.valueOf(appId));
        PrivacyInfoHelper.getInstance().putApproved(true);
        Lib.initIHeadline(getApplicationContext(), String.valueOf(appId), getString(R.string.app_name));
        IHeadline.resetMseUrl();
        IHeadline.setExtraMseUrl(Constants.CONFIG.EVALUATE_URL);
        IHeadline.setExtraMergeAudioUrl(Constants.CONFIG.MERGE_URL);
//        IHeadline.setEnableShare(false);
//        HLDBManager.init(getApplicationContext());
        DLManager.init(getApplicationContext(), 5);  //下载
        BasicDLDBManager.init(getApplicationContext());
        BasicFavorDBManager.init(getApplicationContext());
        BasicFavor.init(getApplicationContext(), String.valueOf(appId));//收藏
    }

    /**
     * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
     */
    private void preVerify() {
        //设置在1000-10000之内
        SecVerify.setTimeOut(5000);
        //移动的debug tag 是CMCC-SDK,电信是CT_ 联通是PriorityAsyncTask
        SecVerify.setDebugMode(true);
//        SecVerify.setUseCache(true);
        SecVerify.preVerify(new PreVerifyCallback() {
            @Override
            public void onComplete(Void data) {
                if (BuildConfig.DEBUG) {
                    ToastUtils.showShort("预登录成功", Toast.LENGTH_LONG);
                }
                Constants.isPreVerifyDone = true;
                XLog.i("预登录成功");
                SecVerify.autoFinishOAuthPage(true);
//                SecVerify.setUiSettings(CustomizeUtils.customizeUi());
            }

            @Override
            public void onFailure(VerifyException e) {
                Constants.isPreVerifyDone = false;
                String errDetail = null;
                if (e != null) {
                    errDetail = e.getMessage();
                }
                XLog.i("onFailure errDetail " + errDetail);
                if (BuildConfig.DEBUG) {
                    // 登录失败
                    XLog.i("preVerify failed");
                    // 错误码
                    int errCode = e.getCode();
                    // 错误信息
                    String errMsg = e.getMessage();
                    // 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
                    String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
                    if (!TextUtils.isEmpty(errDetail)) {
                        msg += "\n详细信息: " + errDetail;
                    }
                    XLog.i(msg);
                    ToastUtils.showShort(msg);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxTimer.cancel();
    }
}
