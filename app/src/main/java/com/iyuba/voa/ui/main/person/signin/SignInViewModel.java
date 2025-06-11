package com.iyuba.voa.ui.main.person.signin;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.JsonObject;
import com.iyuba.module.toolbox.EnDecodeUtils;
import com.iyuba.voa.data.entity.StudyTime;
import com.iyuba.voa.data.entity.UserInfo;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.ui.base.BaseTitleViewModel;
import com.iyuba.voa.utils.Constants;
import com.iyuba.voa.utils.DateUtil;
import com.iyuba.voa.utils.QRCodeEncoder;
import com.iyuba.voa.utils.ThreadControl;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.ConvertUtils;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/8/19
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class SignInViewModel extends BaseTitleViewModel<AppRepository> {
    public UIChangeObservable UC = new UIChangeObservable();
    public ObservableField<String> avatar = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> backgroundUrl = new ObservableField<>();
    public ObservableField<String> shareMsg = new ObservableField<>("天天学习，每日红包！");
    public ObservableField<StudyTime> entity = new ObservableField<>();
    public ObservableField<Drawable> qrBitmap = new ObservableField<>();

    public ObservableBoolean isClickDaka = new ObservableBoolean(false);
    public BindingCommand<Void> clickSignIn = new BindingCommand<>(() -> {
        isClickDaka.set(true);
        uc.setRightTextClick.call();
    });
    public BindingCommand<Void> clickShare = new BindingCommand<>(() -> {
    });

    public SignInViewModel(@NonNull Application application, AppRepository model) {
        super(application, model);
    }

    public class UIChangeObservable {
        public SingleLiveEvent<String> loadDataSuccess = new SingleLiveEvent<>();

    }

    public void loadData(String shareId) {
        int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
        backgroundUrl.set("http://staticvip." + Constants.CONFIG.DOMAIN + "/images/mobile/" + day + ".jpg");

        UserInfo userInfo = model.roomGetUserDataById(model.spGetUid());
        avatar.set(userInfo.getImgSrc());
        userName.set(userInfo.getUsername());
        shareMsg.set("长按图片识别二维码");
        ThreadControl.EXECUTOR.submit(() -> {
            String qrIconUrl = "http://app." + Constants.CONFIG.DOMAIN + "/share.jsp?uid=" + model.spGetUid() + "&appId=" + Constants.CONFIG.appId + "&shareId=" + shareId;
            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(qrIconUrl, ConvertUtils.dp2px(65), Color.BLACK, Color.WHITE, null);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(Utils.getContext().getResources(), bitmap);
            qrBitmap.set(bitmapDrawable);
        });
    }

    public void startInterfaceAddScore() {
        String result = EnDecodeUtils.encode(DateUtil.getNowTime());
        String time = Base64.encodeToString(result.getBytes(), Base64.DEFAULT);
        model.updateScore(model.spGetUid(), time, "82")
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))
                .subscribe((Consumer<JsonObject>) jo -> {
                    if (jo.get("result").getAsString().equals("200")) {
                        String addcredit = jo.get("addcredit").getAsString();
                        String totalcredit = jo.get("totalcredit").getAsString();
                        DecimalFormat df = new DecimalFormat("#.##");  //保留两位小数
                        float addmoney = Float.parseFloat(addcredit);
                        float allmoney = Float.parseFloat(totalcredit);
                        String f1 = df.format(addmoney * 0.01);
                        String f2 = df.format(allmoney * 0.01);
//                        String msg = "分享成功," + " 获得" + f1 + "元,总计: " + f2 + "元," + "满十元可在\"爱语吧\"公众号提现";
                        String msg = "分享成功," + " 获得" + f1 + "元,总计: " + f2 + "元," + "满十元可联系客服提现";
                        UC.loadDataSuccess.setValue(msg);
                    } else {
                        String t = "今日已打卡，重复打卡不能再次获取红包或积分哦！";
                        UC.loadDataSuccess.setValue(t);
                    }
                });
    }
}
