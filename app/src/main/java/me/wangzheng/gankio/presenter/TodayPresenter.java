package me.wangzheng.gankio.presenter;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.wangzheng.gankio.base.BaseEntity;
import me.wangzheng.gankio.contract.GankIoContract;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.model.TodayEntity;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RxSchedulers;

public class TodayPresenter implements GankIoContract.Presenter {

    private GankService mService;
    private GankIoContract.View mView;

    private String lastDate = null;

    @Inject
    public TodayPresenter(GankService service, GankIoContract.View view) {
        this.mService = service;
        this.mView = view;
    }

    @Override
    public void getGankIoDayList() {
        getGankIoDayList(lastDate);
//        if (!TextUtils.isEmpty(lastDate)) {
//            getGankIoDayList(lastDate);
//            return;
//        }
//        mService.getHistoryList()
//                .compose(RxSchedulers.ioMain())
//                .doOnNext(base -> {
//                    if (base.isSuccess()) {
//                        EventBus.getDefault().post(base.getGankIoList());
//                    }
//                })
//                .observeOn(Schedulers.io())
//                .flatMap(stringBaseEntity -> {
//                    if (stringBaseEntity.isSuccess()) {
//                        String date = stringBaseEntity.getGankIoList().get(0);
//                        lastDate = date.replace("-", "/");
//                        return mService.getGankIoToday(lastDate);
//                    }
//                    return null;
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(baseEntity -> {
//                    if (baseEntity.isSuccess()) {
//                        TodayEntity today = baseEntity.getGankIo();
//                        List<GankIoEntity> list = new ArrayList<>();
//                        if (today.getAndroidList() != null && !today.getAndroidList().isEmpty()) {
//                            list.add(new GankIoEntity("Android"));
//                            list.addAll(today.getAndroidList());
//                        }
//                        if (today.getIosList() != null && !today.getIosList().isEmpty()) {
//                            list.add(new GankIoEntity("iOS"));
//                            list.addAll(today.getIosList());
//                        }
//                        if (today.getWebList() != null && !today.getWebList().isEmpty()) {
//                            list.add(new GankIoEntity("前端"));
//                            list.addAll(today.getWebList());
//                        }
//                        if (today.getVideoList() != null && !today.getVideoList().isEmpty()) {
//                            list.add(new GankIoEntity("休息视频"));
//                            list.addAll(today.getVideoList());
//                        }
//                        if (today.getWelfareList() != null && !today.getWelfareList().isEmpty()) {
//                            list.add(new GankIoEntity("福利"));
//                            list.addAll(today.getWelfareList());
//                        }
//                        if (today.getOtherList() != null && !today.getOtherList().isEmpty()) {
//                            list.add(new GankIoEntity("瞎推荐"));
//                            list.addAll(today.getOtherList());
//                        }
//                        if (today.getExpandList() != null && !today.getExpandList().isEmpty()) {
//                            list.add(new GankIoEntity("拓展资源"));
//                            list.addAll(today.getExpandList());
//                        }
//                        mView.onResultGankIoList(list);
//                    }
//                }, throwable -> mView.onFailed(true, throwable.getMessage()));
    }

    @Override
    public void getGankIoDayList(String date) {

        final boolean flag = TextUtils.isEmpty(date);
        Observable<BaseEntity<TodayEntity>> observable;
        if (flag) {
            observable = mService.getHistoryList()
                    .compose(RxSchedulers.ioMain())
                    .doOnNext(base -> {
                        if (base.isSuccess()) {
                            mView.onResultHistoryList(base.getGankIo());
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(stringBaseEntity -> {
                        if (stringBaseEntity.isSuccess()) {
                            lastDate = stringBaseEntity.getGankIo().get(0)
                                    .replace("-", "/");
                            return mService.getGankIoToday(lastDate);
                        }
                        return null;
                    });
        } else {
            // 这里赋值到最后一次选中的日期，如果请求异常后，选择刷新则请求最后一次选中的日期干货
            lastDate = date.replace("-", "/");
            observable = mService.getGankIoToday(lastDate);
        }
        observable.compose(RxSchedulers.ioMain())
                .subscribe(baseEntity -> {
                    if (baseEntity.isSuccess()) {
                        TodayEntity today = baseEntity.getGankIo();
                        List<GankEntity> list = new ArrayList<>();
                        if (today.getAndroidList() != null && !today.getAndroidList().isEmpty()) {
                            list.add(new GankEntity("Android"));
                            list.addAll(today.getAndroidList());
                        }
                        if (today.getIosList() != null && !today.getIosList().isEmpty()) {
                            list.add(new GankEntity("iOS"));
                            list.addAll(today.getIosList());
                        }
                        if (today.getWebList() != null && !today.getWebList().isEmpty()) {
                            list.add(new GankEntity("前端"));
                            list.addAll(today.getWebList());
                        }
                        if (today.getVideoList() != null && !today.getVideoList().isEmpty()) {
                            list.add(new GankEntity("休息视频"));
                            list.addAll(today.getVideoList());
                        }
                        if (today.getWelfareList() != null && !today.getWelfareList().isEmpty()) {
                            list.add(new GankEntity("福利"));
                            list.addAll(today.getWelfareList());
                        }
                        if (today.getOtherList() != null && !today.getOtherList().isEmpty()) {
                            list.add(new GankEntity("瞎推荐"));
                            list.addAll(today.getOtherList());
                        }
                        if (today.getExpandList() != null && !today.getExpandList().isEmpty()) {
                            list.add(new GankEntity("拓展资源"));
                            list.addAll(today.getExpandList());
                        }
                        mView.onResultGankIoList(list);
                    }
                }, throwable -> mView.onFailed(flag, throwable.getMessage()));
    }

}
