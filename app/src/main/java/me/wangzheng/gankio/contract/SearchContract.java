package me.wangzheng.gankio.contract;

import java.util.List;

import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.ui.view.BaseView;

public interface SearchContract {

    interface View extends BaseView {

        void onResultGankList(List<GankEntity> list);

        void onFailed(String msg);
    }

    interface Presenter {

        void getQueryTextSubmit(String query);
    }

}
