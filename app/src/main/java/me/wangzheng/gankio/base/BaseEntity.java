package me.wangzheng.gankio.base;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangzheng on 2017/12/18.
 */

public class BaseEntity<T> {

    private boolean error;

    @SerializedName("results")
    private T gankIo;

    public boolean isSuccess() {
        return !isError();
    }

    private boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getGankIo() {
        return gankIo;
    }

    public void setGankIo(T gankIo) {
        this.gankIo = gankIo;
    }
}
