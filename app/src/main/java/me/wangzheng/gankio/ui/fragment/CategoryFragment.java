package me.wangzheng.gankio.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kingja.loadsir.callback.SuccessCallback;

import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.application.App;
import me.wangzheng.gankio.base.BaseLazyFragment;
import me.wangzheng.gankio.contract.CategoryContract;
import me.wangzheng.gankio.di.component.DaggerGankIoComponent;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.presenter.CategoryPresenter;
import me.wangzheng.gankio.ui.activity.PhotoDetailsActivity;
import me.wangzheng.gankio.ui.activity.WebDetailActivity;
import me.wangzheng.gankio.ui.adapter.CategoryAdapter;
import me.wangzheng.gankio.util.callback.EmptyCallback;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;

/**
 * Created by wangzheng on 2017/12/18.
 * desc:干货-分类
 */
public class CategoryFragment extends BaseLazyFragment implements CategoryContract.View {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private View mHeaderView;
    private AppCompatImageView mCategoryIcon;
    private TextView mCategoryName;
    private View mChooseCategory;

    private String mCategory = "all";
    private CategoryAdapter mAdapter;

    private View mBottomSheetView;
    private BottomSheetDialog mBottomSheetDialog;

    @Inject
    CategoryPresenter mPresenter;

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

        mHeaderView = View.inflate(getContext(), R.layout.header_gankio_category, null);
        mCategoryIcon = (AppCompatImageView) mHeaderView.findViewById(R.id.tv_category_icon);
        mCategoryName = (TextView) mHeaderView.findViewById(R.id.tv_category_name);
        mChooseCategory = mHeaderView.findViewById(R.id.ll_choose_category);

        mBottomSheetView = View.inflate(getContext(), R.layout.view_bottom_sheet_gank_category, null);

        DaggerGankIoComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .gankIoModule(new GankIoModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void initData() {
        mCategoryIcon.setImageResource(R.drawable.ic_vector_category_all);
        mCategoryName.setText(R.string.default_category_all);
        mChooseCategory.setOnClickListener(v -> {
            if (mBottomSheetDialog == null) {
                mBottomSheetDialog = new BottomSheetDialog(getActivity());
                mBottomSheetDialog.setContentView(mBottomSheetView);
                if (mBottomSheetView instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) mBottomSheetView).getChildCount(); i++) {
                        ((ViewGroup) mBottomSheetView).getChildAt(i).setOnClickListener(view -> {
                            if (view instanceof TextView) {
                                final TextView textView = (TextView) view;
                                mCategoryIcon.setImageDrawable(textView.getCompoundDrawables()[0]);
                                mCategoryName.setText(textView.getText());
                                mCategory = view.getTag().toString();
                                mPresenter.getGankIoList(true, mCategory);
                                mSwipeRefreshLayout.setRefreshing(true);
                                if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
                                    mBottomSheetDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
            if (mBottomSheetDialog.isShowing()) {
                mBottomSheetDialog.dismiss();
            } else {
                mBottomSheetDialog.show();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.getGankIoList(true, mCategory));

        mAdapter = new CategoryAdapter(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view, data, position) -> {
            if ("福利".equals(data.getType())) {
                final ImageView imageView = (ImageView) view.findViewById(R.id.item_gank_image);
                final Intent intent = PhotoDetailsActivity.newInstant(getActivity(), data);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        getActivity(), imageView, getString(R.string.transition_photos));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            } else {
                startActivity(WebDetailActivity.newInstant(getActivity(), data));
            }
        });

        mAdapter.addHeaderView(mHeaderView);
        mAdapter.setOnLoadMoreListener(() -> mPresenter.getGankIoList(false, mCategory), mRecyclerView);
    }

    @Override
    protected void loadNet() {
        mPresenter.getGankIoList(true, mCategory);
        loadService.showCallback(LoadingCallback.class);
    }

    @Override
    protected void onNetReload(View v) {
        mPresenter.getGankIoList(true, mCategory);
        loadService.showCallback(LoadingCallback.class);
    }

    @Override
    public void onResultGankIoList(boolean isRefresh, List<GankEntity> list) {
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
        closeSwipeRefreshLayout();
        if (isRefresh) {
            loadService.showCallback(ErrorCallback.class);
        } else {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            mAdapter.loadMoreFail();
        }
    }

    private void closeSwipeRefreshLayout() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
