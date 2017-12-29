package me.wangzheng.gankio.ui.adapter;

import android.content.Context;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.util.GlideUtils;
import me.wangzheng.library.adapter.BaseMultiRecyclerViewAdapter;
import me.wangzheng.library.adapter.BaseViewHolder;

public class CategoryAdapter extends BaseMultiRecyclerViewAdapter<GankEntity> {

    private static final String TAG = CategoryAdapter.class.getSimpleName();

    private final static int TYPE_IMAGE = 0;
    private final static int TYPE_COMMON = 1;
    private final static int TYPE_COMMON_IMAGE = 2;

    public CategoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected int addItemViewType(int position) {
        final GankEntity item = getData().get(position);
        if ("福利".equals(item.getType())) {
            return TYPE_IMAGE;
        } else if (item.getImages() == null || item.getImages().size() == 0) {
            return TYPE_COMMON;
        } else {
            return TYPE_COMMON_IMAGE;
        }
    }

    @Override
    protected BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                return BaseViewHolder.get(parent, R.layout.item_recycler_category_image);
            case TYPE_COMMON:
                return BaseViewHolder.get(parent, R.layout.item_recycler_category_common);
            case TYPE_COMMON_IMAGE:
                return BaseViewHolder.get(parent, R.layout.item_recycler_category_common_image);
            default:
                return null;
        }
    }

    @Override
    protected void onBindBaseViewHolder(BaseViewHolder holder, GankEntity item, int position) {
        Log.d(TAG, "item = " + item.toString());
        if (holder.getItemViewType() == TYPE_IMAGE) {
            Glide.with(mContext)
                    .load(item.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) holder.getView(R.id.item_gank_image));
        } else if (holder.getItemViewType() == TYPE_COMMON_IMAGE) {
            Glide.with(mContext)
                    .load(item.getImages().get(0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(GlideUtils.getCategoryPlaceholder(item.getType()))
                    .into((ImageView) holder.getView(R.id.item_gank_image));
        }
        if (holder.getItemViewType() == TYPE_COMMON
                || holder.getItemViewType() == TYPE_COMMON_IMAGE) {
            ((AppCompatImageView) holder.getView(R.id.item_gank_icon))
                    .setImageResource(GlideUtils.getCategory(item.getType()));
            holder.setText(R.id.item_gank_who, item.getWho())
                    .setText(R.id.item_gank_date, dateConversion(item.getCreatedAt()));
        }
        holder.setText(R.id.item_gank_desc, item.getDesc());
    }

    private String dateConversion(String publishedAt) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault());
        try {
            Date date = format.parse(publishedAt);
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return format.format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
