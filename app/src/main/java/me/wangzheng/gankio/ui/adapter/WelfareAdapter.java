package me.wangzheng.gankio.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.library.adapter.BaseRecyclerViewAdapter;
import me.wangzheng.library.adapter.BaseViewHolder;

public class WelfareAdapter extends BaseRecyclerViewAdapter<GankEntity> {

    public WelfareAdapter(Context context) {
        super(context, R.layout.item_recycler_welfare);
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, GankEntity item, int position) {
        Glide.with(mContext)
                .load(item.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView) holder.getView(R.id.item_photo_image));
    }
}
