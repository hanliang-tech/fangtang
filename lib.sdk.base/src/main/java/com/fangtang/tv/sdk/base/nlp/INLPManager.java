package com.fangtang.tv.sdk.base.nlp;

import com.fangtang.tv.sdk.base.nlp.bean.NLPBean;
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;


public interface INLPManager {

    void registerVoiceSystemCache(String key);

    void registerVoiceViewCache(String className, ConcurrentHashMap<String, String> cacheMap);

    void query(String query, String attributes, NLPQueryListener listener);

    void queryPage(int page, String queryId, NLPPageListener listener);

    void postHistory(@Nullable String cid, @Nullable String movieId);

    void release();

    interface NLPQueryListener {

        void onSuccess(NLPBean nlpBean);

        void onError(Exception e);
    }

    interface NLPPageListener{

        void onSuccess(NLPPageBean nlpPageBean);

        void onError(Exception e);
    }
}


