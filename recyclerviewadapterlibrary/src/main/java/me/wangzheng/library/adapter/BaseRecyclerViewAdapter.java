package me.wangzheng.library.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

public abstract class BaseRecyclerViewAdapter<T> extends BaseMultiRecyclerViewAdapter<T> {

    private static final String TAG = BaseRecyclerViewAdapter.class.getSimpleName();

    private final static int TYPE_ITEM = 0x000001;

    private int mItemLayoutRes;

    public BaseRecyclerViewAdapter(Context context, @LayoutRes int itemLayoutRes) {
        super(context);
        this.mContext = context;
        this.mItemLayoutRes = itemLayoutRes;
    }

    @Override
    protected int addItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    protected BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.get(parent, mItemLayoutRes);
    }

}
