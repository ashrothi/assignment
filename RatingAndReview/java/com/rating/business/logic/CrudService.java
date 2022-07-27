package com.rating.business.logic;

import java.util.List;

import com.rating.bo.common.BaseEntity;

public interface CrudService {

    public <T extends BaseEntity> T createEntity(Class<? extends T> type, T entity);

    public <T extends BaseEntity> void createEntityList(Class<? extends T> type, List<T> entityList);

    public <T extends BaseEntity> void deleteEntity(Class<? extends T> type, Long id);

    public <T extends BaseEntity> T getEntityById(Class<? extends T> type, Long id);

}
