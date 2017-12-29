package me.wangzheng.gankio.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.ui.view.BaseView;

public abstract class BaseCompatActivity extends RxAppCompatActivity implements BaseView {

    protected LoadService loadService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(this, R.layout.activity_base, null);
        if (hasToolbarTitle()) {
            ViewStub viewStub = (ViewStub) rootView.findViewById(R.id.vs_toolbar);
            viewStub.inflate();
        }
        final FrameLayout flContent = (FrameLayout) rootView.findViewById(R.id.fl_content);
        View content = View.inflate(this, getLayoutId(), null);
        if (content != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            flContent.addView(content, params);
            loadService = LoadSir.getDefault().register(content, (Callback.OnReloadListener) this::onNetReload);
        }
        setContentView(rootView);
        initNet();
        initView();
        initData();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract boolean hasToolbarTitle();

    protected abstract void initData();

    protected abstract void initNet();

    protected abstract void onNetReload(View v);

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return super.bindToLifecycle();
    }
}
