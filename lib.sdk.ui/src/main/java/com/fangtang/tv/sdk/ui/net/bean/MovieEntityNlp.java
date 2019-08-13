package com.fangtang.tv.sdk.ui.net.bean;

import android.support.annotation.Keep;

import com.fangtang.tv.sdk.base.nlp.bean.InnerMovieBean;

import java.io.Serializable;
import java.util.List;


@Keep
public class MovieEntityNlp implements Serializable{

    private static final long serialVersionUID = 100L;

    /**
     * total : 99
     * movie : [{"title":"九二神雕之痴心情长剑","origin":"std_album","source_name":"com.fangtang.tv.video","movie_id":14393,"mid":14393,"isvip":"1","iconaddress":"http://img1tuzi.b0.upaiyun.com/upload/origin/4/40711f69e10a5c6d912c38079519012c.jpg","grade":"8.1","sort":121,"custom_data":"{\"jump\": {\"parameter\": \"\", \"type\": \"uri\", \"flag\": \"0x10000000\", \"name\": \"bftv\", \"intent_toast\": \"\\u8df3\\u8f6c\\u7231\\u5947\\u827a\", \"package_name\": \"com.gitvbf.video\", \"uri\": \"gala://page?playType=history&videoId=340945700&episodeId=340945700&history=0&chnId=1&customer=bftv\", \"action\": \"com.gitvbf.video.action.ACTION_DETAIL\"}, \"name\": \"\\u7231\\u5947\\u827a\", \"package\": \"com.gitvbf.video\"}","corner_mark":"","corner_vip":"vip","srcInfo":"{\"aid\":\"340945700\",\"eid\":\"340945700\",\"cid\":\"1\",\"type\":\"video\"}"}]
     */

    private int total;
    private List<InnerMovieBean> movie;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<InnerMovieBean> getMovie() {
        return movie;
    }

    public void setMovie(List<InnerMovieBean> movie) {
        this.movie = movie;
    }
}
