package com.iyuba.voa.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elvishew.xlog.XLog;
import com.iyuba.voa.ui.main.MainActivity;
import com.iyuba.voa.ui.main.home.detail.DetailActivity;

import me.goldze.mvvmhabit.base.AppManager;

public class NotificationClickReceiver extends BroadcastReceiver {
    public static final String TAG = "NotificationClickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.d(TAG, "通知栏点击");
        if (AppManager.getAppManager().getActivity(DetailActivity.class) == null) {
            if (AppManager.getAppManager().isActivity()) {
                intent.setClass(context, DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }

            AppManager.getAppManager().finishAllActivity();
            // TODO: 2022/8/11  卡屏不报错
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClass(context, DetailActivity.class);
            Intent[] intents = {intent1, intent};
            context.startActivities(intents);
        }
        /*else {
            Activity currentActivity = AppManager.getAppManager().currentActivity();
            intent.setClass(context, currentActivity == null ? MainActivity.class : currentActivity.getClass());
            context.startActivity(intent);
        }*/
    }
}
