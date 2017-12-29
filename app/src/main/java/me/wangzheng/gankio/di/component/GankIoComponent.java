package me.wangzheng.gankio.di.component;

import dagger.Component;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.di.scope.FragmentScope;
import me.wangzheng.gankio.ui.activity.SearchActivity;
import me.wangzheng.gankio.ui.fragment.CategoryFragment;
import me.wangzheng.gankio.ui.fragment.TodayFragment;
import me.wangzheng.gankio.ui.fragment.WelfareFragment;
import me.wangzheng.gankio.ui.fragment.XianduFragment;
import me.wangzheng.gankio.ui.fragment.XianduTabFragment;

@FragmentScope
@Component(modules = GankIoModule.class, dependencies = AppComponent.class)
public interface GankIoComponent {

    void inject(TodayFragment fragment);

    void inject(CategoryFragment fragment);

    void inject(WelfareFragment fragment);

    void inject(XianduTabFragment fragment);

    void inject(XianduFragment fragment);

    void inject(SearchActivity activity);
}
