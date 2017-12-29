package me.wangzheng.gankio.model.network;

import java.util.List;

import io.reactivex.Observable;
import me.wangzheng.gankio.base.BaseEntity;
import me.wangzheng.gankio.model.GankEntity;
import me.wangzheng.gankio.model.TodayEntity;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GankService {

    String BASE_URL = "http://gank.io/";

    @GET("api/day/history")
    Observable<BaseEntity<List<String>>> getHistoryList();

    @GET("api/day/{date}")
    Observable<BaseEntity<TodayEntity>> getGankIoToday(@Path("date") String date);

    @GET("api/data/{category}/{count}/{page}")
    Observable<BaseEntity<List<GankEntity>>> getGankIoList(@Path("category") String category,
                                                           @Path("count") int count,
                                                           @Path("page") int page);

    @GET("xiandu/index.html")
    Observable<ResponseBody> getXianduCategory();

    @GET
    Observable<ResponseBody> getXiandu(@Url String url);

    @GET
    Observable<ResponseBody> downloadImage(@Url String url);

}
