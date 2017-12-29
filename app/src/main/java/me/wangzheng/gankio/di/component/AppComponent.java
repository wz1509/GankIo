package me.wangzheng.gankio.di.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import me.wangzheng.gankio.di.module.AppModule;
import me.wangzheng.gankio.di.module.NetworkModule;
import me.wangzheng.gankio.model.network.GankService;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    Application getApplication();

    GankService getGankService();

}
