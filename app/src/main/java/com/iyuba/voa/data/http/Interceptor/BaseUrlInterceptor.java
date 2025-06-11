package com.iyuba.voa.data.http.Interceptor;


import com.elvishew.xlog.XLog;
import com.iyuba.voa.utils.Constants;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/3/31
 * 邮箱：jxfengmtx@gmail.com
 */
public class BaseUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取request
        Request request = chain.request();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        //从request中获取headers，通过给定的键url_name
        List<String> headerValues = request.headers("urlPrefix");
        if (headerValues != null && headerValues.size() > 0) {
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder.removeHeader("urlPrefix");
            //匹配获得新的BaseUrl
            String headerValue = headerValues.get(0);
            HttpUrl newBaseUrl = oldHttpUrl;

            String newHost = Constants.CONFIG.API_HOST_APPS;
            String[] bit = newHost.split("\\.");

            if (headerValue.endsWith(".")) {  //后面加.代表.com.cn
                headerValue = headerValue.replaceFirst("\\.", "");   //删掉.
//                newHost = newHost.replace(".cn", ".com.cn");
                newHost = Constants.CONFIG.API_COM_DOMAIN;
            }
            if (bit.length > 0)
                newHost = newHost.replaceFirst(bit[0], headerValue);

            if (headerValue.equals("updateDomain")) {
                newBaseUrl = oldHttpUrl
                        .newBuilder()
                        .scheme("http")//更换网络协议
                        .host(Constants.CONFIG.DOMAI_IP)//更换主机名
                        .port(Constants.CONFIG.DOMAI_PORT)//更换端口
//                    .removePathSegment(0)//移除第一个参数
                        .build();
            } else {
                //重建新的HttpUrl，修改需要修改的url部分
                newBaseUrl = oldHttpUrl
                        .newBuilder()
                        .scheme("http")//更换网络协议
                        .host(newHost)//更换主机名
                        .port(headerValue.equals("iuserspeech") ? 9001 : 80)//更换端口
//                    .removePathSegment(0)//移除第一个参数
                        .build();
            }


/*
            switch (headerValue) {
                case "apps":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_APPS);
                    break;
                case "api":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_API);
                    break;
                case "apis":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_APIS);
                    break;
                case "ai":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_AI);
                    break;
                case "cms":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_CMS);
                    break;
                case "word":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_WORD);
                    break;
                case "voa":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_VOA);
                    break;
                case "daxue":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_DAXUE);
                    break;
                case "vip":
                    newBaseUrl = HttpUrl.parse(BuildConfig.API_HOST_INQ_VIP);
                    break;
                default:
                    newBaseUrl = oldHttpUrl;
            }*/

            //重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            XLog.i("Url", "intercept: " + newBaseUrl);
            return chain.proceed(builder.url(newBaseUrl).build());
        }
        return chain.proceed(request);
    }
}
