package me.wangzheng.gankio.presenter;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.wangzheng.gankio.base.BaseEntity;
import me.wangzheng.gankio.contract.CategoryContract;
import me.wangzheng.gankio.contract.GankIoContract;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RxSchedulers;

public class CategoryPresenter implements CategoryContract.Presenter {

    private GankService mService;
    private CategoryContract.View mView;

    private final static int COUNT = 15;
    private int page = 1;

    @Inject
    public CategoryPresenter(GankService service, CategoryContract.View view) {
        mService = service;
        mView = view;
    }

    @Override
    public void getGankIoList(boolean isRefresh, String category) {
        if (isRefresh) page = 1;
        mService.getGankIoList(category, COUNT, page)
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
