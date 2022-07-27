package com.rating.business.logic;

import java.util.Map;

import com.rating.dto.ApiResponse;

/**
 * @author Ankita Shrothi
 *
 */
public interface AuthService extends CrudService {


	/**
	 * @param params
	 * @return
	 */
	ApiResponse getToken(Map<?,?> params);

}
