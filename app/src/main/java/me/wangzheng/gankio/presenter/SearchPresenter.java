package me.wangzheng.gankio.presenter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.wangzheng.gankio.contract.SearchContract;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RxSchedulers;

public class SearchPresenter implements SearchContract.Presenter {

    private GankService mService;
    private SearchContract.View mView;

    @Inject
    public SearchPresenter(GankService service, SearchContract.View view) {
        mService = service;
        mView = view;
    }

    @Override
    public void getQueryTextSubmit(final String query) {
        final String url = String.format("http://gank.io/search?q=%s", query);
        Observable.just(url)
                .map(s -> {
                    final List<GankEntity> list = new ArrayList<>();
                    final Connection connect = Jsoup.connect(s);
                    // 伪装成浏览器抓取
                    connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0");
                    final Document document = connect.get();
                    Elements items = document.select("div.typo")
                            .get(0)
                            .select("div.content")
                            .select("ol li");
                    for (int i = 0; i < items.size(); i++) {
                        Element element = items.get(i).select("div.row").get(0);
                        GankEntity gank = new GankEntity();
                        gank.setId(String.valueOf(i));
                        final String desc = element.select("a").text();
                        if (!desc.contains(query)) {
                            continue;
                        }
                        gank.setDesc(desc);
                        gank.setUrl(element.select("a").attr("href"));
                        gank.setType(element.select("small").get(0).text().replace("(", "").replace(")", ""));
                        gank.setWho(element.select("small.u-pull-right").text());
                        list.add(gank);
                    }
                    return list;
                })
                .compose(mView.bindToLife())
                .compose(RxSchedulers.ioMain())
                .subscribe(list -> mView.onResultGankList(list),
                        throwable -> mView.onFailed(throwable.getMessage()));
    }
}
