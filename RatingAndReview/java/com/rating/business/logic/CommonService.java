package com.rating.business.logic;

import com.rating.dl.Repository.ComplexQueryAttribute;
import com.rating.exc.RatingAndReviewException;

/**
 * 
 * @author Ankita Shrothi
 * 
 * To get Common details of globe touch portal
 *
 */
public interface CommonService extends CrudService {
	

	
	/**
	 * This method is use to remove all set of defined special character.
	 * @param columnDescription
	 * @param specialCharacter
	 * @param newChar
	 * @return new string after removing all defined special char.
	 */
	public  String getDescriptionAsKey(String  columnDescription,String []specialCharacter,String newChar);
	
	

	
	public <T> T getDataInfoByName(String searchField,String name, Class type) throws RatingAndReviewException;

	public boolean isAlreadyExist(String field, String name, Long accountId, Class type, Long id);




	ComplexQueryAttribute searchTableData(String searchPropertyNames, String searchValue) throws RatingAndReviewException;
}