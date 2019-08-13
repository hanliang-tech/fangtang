### Android SDK 快速集成

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
        implementation "com.fangtang.tv.sdk:sdk:1.1.0"
		```

*   项目依赖:无需额外引用
    ``` java
    implementation('cn.leancloud.android:avoscloud-sdk:4.7.12')
    implementation('cn.leancloud.android:avoscloud-push:4.7.12@aar') { transitive = true }

    implementation 'com.android.support:support-v4:21.0.3'
    implementation 'com.android.support:support-annotations:28.0.0'

    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.3.41'
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.41'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1'

    implementation 'com.squareup.okhttp3:okhttp:3.10.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
    implementation 'com.squareup.okio:okio:1.15.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.3.0'

    implementation 'com.itkacher.okhttpprofiler:okhttpprofiler:1.0.4'
    implementation 'com.blankj:utilcode:1.23.7'
    ```


## 第三步 SDK的初始化

在工程Application处初始化FangTang的sdk：

* 在调用SDK相关接口前，必须先初始化SDK，否则不能正常使用相关功能。
    ``` java

    public class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()

            val fangTang = FangTang.getInstance()
            val configuration = FangTangConfiguration.Builder(this)
                    .setAppId("your app_id")
                    .setAppKey("your app_secret")
                    .setChannel("your app channel")
                    .setPushAppKey("your leancloud app key")
                    .setPushClientKey("your leancloud client key")
                    //.setAppManager(appManager)
                    .build()
            fangTang.init(configuration)
        }
    }


|方法名
|:-----
|`fangTang.init(configuration)`|


|参数名称|参数说明
|:-----|:---------|
|`appId`|appid
|`appKey`|appkey
|`channel` |渠道|
|`pushAppKey` |leancloud推送sdk使用的appKey|
|`pushClientKey` |leancloud推送sdk使用的clientKey|
|`appManager` |SDK不提供应用管理的功能，需要SDK使用方自行实现App的下载，删除，安装等功能。|

## 第四步 接口使用
### 以推荐分页数据为例

```java
         FangTang.getInstance().nlpManager.queryPage(0, "queryId", object : INLPManager.NLPPageListener {
            override fun onSuccess(pageBean: NLPPageBean?) {
            }

            override fun onError(e: Exception?) {
            }
        })
```

其他相关请查看详细api<br>

## 第五步 混淆

 如果需要混淆请添加如下代码
```java
    -keep class com.fangtang.tv.sdk.**{*;}
    -ignorewarnings
```