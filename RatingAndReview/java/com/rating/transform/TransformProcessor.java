package com.rating.transform;

import java.util.List;
import java.util.Map;

public interface TransformProcessor {

    public Object transformToTo(Object entity, Object destination);

    public <T> T transformTo(Object entity, Class<T> transformerClass);

    public <T> List<T> transformListTo(List<?> entityList, Class<T> transformerClass);

    public Object transform(Object entity, Map<Class<?>, Class<?>> transformerClassMatrix);

    public List<?> transformList(List<?> entityList, Map<Class<?>, Class<?>> transformerClassMatrix);

    public Object transformToTo(Object entity, Object destination, String mapId);

    public <T> T transformTo(Object entity, Class<T> transformerClass, String mapId);

    public <T> List<T> transformListTo(List<?> entityList, Class<T> transformerClass, String mapId);

    public Object transform(Object entity, Map<Class<?>, Class<?>> transformerClassMatrix, String mapId);

    public List<?> transformList(List<?> entityList, Map<Class<?>, Class<?>> transformerClassMatrix, String mapId);

}
