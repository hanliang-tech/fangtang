package com.fangtang.tv.sdk.base.nlp.bean;

import android.support.annotation.Keep;


@Keep
public class NLPBean {

    public String domain;
    public String intention;
    public String query;
    public String raw_query;
    public String query_id;
    public NLPContentBean content;

    @Override
    public String toString() {
        return "NLPBean{" +
                "domain='" + domain + '\'' +
                ", intention='" + intention + '\'' +
                ", query='" + query + '\'' +
                ", raw_query='" + raw_query + '\'' +
                ", query_id='" + query_id + '\'' +
                ", content=" + content +
                '}';
    }
}
