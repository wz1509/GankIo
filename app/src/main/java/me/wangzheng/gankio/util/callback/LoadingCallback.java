package me.wangzheng.gankio.util.callback;

import com.kingja.loadsir.callback.Callback;

import me.wangzheng.gankio.R;

public class LoadingCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.layout_status_loading;
    }
}
