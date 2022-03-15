package com.hzlz.aviation.library.util.aes;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理
 * 对原始数据进行AES加密后，在进行Base64编码转化；
 */
public class AesUtils {

    private static final String encodingFormat = "UTF-8";

    private volatile static AesUtils singleInstance = null;

    private AesUtils() {
    }

    public static AesUtils getInstance() {
        if (singleInstance == null) {
            synchronized (AesUtils.class) {
                if (singleInstance == null) {
                    singleInstance = new AesUtils();
                }
            }
        }
        return singleInstance;
    }

    // 加密
    public String encrypt(String sign, String signIv, String sSrc) {
        try {
            Cipher cipher = Cipher.getInstance("AES_128/CBC/PKCS5Padding");
            byte[] raw = sign.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES_128");
            IvParameterSpec iv = new IvParameterSpec(signIv.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(encodingFormat));
            return new BASE64Encoder().encode(encrypted);//此处使用BASE64做转码。
        } catch (Exception ex) {
            return sSrc;
        }
    }

    // 解密
    public String decrypt(String sign, String signIv, String sSrc) {
        try {
            byte[] raw = sign.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES_128");
            Cipher cipher = Cipher.getInstance("AES_128/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(signIv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, encodingFormat);
        } catch (Exception ex) {
            return sSrc;
        }
    }

}
