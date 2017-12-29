package me.wangzheng.gankio.ui.adapter;

import android.content.Context;

import me.wangzheng.gankio.R;
import me.wangzheng.library.adapter.BaseRecyclerViewAdapter;
import me.wangzheng.library.adapter.BaseViewHolder;

public class BottomSheetAdapter extends BaseRecyclerViewAdapter<String> {

    public BottomSheetAdapter(Context context) {
        super(context, R.layout.item_recycler_bottom_sheet_date);
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, String item, int position) {
        holder.setText(R.id.item_date, item);
    }
}
