package me.wangzheng.gankio.model;

import android.os.Parcel;
import android.os.Parcelable;

public class XianduCategoryEntity implements Parcelable {

    private String title;
    private String url;

    public XianduCategoryEntity() {
    }

    public XianduCategoryEntity(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "XianduCategoryEntity{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    protected XianduCategoryEntity(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<XianduCategoryEntity> CREATOR = new Parcelable.Creator<XianduCategoryEntity>() {
        @Override
        public XianduCategoryEntity createFromParcel(Parcel source) {
            return new XianduCategoryEntity(source);
        }

        @Override
        public XianduCategoryEntity[] newArray(int size) {
            return new XianduCategoryEntity[size];
        }
    };
}
