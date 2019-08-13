package com.fangtang.tv.sdk.base.nlp.bean;

import java.util.List;

/**
 * @author WeiPeng
 * @version 1.0
 * @title NLPPageBean.java
 * @description need_desc
 * @company 北京奔流网络信息技术有限公司
 * @created 2019/07/18 15:03
 * @changeRecord [修改记录] <br/>
 */
public class NLPPageBean {
    /**
     * page : 2
     * total : 15061
     * page_size : 20
     * count : 20
     * items : [{"cover":"http://pic7.iqiyipic.com/image/20190401/a2/1c/v_109109842_m_601_m2_260_360.jpg","grade":"7.1","title":"精神病院大疯狂（原声）","movie_id":12178,"custom_data":"{\"jump\":{\"parameter\":\"\",\"type\":\"uri\",\"flag\":\"0x10000000\",\"name\":\"bftv\",\"intent_toast\":\"正在为您跳转跳转爱奇艺\",\"package_name\":\"com.gitvbf.video\",\"uri\":\"gala:\\/\\/page?playType=history&videoId=362988200&episodeId=362988200&history=0&chnId=1&customer=bftv\",\"action\":\"com.gitvbf.video.action.ACTION_DETAIL\"},\"name\":\"爱奇艺\",\"package\":\"com.gitvbf.video\"}","origin":"iqiyi","origin_name":"银河奇异果","origin_icon":"http://live-fengmi.b0.upaiyun.com/admin/movie_resouce_icon/银河奇异果.png"}]
     */

    private int page;
    private int total;
    private int page_size;
    private int count;
    private List<InnerMovieBean> items;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<InnerMovieBean> getItems() {
        return items;
    }

    public void setItems(List<InnerMovieBean> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataBean{");
        sb.append("page=").append(page);
        sb.append(", total=").append(total);
        sb.append(", page_size=").append(page_size);
        sb.append(", count=").append(count);
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
