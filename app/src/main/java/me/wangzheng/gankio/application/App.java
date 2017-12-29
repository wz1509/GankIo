package me.wangzheng.gankio.application;

import android.app.Application;

import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.LoadSir;

import me.wangzheng.gankio.di.component.AppComponent;
import me.wangzheng.gankio.di.component.DaggerAppComponent;
import me.wangzheng.gankio.di.module.AppModule;
import me.wangzheng.gankio.di.module.NetworkModule;
import me.wangzheng.gankio.util.callback.EmptyCallback;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;

public class App extends Application{

    private static App application;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .setDefaultCallback(SuccessCallback.class)
                .commit();
    }

    public static App getApplication() {
        return application;
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .networkModule(new NetworkModule())
                    .appModule(new AppModule(this))
                    .build();
        }
        return appComponent;
    }

}
