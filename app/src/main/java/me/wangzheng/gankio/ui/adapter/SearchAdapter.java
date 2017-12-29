package me.wangzheng.gankio.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.util.GlideUtils;
import me.wangzheng.library.adapter.BaseRecyclerViewAdapter;
import me.wangzheng.library.adapter.BaseViewHolder;

public class SearchAdapter extends BaseRecyclerViewAdapter<GankEntity> {

    public SearchAdapter(Context context) {
        super(context, R.layout.item_recycler_category_common);
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, GankEntity item, int position) {
        ((AppCompatImageView) holder.getView(R.id.item_gank_icon))
                .setImageResource(GlideUtils.getCategory(item.getType()));
        holder.setText(R.id.item_gank_desc, item.getDesc())
                .setText(R.id.item_gank_who, item.getType())
                .setText(R.id.item_gank_date, item.getWho());
    }
}
