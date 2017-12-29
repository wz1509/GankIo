package me.wangzheng.gankio.presenter;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.contract.XianduContract;
import me.wangzheng.gankio.model.XianduEntity;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RxSchedulers;

public class XianduPresenter implements XianduContract.Presenter {

    private String nextPageUrl = null;

    private GankService mService;
    private XianduContract.View mView;

    @Inject
    public XianduPresenter(GankService service, XianduContract.View view) {
        mService = service;
        mView = view;
    }

    @Override
    public void getXianduList(boolean isRefresh, String url) {
        if (isRefresh) {
            url = "http://gank.io" + url;
        } else {
            url = "http://gank.io" + nextPageUrl;
        }
        mService.getXiandu(url)
                .compose(RxSchedulers.ioMain())
                .compose(mView.bindToLife())
                .map(responseBody -> {
                    final String result = responseBody.string();
                    Log.d("wz", "result = " + result);
                    return result;
                })
                .map(html -> {
                    final List<XianduEntity> list = new ArrayList<>();
                    final Document document = Jsoup.parse(html);
                    Elements containerContent = document.select("div.typo")
                            .get(0)
                            .select("div.container");
                    // 抓取下一页数据url
                    nextPageUrl = containerContent.select("div.row")
                            .select("div")
                            .get(0)
                            .select("a")
                            .last().attr("href");

                    Elements items = containerContent.select("div.xiandu_items").select("div.xiandu_item");
                    for (Element item : items) {
                        XianduEntity xianduEntity = new XianduEntity();
                        final Elements leftElement = item.select("div.xiandu_left");
                        String no = leftElement.select("span.xiandu_index").text();
                        String shareUrl = leftElement.select("a").attr("href");
                        String desc = leftElement.select("a").text();
                        String date = leftElement.select("span small").text();

                        final Elements rightElement = item.select("div.xiandu_right").select("a");
                        String categotyUrl = rightElement.attr("href");
                        String categoryName = rightElement.attr("title");
                        String categoryIcon = rightElement.select("img").attr("src");

                        xianduEntity.setNo(no);
                        xianduEntity.setUrl(shareUrl);
                        xianduEntity.setDesc(desc);
                        xianduEntity.setDate(date);

                        xianduEntity.setCategoryIcon(categoryIcon);
                        xianduEntity.setCategoryName(categoryName);
                        xianduEntity.setCategoryUrl(categotyUrl);

                        list.add(xianduEntity);
                    }
                    return list;
                })
                .subscribe(list -> mView.onResultXianduList(isRefresh, list),
                        throwable -> mView.onFailed(isRefresh, throwable.getMessage()));

//        Observable<String> observable;
//        if (isRefresh) {
//            observable = Observable.just("http://gank.io/" + url);
//        } else {
//            observable = Observable.just("http://gank.io/" + nextPageUrl);
//        }
//        observable
//                .map(result -> {
//                    List<XianduEntity> list = new ArrayList<>();
//                    final Connection connect = Jsoup.connect(result);
//                    // 伪装成浏览器抓取
//                    connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
//                    connect.header("Content-Type", "text/html; charset=utf-8");
//
//                    final Document document = connect.get();
//                    Elements containerContent = document.select("div.typo")
//                            .get(0)
//                            .select("div.container");
//                    // 抓取下一页数据url
//                    nextPageUrl = containerContent.select("div.row")
//                            .select("div")
//                            .get(0)
//                            .select("a")
//                            .last().attr("href");
//
//                    Elements items = containerContent.select("div.xiandu_items").select("div.xiandu_item");
//                    for (Element item : items) {
//                        XianduEntity xianduEntity = new XianduEntity();
//                        final Elements leftElement = item.select("div.xiandu_left");
//                        String no = leftElement.select("span.xiandu_index").text();
//                        String shareUrl = leftElement.select("a").attr("href");
//                        String desc = leftElement.select("a").text();
//                        String date = leftElement.select("span small").text();
//
//                        final Elements rightElement = item.select("div.xiandu_right").select("a");
//                        String categotyUrl = rightElement.attr("href");
//                        String categoryName = rightElement.attr("title");
//                        String categoryIcon = rightElement.select("img").attr("src");
//
//                        xianduEntity.setNo(no);
//                        xianduEntity.setUrl(shareUrl);
//                        xianduEntity.setDesc(desc);
//                        xianduEntity.setDate(date);
//
//                        xianduEntity.setCategoryIcon(categoryIcon);
//                        xianduEntity.setCategoryName(categoryName);
//                        xianduEntity.setCategoryUrl(categotyUrl);
//
//                        list.add(xianduEntity);
//                    }
//                    return list;
//                })
//                .compose(RxSchedulers.ioMain())
//                .compose(mView.bindToLife())
//                .subscribe(list -> mView.onResultXianduList(isRefresh, list),
//                        throwable -> mView.onFailed(isRefresh, throwable.getMessage()));
    }

}
