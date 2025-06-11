package com.iyuba.voa.app;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.iyuba.voa.data.repository.AppRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * 版权：heihei
 *
 * @author JiangFB
 * 版本：1.0
 * 创建日期：2022/4/7
 * 邮箱：jxfengmtx@gmail.com
 */
public class AppViewModelFactory implements ViewModelProvider.Factory {
    @SuppressLint("StaticFieldLeak")
    private static volatile AppViewModelFactory INSTANCE;
    private final Application mApplication;
    private final AppRepository mRepository;

    public static AppViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppViewModelFactory(application, Injection.provideAppRepository());
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private AppViewModelFactory(Application application, AppRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (BaseViewModel.class.isAssignableFrom(modelClass)) {  //BaseViewModel 是否是modelclass父类
//            return (T) new BaseViewModel(mApplication, mRepository);
//        }
        String className = modelClass.getCanonicalName();
        Class<?> classViewModel;
        try {
            classViewModel = Class.forName(className);
            Constructor<?> cons = classViewModel.getConstructor(Application.class, AppRepository.class);
            ViewModel viewModel = (ViewModel) cons.newInstance(mApplication, mRepository);
            return (T) viewModel;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
//        }

       /* switch (modelClass.getSimpleName()) {
            case "LoginViewModel":
                return (T) new LoginViewModel(mApplication, mRepository);
            case "TabHomeViewModel":
                return (T) new TabHomeViewModel(mApplication, mRepository);
        }*/
       /* if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(TabHomeViewModel.class)) {
            return (T) new TabHomeViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(PdOddViewModel.class)) {
            return (T) new PdOddViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ZcpdViewModel.class)) {
            return (T) new ZcpdViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(XjOddViewModel.class)) {
            return (T) new ZcpdViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ZcxjViewModel.class)) {
            return (T) new ZcpdViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(WxOddViewModel.class)) {
            return (T) new ZcpdViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ZcwxViewModel.class)) {
            return (T) new ZcpdViewModel(mApplication, mRepository);
        }*/
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
