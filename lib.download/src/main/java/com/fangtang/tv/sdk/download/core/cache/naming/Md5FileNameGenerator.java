package com.fangtang.tv.sdk.download.core.cache.naming;


import com.fangtang.tv.sdk.download.Download;
import com.fangtang.tv.sdk.download.utils.DL;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5FileNameGenerator implements FileNameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    @Override
    public String generate(Download download) {
        byte[] md5 = getMD5(download.getUrl().getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX) + ".apk";
    }

    private byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            DL.e(e);
        }
        return hash;
    }
}
