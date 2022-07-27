package com.rating.business.logic.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rating.bo.common.BaseEntity;
import com.rating.dto.BaseResponse;
import com.rating.dto.common.AbstractItem;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.rating.business.logic.EncryptionDecryptionService;

@Service
public class EncryptionDecryptionServiceImpl implements EncryptionDecryptionService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static Set<String> excludeParams = null;
	static {

		String[] excludeIt = {  "loggedInUserId", "loggedInUserName", "authToken", "request",
				"requestId", "returnUrl", "requestUrl",  "executeApiUrl", "apiId", "apiName" };

		excludeParams = new HashSet<>(Arrays.asList(excludeIt));
	}

	/**
	 * @param myKey
	 * @return
	 */
	private SecretKeySpec setKey(String myKey) {

		Hasher hasher = Hashing.sha256().newHasher();
		hasher.putString(myKey, Charsets.UTF_8);
		HashCode sha256 = hasher.hash();

		// System.out.println(sha256);
		return new SecretKeySpec(sha256.asBytes(), "AES");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.EncryptionDecryptionService#encrypt(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public String encrypt(String strToEncrypt, String secret) {
		SecretKeySpec secretKey = setKey(secret);
		return encrypt(strToEncrypt, secretKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.EncryptionDecryptionService#encrypt(java.lang.
	 * String, javax.crypto.spec.SecretKeySpec)
	 */
	@Override
	public String encrypt(String strToEncrypt, SecretKeySpec secretKey) {
		if (strToEncrypt != null)
			try {
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
			} catch (Exception e) {
				logger.error("Error while encrypting : {}", e.toString());
			}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.EncryptionDecryptionService#decrypt(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public String decrypt(String strToEncrypt, String secret) throws Exception {
		SecretKeySpec secretKey = setKey(secret);
		return decrypt(strToEncrypt, secretKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rating.bl.EncryptionDecryptionService#decrypt(java.lang.
	 * String, javax.crypto.spec.SecretKeySpec)
	 */
	@Override
	public String decrypt(String strToDecrypt, SecretKeySpec secretKey) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception exception) {
			logger.error("Error while decrypting : {}", exception.toString());
			throw exception;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rating.bl.EncryptionDecryptionService#decryptHeaderMap(java.
	 * util.Map, java.lang.String)
	 */
	@Override
	public Map<String, Object> decryptHeaderMap(Map<String, Object> mapToDecrypt, String secret) throws Exception {

		SecretKeySpec secretKey = setKey(secret);

		for (Map.Entry<String, Object> entry : mapToDecrypt.entrySet()) {
			try {
				String key = entry.getKey();
				
				if (excludeParams.contains(key)) {
					continue;
				} else if (entry.getValue() instanceof List) {
					mapToDecrypt.put(key, decryptObjectValues(entry.getValue(), secretKey));
				} else if (entry.getValue() instanceof Map) {
					mapToDecrypt.put(key, decryptObjectValues(entry.getValue(), secretKey));
				} else {
					mapToDecrypt.put(key, decrypt(String.valueOf(entry.getValue()), secretKey));
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				logger.error("failed to decrypt a key of Header Params" + entry.getKey());
				throw exception;
			}
		}

		return mapToDecrypt;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rating.bl.EncryptionDecryptionService#decryptMapValues(java.
	 * util.Map, java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map decryptMapValues(Map mapToDecrypt, String secret) {
		SecretKeySpec secretKey = setKey(secret);
		for (Object key : mapToDecrypt.keySet()) {
			try {
				Object value = mapToDecrypt.get(key);
				if (value != null)
					mapToDecrypt.put(key, decrypt(String.valueOf(value), secretKey));

			} catch (Exception e) {
				logger.error("failed to decrypt a key of Map" + key);
			}
		}

		return mapToDecrypt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rating.bl.EncryptionDecryptionService#encryptObjectValues(java
	 * .lang.Object, java.lang.String)
	 */
	@Override
	public Object encryptObjectValues(Object objToEncrypt, String secret) {
		SecretKeySpec secretKey = setKey(secret);
		return encryptObjectValues(objToEncrypt, secretKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rating.bl.EncryptionDecryptionService#encryptObjectValues(java
	 * .lang.Object, javax.crypto.spec.SecretKeySpec)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object encryptObjectValues(Object objToEncrypt, SecretKeySpec secretKey) {

		if (objToEncrypt instanceof List) {
			List encryptedList = new ArrayList<>();

			for (Object entry : (List) objToEncrypt) {
				encryptedList.add(encryptObjectValues(entry, secretKey));
			}
			return encryptedList;
		} else if (objToEncrypt instanceof Map) {
			Map encryptedMap = new LinkedHashMap<>();

			for (Object key : ((Map) objToEncrypt).keySet()) {
				try {
					Object value = ((Map) objToEncrypt).get(key);

					encryptedMap.put(key, encryptObjectValues(value, secretKey));
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("failed to decrypt a key of Map" + key);
				}
			}

			encryptedMap.remove("class");
			return encryptedMap;
		} else if (objToEncrypt instanceof AbstractItem || objToEncrypt instanceof BaseResponse
				|| objToEncrypt instanceof BaseEntity) {
			BeanMap mapOfObject = convertPojoToMap(objToEncrypt);

			return encryptObjectValues(mapOfObject, secretKey);
		} else if (objToEncrypt instanceof Date) {
			DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			String date = sdf.format(objToEncrypt);
			return encrypt(date, secretKey);

		} else {
			// string
			return encrypt(String.valueOf(objToEncrypt), secretKey);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rating.bl.EncryptionDecryptionService#decryptObjectValues(java
	 * .lang.Object, java.lang.String)
	 */
	@Override
	public Object decryptObjectValues(Object objToDecrypt, String secret) throws Exception {
		SecretKeySpec secretKey = setKey(secret);
		return decryptObjectValues(objToDecrypt, secretKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rating.bl.EncryptionDecryptionService#decryptObjectValues(java
	 * .lang.Object, javax.crypto.spec.SecretKeySpec)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object decryptObjectValues(Object objToDecrypt, SecretKeySpec secretKey) throws Exception {

		if (objToDecrypt instanceof List) {
			List decryptedList = new ArrayList<>();

			for (Object entry : (List) objToDecrypt) {
				decryptedList.add(decryptObjectValues(entry, secretKey));
			}
			return decryptedList;
		} else if (objToDecrypt instanceof Map) {
			Map decryptedMap = new LinkedHashMap<>();

			for (Object key : ((Map) objToDecrypt).keySet()) {
				try {
					Object value = ((Map) objToDecrypt).get(key);

					decryptedMap.put(key, decryptObjectValues(value, secretKey));
				} catch (Exception exception) {
					exception.printStackTrace();
					logger.error("failed to decrypt a key of Map" + key);
					throw exception;
				}
			}

			decryptedMap.remove("class");
			return decryptedMap;
		} else if (objToDecrypt instanceof AbstractItem || objToDecrypt instanceof BaseResponse
				|| objToDecrypt instanceof BaseEntity) {
			BeanMap mapOfObject = convertPojoToMap(objToDecrypt);

			return decryptObjectValues(mapOfObject, secretKey);
		} else {
			// string
			return decrypt(String.valueOf(objToDecrypt), secretKey);
		}
	}

	/**
	 * @param pojo
	 * @return BeanMap
	 */
	private BeanMap convertPojoToMap(Object pojo) {
		return new BeanMap(pojo);

	}
}
