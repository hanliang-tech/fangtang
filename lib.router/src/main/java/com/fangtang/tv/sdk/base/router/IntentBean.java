package com.fangtang.tv.sdk.base.router;

/**
 * @author WeiPeng
 * @version 1.0
 * @title IntentBean.java
 * @description need_desc
 * @company 北京奔流网络信息技术有限公司
 * @created 2019/07/29 20:44
 * @changeRecord [修改记录] <br/>
 */
public class IntentBean {

    public String mKey;
    public String mValue;
    public String mType;

    public IntentBean(String mKey, String mValue, String mType) {
        this.mKey = mKey;
        this.mValue = mValue;
        this.mType = mType;
    }
}
