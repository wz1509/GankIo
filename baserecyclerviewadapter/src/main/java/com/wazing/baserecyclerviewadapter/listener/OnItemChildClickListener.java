package com.wazing.baserecyclerviewadapter.listener;

import android.view.View;

public interface OnItemChildClickListener<T> {

    void onItemChildClick(View view, T data, int position);
}
