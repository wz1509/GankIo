package me.wangzheng.gankio.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kingja.loadsir.callback.SuccessCallback;

import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.application.App;
import me.wangzheng.gankio.base.BaseCompatActivity;
import me.wangzheng.gankio.contract.SearchContract;
import me.wangzheng.gankio.di.component.DaggerGankIoComponent;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.presenter.SearchPresenter;
import me.wangzheng.gankio.ui.adapter.SearchAdapter;
import me.wangzheng.gankio.util.callback.EmptyCallback;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;

public class SearchActivity extends BaseCompatActivity implements SearchContract.View {

    public static Intent newInstant(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    private Toolbar mToolbar;
    private SearchView mSearchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private SearchAdapter mAdapter;

    @Inject
    SearchPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_refresh_recycler;
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        DaggerGankIoComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .gankIoModule(new GankIoModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected boolean hasToolbarTitle() {
        return true;
    }

    @Override
    protected void initData() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(null);
        mToolbar.setNavigationIcon(R.drawable.ic_vector_arrow_back);
        mToolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.white));
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.getQueryTextSubmit(mSearchView.getQuery().toString()));

        mAdapter = new SearchAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view, data, position) -> {
            startActivity(WebDetailActivity.newInstant(this, data));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        this.mSearchView = searchView;

        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.onActionViewExpanded();
        // 右边提交按钮
//        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(() -> {
            Toast.makeText(SearchActivity.this, "close", Toast.LENGTH_SHORT).show();
            return true;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPresenter.getQueryTextSubmit(query);
                loadService.showCallback(LoadingCallback.class);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void onNetReload(View v) {
        mSearchView.setQuery(mSearchView.getQuery(), true);
        loadService.showCallback(LoadingCallback.class);
    }

    @Override
    public void onResultGankList(List<GankEntity> list) {
        closeSwipeRefreshLayout();
        if (list.isEmpty()) {
            loadService.showCallback(EmptyCallback.class);
        } else {
            mAdapter.setNewData(list);
            loadService.showCallback(SuccessCallback.class);
        }
    }

    @Override
    public void onFailed(String msg) {
        closeSwipeRefreshLayout();
        loadService.showCallback(ErrorCallback.class);
    }

    private void closeSwipeRefreshLayout() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
