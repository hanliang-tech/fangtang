package com.fangtang.tv.sdk.base.nlp;

import android.text.TextUtils;
import android.util.Log;

import com.fangtang.tv.constantplugin.Constant;
import com.fangtang.tv.constantplugin.FunctionCode;
import com.fangtang.tv.constantplugin.SequenceCode;
import com.fangtang.tv.nlp.AsrCacheRegister;
import com.fangtang.tv.nlp.BetterAsrData;
import com.fangtang.tv.nlp.BetterAsrEngine;
import com.fangtang.tv.nlp.EventMap;
import com.fangtang.tv.nlp.Interceptor;
import com.fangtang.tv.nlp.InterceptorCacheMap;
import com.fangtang.tv.nlp.InvaildCacheMap;
import com.fangtang.tv.nlp.NlpConstant;
import com.fangtang.tv.nlp.NlpData;
import com.fangtang.tv.nlp.NlpP;
import com.fangtang.tv.nlp.NlpType;
import com.fangtang.tv.nlp.RootAsrCacheRegister;
import com.fangtang.tv.nlp.RootCacheRegister;
import com.fangtang.tv.nlp.ScoreCacheRegister;
import com.fangtang.tv.nlp.ServiceCacheRegister;
import com.fangtang.tv.nlp.SystemAppCacheRegister;
import com.fangtang.tv.nlp.ThirdPartyAppCacheRegister;
import com.fangtang.tv.nlp.Util;
import com.fangtang.tv.nlp.ViewCacheRegister;
import com.fangtang.tv.nlp.ViewNlpCacheRegister;
import com.fangtang.tv.nlp.VoiceViewCacheRegister;
import com.fangtang.tv.nlp.WhitelistData;
import com.fangtang.tv.nlp.call.BaseCall;
import com.fangtang.tv.nlp.data.SimpleCmdData;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HookGlobalCall extends BaseCall {

    @Override
    protected NlpData deal(String txt, String helpTxt, String flag, OnNlpDealListener onNlpDealListener) {
        NlpData nlpData = new NlpData();
        nlpData.userTxt = txt;
        nlpData.tmpTxt = txt.replace("大耳朵", "");
        nlpData.timeUse = System.currentTimeMillis();
        nlpData.voiceFlag = flag;
        nlpData.helpTxt = helpTxt;
        String rootPck = RootCacheRegister.getRootPck();
        String className;
        String pckName;

        if (TextUtils.isEmpty(rootPck)) {
            className = onNlpDealListener.getTopClassName();
            pckName = onNlpDealListener.getTopApp();
        } else {
            pckName = rootPck;
            className = RootCacheRegister.getRootClassName();
            NlpP.p("ROOT SUCCESS" + pckName);
        }
        boolean isNeedCheck = true;
//        if (Constant.VOICE_PACKAGE.equals(pckName)) {
//            isNeedCheck = false;
//        } else
            if (WhitelistData.getInstance().isContain(className)) {
            NlpP.p("当前在白名单当中:" + className);
            onNlpDealListener.onWriteListInit(className);
        }
        if (isNeedCheck) {
            isNeedCheck = !onNlpDealListener.voicePriority();
        }
        //Root Asr检测
        if (RootAsrCacheRegister.getFunctionData().size() > 0) {
            if (RootAsrCacheRegister.getFunctionData().containsKey(pckName)) {
                nlpData.isDeal = true;
                nlpData.topPck = pckName;
                nlpData.topClassName = className;
                RootAsrCacheRegister.RootAsrCache asrCache = RootAsrCacheRegister.getFunctionData().get(pckName);
                nlpData.isNeedPinYin = asrCache.isNeedPinYin;
                nlpData.isEnableContinuousRecognition = asrCache.isEnableContinuousRecognition;
                nlpData.isHideAnimation = asrCache.isHideAnimation;
                nlpData.isUseDDZGame = asrCache.isUseDDZGame;
                nlpData.nlpType = NlpType.ASR_CACHE;
            }
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        //ASR检测
        if (AsrCacheRegister.getFunctionData().size() > 0) {
            if (AsrCacheRegister.getFunctionData().containsKey(className)) {
                nlpData.isDeal = true;
                nlpData.topPck = pckName;
                nlpData.topClassName = className;
                AsrCacheRegister.AsrCache asrCache = AsrCacheRegister.getFunctionData().get(className);
                nlpData.isNeedPinYin = asrCache.isNeedPinYin;
                nlpData.isEnableContinuousRecognition = asrCache.isEnableContinuousRecognition;
                nlpData.isHideAnimation = asrCache.isHideAnimation;
                nlpData.isUseDDZGame = asrCache.isUseDDZGame;
                nlpData.nlpType = NlpType.ASR_CACHE;
            }
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        if (nlpData.userTxt.length() <= 1 || InvaildCacheMap.getInvaild().contains(nlpData.userTxt)) {
            if (TextUtils.isEmpty(nlpData.helpTxt)) {
                nlpData.isDeal = true;
                nlpData.nlpType = NlpType.INVALID;
            }
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        List<Interceptor> netInterceptor = InterceptorCacheMap.getInterceptors(className);
        nlpData = runInterceptor(nlpData, netInterceptor, nlpData.tmpTxt, pckName, className);
        if (nlpData.isDeal) {
            if (isNeedCheck) {
                nlpData.sequence = SequenceCode.TYPE_CUSTOM;
            }
            return nlpData;
        }
        int sequence = Util.getSequenceCode(className);
        List<Interceptor> interceptors = new ArrayList<>();
        if (sequence != 0) {
            if (SequenceCode.isContainPage(sequence)) {
                if (Constant.FILP_PAGES.equals(nlpData.tmpTxt)) {
                    nlpData.functionType = FunctionCode.FILP_PAGES;
                    nlpData.isDeal = true;
                    nlpData.timeUse = System.currentTimeMillis() - nlpData.timeUse;
                    nlpData.topPck = pckName;
                    nlpData.topClassName = className;
                    nlpData.sequence = SequenceCode.TYPE_PAGE;
                    return nlpData;
                } else if (Constant.FRONT_PAGE.equals(nlpData.tmpTxt) || Constant.JUST_NOW_PAGE.equals(nlpData.tmpTxt)) {
                    nlpData.functionType = FunctionCode.PRE;
                    nlpData.isDeal = true;
                    nlpData.timeUse = System.currentTimeMillis() - nlpData.timeUse;
                    nlpData.topPck = pckName;
                    nlpData.index = 1;
                    nlpData.topClassName = className;
                    nlpData.sequence = SequenceCode.TYPE_PAGE;
                    return nlpData;
                } else if (Constant.NEXT_1.equals(nlpData.tmpTxt) || Constant.NEXT_2.equals(nlpData.tmpTxt) || Constant.NEXT_3.equals(nlpData.tmpTxt)) {
                    nlpData.functionType = FunctionCode.NEXT;
                    nlpData.isDeal = true;
                    nlpData.timeUse = System.currentTimeMillis() - nlpData.timeUse;
                    nlpData.topPck = pckName;
                    nlpData.index = 1;
                    nlpData.topClassName = className;
                    nlpData.sequence = SequenceCode.TYPE_PAGE;
                    return nlpData;
                }
                interceptors.add(NlpConstant.sPreInterceptor);
                interceptors.add(NlpConstant.sNextInterceptor);
                interceptors.add(NlpConstant.sPageInterceptor);
            }
            if (SequenceCode.isContainNum(sequence)) {
                interceptors.add(NlpConstant.sLookInterceptor);
                interceptors.add(NlpConstant.sPlayInterceptor);
                interceptors.add(NlpConstant.sOpenNoInterceptor);
                interceptors.add(NlpConstant.sBuyInterceptor);
                interceptors.add(NlpConstant.sCartInterceptor);
                interceptors.add(NlpConstant.sCollectInterceptor);
                interceptors.add(NlpConstant.sDefaultInterceptor);
            }
        }
        nlpData.sequence = sequence;
        interceptors.add(NlpConstant.sHalfHourShutDownInterceptor);
        interceptors.add(NlpConstant.sHourShutDownInterceptor);
        interceptors.add(NlpConstant.sMinuteShutDownInterceptor);
        interceptors.add(NlpConstant.sSecondShutDownInterceptor);
        interceptors.add(NlpConstant.sHDMIInterceptor);

        //系统指令词
        nlpData = runInterceptor(nlpData, interceptors, nlpData.tmpTxt, pckName, className);
        if (nlpData.isDeal) {
            return nlpData;
        }
        //判断是否处于关机状态
        if (onNlpDealListener.isShutdown()) {
            NlpP.p("当前用户已经关机");
            if (nlpData.tmpTxt.equals("开关") || nlpData.tmpTxt.equals("打开") || nlpData.tmpTxt.equals("开开机") || nlpData.tmpTxt.equals("快进")) {
                nlpData.tmpTxt = "开机";
            }
            String betterAsr = "";
            HashMap<String, String> map = new HashMap<>(1);
            try {
                map.put("开机", PinyinHelper.convertToPinyinString("开机", ",", PinyinFormat.WITHOUT_TONE));
                betterAsr = BetterAsrEngine.getInstance().getBetterAsr(nlpData.tmpTxt, 3, map);
            } catch (PinyinException e) {
                e.printStackTrace();
            }
            NlpP.p("关机better asr :" + betterAsr + "|oldAsr:" + nlpData.tmpTxt);
            nlpData.userTxt = betterAsr;
            nlpData.tmpTxt = betterAsr;
        }

        if (onNlpDealListener.isSupportBetterAsr()) {
            if (BetterAsrData.getInstance().isContain(className)) {
                String betterAsr;
                try {
                    betterAsr = BetterAsrEngine.getInstance().getBetterAsr(nlpData.tmpTxt, ScoreCacheRegister.getScore(className, onNlpDealListener));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Less", "py-Exception:" + e.getMessage());
                    betterAsr = nlpData.tmpTxt;
                }
                NlpP.p("better asr :" + betterAsr + "|oldAsr:" + nlpData.tmpTxt);
                nlpData.tmpTxt = betterAsr;
            }
        }
        //提示词
        nlpData = checkTipsCache(pckName, className, nlpData, isNeedCheck, onNlpDealListener);
        if (nlpData.isDeal) {
            return nlpData;
        }

        //应用缓存
        nlpData = checkAppCache(pckName, className, nlpData, isNeedCheck, onNlpDealListener);
        if (nlpData.isDeal) {
            return nlpData;
        }
        //界面缓存
        nlpData = checkCache(className, pckName, className, nlpData, isNeedCheck ? ViewCacheRegister.getCacheData() : VoiceViewCacheRegister.getCacheData(), NlpType.VIEW_CACHE, isNeedCheck, true, onNlpDealListener);
        if (nlpData.isDeal) {
            return nlpData;
        }

        if (isNeedCheck) {
            //批量修改正则表达式指令词
            nlpData = checkCache(className, pckName, className, nlpData, ViewNlpCacheRegister.getCacheData(), NlpType.VIEW_NLP_CACHE, isNeedCheck, false, onNlpDealListener);
            if (nlpData.isDeal) {
                return nlpData;
            }
        }
        //卸载
        if (nlpData.userTxt.startsWith(Constant.UNINSTALL)) {
            String data = ThirdPartyAppCacheRegister.get(nlpData.userTxt.replace(Constant.UNINSTALL, ""));
            if (!TextUtils.isEmpty(data)) {
                nlpData.isDeal = true;
                nlpData.topPck = pckName;
                nlpData.topClassName = className;
                nlpData.nlpType = NlpType.UNINSTALL_CACHE;
                nlpData.nlpJson = data;
            }
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        //打开
        if (nlpData.userTxt.startsWith(Constant.OPEN)) {
            String system = SystemAppCacheRegister.get(nlpData.userTxt.replace(Constant.OPEN, ""));
            if (!TextUtils.isEmpty(system)) {
                nlpData.isDeal = true;
                nlpData.topPck = pckName;
                nlpData.topClassName = className;
                nlpData.nlpType = NlpType.OPEN_APP_CACHE;
                nlpData.nlpJson = system;
            } else {
                String thirdParty = ThirdPartyAppCacheRegister.get(nlpData.userTxt.replace(Constant.OPEN, ""));
                if (!TextUtils.isEmpty(thirdParty)) {
                    nlpData.isDeal = true;
                    nlpData.topPck = pckName;
                    nlpData.topClassName = className;
                    nlpData.nlpType = NlpType.OPEN_APP_CACHE;
                    nlpData.nlpJson = thirdParty;
                }
            }
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        //确定
        if (nlpData.userTxt.equals(Constant.OK) || nlpData.userTxt.equals(Constant.PLAY) || nlpData.userTxt.equals(Constant.OPEN)) {
            nlpData.isDeal = true;
            nlpData.nlpType = NlpType.OK;
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        SimpleCmdData simpleCmdData = new SimpleCmdData();
        //云端控制词
        if (ServiceCacheRegister.getData().containsKey(nlpData.tmpTxt)) {
            nlpData.nlpType = NlpType.SERVICE;
            nlpData.isDeal = true;
            nlpData.timeUse = System.currentTimeMillis() - nlpData.timeUse;
            nlpData.topClassName = className;
            nlpData.topPck = pckName;
            nlpData.nlpJson = ServiceCacheRegister.getData().get(nlpData.tmpTxt);
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        //固定话术
        nlpData = simpleCmdData.search(nlpData, className, pckName);
        if (nlpData.isDeal) {
            return nlpData;
        }
        //无效的词过滤
        if (nlpData.userTxt.equals("type_cancel") || nlpData.userTxt.equals("type_short")) {
            nlpData.isDeal = true;
            nlpData.nlpType = NlpType.INVALID;
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        //Event事件
        if (EventMap.getEvent().containsKey(nlpData.userTxt)) {
            nlpData.nlpType = NlpType.EVENT;
            nlpData.isDeal = true;
            nlpData.rect = EventMap.getEvent().get(nlpData.userTxt);
        }
        if (nlpData.userTxt.startsWith("@@@")) {
            String result = nlpData.tmpTxt.replace("@@@", "");
            int splitPos = result.indexOf("|");
            nlpData.userTxt = result.substring(0, splitPos);
            nlpData.nlpJson = result.substring(splitPos + 1);
            nlpData.nlpType = NlpType.JSON_CACHE;
            nlpData.isDeal = true;
        }
        if (nlpData.isDeal) {
            return nlpData;
        }
        return nlpData;
    }
}
