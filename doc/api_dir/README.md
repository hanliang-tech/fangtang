### Android SDK API V1.0.0

## 获取相关推荐数据
### query
* 参数名：`query` 类型：String  参数详细：用户要搜索的关键词
* 参数名：`attributes` 类型：String  参数详细：服务端返回的参数，默认传空字符串""
* 参数名：`callBack` 类型：KCallBack 参数详细：获取相关推荐的回调

```java
FangTang.getInstance().nlpManager.query(query, params, object : INLPManager.NLPQueryListener {
    override fun onSuccess(nlpBean: NLPBean) {
    }

    override fun onError(e: java.lang.Exception) {
    }
})
```

NLPBean字段说明：
|字段名称|字段说明
|:-----|:---------|
|`domain`|场景
|`intention`|意图
|`query`|被处理后的 query
|`raw_query`|原始 query
|`query_id`|请求id
|`content`|NLPContentBean类型

NLPContentBean字段说明：
|字段名称|字段说明
|:-----|:---------|
|`display`|展示文本
|`tts`|播报文本
|`reply`|JsonElement skill 返回数据，根据不同的业务返回格式不一样，具体的查看业务列表

## 获取相关推荐分页数据
### queryPage
* 参数名：`page` 类型：int  参数详细：要请求的数据的页数
* 参数名：`queryId` 类型：String  参数详细：用户要搜索的关键词
* 参数名：`listener` 类型：NLPPageListener  参数详细：获取数据接口监听

```java
FangTang.getInstance().nlpManager.queryPage(0, "刘德华的电影", object : INLPManager.NLPPageListener {
        override fun onSuccess(pageBean: NLPPageBean?) {
        }

        override fun onError(e: Exception?) {
        }
    })
})
```

NLPPageBean字段说明：
|字段名称|字段说明
|:-----|:---------|
|`page`|当前的页码
|`total`|数据的总数量
|`page_size`|分页的大小
|`count`|当前页数据的大小
|`items`|InnerMovieBean类型的集合


InnerMovieBean字段说明：
|字段名称|字段说明
|:-----|:---------|
|`grade`|当前的页码
|`movie_id`|视频的id
|`cid`|视频的分类id
|`origin`|数据来源
|`title`|标题
|`custom_data`|跳转地址json
|`cover`|视频的封面
|`icon`|角标标示
|`origin_icon`|数据来源图片
|`origin_name`|数据来源的名称
|`package_name`|包名

## 获取音箱绑定码
### getBindDeviceCode

```java
FangTang.getInstance().remoteManager.getBindDeviceCode(
        object : IRemoteManager.RemoteDeviceBindCodeListener {
            override fun onBindCodeSuccess(bindCode: RemoteDeviceBindCode?) {
            }

            override fun onBindCodeError(e: Exception?) {
            }
        })
```
RemoteDeviceBindCode字段说明：
|字段名称|字段说明
|:-----|:---------|
|`code`|设备绑定码
|`tv_id`|设备的id

## 解除音箱绑定
### unbindDeviceInfo
* 参数名：`deviceId` 类型：String  参数详细：设备的id
```java
FangTang.getInstance().remoteManager.unbindDeviceInfo(id,
                object : IRemoteManager.RemoteDeviceUnBindListener {
                    override fun onUnBindDeviceSuccess(status: RemoteDeviceBindStatus?) {
                    }

                    override fun onUnBindDeviceError(e: Exception?) {
                    }
                }
)
```
RemoteDeviceBindStatus字段说明：
|字段名称|字段说明
|:-----|:---------|
|`status`|值为1代表解绑成功


## 获取绑定设备信息
### getBindDeviceInfo

```java
FangTang.getInstance().remoteManager.getBindDeviceInfo(
        object : IRemoteManager.RemoteDeviceBindInfoListener {
            override fun onBindInfoSuccess(bindInfo: RemoteDeviceBindInfo) {

            }

            override fun onBindInfoError(e: java.lang.Exception?) {
            }
        })
```

RemoteDeviceBindInfo字段说明：
|字段名称|字段说明
|:-----|:---------|
|`devices`|绑定设备信息列表
|`wechat`|微信设备信息列表
|`tv_id`|电视的id
|`connected_image`|设备连接成功的图片地址
|`un_connected_image`|设备未连接的图片地址

RemoteDevice字段说明：
|字段名称|字段说明
|:-----|:---------|
|`id`|
|`device_id`|设备的id
|`source`|设备的来源
|`name`|设备的名称


