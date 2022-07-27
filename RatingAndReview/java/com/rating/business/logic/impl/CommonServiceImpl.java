package com.rating.business.logic.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rating.dl.CrudRepository;
import com.rating.dl.Repository;
import com.rating.dl.Repository.ComplexQueryAttribute;
import com.rating.dl.Repository.QueryMode;
import com.rating.exc.RatingAndReviewException;
import com.rating.transform.TransformProcessor;
import com.rating.business.logic.CommonService;

@Service
@Transactional("RatingAndReviewTransactionManager")
public class CommonServiceImpl extends CrudServiceImpl implements CommonService {

	private static final String DEFAULT_ASSEST_FILEDS = "euiccId,iccid,imsi,msisdn,state,servicePlan_ServicePlanName,entAccount_Name,"
			+ "activationDate,inSession,location,network,subscriberName,subscriberEmail,operationalProfileDataPlan";

	private static final String[] SPECIAL_CHARACTER = {"-","(",")"," "};

	@Autowired(required = true)
	private TransformProcessor transformProcessor;

	@Autowired
	private CrudRepository crudRepository;

	@Resource
	private Environment env;

	private Map<String, String> mapSearch;

	public CommonServiceImpl() {
		mapSearch = new HashMap<>();
		
		mapSearch.put("search.tabledata.user", "userName,contactInfo.firstName,contactInfo.lastName");
	
		mapSearch.put("search.tabledata.apiuser", "userName,firstName,lastName,email,account.name");
		mapSearch.put("search.tabledata.rating", "author,productName,rating,title,reviewSource");
	
	}




	@Override
	public String getDescriptionAsKey(String columnDescription, String[] specialCharacter, String newChar) {

		try {
			for (String character : specialCharacter) {
				columnDescription = columnDescription.replace(character, newChar);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return columnDescription;
	}


	private Map<String, Object> getData(List<String> rowList, String columnListKey, String dataListKey) {

		Map<String, Object> columnDataMap = null;
		List<String> columnList = null;
		List<Map<String, Object>> dataList = null;
		Map<String, Object> mapDataRow = null;
		int index = 0;
		boolean isColumn = true;
		try {
			dataList = new ArrayList<>();
			columnList = new ArrayList<>();
			for (String row : rowList) {
				index = 0;
				if (!isColumn) {
					mapDataRow = new HashMap<>();
				}
				row = row + ",";
				while (row.contains(",")) {
					String columnValue = row.substring(0, row.indexOf(','));
					if (isColumn) {
						columnList.add(this.getDescriptionAsKey(columnValue, SPECIAL_CHARACTER, ""));
					} else {
						if (columnList != null && !columnList.isEmpty()) {
							mapDataRow.put(columnList.get(index), columnValue);
							index++;
						}
					}
					row = row.substring(row.indexOf(',') + 1, row.length());
				}
				if (!isColumn) {
					dataList.add(mapDataRow);
				}
				isColumn = false;
			}
			if ((columnList != null && !columnList.isEmpty()) && (dataList != null && !dataList.isEmpty())) {
				columnDataMap = new HashMap<>();
				columnDataMap.put(columnListKey, columnList);
				columnDataMap.put(dataListKey, dataList);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return columnDataMap;
	}



	private <T> T castObject(Class<T> clazz, Object object) {
		return (T) object;
	}

	

	@Override
	public <T> T getDataInfoByName(String searchField, String name, Class type) throws RatingAndReviewException {

		T t = null;

		try {
			CrudRepository.QueryFilter queryFilter = CrudRepository.QueryFilter.newInstance(type);
			queryFilter.addQueryAttribute(searchField, name, QueryMode.EQ, null);
			t = (T) crudRepository.getSingleEntity(queryFilter);
			if (t != null) {
				t = (T) transformProcessor.transformTo(t, type);
			}
		} catch (Exception exception) {
			throw new RatingAndReviewException("Failed to get data information : ", exception);
		}
		return t;
	}

	@Override
	public boolean isAlreadyExist(String field, String value, Long accountId, Class type, Long id) {
		boolean isExist = false;
		Repository.ComplexQueryAttribute complexQueryAttribute = Repository.ComplexQueryAttribute
				.newInstance(Repository.ComplexQueryMode.AND);

		if (type.getName().equals("com.rating.bo.APNDetails")
				|| type.getName().equals("com.rating.bo.SystemConfiguration")) {
			complexQueryAttribute.addQueryAttribute(
					Repository.QueryAttribute.newInstance("account.id", accountId, QueryMode.EQ, null));
		}
		complexQueryAttribute
				.addQueryAttribute(Repository.QueryAttribute.newInstance(field, value, QueryMode.EQ_IGNORE_CASE, null));

		if (id != null) {
			complexQueryAttribute
					.addQueryAttribute(Repository.QueryAttribute.newInstance("id", id, QueryMode.NOT_EQ, null));
		}

		CrudRepository.QueryFilter<?> queryFilter = CrudRepository.QueryFilter.newInstance(type);
		queryFilter.addComplexQueryAttribute(complexQueryAttribute);
		Long count = crudRepository.countEntityEntries(queryFilter);

		if (count.intValue() > 0) {
			isExist = true;
		}
		return isExist;
	}

	



	@Override
	public ComplexQueryAttribute searchTableData(String searchPropertyNames, String searchValue)
			throws RatingAndReviewException {
		
		Repository.ComplexQueryAttribute searchQueryAttribute = null;
		try {
			searchValue = searchValue.replace('_', '?');
			searchQueryAttribute = Repository.ComplexQueryAttribute.newInstance(Repository.ComplexQueryMode.OR);
			StringTokenizer propertyName = new StringTokenizer(
					mapSearch.get("search.tabledata." + searchPropertyNames.toLowerCase()), ",");
			List<String> associationList = new ArrayList<>();
			while (propertyName.hasMoreTokens()) {
				String property = propertyName.nextToken();

				if (property.contains(".")) {
					String associationObject = property.substring(0, property.lastIndexOf('.'));
					String subProperty = property.substring(property.lastIndexOf('.'), property.length());

					if (!associationList.contains(associationObject)) {
						associationList.add(associationObject);
						searchQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance(
								associationObject.concat(subProperty), "*" + searchValue + "*",
								Repository.QueryMode.LIKE, String.class, associationObject, associationObject));
					} else {
						searchQueryAttribute.addQueryAttribute(
								Repository.QueryAttribute.newInstance(associationObject.concat(subProperty),
										"*" + searchValue + "*", QueryMode.LIKE, String.class));
					}
				} else {
						searchQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance(property,
								"*" + searchValue + "*", QueryMode.LIKE, String.class));
				}
			}

			if (searchPropertyNames.equalsIgnoreCase("simprovisionedrange")) {
				if (mapSearch.get("search.tabledata." + searchPropertyNames.toLowerCase()).contains("iccid")) {
					searchQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("iccidFrom",
							"'" + searchValue + "'", QueryMode.GE, String.class));
					searchQueryAttribute.addQueryAttribute(Repository.QueryAttribute.newInstance("iccidTo",
							"'" + searchValue + "'", QueryMode.LE, String.class));
				}
			}
		} catch (Exception exception) {
			throw new RatingAndReviewException("Table data search failed@" + searchValue, exception);
		}

		return searchQueryAttribute;
	}


	
}
