package com.rating.dl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.rating.bo.common.BaseEntity;

@Repository("crudRepository")
public class CrudRepository extends com.rating.dl.Repository {
	private Logger logger = LoggerFactory.getLogger(getClass());
    public static class QueryFilter<T extends BaseEntity> {
    	
    	

        private Class<? extends T> type;

        private List<QueryAttribute<?>> queryAttributes = new ArrayList<QueryAttribute<?>>();

        private List<ComplexQueryAttribute> complexQueryAttributes = new ArrayList<ComplexQueryAttribute>();

        private List<SortAttribute> sortAttributes = new ArrayList<SortAttribute>();
        
        private Integer offset;

        private Integer limit;

        private boolean emptyResult = false;

        private QueryFilter() {
        }

        public static <T extends BaseEntity> QueryFilter<T> newInstance(Class<? extends T> type) {
            QueryFilter<T> result = new QueryFilter<T>();

            result.type = type;
            return result;
        }

        public QueryFilter<T> addQueryAttribute(QueryAttribute<?> attribute) {
            queryAttributes.add(attribute);
            return this;
        }

        public QueryFilter<T> addComplexQueryAttribute(ComplexQueryAttribute attribute) {
            complexQueryAttributes.add(attribute);
            return this;
        }

        public <V> QueryFilter<T> addQueryAttribute(String name, V value, QueryMode mode, Class<V> type) {
            QueryAttribute<V> attribute = QueryAttribute.newInstance(name, value, mode, type);
            return addQueryAttribute(attribute);
        }

        public <V> QueryFilter<T> addQueryAttribute(String name, V value, QueryMode mode, Class<V> type,String associationPath,String aliasName) {
            QueryAttribute<V> attribute = QueryAttribute.newInstance(name, value, mode, type,associationPath,aliasName);
            return addQueryAttribute(attribute);
        }

        public QueryFilter<T> addSortAttribute(SortAttribute attribute) {
            sortAttributes.add(attribute);
            return this;
        }

        public QueryFilter<T> addSortAttribute(String name, SortAttribute.SortMode sortMode) {
            SortAttribute attribute = SortAttribute.newInstance(name, sortMode);
            return addSortAttribute(attribute);
        }

        public QueryFilter<T> addPagingParams(Integer offset, Integer limit) {
            this.offset = offset;
            this.limit = limit;

            return this;
        }

        public QueryFilter<T> modifyEmptyResult(boolean emptyResult) {
            this.emptyResult = emptyResult;
            return this;
        }

        public List<QueryAttribute<?>> getQueryAttributes() {
            return queryAttributes;
        }

        public List<ComplexQueryAttribute> getComplexQueryAttributes() {
            return complexQueryAttributes;
        }

        public List<SortAttribute> getSortAttributes() {
            return sortAttributes;
        }

        public Class<? extends T> getType() {
            return type;
        }

        public Integer getOffset() {
            return offset;
        }

        public Integer getLimit() {
            return limit;
        }

        public boolean isEmptyResult() {
            return emptyResult;
        }
    }

    public <T extends BaseEntity> T createEntity(Class<? extends T> type, T entity) {
        try {
            saveOrUpdate(entity);
        } catch (NonUniqueObjectException e) {
            logger.error("error occurred ",e);
            logger.debug("Handled NonUniqueObjectException "+e.getMessage());
            mergeEntity(type, entity);
        }
        return entity;
    }
    public <T extends BaseEntity> T mergeEntity(Class<? extends T> type, T entity) {
        merge(entity);
        return entity;
    }

    public <T extends BaseEntity> List<T> createEntityList(Class<? extends T> type, List<T> entityList) {
        saveOrUpdateAll(entityList);
        return entityList;
    }

    public <T extends BaseEntity> void mergeEntityList(Class<? extends T> type, List<T> entityList) {
        mergeAll(entityList);
    }
    
    public <T extends BaseEntity> void deleteEntity(Class<? extends T> type, Long id) {
        delete(type, id);
    }

    public <T extends BaseEntity> T getEntityById(Class<? extends T> type, Long id) {
        return super.getEntryById(type, id);
    }

    public <T extends BaseEntity> Long countEntityEntries(QueryFilter<T> queryFilter) {
        if (queryFilter.isEmptyResult()) {
            return 0L;
        }

        Criteria query = getCurrentSession().createCriteria(queryFilter.getType())
                .setProjection(Projections.countDistinct("id"));

        query = addQueryRestrictions(query, queryFilter.getQueryAttributes());
        query = addComplexQueryRestrictions(query, queryFilter.getComplexQueryAttributes());

        Long result = (Long) query.uniqueResult();

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> getEntityEntries(QueryFilter<T> queryFilter) {
        if (queryFilter.isEmptyResult()) {
            return new ArrayList<T>();
        }
        Criteria query = getCurrentSession().createCriteria(queryFilter.getType())
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        
        query = addQueryRestrictions(query, queryFilter.getQueryAttributes());
        query = addComplexQueryRestrictions(query, queryFilter.getComplexQueryAttributes());
        query = addQueryOrders(query, queryFilter.getSortAttributes());
        query = applyPageParameters(query, queryFilter.getOffset(), queryFilter.getLimit());

        List<T> result = query.list();
        //printStatistics(getCurrentSession().getSessionFactory());
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> ResultSet<T> getEntityResultSet(QueryFilter<T> queryFilter) {
        if (queryFilter.isEmptyResult()) {
            return ResultSet.newEmptyInstance();
        }

        Criteria query = getCurrentSession().createCriteria(queryFilter.getType());

        query = addQueryRestrictions(query, queryFilter.getQueryAttributes());
        query = addComplexQueryRestrictions(query, queryFilter.getComplexQueryAttributes());
        query = addQueryOrders(query, queryFilter.getSortAttributes());

        List<T> result = query.list();
        //printStatistics(getCurrentSession().getSessionFactory());
        return ResultSet.newInstance(queryFilter.getOffset(), queryFilter.getLimit(), result);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T getSingleEntity(QueryFilter<T> queryFilter) {
        if (queryFilter.isEmptyResult()) {
            return null;
        }

        Criteria query = getCurrentSession().createCriteria(queryFilter.getType());

        query = addQueryRestrictions(query, queryFilter.getQueryAttributes());
        query = addComplexQueryRestrictions(query, queryFilter.getComplexQueryAttributes());

        T result = (T) query.uniqueResult();
        //printStatistics(getCurrentSession().getSessionFactory());
        return result;
    }

    public <T extends BaseEntity> void deleteEntityEntries(QueryFilter<T> queryFilter) {
        delete(queryFilter.getType(), queryFilter.getQueryAttributes());

    }
    
    public void updateSqlQuery(String query, HashMap params) {
        updateSqlQueryWithParam(query, params);
     }
    
 


}