package me.wangzheng.gankio.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.Serializable;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.base.BaseCompatActivity;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.model.XianduEntity;

public class WebDetailActivity extends BaseCompatActivity {

    private static final String EXTRA_ITEM = "extra_item";

    public static Intent newInstant(Context context, GankEntity item) {
        final Intent intent = new Intent(context, WebDetailActivity.class);
        intent.putExtra(EXTRA_ITEM, item);
        return intent;
    }

    public static Intent newInstant(Context context, XianduEntity item) {
        final Intent intent = new Intent(context, WebDetailActivity.class);
        intent.putExtra(EXTRA_ITEM, item);
        return intent;
    }

    private Toolbar mToolbar;
    private WebView mWebView;

    private ProgressBar mProgressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_details;
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected boolean hasToolbarTitle() {
        return true;
    }

    @Override
    protected void initData() {

        final Serializable serializable = getIntent().getSerializableExtra(EXTRA_ITEM);
        if (serializable instanceof GankEntity) {
            mToolbar.setTitle(((GankEntity) serializable).getDesc());
        } else if (serializable instanceof XianduEntity) {
            mToolbar.setTitle(((XianduEntity) serializable).getDesc());
        }
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_vector_arrow_back);
        mToolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.white));
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        initWebSettings();
    }

    private void initWebSettings() {
        final WebSettings webSettings = mWebView.getSettings();
        //设置自适应屏幕，两者合用
        //将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //缩放操作
        //支持缩放，默认为true。是下面那个的前提。
        webSettings.setSupportZoom(true);
        //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setBuiltInZoomControls(true);
        //隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);
        // 缓存模式如下：
        // LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        // LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        final Serializable serializable = getIntent().getSerializableExtra(EXTRA_ITEM);
        if (serializable instanceof GankEntity) {
            mWebView.loadUrl(((GankEntity) serializable).getUrl());
        } else if (serializable instanceof XianduEntity) {
            mWebView.loadUrl(((XianduEntity) serializable).getUrl());
        }
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                mProgressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mToolbar.setTitle(title);
            }
        });
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void onNetReload(View v) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {

            String shareText = "";
            final Serializable serializable = getIntent().getSerializableExtra(EXTRA_ITEM);
            if (serializable instanceof GankEntity) {
                final GankEntity gank = (GankEntity) serializable;
                shareText = String.format("%s\n%s\n来自:%s", gank.getDesc(), gank.getUrl(), getString(R.string.app_name));
            } else if (serializable instanceof XianduEntity) {
                final XianduEntity xiandu = (XianduEntity) serializable;
                shareText = String.format("%s\n%s\n来自:%s", xiandu.getDesc(), xiandu.getUrl(), getString(R.string.app_name));
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "分享"));
            return true;
        }
        return false;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        // 在 Activity 销毁（ WebView ）的时候，先让 WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
