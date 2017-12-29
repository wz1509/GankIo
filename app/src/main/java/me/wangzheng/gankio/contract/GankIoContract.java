package me.wangzheng.gankio.contract;

import java.util.List;

import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.ui.view.BaseView;

public interface GankIoContract {

    interface View extends BaseView {

        /**
         * 数据回调
         *
         * @param list 数据集合
         */
        void onResultGankIoList(List<GankEntity> list);

        void onResultHistoryList(List<String> list);

        /**
         * 请求失败
         *
         * @param msg 错误信息
         */
        void onFailed(boolean isRefresh, String msg);
    }

    interface Presenter {

        /**
         * 请求今日干货
         */
        void getGankIoDayList();

        /**
         * 请求更多
         *
         * @param date 类型
         */
        void getGankIoDayList(String date);
    }

}
