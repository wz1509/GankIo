package me.wangzheng.gankio.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.util.Log;

import me.wangzheng.gankio.R;

public class GlideUtils {

    @DrawableRes
    public static int getCategoryPlaceholder(String category) {
        switch (category) {
            case "Android":
                return R.drawable.bg_glide_placeholder_android;
            case "iOS":
                return R.drawable.bg_glide_placeholder_ios;
            case "前端":
                return R.drawable.bg_glide_placeholder_web;
            case "休息视频":
            case "拓展资源":
            case "瞎推荐":
            default:
                return R.drawable.bg_glide_placeholder_common;
        }
    }

    @DrawableRes
    public static int getCategory(String category) {
        switch (category) {
            case "App":
                return R.drawable.ic_vector_category_app;
            case "Android":
                return R.drawable.ic_vector_category_android;
            case "iOS":
                return R.drawable.ic_vector_category_ios;
            case "前端":
                return R.drawable.ic_vector_category_web;
            case "休息视频":
                return R.drawable.ic_vector_category_video;
            case "拓展资源":
                return R.drawable.ic_vector_category_expand;
            case "瞎推荐":
                return R.drawable.ic_vector_category_recommend;
            default:
        }
        return 0;
    }


}
