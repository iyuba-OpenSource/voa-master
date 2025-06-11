package me.goldze.mvvmhabit.utils;

import android.text.InputType;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/6
 * 邮箱：jxfengmtx@gmail.com
 * @description
 */
public class MaterialDialogUtil {
    public static MaterialDialog showLoadingDialog(AppCompatActivity activity, String title) {
        MaterialDialog waitForDialog = new MaterialDialog.Builder(activity)
                .content(TextUtils.isEmpty(title) ? "请稍后..." : title)
                .progress(true, -1)//等待图标 true=圆形icon false=进度条
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        return waitForDialog;
    }

    public static void showMsgDialog(AppCompatActivity activity, String msg) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .content(msg)
                .positiveText("确定")
                .negativeText("取消")
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }

    public static void showListDialog(AppCompatActivity activity, String title,
                                      List<String> lists, MaterialDialog.ListCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(title)
                .items(lists)
                .itemsCallback(callback)
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }

    public static void showInputDialog(AppCompatActivity activity, String title,
                                       String hint, MaterialDialog.InputCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(title)
                .input(hint, "", callback)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .positiveText("确定")
                .negativeText("取消")
                .canceledOnTouchOutside(false)//点击外部不取消对话框
                .build();
        dialog.show();
    }
}
