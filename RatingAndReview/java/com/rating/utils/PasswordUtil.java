package com.rating.utils;

import java.security.Key;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class PasswordUtil {

	private static final String ALGORITHM = "AES";
	private static final String KEY = "1Hbfh667adfDEJ78";

	/**
	 * This method create to get Random password. Password format is UUID.
	 * 
	 * @return
	 */
	public static String generatePassword() {

		try {
			return encrypt(UUID.randomUUID().toString()).replaceAll("/", "-");
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Using method to encrypt password. Here used BCrypt Password Encoder for
	 * encoding.
	 * 
	 * @param password
	 * @return
	 */
	public static String encryptPassword(String password) {
		try {
			return (new BCryptPasswordEncoder()).encode(password);
		} catch (Exception e) {

		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(encryptPassword("Admin@123"));
	}

	/**
	 * This method encrypt string values using AES algo. Here using Base64 encoder
	 * class.
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String value) throws Exception {
		Key key = generateKey();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
		return new BASE64Encoder().encode(encryptedByteValue);
	}

	/**
	 * This method dencrypt encrypted values using AES algo. Here using Base64
	 * Decoder class.
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String value) throws Exception {
		Key key = generateKey();
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
		byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
		String decryptedValue = new String(decryptedByteValue, "utf-8");
		return decryptedValue;
	}

	/**
	 * This method used to generate AES key.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
		return key;
	}

	/**
	 * This method used when we trying to match two encrypted password(Using BCrypt
	 * Password Encode). Basically this method used when user trying to login.
	 * 
	 * @param rawPassword
	 * @param encodedPassword
	 * @return
	 */
	public static Boolean matchPassword(String rawPassword, String encodedPassword) {
		try {
			return (new BCryptPasswordEncoder()).matches(rawPassword, encodedPassword);
		} catch (Exception e) {

		}
		return false;
	}
}
