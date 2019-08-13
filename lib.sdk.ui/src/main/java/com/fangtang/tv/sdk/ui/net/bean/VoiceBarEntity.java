package com.fangtang.tv.sdk.ui.net.bean;

import android.support.annotation.Keep;

import java.util.List;

@Keep
public class VoiceBarEntity {

    public String tip;
    public List<Tip> words;


    @Keep
    public static class Tip{
        public int type;
        public String key;
        public String value;

        public Tip(String key, String value, int type) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Tip{");
            sb.append("type=").append(type);
            sb.append(", key='").append(key).append('\'');
            sb.append(", value='").append(value).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

}
