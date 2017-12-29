package me.wangzheng.gankio.presenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.contract.XianduTabContract;
import me.wangzheng.gankio.model.XianduCategoryEntity;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RxSchedulers;
import okhttp3.ResponseBody;

public class XianduTabPresenter implements XianduTabContract.Presenter {

    private GankService mService;
    private XianduTabContract.View mView;

    @Inject
    public XianduTabPresenter(GankService service, XianduTabContract.View view) {
        mService = service;
        mView = view;
    }

    @Override
    public void getXianduTabList() {
        mService.getXianduCategory()
                .compose(RxSchedulers.ioMain())
                .compose(mView.bindToLife())
                .map(ResponseBody::string)
                .map(html -> {
                    List<XianduCategoryEntity> list = new ArrayList<>();
                    final Document document = Jsoup.parse(html);
                    Elements catsLi = document.select("#xiandu_cat ul li");
                    for (Element element : catsLi) {
                        String href = element.select("a").attr("href");
                        String text = element.text();
                        list.add(new XianduCategoryEntity(text, href));
                    }
                    return list;
                }).subscribe(list -> mView.onResultTabList(list),
                throwable -> mView.onFailed(throwable.getMessage()));

//        Observable.just("http://gank.io/xiandu/")
//                .map(result -> {
//                    List<XianduCategoryEntity> list = new ArrayList<>();
//                    final Connection connect = Jsoup.connect(result);
//                    connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
//                    final Document document = connect.get();
//                    Elements catsLi = document.select("#xiandu_cat ul li");
//                    for (Element element : catsLi) {
//                        String href = element.select("a").attr("href");
//                        String text = element.text();
//
//                        Log.d("wz", href + "--" + text);
//                        list.add(new XianduCategoryEntity(text, href));
//                    }
//                    return list;
//                })
//                .compose(RxSchedulers.ioMain())
//                .compose(mView.bindToLife())
//                .subscribe(list -> mView.onResultTabList(list),
//                        throwable -> mView.onFailed(throwable.getMessage()));
    }
}
