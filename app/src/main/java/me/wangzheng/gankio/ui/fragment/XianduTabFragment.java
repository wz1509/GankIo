package me.wangzheng.gankio.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.kingja.loadsir.callback.SuccessCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.application.App;
import me.wangzheng.gankio.base.BaseFragment;
import me.wangzheng.gankio.contract.XianduTabContract;
import me.wangzheng.gankio.di.component.DaggerGankIoComponent;
import me.wangzheng.gankio.di.module.GankIoModule;
import me.wangzheng.gankio.model.XianduCategoryEntity;
import me.wangzheng.gankio.presenter.XianduTabPresenter;
import me.wangzheng.gankio.ui.adapter.ViewPagerAdapter;
import me.wangzheng.gankio.util.callback.ErrorCallback;
import me.wangzheng.gankio.util.callback.LoadingCallback;

public class XianduTabFragment extends BaseFragment implements XianduTabContract.View {

    private final String TAG = this.getClass().getSimpleName();
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Inject
    XianduTabPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_layout;
    }

    @Override
    protected void initView(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        DaggerGankIoComponent.builder()
                .appComponent(App.getApplication().getAppComponent())
                .gankIoModule(new GankIoModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void loadNet() {
        loadService.showCallback(LoadingCallback.class);
        mPresenter.getXianduTabList();
    }

    @Override
    protected void onNetReload(View v) {
        loadNet();
    }

    @Override
    public void onResultTabList(List<XianduCategoryEntity> list) {
        List<String> titleList = new ArrayList<>();
        final List<Fragment> fragmentList = new ArrayList<>(list.size());
        for (XianduCategoryEntity entity : list) {
            titleList.add(entity.getTitle());
            fragmentList.add(XianduFragment.newInstant(entity.getUrl()));
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), titleList, fragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.setCurrentItem(0);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(mViewPager);

        loadService.showCallback(SuccessCallback.class);
    }

    @Override
    public void onFailed(String msg) {
        Log.e(TAG, msg);
        loadService.showCallback(ErrorCallback.class);
    }
}
