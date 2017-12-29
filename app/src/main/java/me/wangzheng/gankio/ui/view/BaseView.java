package me.wangzheng.gankio.ui.view;

import com.trello.rxlifecycle2.LifecycleTransformer;

public interface BaseView {

    /**
     * 绑定生命周期
     *
     * @param <T>
     * @return
     */
    <T> LifecycleTransformer<T> bindToLife();

}
