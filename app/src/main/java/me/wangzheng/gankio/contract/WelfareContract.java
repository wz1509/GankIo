package me.wangzheng.gankio.contract;

import java.util.List;

import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.ui.view.BaseView;

public interface WelfareContract {

    interface View extends BaseView {

        void onResultGankIoList(boolean isRefresh, List<GankEntity> list);

        void onFailed(boolean isRefresh, String msg);
    }

    interface Presenter {

        void getGankIoList(boolean isRefresh);
    }

}
