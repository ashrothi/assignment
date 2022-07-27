package com.rating.business.logic.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.rating.bo.common.BaseEntity;
import com.rating.dl.CrudRepository;
import com.rating.business.logic.CrudService;

@org.springframework.stereotype.Service("crudService")
public class CrudServiceImpl implements CrudService {

    @Autowired
    protected CrudRepository crudDao;

    @Transactional("RatingAndReviewTransactionManager")
    @Override
    public <T extends BaseEntity> T createEntity(Class<? extends T> type, T entity) {
            return crudDao.createEntity(type, entity);
        // return crudDao.createEntity(type, entity);
    }

    @Transactional("RatingAndReviewTransactionManager")
    @Override
    public <T extends BaseEntity> void deleteEntity(Class<? extends T> type, Long id) {
        crudDao.deleteEntity(type, id);
    }

    @Transactional("RatingAndReviewTransactionManager")
    @Override
    public <T extends BaseEntity> T getEntityById(Class<? extends T> type, Long id) {
        return crudDao.getEntityById(type, id);
    }

    @Transactional("RatingAndReviewTransactionManager")
    @Override
    public <T extends BaseEntity> void createEntityList(Class<? extends T> type, List<T> entityList) {
        crudDao.createEntityList(type, entityList);
    }

}
