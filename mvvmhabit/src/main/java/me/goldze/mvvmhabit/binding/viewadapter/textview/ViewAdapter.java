package me.goldze.mvvmhabit.binding.viewadapter.textview;

import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

/**
 * Created by goldze on 2017/6/18.
 */
public class ViewAdapter {
    @BindingAdapter({"richText"})
    public static void richText(TextView textView, final String html) {
        if (!TextUtils.isEmpty(html)) {
            textView.setText(Html.fromHtml(html));
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
