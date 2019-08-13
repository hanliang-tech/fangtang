package com.fangtang.tv.sdk.base.nlp.bean;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * @author WeiPeng
 * @version 1.0
 * @title InnerMovieBean.java
 * @description need_desc
 * @company 北京奔流网络信息技术有限公司
 * @created 2019/05/28 11:23
 * @changeRecord [修改记录] <br/>
 */
@Keep
public class InnerMovieBean implements Serializable {

    private static final long serialVersionUID = 100L;

    private String grade;
    private String movie_id;
    private String cid;
    private String origin;
    private String title;
    private String custom_data;
    private String cover;
    private String icon;

    private String origin_icon;
    private String origin_name;
    private String package_name;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustom_data() {
        return custom_data;
    }

    public void setCustom_data(String custom_data) {
        this.custom_data = custom_data;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getOrigin_icon() {
        return origin_icon;
    }

    public void setOrigin_icon(String origin_icon) {
        this.origin_icon = origin_icon;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }
}
