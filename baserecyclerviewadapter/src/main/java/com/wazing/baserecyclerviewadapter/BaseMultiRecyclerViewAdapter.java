package com.wazing.baserecyclerviewadapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wazing.baserecyclerviewadapter.listener.OnItemClickListener;
import com.wazing.baserecyclerviewadapter.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseMultiRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private final static int TYPE_HEADER = 10001;
    private final static int TYPE_FOOTER = 10002;
    private final static int TYPE_FOOTER_VIEW = 10003;

    protected Context mContext;
    private LinearLayout mHeaderLayout, mFooterLayout;

    private BaseViewHolder mFooterViewHolder;
    private boolean isOpenLoadMore = true;
    // 是否自动加载，当数据不满一屏幕会自动加载
    private boolean isAutoLoadMore = true;
    // 开始重新加载数据
    private boolean isReset;
    // 是否正在加载更多
    private boolean isLoading;
    // 所有数据加载完成
    private boolean isLoadMoreEnd;

    private List<T> mList = new ArrayList<>();

    private OnItemClickListener<T> mOnItemClickListener;
    private OnLoadMoreListener mLoadMoreListener;

    public BaseMultiRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener, RecyclerView recyclerView) {
        this.mLoadMoreListener = listener;
        startLoadMore(recyclerView, recyclerView.getLayoutManager());
    }

    public void setOpenLoadMore(boolean isOpenLoadMore) {
        this.isOpenLoadMore = isOpenLoadMore;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (hasHeaderLayout()) {
            count++;
        }
        if (hasFooterLayout()) {
            count++;
        }
        if (isOpenLoadMore) {
            count++;
        }
        count += mList.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {
//        Log.d(TAG, "getItemViewType: getItemCount() = " + getItemCount());
//        Log.d(TAG, "getItemViewType: position = " + position);
        if (position == 0 && hasHeaderLayout()) {
            return TYPE_HEADER;
        } else if (hasFooterLayout() && isOpenLoadMore && getItemCount() == (position + 2)) {
            return TYPE_FOOTER;
        } else if (hasFooterLayout() && !isOpenLoadMore && getItemCount() == position + 1) {
            return TYPE_FOOTER;
        } else if (isOpenLoadMore && getItemCount() == (position + 1)) {
            return TYPE_FOOTER_VIEW;
        }
//        else if (isOpenLoadMore && !hasFooterLayout() && getItemCount() == position + 1) {
//            return TYPE_FOOTER_VIEW;
//        }
        return addItemViewType(hasHeaderLayout() ? position - 1 : position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: viewType = " + viewType);
        if (viewType == TYPE_HEADER) {
            return BaseViewHolder.get(mHeaderLayout);
        } else if (viewType == TYPE_FOOTER) {
            return BaseViewHolder.get(mFooterLayout);
        } else if (viewType == TYPE_FOOTER_VIEW) {
            mFooterViewHolder = BaseViewHolder.get(parent, R.layout.base_adapter_layout_footer);
            mFooterViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLoading || isLoadMoreEnd) return;
                    scrollLoadMore();
                }
            });
            return mFooterViewHolder;
        }
        final BaseViewHolder holder = onCreateBaseViewHolder(parent, viewType);
        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = holder.getLayoutPosition();
                    if (hasHeaderLayout()) {
                        position--;
                    }
                    final T item = mList.get(position);
                    mOnItemClickListener.onItemClick(v, item, position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final int itemViewType = holder.getItemViewType();
//        Log.i(TAG, "onBindViewHolder: itemViewType = " + itemViewType);
        if (itemViewType == TYPE_HEADER || itemViewType == TYPE_FOOTER || itemViewType == TYPE_FOOTER_VIEW) {
            return;
        }
        if (holder.getItemViewType() == getItemViewType(position)) {
            if (hasHeaderLayout())
                position--;
            final T item = mList.get(position);
            onBindBaseViewHolder(holder, item, position);
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
//            Log.d(TAG, "onViewAttachedToWindow: StaggeredGridLayoutManager");
            final int position = holder.getLayoutPosition();
            if (isFooterView(position) || isHeaderView(position)
                    || (hasFooterLayout() && isOpenLoadMore && getItemCount() == position + 2)
                    || (hasFooterLayout() && !isOpenLoadMore && getItemCount() == position + 1)) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
//            Log.d(TAG, "onAttachedToRecyclerView: GridLayoutManager");
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return isFooterView(position) ? gridManager.getSpanCount() : 1;
                }
            });
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            Log.d(TAG, "onAttachedToRecyclerView: StaggeredGridLayoutManager");
//            final StaggeredGridLayoutManager sgm = (StaggeredGridLayoutManager) layoutManager;
        }
        startLoadMore(recyclerView, layoutManager);
    }

    private boolean isHeaderView(int position) {
        return hasHeaderLayout() && position == 0;
    }

    private boolean isFooterView(int position) {
//        Log.d(TAG, "isFooterView: position = " + position + " getItemCount() = " + getItemCount());
        return isOpenLoadMore && position >= getItemCount() - 1;
    }

    private void startLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
//        Log.d(TAG, "startLoadMore: isOpenLoadMore = " + isOpenLoadMore + " mLoadMoreListener = " + mLoadMoreListener);
        if (!isOpenLoadMore || mLoadMoreListener == null) {
            return;
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Log.d(TAG, "onScrollStateChanged: newState = " + newState);
//                Log.d(TAG, "onScrollStateChanged: isAutoLoadMore = " + isAutoLoadMore);
//                Log.d(TAG, "onScrollStateChanged: findLastVisibleItemPosition(layoutManager) = " + findLastVisibleItemPosition(layoutManager));
//                Log.d(TAG, "onScrollStateChanged: getItemCount() = " + getItemCount());
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        scrollLoadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.d(TAG, "onScrolled: dx = " + dx + " dy = " + dy);
//                Log.d(TAG, "onScrolled: isAutoLoadMore = " + isAutoLoadMore);
//                Log.d(TAG, "onScrolled: findLastVisibleItemPosition(layoutManager) = " + findLastVisibleItemPosition(layoutManager));
//                Log.d(TAG, "onScrolled: getItemCount() = " + getItemCount());
                if (isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                    scrollLoadMore();
                } else if (isAutoLoadMore) {
                    isAutoLoadMore = false;
                }
            }
        });
    }

    /**
     * 到达底部开始刷新
     */
    private void scrollLoadMore() {
        if (isReset) {
            return;
        }
        if (!isLoading && !isLoadMoreEnd) {
            if (mLoadMoreListener != null) {
                isLoading = true;
                loadMoreLoading();
                mLoadMoreListener.onLoadMore();
            }
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            return findMax(lastVisibleItemPositions);
        }
        return -1;
    }

    private int findMax(int[] lastVisiblePositions) {
        int max = lastVisiblePositions[0];
        for (int value : lastVisiblePositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    protected abstract int addItemViewType(int position);

    protected abstract BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindBaseViewHolder(BaseViewHolder holder, T item, int position);

    public List<T> getData() {
        return mList;
    }

    public void setNewData(@Nullable List<T> data) {
        if (isReset) isReset = false;
        isLoading = false;
        isAutoLoadMore = true;
        isLoadMoreEnd = false;

        if (data != null && data.size() > 0) {
            mList.clear();
            mList.addAll(data);
            loadMoreLoading();
            notifyDataSetChanged();
        }
    }

    public void addData(@Nullable T data) {
        addData(Collections.singletonList(data));
    }

    public void addData(@Nullable List<T> data) {
        isLoading = false;
        if (data != null && data.size() > 0) {
            int listSize = getItemCount();
            if (hasFooterLayout()) {
                listSize--;
            }
            mList.addAll(mList.size(), data);
            notifyItemRangeInserted(listSize, data.size());
        }
    }

    private void loadMoreLoading() {
        loadMoreStatus(true, mContext.getString(R.string.adapter_status_loading));
    }

    public void loadMoreFail() {
        loadMoreFail(mContext.getString(R.string.adapter_status_failed));
    }

    public void loadMoreFail(String message) {
        isLoading = false;
        loadMoreStatus(false, message);
    }

    public void loadMoreEnd() {
        loadMoreEnd(mContext.getString(R.string.adapter_status_no_data));
    }

    public void loadMoreEnd(String message) {
        isLoading = false;
        isLoadMoreEnd = true;
        loadMoreStatus(false, message);
    }

    private void loadMoreStatus(boolean isVisible, String message) {
        if (mFooterViewHolder == null) return;
        ProgressBar pb = mFooterViewHolder.getView(R.id.adapter_footer_pb);
        TextView tv = mFooterViewHolder.getView(R.id.adapter_footer_tv);

        pb.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        tv.setText(message);
    }

    private boolean hasHeaderLayout() {
        return !(mHeaderLayout == null || mHeaderLayout.getChildCount() == 0);
    }

    private boolean hasFooterLayout() {
        return !(mFooterLayout == null || mFooterLayout.getChildCount() == 0);
    }

    /**
     * 获取头部数量
     *
     * @return 头部数量
     */
    public int getHeaderCount() {
        return mHeaderLayout == null ? 0 : mHeaderLayout.getChildCount();
    }

    @Nullable
    public View getHeaderView(int index) {
        return mHeaderLayout != null ? mHeaderLayout.getChildAt(index) : null;
    }

    /**
     * 添加头部
     *
     * @param header view
     * @return
     */
    public int addHeaderView(View header) {
        return addHeaderView(header, getHeaderCount());
    }

    public int addHeaderView(View header, int index) {
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public int addHeaderView(View header, int index, int orientation) {
        if (header == null) {
            Log.e(TAG, "addHeaderView: headerView is null");
            return -1;
        }
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        int childCount = mHeaderLayout.getChildCount();
        if (index < 0) {
            index = 0;
        } else if (index > childCount) {
            index = childCount;
        }
        mHeaderLayout.addView(header, index);

        childCount = mHeaderLayout.getChildCount();
        if (childCount == 1) {
            notifyItemInserted(0);
        }
        return childCount;
    }

    public void removeHeaderView() {
        removeHeaderView(getHeaderCount() - 1);
    }

    public void removeHeaderView(int index) {
        if (mHeaderLayout != null) {
            View view = mHeaderLayout.getChildAt(index);
            removeHeaderView(view);
        }
    }

    public void removeHeaderView(@Nullable View view) {
        if (mHeaderLayout != null && view != null) {
            mHeaderLayout.removeView(view);
            if (mHeaderLayout.getChildCount() == 0) {
                notifyItemRangeRemoved(0, 1);
            }
        } else {
            Log.e(TAG, "removeHeaderView(View view) mHeaderLayout or view is null");
        }
    }

    /**
     * 获取尾部数量
     *
     * @return 尾部数量
     */
    public int getFooterCount() {
        return mFooterLayout == null ? 0 : mFooterLayout.getChildCount();
    }

    /**
     * 添加尾部
     *
     * @param footer
     * @return
     */
    public int addFooterView(View footer) {
        return addFooterView(footer, 0);
    }

    public int addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    public int addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            final LinearLayout.LayoutParams params;
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            mFooterLayout.setLayoutParams(params);
        }
        int childCount = mFooterLayout.getChildCount();
        if (index == 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        childCount = mFooterLayout.getChildCount();
        if (childCount == 1) {
            notifyItemInserted(getItemCount());
        }
        return childCount;
    }

    public void removeFooterView() {
        removeFooterView(getFooterCount() - 1);
    }

    public void removeFooterView(int index) {
        if (mFooterLayout != null) {
            View view = mFooterLayout.getChildAt(index);
            removeFooterView(view);
        }
    }

    public void removeFooterView(@Nullable View view) {
        if (mFooterLayout != null && view != null) {
            mFooterLayout.removeView(view);
            if (mFooterLayout.getChildCount() == 0) {
                notifyItemRangeRemoved(getItemCount(), 1);
            }
        } else {
            Log.e(TAG, "mFooterLayout(View view) mFooterLayout or view is null");
        }
    }

}
