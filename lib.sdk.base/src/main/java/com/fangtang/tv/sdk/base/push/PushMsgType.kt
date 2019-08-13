package com.fangtang.tv.sdk.base.push


object PushMsgType {

    const val PUSH_MSG_INIT = 300
    // 本地先预处理的数据
    const val PUSH_MSG = 301
    const val PUSH_MSG_BIND = 302
    const val PUSH_IOT_BIND = 303
    const val PUSH_IOT_UNBIND = 3031
    const val PUSH_MSG_LOGIN = 304

    const val PUSH_MSG_KEY_EVENT = 304

    // 向NLP接口发送的数据
    const val PUSH_MSG_QUERY = 13
    const val PUSH_MSG_COMMAND = 105


    const val TYPE_REG_COMMON_COMMAND = 103  // 绑定基础事件
    const val TYPE_REG_NAME_COMMAND = 104  // 绑定影片名字
}