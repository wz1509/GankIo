package me.wangzheng.gankio.di.module;

import dagger.Module;
import dagger.Provides;
import me.wangzheng.gankio.contract.CategoryContract;
import me.wangzheng.gankio.contract.GankIoContract;
import me.wangzheng.gankio.contract.SearchContract;
import me.wangzheng.gankio.contract.WelfareContract;
import me.wangzheng.gankio.contract.XianduContract;
import me.wangzheng.gankio.contract.XianduTabContract;
import me.wangzheng.gankio.di.scope.FragmentScope;

@Module
public class GankIoModule {

    private GankIoContract.View todayGankIoView;

    private CategoryContract.View mCategoryView;

    private WelfareContract.View mWelfareView;

    private XianduTabContract.View mXianduTabView;

    private XianduContract.View mXianduView;

    private SearchContract.View mSearchView;

    public GankIoModule(GankIoContract.View view) {
        this.todayGankIoView = view;
    }

    public GankIoModule(CategoryContract.View view) {
        this.mCategoryView = view;
    }

    public GankIoModule(WelfareContract.View view) {
        this.mWelfareView = view;
    }

    public GankIoModule(XianduTabContract.View view) {
        this.mXianduTabView = view;
    }

    public GankIoModule(XianduContract.View view) {
        this.mXianduView = view;
    }

    public GankIoModule(SearchContract.View view) {
        this.mSearchView = view;
    }

    @FragmentScope
    @Provides
    public GankIoContract.View provideTodayGankIoView() {
        return todayGankIoView;
    }

    @FragmentScope
    @Provides
    public CategoryContract.View provideCategoryView() {
        return mCategoryView;
    }

    @FragmentScope
    @Provides
    public WelfareContract.View provideWelfareView() {
        return mWelfareView;
    }

    @FragmentScope
    @Provides
    public XianduTabContract.View provideXianduTabView() {
        return mXianduTabView;
    }

    @FragmentScope
    @Provides
    public XianduContract.View provideXianduView() {
        return mXianduView;
    }

    @FragmentScope
    @Provides
    public SearchContract.View provideSearchView() {
        return mSearchView;
    }
}
