package me.wangzheng.gankio.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kingja.loadsir.callback.SuccessCallback;

import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.application.App;
import me.wangzheng.gankio.base.BaseLazyFragment;
import me.wangzheng.gankio.contract.XianduContract;
import me.wangzheng.gankio.di.component.DaggerGankIoComponent;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.model.XianduEntity;
import me.wangzheng.gankio.presenter.XianduPresenter;
import me.wangzheng.gankio.ui.activity.WebDetailActivity;
import me.wangzheng.gankio.ui.adapter.XianduAdapter;
import me.wangzheng.gankio.util.callback.EmptyCallback;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;

public class XianduFragment extends BaseLazyFragment implements XianduContract.View {

    private final String TAG = this.getClass().getSimpleName();
    private static final String KEY_URL = "key_url";

    public static Fragment newInstant(String category) {
        Fragment fragment = new XianduFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, category);
        fragment.setArguments(args);
        return fragment;
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private XianduAdapter mAdapter;

    @Inject
    XianduPresenter mPresenter;

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
        mSwipeRefreshLayout.setOnRefreshListener(() ->
                mPresenter.getXianduList(true, getUrl()));

        mAdapter = new XianduAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> mPresenter.getXianduList(false, getUrl()), mRecyclerView);
        mAdapter.setOnItemClickListener((view, data, position) -> {
            startActivity(WebDetailActivity.newInstant(getActivity(), data));
        });
    }

    @Override
    protected void loadNet() {
        loadService.showCallback(LoadingCallback.class);
        mPresenter.getXianduList(true, getUrl());
    }

    @Override
    protected void onNetReload(View v) {
        loadNet();
    }

    @Override
    public void onResultXianduList(boolean isRefresh, List<XianduEntity> list) {
        closeSwipeRefreshLayout();
        if (list == null || list.isEmpty()) {
            if (isRefresh) {
                loadService.showCallback(EmptyCallback.class);
            } else {
                mAdapter.loadMoreEnd();
            }
        } else {
            if (isRefresh) {
                mAdapter.setNewData(list);
            } else {
                mAdapter.addData(list);
            }
        }
        loadService.showCallback(SuccessCallback.class);
    }

    @Override
    public void onFailed(boolean isRefresh, String msg) {
        Log.e(TAG, "onFailed: " + msg);
        closeSwipeRefreshLayout();
        if (isRefresh) {
            loadService.showCallback(ErrorCallback.class);
        } else {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            mAdapter.loadMoreFail();
        }
    }

    private String getUrl() {
        return getArguments().getString(KEY_URL);
    }

    private void closeSwipeRefreshLayout() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
