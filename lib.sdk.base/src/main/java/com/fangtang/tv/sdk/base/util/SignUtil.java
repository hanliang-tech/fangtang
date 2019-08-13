package com.fangtang.tv.sdk.base.util;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SignUtil {

    public static String getSign(Map<String, String> values, String key)  {
        List<Map.Entry<String, String>> list = new ArrayList<>(values.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> e : list) {
                sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8")).append('&');
            }
        } catch (UnsupportedEncodingException e) {
        }

        if(sb.length() > 0) sb.setLength(sb.length() - 1);
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        }
        byte[] bytes = mac.doFinal(sb.toString().getBytes());
        byte[] bytes2 = new byte[40];

        for (int i = 0; i < 20; ++i) {
            byte b = bytes[i];

            bytes2[i * 2] = toHexChar(b >> 4 & 0xF);
            bytes2[i * 2 + 1] = toHexChar(b & 0xF);
        }
        String data = Base64.encodeToString(bytes2, Base64.DEFAULT);
        return data.replace("\n","");
    }


    private static byte toHexChar(int x) {
        if (0 <= x && x <= 9) {
            return (byte) ('0' + x);
        } else {
            return (byte) ('a' - 10 + x);
        }
    }

    public static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception{
        byte[] data = encryptKey.getBytes("UTF-8");

        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");

        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1");

        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes("UTF-8");

        // 完成 Mac 操作
        byte[] finalText = mac.doFinal(text);

        StringBuilder sBuilder = bytesToHexString(finalText);
        return sBuilder.toString().toUpperCase();
    }

    public static StringBuilder bytesToHexString(byte[] bytesArray){
        if (bytesArray == null){
            return null;
        }
        StringBuilder sBuilder = new StringBuilder();
        for (byte b : bytesArray){
            String hv = String.format("%02x", b);
            sBuilder.append(hv);
        }
        return sBuilder;
    }

}
