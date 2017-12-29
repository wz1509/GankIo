package me.wangzheng.gankio.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import me.wangzheng.gankio.ui.view.BaseView;

public abstract class BaseFragment extends RxFragment implements BaseView {

    protected LoadService loadService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = View.inflate(getActivity(), getLayoutId(), null);

        loadService = LoadSir.getDefault().register(rootView, (Callback.OnReloadListener) this::onNetReload);
        return loadService.getLoadLayout();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (!isLazy()) {
            loadNet();
            initData();
        }
    }

    /**
     * 是否懒加载
     *
     * @return 默认false
     */
    protected boolean isLazy() {
        return false;
    }

    /**
     * 布局id
     *
     * @return id
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化view
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 这里获取数据，刷新界面
     */
    protected abstract void initData();

    protected abstract void loadNet();

    protected abstract void onNetReload(View v);

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return super.bindToLifecycle();
    }

}
