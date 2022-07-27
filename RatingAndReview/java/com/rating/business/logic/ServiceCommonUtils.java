/**
 * 
 */
package com.rating.business.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Ankita Shrothi
 *This common utils class contain all common method
 */
public class ServiceCommonUtils {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceCommonUtils.class);
	public static final String SQL_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String GU_DATE_TIME_FORMAT_1 = "dd-MMM-yyy hh:mm:ss";
	public static final Object SEARCH_BY_ACCOUNT = "searchByAccount";
	public static final Object SEARCH_BY_STATUS = "searchByStatus";
	public static final Object SEARCH_BY_ROMING_RESIION = "romingresigon";
	public static final String MERGED_DATE_TIME_FORMAT = "yyyyMMddHHmmssSSS";
	
	
	
	public static String getDateTime(){
		String currentDateTime = "";
		try {
			
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat(SQL_DATE_TIME_FORMAT);
	        currentDateTime  = dateFormat.format(date);
	        LOG.info("Current Date Time=={}", currentDateTime);
			
		} catch (Exception exception) {
			currentDateTime = "";
			exception.printStackTrace();
		}
		
		return currentDateTime;
	}
	
	public static String convertDateFormat(String strDate,String currentFormat,String newFormat){
		
		String newDateFormat = ""; 
		try {
			 DateFormat currentDf = new SimpleDateFormat(currentFormat);
			 Date date = currentDf.parse(strDate);
			 DateFormat dateFormat = new SimpleDateFormat(newFormat);
			 newDateFormat  = dateFormat.format(date);
			 
			 newDateFormat = newDateFormat.replace(" ", " | ");
			
		} catch (Exception exception) {
			
		}
		
		return newDateFormat;
	}

	public static String generateUniqueIdByDateTime(){
		String currentDateTime = "";
		try {
			
			Date date = new Date();
			DateFormat dateFormat = new SimpleDateFormat(MERGED_DATE_TIME_FORMAT);
	        currentDateTime  = dateFormat.format(date);
			
		} catch (Exception exception) {
			currentDateTime = "";
			exception.printStackTrace();
		}
		
		return currentDateTime;
	}
	


	
}
