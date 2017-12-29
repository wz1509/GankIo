package me.wangzheng.gankio.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by wangzheng on 2017/12/18.
 */

public abstract class BaseLazyFragment extends BaseFragment{

    private boolean isVisible = false;
    private boolean isPrepared = false;
    private boolean isFirst = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 懒加载
     */
    private void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        loadNet();
        initData();
        isFirst = false;
    }

    @Override
    protected boolean isLazy() {
        return true;
    }

    /**
     * fragment被设置为不可见时调用
     */
    protected abstract void onInvisible();

}
