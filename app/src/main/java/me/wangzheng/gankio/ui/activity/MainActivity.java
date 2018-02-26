package me.wangzheng.gankio.ui.activity;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.wangzheng.gankio.R;
import me.wangzheng.gankio.base.BaseCompatActivity;
import me.wangzheng.gankio.ui.fragment.GankIoTabFragment;
import me.wangzheng.gankio.ui.fragment.XianduTabFragment;

public class MainActivity extends BaseCompatActivity {

    private Toolbar mToolbar;
    private GankIoTabFragment gankIoTabFragment;
    private XianduTabFragment mXianduTabFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected boolean hasToolbarTitle() {
        return false;
    }

    @Override
    protected void initData() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setSupportActionBar(mToolbar);
        showFragment(0);

//        Log.d("wz", "time start");
//
//        final int count = 5;
//        Observable.interval(0, 1, TimeUnit.SECONDS)
//                .take(count + 1)
//                .map(aLong -> count - aLong)
//                .compose(bindToLifecycle())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        Log.d("wz", "onNext: aLong = " + aLong);
//                        if (aLong == 0) {
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                            builder.setTitle("title");
//                            builder.setMessage("test");
//                            final AlertDialog alertDialog = builder.create();
//                            alertDialog.show();
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e("wz", "accept: " + throwable.getMessage(), throwable);
//                    }
//                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_gankio:
                showFragment(0);
                return true;
            case R.id.navigation_xd:
                showFragment(1);
                return true;
            case R.id.navigation_personal:
                showFragment(2);
                return true;
            default:
        }
        return false;
    };

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (gankIoTabFragment == null && fragment instanceof GankIoTabFragment) {
            gankIoTabFragment = (GankIoTabFragment) fragment;
        } else if (mXianduTabFragment == null && fragment instanceof XianduTabFragment) {
            mXianduTabFragment = (XianduTabFragment) fragment;
        } else {

        }
    }

    private void showFragment(final int index) {
        setToolbarTitle(index);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);
        switch (index) {
            case 0:
                if (gankIoTabFragment == null) {
                    gankIoTabFragment = new GankIoTabFragment();
                    fragmentTransaction.add(R.id.frame_layout, gankIoTabFragment);
                } else {
                    fragmentTransaction.show(gankIoTabFragment);
                }
                break;
            case 1:
                if (mXianduTabFragment == null) {
                    mXianduTabFragment = new XianduTabFragment();
                    fragmentTransaction.add(R.id.frame_layout, mXianduTabFragment);
                } else {
                    fragmentTransaction.show(mXianduTabFragment);
                }
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (gankIoTabFragment != null)
            fragmentTransaction.hide(gankIoTabFragment);
        if (mXianduTabFragment != null)
            fragmentTransaction.hide(mXianduTabFragment);
    }

    private void setToolbarTitle(int index) {
        switch (index) {
            case 0:
                mToolbar.setTitle("干货");
                break;
            case 1:
                mToolbar.setTitle("闲读");
                break;
            case 2:
                mToolbar.setTitle("个人中心");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(SearchActivity.newInstant(this));
            return true;
        }
        return false;
    }

    @Override
    protected void initNet() {

    }

    @Override
    protected void onNetReload(View v) {

    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

}
