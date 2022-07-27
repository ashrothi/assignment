package com.rating.utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.security.spec.KeySpec;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class create for common operations like randomNumber, Convert epoch time
 * into formatted date time, encrypt decrypt strings etc.
 * 
 * @author Ankita Shrothi 
 *
 */
public class CommonUtils {

	public static final String downloadCsvCommonLimit = "download.csv.common.limit";

	public static final String SIM_STATE_CHANGE = "SimState";
	public static final String ATTACH_SERVICE_PLAN = "AttachServicePlan";
	public static final String ATTACH_DEVICE_PLAN = "AttachDevicePlan";
	public static final String ATTACH_ADDON_PLAN = "AttachAddonPlan";

	public static final String CREATE_ACCOUNT_PLAN = "Account_Plan_Create";
	public static final String UPDATE_ACCOUNT_PLAN = "Account_Plan_Update";
	public static final String DELETE_ACCOUNT_PLAN = "Account_Plan_Delete";

	public static final String CREATE_PRICE_MODEL = "Price_Model_Create";
	public static final String UPDATE_PRICE_MODEL = "Price_Model_Update";
	public static final String DELETE_PRICE_MODEL = "Price_Model_Delete";

	public static final String CREATE_ACCOUNT = "Account_Create";
	public static final String UPDATE_ACCOUNT = "Account_Update";
	public static final String DELETE_ACCOUNT = "Account_Delete";

	public static final String CREATE_DEVICE_PLAN = "Device_Plan_Create";
	public static final String UPDATE_DEVICE_PLAN = "Device_Plan_Update";
	public static final String ASSIGN_DEVICE_PLAN = "Device_Plan_Assign";
	public static final String DELETE_DEVICE_PLAN = "Device_Plan_Delete";

	public static final String CREATE_SERVICE_PLAN = "Service_Plan_Create";
	public static final String UPDATE_SERVICE_PLAN = "Service_Plan_Update";
	public static final String ASSIGN_SERVICE_PLAN = "Service_Plan_Assign";
	public static final String DELETE_SERVICE_PLAN = "Service_Plan_Delete";

	public static final String SIM_STATE_CHANGE_ATTRIBUTE = "Sim_State";
	public static final String DEVICE_PLAN_CHANGE_ATTRIBUTE = "Device_State";

	public static final String CREATE_ADDON_PLAN = "Addon_Plan_Create";
	public static final String UPDATE_ADDON_PLAN = "Addon_Plan_Update";
	//public static final String ASSIGN_ADDON_PLAN = "Addon_Plan_Assign";
	public static final String DELETE_ADDON_PLAN = "Addon_Plan_Delete";

	/**
	 * Async Code
	 */
	public static final int ASYNC_API_STATUS = 90000;

	/**
	 * Other user loggedin status
	 */
	public static final String OTHER_USER_API_STATUS = "90100";

	/**
	 * Secret key and salt.
	 */
	private static String secretKey = "AIRLINQ";
	private static String salt = "airling.com";

	/**
	 * Mails Subjects
	 */
	public static final String SIGN_IN_SUBJECT = "sign.in.subject";
	public static final String ACCOUNT_CREATE_IN_SUBJECT = "account.create.in.subject";
	public static final String APIUSER_CREATE_IN_SUBJECT = "apiuser.create.in.subject";
	public static final String APIUSER_FORGOT_IN_SUBJECT = "apiuser.forgot.in.subject";
	public static final String APIUSER_GENERATEPASSWORD_IN_SUBJECT = "apiuser.generate.password.in.subject";
	public static final String APIUSER_ADDUSER_IN_SUBJECT = "apiuser.adduser.in.subject";
	public static final String APIUSER_FORGOT_PASSWORD_IN_SUBJECT = "apiuser.forgot.password.in.subject";
	public static final String SIMORDER_IN_SUBJECT = "simorder.in.subject";
	public static final String SIMORDER_UPDATE_IN_SUBJECT = "simorderupdate.in.subject";
	public static final String CREATEUSER_IN_SUBJECT = "createuser.in.subject";
	public static final String USER_FORGOT_PASSWORD_IN_SUBJECT = "user.forgot.password.in.subject";
	public static final String ADDUSER_IN_SUBJECT = "adduser.in.subject";
	public static final String BLOCK_INACTIVE_USER_IN_SUBJECT = "block.inactive.user.in.subject";
	public static final String LOCK_REMINDER_IN_SUBJECT = "lock.reminder.in.subject";
	public static final String RETENTION_IN_SUBJECT = "retention.in.subject";
	public static final String TERMINATION_IN_SUBJECT = "termination.in.subject";
	

	/**
	 * Mails FileNames
	 */
	public static final String SIGN_IN_FILE = "SignIn.vm";
	public static final String ACCOUNT_CREATE_IN_FILE = "AccountCreate.vm";
	public static final String APIUSER_CREATE_IN_FILE = "ApiuserCreate.vm";
	public static final String APIUSER_FORGOT_IN_FILE = "ApiuserForgot.vm";
	public static final String APIUSER_GENERATEPASSWORD_IN_FILE = "ApiuserGeneratePassword.vm";
	public static final String APIUSER_ADDUSER_IN_FILE = "ApiuserAddUser.vm";
	public static final String APIUSER_FORGOT_PASSWORD_IN_FILE = "ApiuserForgotPassword.vm";
	public static final String SIMORDER_IN_FILE = "SimOrder.vm";
	public static final String SIMORDER_UPDATE_IN_FILE = "SimOrderUpdate.vm";
	public static final String CREATEUSER_IN_FILE = "CreateUser.vm";
	public static final String USER_FORGOT_PASSWORD_IN_FILE = "UserForgotPassword.vm";
	public static final String ADDUSER_IN_FILE = "AddUser.vm";
	public static final String BLOCK_INACTIVE_USER_IN_FILE = "BlockInactiveUser.vm";
	public static final String LOCK_REMINDER_IN_FILE = "LockReminder.vm";
	public static final String RETENTION_IN_FILE = "Retention.vm";
	public static final String TERM_CONDITION_FILE = "Termcondition.vm";
	public static final String TERMINATION_IN_FILE = "Termination.vm";
	
	
	/**
	 * PDF Files
	 */
	
	public static final String REPORTOREDR = "order_invoice.vm";
	
	public static final String CREATETYPE = "create";
	public static final String UPDATETYPE = "update";
	public static final String DELETETYPE = "delete";
	
	/**
	 * 
	 * This method create to get random numbers.
	 *
	 * @return
	 */
	public static String generateOrderNumber(String simType) {

		String orderNumber = "CMP" + new Date().getTime() + "" + simType;

		return orderNumber;

		// Random random = new Random();
		// int number = random.nextInt(9999);
		// return String.format("%04d", number);
	}

	public static String getRandomNumberString() {
		// It will generate 6 digit random Number.
		// from 0 to 999999
		Random rnd = new Random();
		int number = rnd.nextInt(9999);

		// this will convert any number sequence into 6 character.
		return String.format("%04d", number);
	}



	public static String get5DigitRandomNum() {
		// It will generate 6 digit random Number.
		// from 0 to 999999
		Random rnd = new Random();
		int number = rnd.nextInt(99999);

		// this will convert any number sequence into 6 character.
		return String.format("%05d", number);
	}
	
	public static String get6DigitRandomNum() {
		// It will generate 6 digit random Number.
		// from 0 to 999999
		Random rnd = new Random();
		int number = rnd.nextInt(999999);

		// this will convert any number sequence into 6 character.
		return String.format("%06d", number);
	}


	/**
	 * This method create to convert epoch time into formatted date with time zone.
	 * Date format is 'MM/dd/yyyy HH:mm:ss'.
	 * 
	 * @param inputDate
	 * @return
	 */
	public static String epochtTimeToDateWithTimeZone(String inputDate) {
		long input_seconds = Long.parseLong(inputDate);
		// convert seconds to milliseconds
		Date date = new Date(input_seconds);
		// format of the date
		// SimpleDateFormat jdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String input_date = jdf.format(date);
		return input_date;
	}

	/**
	 * This method create to convert epoch time into formatted date. Date format is
	 * 'yyyy-MM-dd HH:mm:ss'.
	 * 
	 * @param date
	 * @return
	 */
	public static String epochDateToDateWithTime(String date) {
		String output = null;
		try {
			Date expiry = new Date(Long.parseLong(date));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			output = sdf.format(expiry);
			// System.out.println("after epoc[" + date + "] to date and time conversion:" +
			// output);

		} catch (Exception e) {
			System.out.println("Exception occured converting date with time : {}" + e.getMessage());
		}
		return output;
	}

	public static Date epochToDatetime(String value) {
		Date finalValue = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date date = new Date(Long.parseLong(value));

			// Get-Ids
			finalValue = dateFormat.parse(dateFormat.format(date));

			return finalValue;
		} catch (Exception e) {
			System.out.println("Exception occured converting date with time : {}" + e.getMessage());
		}
		return finalValue;
	}

	/**
	 * This method create to convert epoch time into formatted date. Date format is
	 * 'yyyy-MM-dd HH:mm'.
	 * 
	 * @param date
	 * @return
	 */
	public static String epochDateToDateWithTimeWithoutSec(String date) {
		String output = null;
		try {
			Date expiry = new Date(Long.parseLong(date));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			output = sdf.format(expiry);
			System.out.println("after epoc[" + date + "] to date and time conversion:" + output);

		} catch (Exception e) {
			System.out.println("Exception occured converting date with time : {}" + e.getMessage());
		}
		return output;
	}

	/**
	 * This method create to convert epoch time into formatted date. Date format is
	 * 'yyyy-MM-dd HH:mm:ss.000'.
	 * 
	 * @param date
	 * @return
	 */
	public static String epochDateToDateWithTimeWithMiliSec(String date) {
		String output = null;
		try {
			Date expiry = new Date(Long.parseLong(date));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000");
			output = sdf.format(expiry);
			System.out.println("after epoc[" + date + "] to date and time conversion:" + output);
		} catch (Exception e) {
			System.out.println("Exception occured converting date with time : {}" + e.getMessage());
		}
		return output;
	}

	/**
	 * This method used to decrypt string. Here used AES algo for decryption.
	 * 
	 * @param strToDecrypt
	 * @return
	 */
	public static String decryptString(String strToDecrypt) {
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * Using this method to encrypt the string. Here used AES algo for encrypt
	 * string.
	 * 
	 * @param strToEncrypt
	 * @return
	 */
	public static String encryptString(String strToEncrypt) {
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * This method used to get current time.
	 * 
	 * @return
	 */
	public static String getSystemCurrentTimeInUtc() {
		final Date currentTime = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.out.println("System Current time in UTC time: " + sdf.format(currentTime));
		return sdf.format(currentTime);
	}

	/**
	 * This method used to get current time.
	 * 
	 * @return
	 */
	public static String getSystemCurrentTimeInUtc(String format) {
		final Date currentTime = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		System.out.println("System Current time in UTC time: " + sdf.format(currentTime));
		return sdf.format(currentTime);
	}

	/**
	 * This method create to convert epoch time into formatted date. Date format is
	 * 'yyyy-MM-dd HH:mm:ss'.
	 * 
	 * @param dateStr
	 * @return
	 */
	public static String dateToString(String dateStr) {

		try {

			Long millis = dateToEpochWithTime(dateStr);
			Date date = new Date(millis);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			String formatted = format.format(date);
			return formatted;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * This method create to convert date into epoch time. Date format is
	 * 'yyyy-MM-dd HH:mm:ss'.
	 * 
	 * @param dateValue
	 * @return
	 */
	public static long dateToEpochWithTime(String dateValue) {

		Date date = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			date = df.parse(dateValue);
		} catch (ParseException e) {
			// TODO Auto-generated catch block

		}
		long time = date.getTime();
		return time;
	}

	/**
	 * 
	 * @param dateValue
	 * @return
	 */
	public static long dateToEpoch(String dateValue) {

		Date date = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			date = df.parse(dateValue);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time = date.getTime();

		System.out.println(time);
		return time;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String epocToDateConverter(String date) {
		String output = null;
		try {
			Date expiry = new Date(Long.parseLong(date));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			output = sdf.format(expiry);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * This method used to convert Byte into MB.
	 * 
	 * @param size
	 * @return
	 */
	public static double getSizeInMb(long size) {
		String s = "";
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			double n = 1024.0;
			double kb = ((size / n) / n);
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
			s = decimalFormat.format(kb);
		} catch (Exception e) {

		}

		return Double.valueOf(s);
	}

	/**
	 * get value, from object
	 *
	 * @param obj
	 *            - object that contains value
	 * @return -
	 */
	public static String getStringValueFromObject(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return String.valueOf(obj);
		}
	}

	/**
	 * get value, from object and check that string starts from @+=- then add double
	 * quotes check if value is numeric and contains + - then send as it is if
	 * string contains extra double quotes then remove extra double quote if value
	 * contains multiple comma (,) then write value in double quotes
	 * 
	 * @param value
	 *            - object that contains value
	 * @return -
	 */
	public static String getDataValue(Object value) {
		if (value != null) {
			// remove double quote from beginning and end
			value = value.toString().replaceAll("^\"|\"$", "");
			// if value contains some special
			Pattern compile = Pattern.compile("(^[@+=-])|(\\s*,\\s*)");
			Matcher matcher = compile.matcher(value.toString());
			if (matcher.find()) {
				// check value is number then do not add single quote else add
				if (NumberUtils.isNumber(value.toString())) {
					return value.toString();
				} else {
					return "\"'" + value + "'\"";
				}

			} else {
				return value.toString();
			}
		} else {
			return String.valueOf(" ");
		}
	}

	/**
	 * get locale for a specific country based on keyword
	 * 
	 * @param keyword
	 *            - language keyword, supported language pt,en,zh,hi,es,fr
	 * @return - return locale with selected country
	 */
	public static Locale getLocaleByLanguageKeyword(String keyword) {
		Locale locale = null;
		
		if(keyword==null)
		{
			locale = Locale.ENGLISH;
			return locale;
		}
		
		switch (keyword) {
		case "fr":
			locale = Locale.FRANCE;
			break;
		case "zh":
			locale = Locale.CHINA;
			break;
		case "es":
			locale = new Locale("es", "ES");
			break;
		case "hi":
			locale = new Locale("hi", "IN");
			break;
		case "pt":
			locale = new Locale("pt", "PT");
			break;
		case "ar":
			locale = new Locale("ar", "AR");
			break;
		default:
			locale = Locale.ENGLISH;

		}
		return locale;
	}

	/**
	 * Implementation of String.valueOf method which gives null instead of "null"
	 * (null in string)
	 * 
	 * @return toString() of given object or null
	 */
	public static String valueOf(Object obj) {
		if (obj == null)
			return null;
		return obj.toString();
	}

	public static void log(Level debug, String module, String sourceIp, String userName, String sessionId,
			String activityPerformed, String activityStatus, String activityMessage, String endpointUrl,
			String responseCode, Throwable throwable, String threadId, String responseTime) {
		// try {
		// CustomLogger.log(debug, StringUtils.defaultString(module),
		// StringUtils.isNotBlank(sourceIp) ? sourceIp :
		// InetAddress.getLocalHost().toString(),
		// StringUtils.defaultString(userName), StringUtils.defaultString(sessionId),
		// StringUtils.defaultString(activityPerformed),
		// StringUtils.defaultString(activityStatus),
		// StringUtils.defaultString(activityMessage),
		// StringUtils.defaultString(endpointUrl),
		// StringUtils.defaultString(responseCode), throwable,
		// StringUtils.defaultString(threadId),
		// StringUtils.defaultString(responseTime));
		// CustomLogger.log(debug, module, StringUtils.isNotBlank(sourceIp) ? sourceIp :
		// InetAddress.getLocalHost().toString(), userName,
		// StringUtils.isNotBlank(sessionId) ? sessionId
		// : null , activityPerformed, activityStatus, activityMessage, endpointUrl,
		// responseCode, throwable,
		// threadId, responseTime);
		// } catch (UnknownHostException e) {
		// e.printStackTrace();
		// }
	}

	public static int generateOtp() {
		return (int) Math.round(Math.random() * (999999 - 100000 + 1) + 100000);
	}

	public static String[] decode(final String encoded) {
		try {
			final byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes());
			final String pair = new String(decodedBytes);
			final String[] userDetails = pair.split(":", 2);
			return userDetails;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * this method add single quote
	 * 
	 * @param value
	 *            - returns value with single quote
	 * @return - quoted value
	 */
	public static String getStringWithSingleQuote(String value) {
		return "\"'" + value + "'\"";
	}

	public static String getDataValueWithKeyword(Object value, String keyword) {
		if (value != null) {
			// remove double quote from beginning and end
			value = value.toString().replaceAll("^\"|\"$", "");
			// if value contains some special
			Pattern compile = Pattern.compile("(^[@+=-])|(\\s*,\\s*)");
			Matcher matcher = compile.matcher(value.toString());
			if (matcher.find()) {
				// check value is number then do not add single quote else add
				if (NumberUtils.isNumber(value.toString())) {
					return value.toString();
				} else {
					return "\"'" + value + "'\"";
				}

			} else {
				// If value is for IMSI and MSISDN then we will add single quote on the value
				// else it will work as it is.
				if (StringUtils.isNotBlank(keyword) && (keyword.equalsIgnoreCase("IMSI")
						|| keyword.equalsIgnoreCase("MSISDN") || keyword.equalsIgnoreCase("ICCID"))) {
					return "'" + value + "'";
				} else {
					return value.toString();
				}
			}
		} else {
			return String.valueOf(" ");
		}
	}

	public static HashMap<String, Object> checkSpecialCharacters(String data) {
		HashMap<String, Object> errorMap = new LinkedHashMap<String, Object>();
		if (data.matches("[^a-zA-Z0-9\\s+\\.]")) {
			errorMap.put("errorMessage", "Special characters are not allowed.");
			return errorMap;
		}

		if (data.matches("\\<.*?\\>")) {
			errorMap.put("errorMessage", "Special characters are not allowed.");
			return errorMap;
		}

		Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>^*()%!-]");
		if (regex.matcher(data).find()) {
			errorMap.put("errorMessage", "Special characters are not allowed.");
			return errorMap;
		}

		Pattern specialCharacterRegex = Pattern.compile("[^a-zA-Z0-9\\s+\\.]");
		if (specialCharacterRegex.matcher(data).find()) {
			errorMap.put("errorMessage", "Special characters are not allowed.");
			return errorMap;
		}

		Pattern bracketRegex = Pattern.compile("\\<.*?\\>");
		if (bracketRegex.matcher(data).find()) {
			errorMap.put("errorMessage", "Special characters are not allowed.");
			return errorMap;
		}
		return errorMap;
	}

	public static String modifyConstantProcedure(String proc, HashMap<String, String> inputParams) {
		if (!inputParams.isEmpty()) {
			for (Map.Entry<String, String> entry : inputParams.entrySet()) {
				if (entry.getValue() != null)
					proc = proc.replace("<" + entry.getKey() + ">", entry.getValue());
			}
		}
		return proc;
	}




	
	
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param differenceOfDays
	 * @return
	 */
	public static Map<String, Object> validateDates(String startDate, String endDate, int differenceOfDays) {

		SimpleDateFormat simpleDataFormatter = new SimpleDateFormat("yyyy-MM-dd");

		Map<String, Object> responseMap = new HashMap<>();
		try {
			simpleDataFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

			Date date1 = simpleDataFormatter.parse(startDate);
			Date date2 = simpleDataFormatter.parse(endDate);
			Date currentDate = simpleDataFormatter.parse(simpleDataFormatter.format(new Date()));

			// Start Date Should Not be Greater than Current Date.
			if (date1.after(currentDate)) {
				responseMap.put("errorMessage", "Start Date Cannot Be Greater than Current Date");
				responseMap.put("valid", "false");
				return responseMap;
			}

			// End Date Should Not be Greater than Current Date.
			if (date2.after(currentDate)) {
				responseMap.put("errorMessage", "End Date Cannot Be Greater than Current Date");
				responseMap.put("valid", "false");
				return responseMap;
			}

			// Start Date Should Not be Greater than End Date.
			if (date1.after(date2)) {
				responseMap.put("errorMessage", "Start Date Cannot Be Greater than End Date");
				responseMap.put("valid", "false");
				return responseMap;
			}

			long diff = date2.getTime() - date1.getTime();
			long differenceBetweenInputDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

			// Difference Of Days Should not be greater than defined days.
			if (differenceBetweenInputDays > differenceOfDays) {
				responseMap.put("errorMessage", "Only Data of " + differenceOfDays + " days is allowed.");
				responseMap.put("valid", "false");
				return responseMap;

			}

			responseMap.put("Result", "Start Date and End Date are correct");
			responseMap.put("valid", "true");
			return responseMap;

		} catch (Exception e) {
			responseMap.put("errorMessage", "Start Date and End Date are not correct");
			responseMap.put("valid", "false");
			return responseMap;
		}

	}
}
