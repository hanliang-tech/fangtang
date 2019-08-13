package com.fangtang.tv.sdk;


public class Constant {

    // 接口请求的默认数据大小
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DISTANCE_ITEM = 42;
    public static final int ROW_COUNT = 7;

    // Video Item Size
    public static final float ITEM_VIDEO_WIDTH = 214F;
    public static final float ITEM_VIDEO_HEIGHT = 374F;
    public static final float SPECIAL_HOST_VIEW_WIDTH = 1830F;
    // Query & TTS
    public static final float HEIGHT_TTS_VIEW = 70F + 50F;

    public static final String ITEM_VIDEO = "video";
    public static final String ITEM_TTS = "tts";
    public static final String ITEM_NO_DATA = "empty";

    public static final String KEY_PAGE_IOT = "iot";
    public static final String KEY_PAGE_NLP = "nlp";

    public static final String KEY_SHOW_DEVICE_FIND_DIALOG = "iot_search";
    public static final String KEY_IOT_SEARCH_SWITCH = "iot_search_switch";

    public static final class NLP {

        public static final String DOMAIN_MOVIE = "movie";
        public static final String DOMAIN_CHAT = "chat";

        public static final String INTENTION_SEARCH = "searching";
        public static final String INTENTION_SEARCH_PLUS = "searching+";
        public static final String INTENTION_LIVE = "channel_live";



    }
}
