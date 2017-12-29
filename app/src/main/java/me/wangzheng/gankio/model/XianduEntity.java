package me.wangzheng.gankio.model;

import java.io.Serializable;

public class XianduEntity implements Serializable{

    private String no;
    private String date;
    private String desc;
    private String url;

    private String categoryIcon;
    private String categoryName;
    private String categoryUrl;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    @Override
    public String toString() {
        return "XianduEntity{" +
                "no=" + no +
                ", date='" + date + '\'' +
                ", desc='" + desc + '\'' +
                ", url='" + url + '\'' +
                ", categoryIcon='" + categoryIcon + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryUrl='" + categoryUrl + '\'' +
                '}';
    }

}
