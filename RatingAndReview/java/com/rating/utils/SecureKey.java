package com.rating.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureKey {

	@SuppressWarnings("unused")
	private static SecretKeySpec secretKey;

	private static byte[] key;

	@SuppressWarnings("unused")
	private static String secret = "AES";

	private static Logger logger = LoggerFactory.getLogger(SecureKey.class);

	/**
	 * This method create to set AES key.
	 * 
	 * @param myKey
	 */
	@SuppressWarnings("unused")
	private static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {

		} catch (UnsupportedEncodingException e) {

		}
	}

	/**
	 * This method used to encrypt string values.
	 * 
	 * @param strToEncrypt
	 * @return
	 */
	public static String encrypt(String strToEncrypt) {
		try {
			// setKey(secret);
			// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			// cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			// return
			// Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

			return String.valueOf(Hex.encodeHex(strToEncrypt.getBytes(StandardCharsets.UTF_8)));

		} catch (Exception e) {
			logger.debug("Error while encrypting: {}", e.toString());
		}
		return null;
	}

	/**
	 * This method used to decrypt string values.
	 * 
	 * @param strToDecrypt
	 * @return
	 */
	public static String decrypt(String strToDecrypt) {
		try {
			// setKey(secret);
			// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			// cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			// return
			// Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

			return new String(Hex.decodeHex(strToDecrypt.toCharArray()), "UTF-8");

		} catch (Exception e) {
			logger.debug("Error while encrypting: {}", e.toString());
		}
		return null;
		// try {
		// setKey(secret);
		// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		// cipher.init(Cipher.DECRYPT_MODE, secretKey);
		// return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		// } catch (Exception e) {
		// System.out.println("Error while decrypting: " + e.toString());
		// }
		// return null;
	}
}
