package me.wangzheng.gankio.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.wangzheng.gankio.contract.GankIoContract;
import me.wangzheng.gankio.contract.WelfareContract;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RxSchedulers;
import okhttp3.ResponseBody;

public class WelfarePresenter implements WelfareContract.Presenter {

    private GankService mService;
    private WelfareContract.View mView;

    private final static int COUNT = 15;
    private int page = 1;

    @Inject
    public WelfarePresenter(GankService service, WelfareContract.View view) {
        mService = service;
        mView = view;
    }

    @Override
    public void getGankIoList(boolean isRefresh) {
        if (isRefresh) page = 1;
        mService.getGankIoList("福利", COUNT, page)
                .compose(mView.bindToLife())
                .compose(RxSchedulers.ioMain())
                .subscribe(base -> {
                    if (base.isSuccess()) {
                        page++;
                        mView.onResultGankIoList(isRefresh, base.getGankIo());
                    } else {
                        mView.onFailed(isRefresh, "error");
                    }
                }, throwable -> mView.onFailed(isRefresh, throwable.getMessage()));
    }
}
