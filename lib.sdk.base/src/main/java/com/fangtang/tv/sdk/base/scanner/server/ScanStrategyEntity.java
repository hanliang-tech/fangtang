package com.fangtang.tv.sdk.base.scanner.server;

import android.support.annotation.Keep;

import com.fangtang.tv.sdk.base.bean.StatusBean;

/**
 * @author WeiPeng
 * @version 1.0
 * @title ScanStrategyEntity.java
 * @description need_desc
 * @company 北京奔流网络信息技术有限公司
 * @created 2019/07/17 16:09
 * @changeRecord [修改记录] <br/>
 */
@Keep
public class ScanStrategyEntity extends StatusBean<ScanStrategyEntity.StrategyBean> {

    @Keep
    public static final class StrategyBean{
        public String dayNum;                   // 日最大次数
        public String weekNum;                  // 周最大次数
        public String monthNum;                 // 月最大次数
        public String maxBlockDeviceNum;        // 设备数量超过15个，则不弹出发现音箱浮窗

        @Override
        public String toString() {
            return "StrategyBean{" +
                    "dayNum='" + dayNum + '\'' +
                    ", weekNum='" + weekNum + '\'' +
                    ", monthNum='" + monthNum + '\'' +
                    ", maxBlockDeviceNum='" + maxBlockDeviceNum + '\'' +
                    '}';
        }
    }
}
