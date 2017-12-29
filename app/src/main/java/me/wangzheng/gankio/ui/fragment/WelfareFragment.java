package me.wangzheng.gankio.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kingja.loadsir.callback.SuccessCallback;

import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.application.App;
import me.wangzheng.gankio.base.BaseLazyFragment;
import me.wangzheng.gankio.contract.WelfareContract;
import me.wangzheng.gankio.di.component.DaggerGankIoComponent;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.presenter.WelfarePresenter;
import me.wangzheng.gankio.ui.activity.PhotoDetailsActivity;
import me.wangzheng.gankio.ui.adapter.WelfareAdapter;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;
import me.wangzheng.library.adapter.listener.OnRecyclerViewLoadMoreListener;

public class WelfareFragment extends BaseLazyFragment implements WelfareContract.View {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private WelfareAdapter mAdapter;

    @Inject
    WelfarePresenter mPresenter;

    @Override
    protected void onInvisible() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_refresh_recycler;
    }

    @Override
    protected void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        DaggerGankIoComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .gankIoModule(new GankIoModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void initData() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.getGankIoList(true));
        mAdapter = new WelfareAdapter(getActivity());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setLoadMoreListener(mRecyclerView, () -> mPresenter.getGankIoList(false));
        mAdapter.setOnItemClickListener((view, data, position) -> {
            final ImageView imageView = (ImageView) view.findViewById(R.id.item_photo_image);
            final Intent intent = PhotoDetailsActivity.newInstant(getActivity(), data);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    getActivity(), imageView, getString(R.string.transition_photos));
            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        });
    }

    @Override
    protected void loadNet() {
        mPresenter.getGankIoList(true);
        loadService.showCallback(LoadingCallback.class);
    }

    @Override
    protected void onNetReload(View v) {
        loadNet();
    }

    @Override
    public void onResultGankIoList(boolean isRefresh, List<GankEntity> list) {
        closeSwipeRefreshLayout();
        if (isRefresh) {
            mAdapter.setNewData(list);
        } else {
            mAdapter.addData(list);
        }
        loadService.showCallback(SuccessCallback.class);
    }

    @Override
    public void onFailed(boolean isRefresh, String msg) {
        closeSwipeRefreshLayout();
        if (isRefresh) {
            loadService.showCallback(ErrorCallback.class);
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void closeSwipeRefreshLayout() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
