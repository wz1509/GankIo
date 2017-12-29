package me.wangzheng.gankio.model.network;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangzheng on 2017/12/18.
 */

public class RxSchedulers {

    /**
     * 线程切换
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> ioMain() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

}
