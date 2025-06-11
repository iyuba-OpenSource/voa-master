package com.iyuba.voa.app.manage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iyuba.voa.app.Injection;
import com.iyuba.voa.data.repository.AppRepository;

import me.goldze.mvvmhabit.base.ContainerActivity;

public class ActivityManager {
    private static AppRepository appRepository;
    private static ActivityManager instanace;
//引用context的类 最好不要全局context 且写成单例
    ActivityManager() {

    }

    public static ActivityManager getInstance() {
        appRepository = Injection.provideAppRepository();
        if (instanace == null)
            instanace = new ActivityManager();
        return instanace;
    }

    public void startContainerActivity(Context context, String canonicalName, Bundle bundle) {
        Intent intent = new Intent(context, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        context.startActivity(intent);
    }


    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(Context context, String canonicalName) {
        startContainerActivity(context, canonicalName, null);
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Context context, Class<?> clz) {
        startActivity(context, clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
