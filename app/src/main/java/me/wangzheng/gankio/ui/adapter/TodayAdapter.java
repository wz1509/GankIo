package me.wangzheng.gankio.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wazing.baserecyclerviewadapter.BaseMultiRecyclerViewAdapter;
import com.wazing.baserecyclerviewadapter.BaseViewHolder;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.model.GankEntity;

public class TodayAdapter extends BaseMultiRecyclerViewAdapter<GankEntity> {

    private static final int COMMON = 0x1;
    private static final int CATEGORY = 0x2;
    private static final int IMAGE = 0x3;

    public TodayAdapter(Context context) {
        super(context);
    }

    @Override
    protected int addItemViewType(int position) {
        final GankEntity item = getData().get(position);
        if (!TextUtils.isEmpty(item.getCategory())) {
            return CATEGORY;
        } else if ("福利".equals(item.getType())) {
            return IMAGE;
        } else {
            return COMMON;
        }
    }

    @Override
    protected BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CATEGORY) {
            return BaseViewHolder.get(parent, R.layout.item_recycler_today_title);
        } else if (viewType == IMAGE) {
            return BaseViewHolder.get(parent, R.layout.item_recycler_today_image);
        } else {
            return BaseViewHolder.get(parent, R.layout.item_recycler_today_common);
        }
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, GankEntity item, int position) {
        Log.d("wazing", "onBindBaseViewHolder: " + item.toString());
        switch (holder.getItemViewType()) {
            case CATEGORY:
                holder.setText(R.id.item_category, item.getCategory());
                break;
            case IMAGE:
                Glide.with(mContext)
                        .load(item.getUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into((ImageView) holder.getView(R.id.item_gank_image));
                break;
            case COMMON:

                break;
            default:
        }
        if (holder.getItemViewType() == IMAGE || holder.getItemViewType() == COMMON) {
            StringBuilder stringBuilder = new StringBuilder(item.getDesc());
            final String who = item.getWho();
            if (!TextUtils.isEmpty(who) && !"null".equals(who)) {
                stringBuilder.append(String.format(" [%s]", who));
            }
            holder.setText(R.id.item_gank_desc, stringBuilder.toString());
        }
    }

}
