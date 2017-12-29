package me.wangzheng.gankio.di.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.wangzheng.gankio.model.network.GankService;
import me.wangzheng.gankio.model.network.RetrofitNetworkHelper;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    public GankService provideGankService() {
        return RetrofitNetworkHelper.createApi(GankService.class, GankService.BASE_URL);
    }

}
