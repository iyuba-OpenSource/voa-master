package com.iyuba.voa.app;


import com.iyuba.voa.data.http.HttpService;
import com.iyuba.voa.data.http.RetrofitClient;
import com.iyuba.voa.data.repository.AppRepository;
import com.iyuba.voa.data.source.HttpDataSource;
import com.iyuba.voa.data.source.HttpDataSourceImpl;
import com.iyuba.voa.data.source.LocalDataSource;
import com.iyuba.voa.data.source.LocalDataSourceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class Injection {
    public static Map<String, String> provideMapInstance() {
        return new HashMap<>();
    }

    public static AppRepository provideAppRepository() {
        //网络API服务
        HttpService httpService = RetrofitClient.getInstance().create(HttpService.class);
        //网络数据源
        HttpDataSource httpDataSource = HttpDataSourceImpl.getInstance(httpService);

        //创建数据库
        AppDatabase appDatabase = AppDatabase.getInstance();
        //本地数据源
        LocalDataSource localDataSource = LocalDataSourceImpl.getInstance(appDatabase);
        //两条分支组成一个数据仓库
        return AppRepository.getInstance(httpDataSource,localDataSource);
    }
}
