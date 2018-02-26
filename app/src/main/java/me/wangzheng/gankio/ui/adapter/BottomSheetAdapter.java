package me.wangzheng.gankio.ui.adapter;

import android.content.Context;

import com.wazing.baserecyclerviewadapter.BaseRecyclerViewAdapter;
import com.wazing.baserecyclerviewadapter.BaseViewHolder;

import me.wangzheng.gankio.R;

public class BottomSheetAdapter extends BaseRecyclerViewAdapter<String> {

    public BottomSheetAdapter(Context context) {
        super(context, R.layout.item_recycler_bottom_sheet_date);
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, String item, int position) {
        holder.setText(R.id.item_date, item);
    }
}
