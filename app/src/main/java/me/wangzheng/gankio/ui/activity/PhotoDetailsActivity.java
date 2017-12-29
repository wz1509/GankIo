package me.wangzheng.gankio.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.base.BaseCompatActivity;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RetrofitNetworkHelper;
import me.wangzheng.gankio.model.network.RxSchedulers;
import me.wangzheng.gankio.util.ConfigUtils;
import okhttp3.ResponseBody;

public class PhotoDetailsActivity extends BaseCompatActivity {

    private static final String EXTRA_ITEM = "extra_item";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x100;

    public static Intent newInstant(Context context, GankEntity item) {
        final Intent intent = new Intent(context, PhotoDetailsActivity.class);
        intent.putExtra(EXTRA_ITEM, item);
        return intent;
    }

    private boolean isShare = false;
    private Toolbar mToolbar;
    private ImageView mImageView;

    @Override
    protected int getLayoutId() {
        // 5.0及以上
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        return R.layout.activity_photo_details;
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mImageView = (ImageView) findViewById(R.id.photo_view);
    }

    @Override
    protected boolean hasToolbarTitle() {
        return true;
    }

    @Override
    protected void initData() {
        postponeEnterTransition();

        final GankEntity item = getGankItem();
        mToolbar.setTitle(item.getDesc());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_vector_arrow_back);
        mToolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.white));
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        Glide.with(this)
                .load(item.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new GlideDrawableImageViewTarget(mImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        // 图片加载完成的回调中，启动过渡动画
                        supportStartPostponedEnterTransition();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            isShare = true;
            checkSelfPermission();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            isShare = false;
            checkSelfPermission();
            return true;
        }
        return false;
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void onNetReload(View v) {

    }

    private GankEntity getGankItem() {
        return (GankEntity) getIntent().getSerializableExtra(EXTRA_ITEM);
    }

    private void checkSelfPermission() {
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            downloadImage();
        }
    }

    private void downloadImage() {
        final GankEntity gank = getGankItem();

        final File fileDr = new File(ConfigUtils.ROOT_FILE_PATH, getString(R.string.app_name));
        final File file = new File(fileDr.getPath(), gank.getId() + ".jpg");
        if (file.exists()) {
            // 文件已存在
            if (isShare) {
                shareImage(gank, file);
            } else {
                Toast.makeText(this, "保存成功，" + file.getPath(), Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (!fileDr.exists()) {
            // 不存在就创建根目录
            fileDr.mkdir();
        }
        RetrofitNetworkHelper.createApi(GankService.class, GankService.BASE_URL)
                .downloadImage(gank.getUrl())
                .compose(RxSchedulers.ioMain())
                .compose(bindToLifecycle())
                .map(ResponseBody::byteStream)
                .map(inputStream -> {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(inputStream);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        bis.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return file;
                })
                .subscribe(filePath -> {
                            if (isShare) {
                                shareImage(gank, filePath);
                            } else {
                                Toast.makeText(this, "保存成功，" + filePath.getPath(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> Log.e("wz", throwable.getMessage(), throwable));
    }

    private void shareImage(GankEntity gank, File file) {
        final String shareText = String.format("%s\n%s\n来自:%s", gank.getDesc(), gank.getUrl(), getString(R.string.app_name));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.putExtra("Kdescription", shareText);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, "分享"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadImage();
                } else {
                    Toast.makeText(this, "取消分享", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
