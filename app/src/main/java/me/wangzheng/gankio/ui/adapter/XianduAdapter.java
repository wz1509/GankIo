package me.wangzheng.gankio.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wazing.baserecyclerviewadapter.BaseRecyclerViewAdapter;
import com.wazing.baserecyclerviewadapter.BaseViewHolder;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.model.XianduEntity;

public class XianduAdapter extends BaseRecyclerViewAdapter<XianduEntity> {

    public XianduAdapter(Context context) {
        super(context, R.layout.item_recycler_category_common);
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, XianduEntity item, int position) {
        Glide.with(mContext)
                .load(item.getCategoryIcon())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView) holder.getView(R.id.item_gank_icon));
        holder.setText(R.id.item_gank_desc, item.getDesc())
                .setText(R.id.item_gank_who, item.getCategoryName())
                .setText(R.id.item_gank_date, item.getDate());
    }

}
