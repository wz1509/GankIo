package me.wangzheng.gankio.contract;

import java.util.List;

import me.wangzheng.gankio.model.XianduEntity;
import me.wangzheng.gankio.ui.view.BaseView;

public interface XianduContract {

    interface View extends BaseView {

        void onResultXianduList(boolean isRefresh, List<XianduEntity> list);

        void onFailed(boolean isRefresh, String msg);
    }

    interface Presenter {

        void getXianduList(boolean isRefresh, String url);
    }

}
