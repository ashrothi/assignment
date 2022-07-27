package com.rating.utils;

import java.lang.reflect.Field;
import java.util.Map;

import com.rating.dl.CrudRepository.QueryFilter;
import com.rating.dl.Repository.SortAttribute.SortMode;

public class QueryUtils {

	public static QueryFilter buildPagingAndSortingFilter(QueryFilter queryFilter, Map<String, Object> params,
			Class clazz) {

		Integer start = params.get("start") != null ? Integer.parseInt(params.get("start").toString()) : 0;
		Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 20;

		queryFilter.addPagingParams(start, size);

		String ascendingColumn = (String) params.get("asc");
		String descendingColumn = (String) params.get("desc");

		if (ascendingColumn != null && !ascendingColumn.isEmpty() && isFieldPresent(clazz, ascendingColumn.trim())) {
			queryFilter.addSortAttribute(ascendingColumn, SortMode.ASC);
		}

		if (descendingColumn != null && !descendingColumn.isEmpty() && isFieldPresent(clazz, descendingColumn)) {
			queryFilter.addSortAttribute(descendingColumn, SortMode.DESC);
		}

		if (queryFilter.getSortAttributes() == null || queryFilter.getSortAttributes().size() == 0) {
			queryFilter.addSortAttribute("id", SortMode.ASC);
		}

		return queryFilter;
	}

	private static Boolean isFieldPresent(Class<?> clazz, String name) {
		Field field = null;
		while (clazz != null && field == null) {
			try {
				field = clazz.getDeclaredField(name);
				return true;
			} catch (Exception e) {
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}

}
