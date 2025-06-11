package com.iyuba.voa.data.http.observer;

import android.app.job.JobScheduler;

import com.elvishew.xlog.XLog;
import com.google.gson.JsonObject;

import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.NetworkUtil;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 * 统一的Code封装处理。该类仅供参考，实际业务逻辑, 根据需求来定义，
 * <p>
 * 适用于json单层，简单封装
 */

public abstract class SimpleDisposableObserver<T> extends DisposableObserver<T> {
    public abstract void onResult(T t);

    @Override
    public void onComplete() {

    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        onComplete();
        if (e instanceof ResponseThrowable) {
            ResponseThrowable rError = (ResponseThrowable) e;
            ToastUtils.showShort(rError.message);
            return;
        }
        //其他全部甩锅网络异常
        ToastUtils.showShort("网络异常");
    }

    @Override
    public void onStart() {
        super.onStart();
        XLog.i("http is start");
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtil.isNetworkAvailable(Utils.getContext())) {
            XLog.d("无网络，读取缓存数据");
            onComplete();
        }
    }

    @Override
    public void onNext(T o) {
        if (o instanceof JsonObject) {
            onResult(o);
            return;
        }
        //有totalpage 或 reselt的才可用此接口
        BaseResponse baseResponse = (BaseResponse) o;
        if (baseResponse.getTotal() > 0) {
            onResult(o);
            return;
        }
        if (null == baseResponse.getResult()) {
            ToastUtils.showShort("无数据");
            return;
        }
        switch (baseResponse.getResult()) {
            case "1":
            case "111":
            case "911":
            case "101":
            case "201":
                onResult(o);
                break;
            default:
                String message = baseResponse.getMessage();
                if (message.contains("password")) {
                    ToastUtils.showShort("密码验证失败");
                    break;
                }
                if (message.contains("registed")) {
                    ToastUtils.showShort("用户已经注册");
                    break;
                }
                ToastUtils.showShort(message);
                break;
        }

        onComplete();

//        switch (baseResponse.getStatus()) {
//            case CodeRule.CODE_200:
//                //请求成功, 正确的操作方式
//                onResult((T) baseResponse.getData());
//                break;
//            case CodeRule.CODE_500:
//                //请求错误, 正确的操作方式
//                ToastUtils.showShortSafe(baseResponse.getMessage());
//                break;
//            case CodeRule.CODE_10:
////                onResult((T) baseResponse.getData());
//                onComplete();
//                break;
//            case CodeRule.CODE_220:
//                // 请求成功, 正确的操作方式, 并消息提示
////                onResult((T) baseResponse.getData());
//                break;
//            case CodeRule.CODE_300:
//                //请求失败，不打印Message
//                XLog.e("请求失败");
//                ToastUtils.showShortSafe("错误代码:", baseResponse.getStatus());
//                break;
//            case CodeRule.CODE_330:
//                //请求失败，打印Message
//                ToastUtils.showShort(baseResponse.getMessage());
//                break;
//     /*       case CodeRule.CODE_500:
//                //服务器内部异常
//                ToastUtils.showShortSafe("错误代码:", baseResponse.getStatus());
//                break;*/
//            case CodeRule.CODE_503:
//                //参数为空
//                XLog.e("参数为空");
//                break;
//            case CodeRule.CODE_502:
//                //没有数据
//                XLog.e("没有数据");
//                break;
//            case CodeRule.CODE_510:
//                //无效的Token，提示跳入登录页
//                ToastUtils.showShortSafe("token已过期，请重新登录");
//                //关闭所有页面
//                AppManager.getAppManager().finishAllActivity();
//                //跳入登录界面
////                Intent intent = new Intent(Utils.getContext(), LoginActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
////                Utils.getContext().startActivity(intent);
//                //*****该类仅供参考，实际业务Code, 根据需求来定义，******//
//                break;
//            case CodeRule.CODE_530:
//                ToastUtils.showShortSafe("请先登录");
//                break;
//            case CodeRule.CODE_551:
//                ToastUtils.showShortSafe("错误代码:", baseResponse.getStatus());
//                break;
//            default:
//                ToastUtils.showShortSafe("错误代码:", baseResponse.getStatus());
//                break;
//        }
    }

    public static final class CodeRule {
        static final int CODE_0 = 0;
        static final int CODE_1 = 1;
        static final int CODE_10 = 10;
        static final int CODE_11 = 11;

        //请求成功, 正确的操作方式
        static final int CODE_200 = 200;
        //请求成功, 消息提示
        static final int CODE_220 = 220;
        //请求失败，不打印Message
        static final int CODE_300 = 300;
        //请求失败，打印Message
        static final int CODE_330 = 330;
        //服务器内部异常
        static final int CODE_500 = 500;
        //参数为空
        static final int CODE_503 = 503;
        //没有数据
        static final int CODE_502 = 502;
        //无效的Token
        static final int CODE_510 = 510;
        //未登录
        static final int CODE_530 = 530;
        //请求的操作异常终止：未知的页面类型
        static final int CODE_551 = 551;
    }
}