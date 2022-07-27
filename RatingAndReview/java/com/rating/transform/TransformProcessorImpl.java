package com.rating.transform;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(value = "transformProcessor")
public class TransformProcessorImpl implements TransformProcessor {

 
    @Override
    public Object transformToTo(Object entity, Object destination) {
        return transformToTo(entity, destination, null);
    }

    @Override
    public <T> T transformTo(Object entity, Class<T> transformerClass) {
        return transformTo(entity, transformerClass, null);
    }

    @Override
    public <T> List<T> transformListTo(List<?> entityList, Class<T> transformerClass) {
        return transformListTo(entityList, transformerClass, null);
    }

    @Override
    public Object transform(Object entity, Map<Class<?>, Class<?>> transformerClassMatrix) {
        return transform(entity, transformerClassMatrix, null);
    }

    @Override
    public List<?> transformList(List<?> entityList, Map<Class<?>, Class<?>> transformerClassMatrix) {
        return transformList(entityList, transformerClassMatrix, null);
    }

    @Override
    public Object transformToTo(Object entity, Object destination, String mapId) {
        if (StringUtils.isEmpty(mapId)) {
            getMapper().map(entity, destination);
        } else {
            getMapper().map(entity, destination, mapId);
        }
        return destination;
    }

    @Override
    public <T> T transformTo(Object entity, Class<T> transformerClass, String mapId) {
        T transformedObject = null;
        
        if (entity == null)
			return transformedObject;

		/*
		 * do not perform transformation of source entity type and destination class
		 * type are same.
		 */
		if (entity.getClass().equals(transformerClass)) {
			// and return the same entity
			return (T) entity;
		}
        if (StringUtils.isEmpty(mapId)) {
            transformedObject = getMapper().map(entity, transformerClass);
        } else {
            transformedObject = getMapper().map(entity, transformerClass, mapId);
        }
        return transformedObject;
    }

    @Override
    public <T> List<T> transformListTo(List<?> entityList, Class<T> transformerClass, String mapId) {
    	if (CollectionUtils.isEmpty(entityList)) {
			return new ArrayList<T>();
		}
		
		/*
		 * do not perform transformation of source entity types and destination class
		 * type are same.
		 */
		if (entityList.get(0).getClass().equals(transformerClass)) {
			// and return the same entity list
			return (List<T>) entityList;
		}
		
    	if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<T>();
        }
        ArrayList<T> transformedEntityList = new ArrayList<>();
        for (Object entity : entityList) {
            transformedEntityList.add(
                    transformTo(entity, transformerClass, mapId));
        }
        return transformedEntityList;
    }

    @Override
    public Object transform(Object entity, Map<Class<?>, Class<?>> transformerClassMatrix, String mapId) {
        Class<?> destinationClass = transformerClassMatrix.get(
                entity.getClass());
        if (destinationClass == null) {
            return null;
        }
        return transformTo(entity, destinationClass, mapId);
    }

    @Override
    public List<?> transformList(List<?> entityList, Map<Class<?>, Class<?>> transformerClassMatrix, String mapId) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<Object>();
        }
        ArrayList<Object> transformedEntityList = new ArrayList<Object>();
        for (Object entity : entityList) {
            transformedEntityList.add(
                    transform(entity, transformerClassMatrix, mapId));
        }
        return transformedEntityList;
    }

    public DozerBeanMapper getMapper() {
        try {
        	
        	DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean=new DozerBeanMapperFactoryBean();
            return (DozerBeanMapper) dozerBeanMapperFactoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
