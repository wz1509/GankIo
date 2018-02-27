package com.wazing.baserecyclerviewadapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

public abstract class BaseRecyclerViewAdapter<T> extends BaseMultiRecyclerViewAdapter<T> {

    private final static int TYPE_ITEM = 10000;

    private final int mItemLayoutRes;

    public BaseRecyclerViewAdapter(Context context, @LayoutRes int itemLayoutRes) {
        super(context);
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
