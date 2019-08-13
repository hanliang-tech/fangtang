# 方糖SDK UI版集成文档

## 第一步 集成准备
`申请appid,appkey，方糖SDK接入方提供相关资料，向直方糖SDK提供方的运营申请即可。`
## 第二步 将SDK引入项目
  *  maven引入 
		``` java
		// 配置根目录gradle
		 maven {
            url('http://111.202.106.145:8083/repository/maven-3rd/')
            credentials {
                username 'guest'
                password 'guest'
            }
        }
		// 引入项目
		implementation "com.fangtang.tv.sdk:ui:1.1.0"
		```
		
## 第三步 SDK的初始化

在工程Application处初始化FangTang的sdk:
```
FangTangSDK.get().init(this, "your channel")
```
## 提供接口
发送Query:
```
FangTangSDK.get().query("刘德华的电影")
```

发送Query，接收sdk未处理的消息:
```
FangTangSDK.get().query("刘德华的电影", FangTangSDK.IQueryCallBack callBack)
```

FangTangSDK.IQueryCallBack中返回数据的说明：
```
nlpBean{
domain;         // String 场景
intention;      // String 意图
query;          // String 被处理后的 query
query_id;       // String 请求id
raw_query;      // String 原始 query
    content{    // skill 返回结果
        tts;        // String TTS 播报文本
        display;    // String 展示文本
        reply;      // JsonElement skill 返回数据，根据不同的业务返回格式不一样，具体的查看业务列表
    }

}
```

目前开放的domain和intention

| domain | intention |
| --- | --- |
| movie | searching 影视搜索<br>searching+ 影视搜索</br>channel_live 直播 |
| chat | base 基础tts播报<br>sensitive 敏感query |

