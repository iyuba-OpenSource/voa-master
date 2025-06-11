package me.goldze.mvvmhabit.http.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.goldze.mvvmhabit.utils.SPUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by goldze on 2017/5/10.
 */
public class BaseInterceptor implements Interceptor {
    private Map<String, String> headers;

    public BaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder();
        String token = SPUtils.getInstance().getString("token");
        if (!TextUtils.isEmpty(token)) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put("Authorization", "Bearer "+ token);
        }
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        //请求信息
        return chain.proceed(builder.build());
    }
}