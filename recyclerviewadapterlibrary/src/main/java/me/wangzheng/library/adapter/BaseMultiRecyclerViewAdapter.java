package me.wangzheng.library.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import me.wangzheng.library.adapter.listener.OnItemChildClickListener;
import me.wangzheng.library.adapter.listener.OnItemClickListener;
import me.wangzheng.library.adapter.listener.OnRecyclerViewLoadMoreListener;

public abstract class BaseMultiRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private final static int TYPE_HEADER = 0x100001;
    private final static int TYPE_FOOTER = 0x100002;

    private boolean isLoadMore = false;

    protected Context mContext;
    private LinearLayout mHeaderLayout, mFooterLayout;
    private RecyclerView mRecyclerView;
    private List<T> mList = new ArrayList<>();

    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemChildClickListener<T> mOnItemChildClickListener;

    public BaseMultiRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener<T> onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
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
        count += mList.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeaderLayout()) {
            return TYPE_HEADER;
        } else if (position + 1 == getItemCount() && hasFooterLayout()) {
            return TYPE_FOOTER;
        }
        return addItemViewType(hasHeaderLayout() ? position - 1 : position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return BaseViewHolder.get(mHeaderLayout);
        } else if (viewType == TYPE_FOOTER) {
            return BaseViewHolder.get(mFooterLayout);
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

        // 这样是将子view全部添加事件，在 OnItemChildClickListener 回调里对 viewId 进行判断
//        if (holder.getView() instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) holder.getView();
//            for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                View v = viewGroup.getChildAt(i);
//                v.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (mOnItemChildClickListener != null) {
//                            int position = holder.getLayoutPosition();
//                            if (hasHeaderLayout()) {
//                                position--;
//                            }
//                            final T item = mList.get(position);
//                            mOnItemChildClickListener.onItemChildClick(v, item, position);
//                        }
//                    }
//                });
//            }
//        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: itemType = " + holder.getItemViewType() + " position = " + position);
        if (holder.getItemViewType() == TYPE_HEADER || holder.getItemViewType() == TYPE_FOOTER) {
            return;
        }
        if (holder.getItemViewType() == getItemViewType(position)) {
            if (hasHeaderLayout())
                position--;
            final T item = mList.get(position);
            onBindBaseViewHolder(holder, item, position);
        }
    }

    protected abstract int addItemViewType(int position);

    protected abstract BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindBaseViewHolder(BaseViewHolder holder, T item, int position);

    private boolean hasHeaderLayout() {
        return !(mHeaderLayout == null || mHeaderLayout.getChildCount() == 0);
    }

    private boolean hasFooterLayout() {
        return !(mFooterLayout == null || mFooterLayout.getChildCount() == 0);
    }

    public void setNewData(@Nullable List<T> data) {
        if (data != null && data.size() > 0) {
            mList.clear();
//            int positionStart = 0;
//            if (hasFooterLayout()) {
//                positionStart++;
//            }
            mList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addData(@Nullable T data) {
        final List<T> list = new ArrayList<>();
        list.add(data);
        addData(list);
    }

    public void addData(@Nullable List<T> data) {
        if (data != null && data.size() > 0) {
            int listSize = getItemCount();
            if (hasFooterLayout()) {
                listSize--;
            }
            mList.addAll(mList.size(), data);
            notifyItemRangeInserted(listSize, data.size());
        }
    }

    public List<T> getData() {
        return mList;
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
        if (mRecyclerView != null)
            mRecyclerView.scrollToPosition(0);
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
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(getItemCount() - 1);
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

    public void setLoadMoreListener(@NonNull RecyclerView recyclerView,
                                    @NonNull final OnRecyclerViewLoadMoreListener listener) {
        isLoadMore = true;
        this.mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onLoadMore() {
                if (isLoadMore) {
                    listener.onLoadMore();
                }
            }
        });
    }

    /**
     * 设置是否加载更多
     *
     * @param enable true 加载更多，false 禁止加载更多
     */
    public void setLoadMoreEnable(boolean enable) {
        isLoadMore = enable;
    }

    private abstract static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener
            implements OnRecyclerViewLoadMoreListener {

        // 最后几个完全可见项的位置（瀑布式布局会出现这种情况）
        private int[] lastCompletelyVisiblePositions;
        // 最后一个完全可见项的位置
        private int lastCompletelyVisibleItemPosition;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 找到最后一个完全可见项的位置
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
                if (lastCompletelyVisiblePositions == null) {
                    lastCompletelyVisiblePositions = new int[manager.getSpanCount()];
                }
                manager.findLastCompletelyVisibleItemPositions(lastCompletelyVisiblePositions);
                lastCompletelyVisibleItemPosition = getMaxPosition(lastCompletelyVisiblePositions);
            } else if (layoutManager instanceof GridLayoutManager) {
                lastCompletelyVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            } else if (layoutManager instanceof LinearLayoutManager) {
                lastCompletelyVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            } else {
                throw new RuntimeException("Unsupported LayoutManager.");
            }
        }

        private int getMaxPosition(int[] positions) {
            int max = positions[0];
            for (int i = 1; i < positions.length; i++) {
                if (positions[i] > max) {
                    max = positions[i];
                }
            }
            return max;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            // 通过比对 最后完全可见项位置 和 总条目数，来判断是否滑动到底部
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (visibleItemCount > 0 && lastCompletelyVisibleItemPosition >= totalItemCount - 1) {
                    onLoadMore();
                }
            }
        }
    }

}
