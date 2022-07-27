package com.rating.business.logic;

import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

public interface EncryptionDecryptionService {
	/**
	 * @param strToEncrypt
	 * @param secret
	 * @return Encrypted String of String data which needs to be encrypt. 
	 */
	String encrypt(String strToEncrypt, String secret);
	
	/**
	 * @param strToEncrypt
	 * @param secretKey
	 * @return Encrypted String of String data which needs to be encrypt.
	 */
	String encrypt(String strToEncrypt, SecretKeySpec secretKey);

	/**
	 * @param strToEncrypt
	 * @param secret
	 * @return Decrypted String of String data which needs to be decrypt.
	 */
	String decrypt(String strToEncrypt, String secret) throws Exception;

	/**
	 * @param strToDecrypt
	 * @param secretKey
	 * @return Decrypted String of String data which needs to be decrypt.
	 */
	String decrypt(String strToDecrypt, SecretKeySpec secretKey) throws Exception;

	/**
	 * @param mapToEncrypt
	 * @param secret
	 * @return Decrypted Map<String, Object> data which needs to be decrypt in form of Map<String, Object>.
	 */
	Map<String, Object> decryptMapValues(Map<String, Object> mapToEncrypt, String secret);

	/**
	 * @param objToEncrypt
	 * @param secret
	 * @return Encrypted values in form of Object for provided object literal.
	 */
	Object encryptObjectValues(Object objToEncrypt, String secret);

	/**
	 * @param objToEncrypt
	 * @param secretKey
	 * @return Encrypted values in form of Object for provided object literal.
	 */
	Object encryptObjectValues(Object objToEncrypt, SecretKeySpec secretKey);

	/**
	 * @param objToDecrypt
	 * @param secretKey
	 * @return Decrypted values in form of Object for provided object literal.
	 */
	Object decryptObjectValues(Object objToDecrypt, SecretKeySpec secretKey) throws Exception;

	/**
	 * @param objToDecrypt
	 * @param secret
	 * @return Decrypted values in form of Object for provided object literal.
	 */
	Object decryptObjectValues(Object objToDecrypt, String secret) throws Exception;

	/**
	 * @param mapToDecrypt
	 * @param secret
	 * @return Decrypted values in form of Map<String, Object> for provided Map<String, Object> literal.
	 */
	Map<String, Object> decryptHeaderMap(Map<String, Object> mapToDecrypt, String secret) throws Exception;

}
