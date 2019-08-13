package com.fangtang.tv.sdk.base.scanner


enum class SupportDevice(val macPrefix: String,
                         val displayName: String,
                         val company: String) {

    T_MALL_1("18BC5A", "天猫精灵", "Zhejiang Tmall Technology Co., Ltd."),
    T_MALL_2("188219", "天猫精灵", "Alibaba Cloud Computing Ltd."),
    XIAO_AI("3CBD3E", "小爱同学", "Beijing Xiaomi Electronics Co., Ltd."),
    JD("DCD87C", "京东音箱", "Beijing Jingdong Century Trading Co., LTD."),
    BAIDU("882D53", "百度音箱", "Baidu Online Network Technology (Beijing) Co., Ltd."), ;

    override fun toString(): String {
        return "macPrefix:$macPrefix, displayName:$displayName, company:$company"
    }
}