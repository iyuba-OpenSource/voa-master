package me.goldze.mvvmhabit.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ExceptionHandle;

/**
 * Created by goldze on 2017/6/19.
 * 有关Rx的工具类
 */
public class RxUtils {
    /**
     * 生命周期绑定
     *
     * @param lifecycle Activity
     */
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull Context lifecycle) {
        if (lifecycle instanceof LifecycleProvider) {
            return ((LifecycleProvider) lifecycle).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("context not the LifecycleProvider type");
        }
    }

    /**
     * 生命周期绑定
     *
     * @param lifecycle Fragment
     */
    public static LifecycleTransformer bindToLifecycle(@NonNull Fragment lifecycle) {
        if (lifecycle instanceof LifecycleProvider) {
            return ((LifecycleProvider) lifecycle).bindToLifecycle();
        } else {
            throw new IllegalArgumentException("fragment not the LifecycleProvider type");
        }
    }

    /**
     * 生命周期绑定
     *
     * @param lifecycle Fragment
     */
    public static LifecycleTransformer bindToLifecycle(@NonNull LifecycleProvider lifecycle) {
        return lifecycle.bindToLifecycle();
    }

    /**
     * 线程调度器
     */
    public static ObservableTransformer schedulersTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 本地数据库线程调度器
     */
    public static MaybeTransformer MaybeSchTransformer() {
        return new MaybeTransformer() {
            @Override
            public MaybeSource apply(Maybe upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static ObservableTransformer exceptionTransformer() {

        return observable -> observable
//                        .map(new HandleFuc<T>())  //这里可以取出BaseResponse中的Result
                .onErrorResumeNext(new HttpResponseFunc());
    }

    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

    private static class HandleFuc<T> implements Function<BaseResponse<T>, T> {
        @Override
        public T apply(BaseResponse<T> response) {
            if (!response.isOk())
                throw new RuntimeException(!"".equals(response.getStatus() + "" + response.getMessage()) ? response.getMessage() : "");
            return response.getData();
        }
    }

}
