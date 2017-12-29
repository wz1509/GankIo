package me.wangzheng.gankio.contract;

import java.util.List;

import me.wangzheng.gankio.model.XianduCategoryEntity;
import me.wangzheng.gankio.ui.view.BaseView;

public interface XianduTabContract {

    interface View extends BaseView {

        void onResultTabList(List<XianduCategoryEntity> list);

        void onFailed(String msg);
    }

    interface Presenter {

        void getXianduTabList();
    }

}
