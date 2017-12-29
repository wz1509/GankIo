package me.wangzheng.gankio.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangzheng on 2017/12/19.
 */

public class TodayEntity implements Serializable{

    @SerializedName("Android")
    private List<GankEntity> androidList;

    @SerializedName("iOS")
    private List<GankEntity> iosList;

    @SerializedName("前端")
    private List<GankEntity> webList;

    @SerializedName("休息视频")
    private List<GankEntity> videoList;

    @SerializedName("拓展资源")
    private List<GankEntity> expandList;

    @SerializedName("瞎推荐")
    private List<GankEntity> otherList;

    @SerializedName("福利")
    private List<GankEntity> welfareList;

    public List<GankEntity> getAndroidList() {
        return androidList;
    }

    public void setAndroidList(List<GankEntity> androidList) {
        this.androidList = androidList;
    }

    public List<GankEntity> getIosList() {
        return iosList;
    }

    public void setIosList(List<GankEntity> iosList) {
        this.iosList = iosList;
    }

    public List<GankEntity> getWebList() {
        return webList;
    }

    public void setWebList(List<GankEntity> webList) {
        this.webList = webList;
    }

    public List<GankEntity> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<GankEntity> videoList) {
        this.videoList = videoList;
    }

    public List<GankEntity> getExpandList() {
        return expandList;
    }

    public void setExpandList(List<GankEntity> expandList) {
        this.expandList = expandList;
    }

    public List<GankEntity> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<GankEntity> otherList) {
        this.otherList = otherList;
    }

    public List<GankEntity> getWelfareList() {
        return welfareList;
    }

    public void setWelfareList(List<GankEntity> welfareList) {
        this.welfareList = welfareList;
    }

    @Override
    public String toString() {
        return "TodayEntity{" +
                "androidList=" + androidList +
                ", iosList=" + iosList +
                ", videoList=" + videoList +
                ", expandList=" + expandList +
                ", otherList=" + otherList +
                ", welfareList=" + welfareList +
                '}';
    }
}
