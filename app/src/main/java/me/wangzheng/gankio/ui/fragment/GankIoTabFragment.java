package me.wangzheng.gankio.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.base.BaseFragment;
import me.wangzheng.gankio.ui.adapter.ViewPagerAdapter;

public class GankIoTabFragment extends BaseFragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_layout;
    }

    @Override
    protected void initView(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
    }

    @Override
    protected void initData() {
        final List<String> titleList = Arrays.asList("今日干货", "分类", "福利");
        final List<Fragment> fragmentList = new ArrayList<>(titleList.size());
        fragmentList.add(new TodayFragment());
        fragmentList.add(new CategoryFragment());
        fragmentList.add(new WelfareFragment());
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),
                titleList, fragmentList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        mViewPager.setCurrentItem(0);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void loadNet() {

    }

    @Override
    protected void onNetReload(View v) {

    }

}
