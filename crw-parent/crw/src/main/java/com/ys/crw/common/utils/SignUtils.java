package com.ys.crw.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author oscar.wu
 *
 */
public class SignUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SignUtils.class);

	
	public static String computeSign(byte[] bytes, String nonce, long timestamp, String secret){
		return computeSign(bytes, nonce, timestamp + "", secret);
	}
	
	public static String computeSign(byte[] bytes, String nonce, String timestamp, String secret){
		String plaintext = Base64.encodeBase64URLSafeString(bytes);
		return computeSign(plaintext, nonce, timestamp, secret);
	}
	
	public static String computeSign(String plaintext, String nonce, String timestamp, String secret) {
		String sign = hmacSha256(plaintext + "&nonce=" + nonce + "&timestamp=" + timestamp, secret);
		return sign;
	}
	
	/**
     * HmacSHA256加密
     * 
     * @param message
     *            消息
     * @param secret
     *            秘钥
     * @return 加密后字符串
     */
    private static String hmacSha256(String message, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            String sign = Base64.encodeBase64URLSafeString(bytes);
            return sign;
        } catch (Exception e) {
            LOG.error("Error HmacSHA256", e);
        }
        return "";
    }
}
