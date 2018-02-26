package me.wangzheng.gankio.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kingja.loadsir.callback.SuccessCallback;

import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.application.App;
import me.wangzheng.gankio.base.BaseLazyFragment;
import me.wangzheng.gankio.contract.GankIoContract;
import me.wangzheng.gankio.di.component.DaggerGankIoComponent;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.presenter.TodayPresenter;
import me.wangzheng.gankio.ui.activity.PhotoDetailsActivity;
import me.wangzheng.gankio.ui.activity.WebDetailActivity;
import me.wangzheng.gankio.ui.adapter.BottomSheetAdapter;
import me.wangzheng.gankio.ui.adapter.TodayAdapter;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;

public class TodayFragment extends BaseLazyFragment implements GankIoContract.View {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private FloatingActionButton mFabHistory;
    private View mBottomSheetView;
    private TextView mBottomSheetTitle;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetAdapter mBottomSheetAdapter;

    private TodayAdapter mAdapter;

    @Inject
    TodayPresenter presenter;

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

        mFabHistory = (FloatingActionButton) view.findViewById(R.id.fab_history);
        mFabHistory.setVisibility(View.VISIBLE);

        mBottomSheetView = View.inflate(getActivity(), R.layout.view_bottom_sheet_gank_history, null);
        mBottomSheetTitle = (TextView) mBottomSheetView.findViewById(R.id.tv_title);

        DaggerGankIoComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .gankIoModule(new GankIoModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void initData() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> presenter.getGankIoDayList());

        mAdapter = new TodayAdapter(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOpenLoadMore(false);
        mAdapter.setOnItemClickListener((view, item, position) -> {
            if ("福利".equals(item.getType())) {
                final ImageView imageView = (ImageView) view.findViewById(R.id.item_gank_image);
                final Intent intent = PhotoDetailsActivity.newInstant(getActivity(), item);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), imageView, getString(R.string.transition_photos));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            } else {
                startActivity(WebDetailActivity.newInstant(getActivity(), item));
            }
        });

        // FloatingActionButton 按钮以及一些事件
        final RecyclerView recyclerView = (RecyclerView) mBottomSheetView.findViewById(R.id.recycler_view);
        mBottomSheetAdapter = new BottomSheetAdapter(getContext());
        mBottomSheetAdapter.setOnItemClickListener((view, item, position) -> {
            if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
                mBottomSheetDialog.dismiss();
            }
            mBottomSheetTitle.setText("今日干货：" + item);

            mRecyclerView.smoothScrollToPosition(0);
            mSwipeRefreshLayout.setRefreshing(true);
            presenter.getGankIoDayList(item);
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(mBottomSheetAdapter);

        mFabHistory.setOnClickListener(v -> {
            if (mBottomSheetAdapter.getData().isEmpty()) {
                Snackbar.make(v, "请刷新后再访问", Snackbar.LENGTH_LONG)
                        .setAction("Ok", v1 -> {
                        })
                        .show();
                return;
            }

            if (mBottomSheetDialog == null) {
                mBottomSheetDialog = new BottomSheetDialog(getActivity());
                mBottomSheetDialog.setContentView(mBottomSheetView);
            }
            mBottomSheetDialog.show();
        });
    }

    @Override
    public void onResultGankIoList(List<GankEntity> list) {
        closeSwipeRefreshLayout();
        mAdapter.setNewData(list);
        loadService.showCallback(SuccessCallback.class);
    }

    @Override
    public void onResultHistoryList(List<String> list) {
        mBottomSheetTitle.setText("今日干货：" + list.get(0));
        mBottomSheetAdapter.setNewData(list);
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

    @Override
    protected void loadNet() {
        loadService.showCallback(LoadingCallback.class);
        presenter.getGankIoDayList();
    }

    @Override
    protected void onNetReload(View v) {
        loadService.showCallback(LoadingCallback.class);
        presenter.getGankIoDayList();
    }

}
