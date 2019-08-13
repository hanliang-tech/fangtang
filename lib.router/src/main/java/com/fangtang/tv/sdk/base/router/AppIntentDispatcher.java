package com.fangtang.tv.sdk.base.router;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.blankj.utilcode.util.IntentUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppIntentDispatcher {

    private static final String TYPE_INTENT = "type";
    private static final String TYPE_PACKAGE = "package";
    private static final String TYPE_ACTION = "action";
    private static final String TYPE_BROADCAST = "broadcast";
    private static final String TYPE_SERVICE = "service";
    private static final String TYPE_SYSTEM = "system";
    private static final String TYPE_URI = "uri";
    private static final String TYPE_AM = "am";
    private static final String INTENT_PACKAGE_NAME = "package_name";
    private static final String INTENT_ACTIVITY_NAME = "activity_name";
    private static final String INTENT_ACTION = "action_name";
    private static final String INTENT_PARAMETER = "parameter";
    private static final String INTENT_FLAG = "flag";
    private static final String INTENT_CATEGORY = "category";
    private static final String INTENT_EXTRA = "extra";
    private static final String INTENT_URI = "uri";
    private static final String DOWNLOAD_URL = "download";
    private static final String TOAST = "toast";
    private static final String TTS_DATA = "tts_data";
    private static final String AM_INTENT = "am";
    private static final String NAME = "name";
    private static final String VERSION_CODE = "version_code";
    private static final String ACTION = "action";
    private static final String MD5 = "md5";
    private static final String INTENT_TOAST = "intent_toast";
    private static final String BASE64_SUPPORT = "base64";

    public static JsonBean parserIntent(String json) {
        JsonBean jsonBean;
        try {
            JSONObject jo = new JSONObject(json);
            jsonBean = new JsonBean(jo.has("jump") ? jo.getJSONObject("jump") : jo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonBean;
    }

    public static void dispatch(@NotNull Context context, String json, @NotNull DispatcherCallBack callBack) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            callBack.jsonParseError(e.getMessage(), json);
        }

        if (jo != null) {
            JsonBean jsonBean = new JsonBean(jo);
            if (TextUtils.isEmpty(jsonBean.packageName)) {
                callBack.jsonParseError("package_name is NULL", json);
                return;
            }
            // 如果已经安装
            if (callBack.checkAppValidate(jsonBean.packageName, jsonBean.versioncode)) {
                // 跳转
                Intent intent = new Intent();
                try {
                    callBack.beforeJump(intent, jsonBean.packageName, jsonBean.intentToast);
                    jump(context, jsonBean, intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                    callBack.jumpError(json, e.getMessage());
                }
            } else {
                callBack.doDownload(jsonBean.name, jsonBean.packageName, jsonBean.versioncode, jsonBean.downloadUrl, jsonBean.md5);
            }
        }
    }

    public static void jump(Context context, JsonBean bean, Intent intent) throws Exception {
        intent.setPackage(bean.packageName);
        applyParams(intent, bean);
        applyFlags(intent, bean);
        applyCategory(intent, bean);
        applyAction(intent, bean);
        applyUri(intent, bean);
        switch (bean.type) {
            case TYPE_PACKAGE:
                intent.setComponent(new ComponentName(bean.packageName, bean.activityName));
            case TYPE_URI:
            case TYPE_ACTION:
                context.startActivity(intent);
                break;
            case TYPE_BROADCAST:
                intent.setPackage(null);
                context.sendBroadcast(intent);
                break;
            case TYPE_SERVICE:
                intent.setComponent(new ComponentName(bean.packageName, bean.activityName));
                context.startService(intent);
                break;
            case TYPE_SYSTEM:
                Intent i = IntentUtils.getLaunchAppIntent(bean.packageName);
                i.addFlags(intent.getFlags());
                context.startActivity(i);
                break;
            case TYPE_AM:
                new Am().run(bean.amIntent, context);
                break;
            default:
                Log.e("[-sunrain-]", "暂不支持的跳转类型：" + bean.type);
                break;
        }
    }

    private static void applyAction(Intent intent, JsonBean bean) {
        if (!TextUtils.isEmpty(bean.actionName)) {
            intent.setAction(bean.actionName);
        } else if (!TextUtils.isEmpty(bean.action)) {
            intent.setAction(bean.action);
        }
    }

    private static void applyUri(Intent intent, JsonBean bean) {
        if (!TextUtils.isEmpty(bean.uri)) {
            Uri uri = Uri.parse(bean.uri);
            intent.setData(uri);
        }
    }

    private static void applyCategory(Intent intent, JsonBean bean) {
        if (!TextUtils.isEmpty(bean.category)) {
            String[] strCategory = bean.category.split("\\|");
            for (String c : strCategory) {
                intent.addCategory(c);
            }
        }
    }

    private static void applyFlags(Intent intent, JsonBean bean) {
        if (!TextUtils.isEmpty(bean.flag)) {
            String[] strFlag = bean.flag.split("\\|");
            for (String f : strFlag) {
                intent.addFlags(Integer.parseInt(f.substring(2), 16));
            }
        }
    }

    private static void applyParams(Intent intent, JsonBean bean) {
        if (!TextUtils.isEmpty(bean.parameter)) {
            ArrayList<IntentBean> arrayList = new ArrayList<>();
            String[] split = bean.parameter.split("\\|");
            for (String str : split) {
                String[] sP = str.split("\\^");
                String value;
                if (!TextUtils.isEmpty(bean.base64) && bean.base64.equals("parameter_value")) {
                    value = new String(Base64.decode(sP[1], Base64.DEFAULT));
                } else {
                    value = sP[1];
                }
                arrayList.add(new IntentBean(sP[0], value, sP[2]));
            }

            Bundle bundle = new Bundle();
            for (IntentBean intentBean : arrayList) {
                setExtras(intentBean.mType, intentBean.mKey, intentBean.mValue, bundle);
            }
            intent.putExtras(bundle);
        }
    }

    private static void setExtras(String type, String key, String value, Bundle bundle) {
        switch (type) {
            case "S":
                bundle.putString(key, Uri.decode(value));
                break;
            case "B":
                bundle.putBoolean(key, Boolean.parseBoolean(value));
                break;
            case "b":
                bundle.putByte(key, Byte.parseByte(value));
                break;
            case "c":
                bundle.putChar(key, Uri.decode(value).charAt(0));
                break;
            case "d":
                bundle.putDouble(key, Double.parseDouble(value));
                break;
            case "f":
                bundle.putFloat(key, Float.parseFloat(value));
                break;
            case "i":
                bundle.putInt(key, Integer.parseInt(value));
                break;
            case "l":
                bundle.putLong(key, Long.parseLong(value));
                break;
            case "s":
                bundle.putShort(key, Short.parseShort(value));
                break;
        }
    }

    public static class JsonBean {
        public String type, parameter, flag, category, extra, uri,
                downloadUrl, toast, name, versioncode, md5,
                packageName, activityName, actionName,
                intentToast, base64, ttsData, amIntent, action;

        public JsonBean(JSONObject jo) {
            type = jo.optString(TYPE_INTENT);
            parameter = jo.optString(INTENT_PARAMETER);
            flag = jo.optString(INTENT_FLAG);
            category = jo.optString(INTENT_CATEGORY);
            packageName = jo.optString(INTENT_PACKAGE_NAME);
            activityName = jo.optString(INTENT_ACTIVITY_NAME);
            actionName = jo.optString(INTENT_ACTION);
            extra = jo.optString(INTENT_EXTRA);
            uri = jo.optString(INTENT_URI);
            downloadUrl = jo.optString(DOWNLOAD_URL);
            toast = jo.optString(TOAST);
            name = jo.optString(NAME);
            versioncode = jo.optString(VERSION_CODE);
            md5 = jo.optString(MD5);
            intentToast = jo.optString(INTENT_TOAST);
            base64 = jo.optString(BASE64_SUPPORT);
            ttsData = jo.optString(TTS_DATA);
            amIntent = jo.optString(AM_INTENT);
            action = jo.optString(ACTION);
        }
    }

    public interface DispatcherCallBack {

        void jsonParseError(String msg, String json);

        boolean checkAppValidate(String packageName, String versioncode);

        void doDownload(String name, String packageName, String versioncode, String downloadUrl, String md5);

        void beforeJump(Intent intent, String pck, String toast);

        void jumpError(String json, String message);
    }
}
