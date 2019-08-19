# Jinwu Restful API Document

该文档记录 Jinwu 项目各业务接口描述

## 项目地址

http://

## 基础参数

所有接口必传

参数名称 | 类型 | 必传 | 描述
--- | --- | --- | ---
appkey | str | 是 | 合作方key
token | str | 是 | 认证token

## 媒资数据接收

由合作方上传媒资数据的接口

请求方式 | URL
--- | ---
POST | /api/content

参数名称 | 类型 | 必传 | 描述
--- | --- | --- | ---
results | list | 是 | 媒资信息列表, 字段列表请参考附一

**example**

request

```json
{
    "appkey": "appkey",
    "token": "0ax14x81xf5x0bxf7xd3",
    "results": [
        {
            "id": 26425063,
            "url": "https://movie.douban.com/subject/26425063/",
            "category": {
                "1": "电影"
            },
            "title": "无双",
            "titleOther": "無雙",
            "description": "身陷囹圄的李问（郭富城 饰）被引渡回港，他原本隶属于一个的跨国假钞制贩组织。该组织曾犯下过多宗恶性案件，而首脑“画家”不仅始终逍遥法外，连真面目都没人知道。为了逼迫李问吐露“画家”真实身份，警方不惜用...",
            "cover": "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2535260806.jpg",
            "area": [
                "中国大陆",
                "中国香港"
            ],
            "language": [
                "汉语普通话",
                "粤语"
            ],
            "alias": [
                "Project Gutenberg",
                "Mo seung"
            ],
            "currentTotal": 1,
            "episodeTotal": 1,
            "score": "8.1",
            "views": -1,
            "genre": [
                "动作",
                "悬疑"
            ],
            "tags": [
                "香港",
                "犯罪"
            ],
            "duration": 7800,
            "year": 2018,
            "isPay": 0,
            "onLine": 1,
            "contentType": 1,
            "releaseDate": "2018-09-30"
        }
    ]
}
```

response

```json
{
  "info": "ok"
}
```

## 附一、媒资信息字段列表

字段 | 字段类型 | 描述
--- | --- | ---
id | int、string | 专辑ID 
url | string | 地址
category | dict | 分类
title | string | 名称
titleOther | string | 外文名
description | string | 简介
cover | string | 封面
alias | list | 别名
score | string | 评分
views | string | 播放量
director | list | 导演
author | list | 编剧
actor | list | 主演
genre | list | 类型
area | list | 地区
tags | list | 标签
language | list | 语言
duration | int | 时长 秒
year | int | 上映年份
isPay | int | 是否付费 0 免费、1 付费、2 部分付费
onLine | int | 是否在线 1 在线、其余下线
contentType | int | 资源类型 0 非正片、1 正片
releaseDate | string | 上映日期
sourceUpdateTime | string | 资源站更新时间
currentTotal | int | 当前分集总数
episodeTotal | int | 分集总数
playInfo | dict | 播放参数
extend | dict | 扩展字段
# Jinwu Restful API Document

该文档记录 Jinwu 项目各业务接口描述

## 项目地址

http://jinwu.fangtangtv.com

## 基础参数

所有接口必传

参数名称 | 类型 | 必传 | 描述
--- | --- | --- | ---
appkey | str | 是 | 合作方key
token | str | 是 | 认证token

## 媒资数据接收

由合作方上传媒资数据的接口

请求方式 | URL
--- | ---
POST | /api/content

参数名称 | 类型 | 必传 | 描述
--- | --- | --- | ---
results | list | 是 | 媒资信息列表, 字段列表请参考附一

**example**

request

```json
{
    "appkey": "appkey",
    "token": "0ax14x81xf5x0bxf7xd3",
    "results": [
        {
            "id": 26425063,
            "url": "https://movie.douban.com/subject/26425063/",
            "category": {
                "1": "电影"
            },
            "title": "无双",
            "titleOther": "無雙",
            "description": "身陷囹圄的李问（郭富城 饰）被引渡回港，他原本隶属于一个的跨国假钞制贩组织。该组织曾犯下过多宗恶性案件，而首脑“画家”不仅始终逍遥法外，连真面目都没人知道。为了逼迫李问吐露“画家”真实身份，警方不惜用...",
            "cover": "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2535260806.jpg",
            "area": [
                "中国大陆",
                "中国香港"
            ],
            "language": [
                "汉语普通话",
                "粤语"
            ],
            "alias": [
                "Project Gutenberg",
                "Mo seung"
            ],
            "currentTotal": 1,
            "episodeTotal": 1,
            "score": "8.1",
            "views": -1,
            "genre": [
                "动作",
                "悬疑"
            ],
            "tags": [
                "香港",
                "犯罪"
            ],
            "duration": 7800,
            "year": 2018,
            "isPay": 0,
            "onLine": 1,
            "contentType": 1,
            "releaseDate": "2018-09-30"
        }
    ]
}
```

response

```json
{
  "info": "ok"
}
```

## 附一、媒资信息字段列表

字段 | 字段类型 | 描述
--- | --- | ---
id | int、string | 专辑ID 
url | string | 地址
category | dict | 分类
title | string | 名称
titleOther | string | 外文名
description | string | 简介
cover | string | 封面
alias | list | 别名
score | string | 评分
views | string | 播放量
director | list | 导演
author | list | 编剧
actor | list | 主演
genre | list | 类型
area | list | 地区
tags | list | 标签
language | list | 语言
duration | int | 时长 秒
year | int | 上映年份
isPay | int | 是否付费 0 免费、1 付费、2 部分付费
onLine | int | 是否在线 1 在线、其余下线
contentType | int | 资源类型 0 非正片、1 正片
releaseDate | string | 上映日期
sourceUpdateTime | string | 资源站更新时间
currentTotal | int | 当前分集总数
episodeTotal | int | 分集总数
playInfo | dict | 播放参数
extend | dict | 扩展字段
