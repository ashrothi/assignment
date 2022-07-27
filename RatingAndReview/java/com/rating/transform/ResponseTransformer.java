package com.rating.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rating.dto.common.AbstractItem;
import org.apache.commons.beanutils.BeanMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rating.bo.common.BaseEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Ankita Shrothi
 *
 */
@Component
public class ResponseTransformer {
	
	/**
	 * 
	 */
	@Autowired
	Gson gson;
	/**
	 * @param object
	 * @param fileName
	 * @return
	 */
	public Object transform(Object object, String fileName) {
		try {
			if (object instanceof List) {
				List list = new ArrayList<>();
				for (BaseEntity entity : (List<BaseEntity>) object) {
					JsonObject target = gson.fromJson(fileName, JsonObject.class);
					JsonParser parser = new JsonParser();
					ObjectMapper mapperObj = new ObjectMapper();
					Map finalObject = copyProperties(new BeanMap(entity), gson.fromJson(target, Map.class));
					list.add(finalObject);
				}
				return list;
			} else {
				JsonObject target = gson.fromJson(fileName, JsonObject.class);
				return copyProperties(new BeanMap(object), gson.fromJson(target, Map.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;

	}

	/**
	 * @param beanMap
	 * @param dest
	 * @return
	 */
	private Map copyProperties(BeanMap beanMap, Map<String, Object> dest) {
		Set<String> keys = dest.keySet();

		for (String key : keys) {
			if (beanMap.get(key) instanceof BaseEntity || beanMap.get(key) instanceof AbstractItem) {
				copyProperties(new BeanMap(beanMap.get(key)), (Map) dest.get(key));
			} else {
				dest.put(key, beanMap.get(key));
			}
		}
		return dest;
	}
}
