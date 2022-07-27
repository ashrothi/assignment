package com.rating.dl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;

import com.rating.bo.common.BaseEntity;

public abstract class Repository {
	private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    /**
     * Set session factory
     *
     * @param sessionFactory session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }
    
    
    /**
     * Create query Obj within new session.
     * @param queryStr
     * @return
     */
    public Query getQuery(String queryStr) {
		Query query = sessionFactory.openSession().createQuery(queryStr);
		return query;
	}
    
    /**
     * Save or update entity
     *
     * @param obj object
     */
    protected void saveOrUpdate(Object obj) {
        getCurrentSession().saveOrUpdate(obj);

    }

    /**
     * merge entity
     *
     * @param obj object
     */
    protected void merge(Object obj) {
        getCurrentSession().merge(obj);
    }

    /**
     * Save or update collection
     *
     * @param objects object
     */
    protected void saveOrUpdateAll(Collection<?> objects) {
        for (Object entry : objects) {
            getCurrentSession().saveOrUpdate(entry);
        }
    }

    /**
     * merge collection
     *
     * @param objects object
     */
    protected void mergeAll(Collection<?> objects) {
        for (Object entry : objects) {
            getCurrentSession().merge(entry);
        }
    }
    /***
     * FIND entity or entities
     ***/


    @SuppressWarnings({"rawtypes"})
    protected Object findSingle(Class clazz, Long id) {
        return getCurrentSession().load(clazz, id);
    }

    @SuppressWarnings({"rawtypes"})
    protected Object findSingle(Class clazz, Long id, boolean lock) {
        return getCurrentSession().load(clazz, id, LockOptions.UPGRADE);
    }

    protected Object findSingle(String query, Object... namedParams) {
        List<?> objects = find(query, namedParams);

        return objects.isEmpty() ? null : objects.get(0);
    }

    @SuppressWarnings("rawtypes")
    protected List find(String queryName, Object... parameters) {
        QueryParameters params = createQueryParameters(queryName, parameters);
        return findByNamedQueryAndNamedParam(queryName, params.names, params.values, null, null);
    }

    @SuppressWarnings({"rawtypes"})
    protected List findInterval(final Integer offset, final Integer limit, final String queryName, Object... parameters) {
        QueryParameters params = createQueryParameters(queryName, parameters);
        return findByNamedQueryAndNamedParam(queryName, params.names, params.values, offset, limit);
    }

    /***
     * UPDATE entity or entities
     ***/

    protected int update(final String queryName, Object... parameters) {
        QueryParameters params = createQueryParameters(queryName, parameters);
        final String[] names = params.names;
        final Object[] values = params.values;

        Query query = getCurrentSession().getNamedQuery(queryName);
        for (int index = 0; index < names.length; index++) {
            query.setParameter(names[index], values[index]);
        }
        Object affectedRows = query.executeUpdate();

        return (Integer) affectedRows;
    }

    /***
     * DELETE entity or entities
     ***/

    protected void delete(Object object) {
        getCurrentSession().delete(object);
    }

    protected void delete(Collection<?> entities) {
        for (Object entity : entities) {
            getCurrentSession().delete(entity);
        }
    }

    /***
     * COUNT entities
     ***/

    @SuppressWarnings("unchecked")
    protected Long count(String queryName, Object... parameters) {
        List<Long> count = (List<Long>) find(queryName, parameters);
        return count.isEmpty() ? 0L : count.get(0);
    }

    /***
     * UTILS
     ***/

    private List<?> findByNamedQueryAndNamedParam(String queryName, String[] names, Object[] values, Integer offset, Integer limit) {
        Query query = getCurrentSession().getNamedQuery(queryName);
        query = applyPageParameters(query, offset, limit);

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                applyNamedParameterToQuery(query, names[i], values[i]);
            }
        }
        return query.list();
    }

    protected Query applyPageParameters(Query query, Integer offset, Integer limit) {
        if (offset != null) {
            query = query.setFirstResult(offset);
        }

        if (limit != null) {
            query = query.setMaxResults(limit);
        }
        return query;
    }

    protected Criteria applyPageParameters(Criteria query, Integer offset, Integer limit) {
        if (offset != null) {
            query = query.setFirstResult(offset);
        }

        if (limit != null) {
            query = query.setMaxResults(limit);
        }
        return query;
    }

    private Query applyNamedParameterToQuery(Query queryObject, String paramName, Object value)
            throws HibernateException {

        if (value instanceof Collection) {
            queryObject.setParameterList(paramName, (Collection<?>) value);
        } else if (value instanceof Object[]) {
            queryObject.setParameterList(paramName, (Object[]) value);
        } else if (value instanceof Date) {
            queryObject.setTimestamp(paramName, (Date) value);
        } else {
            queryObject.setParameter(paramName, value);
        }
        return queryObject;
    }

    private QueryParameters createQueryParameters(String query, Object... namedParams) {
        if (namedParams.length % 2 != 0) {
            throw new IllegalArgumentException("Number of named " +
                    "parameters is not the same as number of values: " + query);
        }

        int length = namedParams.length / 2;
        String[] names = new String[length];
        Object[] values = new Object[length];

        int paramIndex = 0;
        for (int i = 0; i < namedParams.length; i += 2) {

            Object param = namedParams[i];
            if (!(param instanceof String)) {
                throw new IllegalArgumentException("Parameter name must be string: " + query);
            }

            names[paramIndex] = (String) param;
            values[paramIndex] = namedParams[i + 1];
            paramIndex++;
        }

        return new QueryParameters(names, values);
    }

    private class QueryParameters {

        protected String[] names;
        protected Object[] values;

        protected QueryParameters(String[] names, Object[] values) {
            this.names = names;
            this.values = values;
        }
    }

    /**
     * Check if exception occurred, because of duplicate data
     *
     * @param val field name
     * @param dve main exception
     * @return true if duplicated data
     */
    protected boolean isDuplicateException(String val, DataIntegrityViolationException dve) {
        Throwable cvt = dve.getCause();
        if (cvt != null) {
            Throwable mit = cvt.getCause();
            if (mit != null && mit.getMessage() != null) {
                String msg = mit.getMessage().toLowerCase();
                return msg.contains("duplicate")
                        && msg.contains("'" + val.toLowerCase() + "'");
            }
        }
        return false;
    }

    protected boolean isDuplicateException(String val, ConstraintViolationException dve) {
        Throwable cvt = dve.getCause();
        if (cvt != null) {
            Throwable mit = cvt.getCause();
            if (mit != null && mit.getMessage() != null) {
                String msg = mit.getMessage().toLowerCase();
                return msg.contains("duplicate")
                        && msg.contains("'" + val.toLowerCase() + "'");
            }
        }
        return false;
    }

    protected boolean isDuplicateException(String val, java.sql.BatchUpdateException dve) {
        Throwable cvt = dve.getCause();
        if (cvt != null) {
            Throwable mit = cvt.getCause();
            if (mit != null && mit.getMessage() != null) {
                String msg = mit.getMessage().toLowerCase();
                return msg.contains("duplicate")
                        && msg.contains("'" + val.toLowerCase() + "'");
            }
        }
        return false;
    }

    public static class ResultSet<T> {

        private Long count = 0L;

        private List<T> entries = new ArrayList<T>();

        private List<T> page = new ArrayList<T>();

        private ResultSet() {
        }

        public static <T> ResultSet<T> newInstance(Integer offset, Integer limit, List<T> entries) {
            ResultSet<T> result = new ResultSet<T>();
            result.count = (long) entries.size();
            result.entries = entries;

            if (offset != null && limit != null) {
                int startIndex = offset;
                int endIndex = offset + limit;
                if (endIndex > entries.size()) {
                    endIndex = entries.size();
                }
                result.page = entries.subList(startIndex, endIndex);
            } else {
                result.page = entries;
            }

            return result;
        }

        public static <T> ResultSet<T> newEmptyInstance() {
            ResultSet<T> result = new ResultSet<T>();
            return result;
        }

        public Long getCount() {
            return count;
        }

        public List<T> getEntries() {
            return entries;
        }

        public List<T> getPage() {
            return page;
        }

    }

    public static class MatchModeData implements Serializable {

        private static final long serialVersionUID = 1072532403360213465L;

        private String data;

        private MatchMode matchMode;

        private boolean empty;

        private MatchModeData() {
        }

        public static MatchModeData newInstance(String data) {
            data = StringUtils.remove(data, "%");
            data = StringUtils.remove(data, "_");

            MatchModeData result = new MatchModeData();

            data = StringUtils.trimToEmpty(data);

            boolean endSearch = StringUtils.startsWith(data, "*");
            boolean startSearch = StringUtils.endsWith(data, "*");

            MatchMode matchMode = MatchMode.EXACT;

            if (startSearch == true && endSearch == false) {
                matchMode = MatchMode.START;
                data = StringUtils.removeEnd(data, "*");
            }
            if (startSearch == false && endSearch == true) {
                matchMode = MatchMode.END;
                data = StringUtils.removeStart(data, "*");
            }
            if (startSearch == true && endSearch == true) {
                matchMode = MatchMode.ANYWHERE;
                data = StringUtils.removeStart(data, "*");
                data = StringUtils.removeEnd(data, "*");
            }

			/* extra case when search is custom/contains in the middle of search string char '*',
               data string will be returned with % operator in it */
            if (data.contains("*")) {
                data = StringUtils.replace(data, "*", "%");
                matchMode = MatchMode.EXACT;
            }

            data = StringUtils.replace(data, "?", "_");

            result.data = data;
            result.matchMode = matchMode;

            result.empty = StringUtils.isEmpty(data);

            return result;
        }

        public static MatchModeData newPlainInstance(String data) {
            MatchModeData result = new MatchModeData();

            result.data = data;
            result.matchMode = MatchMode.EXACT;
            result.empty = StringUtils.isEmpty(data);

            return result;
        }

        public String getData() {
            return data;
        }

        public MatchMode getMatchMode() {
            return matchMode;
        }

        public boolean isEmpty() {
            return empty;
        }

    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity> T getEntryById(Class<? extends T> type, Long id) {
        T result = (T) getCurrentSession().createCriteria(type)
        		.add(Restrictions.eq("id", id))
                .uniqueResult();

        return result;
    }

    public enum QueryMode {EQ, EQ_OR_NULL, LIKE, ILIKE, GT, LT, GE, LE, IN, NOT_IN, NOT_NULL, NULL,EQ_IGNORE_CASE, EQ_IGNORE_CASE_AND_SPACE, NOT_EQ}

    ;

    public enum ComplexQueryMode {OR,AND}

    ;

    public static class QueryAttribute<T> {

        public static final String ID_FIELD_NAME = "id";

        private Class<? extends T> type;

        private String name;

        private T value;

        private QueryMode mode;

        private String alias;

        private String sqlParameterNameWrapper;

        private String sqlParameterValueWrapper;

        private MatchMode matchMode;
        
        private String associationPath;
        
        private String aliasName;

        protected QueryAttribute(String name, T value, QueryMode mode, Class<? extends T> type) {
            this.name = name;
            this.value = value;
            this.mode = mode;
            this.type = type;
            
        }
        
        protected QueryAttribute(String name, T value, QueryMode mode,MatchMode matchMode, Class<? extends T> type) {
            this.name = name;
            this.value = value;
            this.mode = mode;
            this.type = type;
            this.matchMode = matchMode;
        }

        protected QueryAttribute(String name, T value, QueryMode mode, Class<? extends T> type,String associationPath,String aliasName) {
            this.name = name;
            this.value = value;
            this.mode = mode;
            this.type = type;
            this.associationPath = associationPath;
            this.aliasName = aliasName;
        }
        
        public static QueryAttribute<Long> newIdInstance(Long id) {
            return newInstance(ID_FIELD_NAME, id, QueryMode.EQ, Long.class);
        }

        public static <T> QueryAttribute<T> newInstance(String name, T value, QueryMode mode, Class<? extends T> type) {
            if (mode == QueryMode.IN) {
                if (value instanceof Collection == false) {
                    throw new IllegalArgumentException("Non list parameter value was applied for query mode IN");
                }
            }

            QueryAttribute<T> result = new QueryAttribute<T>(name, value, mode, type);

            return result;
        }

        public static <T> QueryAttribute<T> newInstance(String name, T value, QueryMode mode, Class<? extends T> type,String associationPath,String aliasName) {
            if (mode == QueryMode.IN) {
                if (value instanceof Collection == false) {
                    throw new IllegalArgumentException("Non list parameter value was applied for query mode IN");
                }
            }

            QueryAttribute<T> result = new QueryAttribute<T>(name, value, mode, type,associationPath,aliasName);

            return result;
        }
        
        public static <T> QueryAttribute<T> newInstance(String name, T value, QueryMode mode,MatchMode matchMode, Class<? extends T> type) {
            if (mode == QueryMode.IN) {
                if (value instanceof Collection == false) {
                    throw new IllegalArgumentException("Non list parameter value was applied for query mode IN");
                }
            }

            QueryAttribute<T> result = new QueryAttribute<T>(name, value, mode,matchMode ,type);

            return result;
        }
        
        public QueryAttribute<T> modifyAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public QueryAttribute<T> modifySqlParameterNameWrapper(String sqlParameterNameWrapper) {
            this.sqlParameterNameWrapper = sqlParameterNameWrapper;
            return this;
        }

        public QueryAttribute<T> modifySqlParameterValueWrapper(String sqlParameterValueWrapper) {
            this.sqlParameterValueWrapper = sqlParameterValueWrapper;
            return this;
        }

        public QueryAttribute<T> modifyMatchMode(MatchMode matchMode) {
            this.matchMode = matchMode;
            return this;
        }

        public Class<? extends T> getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }

        public QueryMode getMode() {
            return mode;
        }

        public String getParameterName() {
            if (StringUtils.isNotEmpty(alias)) {
                return alias;
            }
            if (StringUtils.contains(name, ".")) {
                return StringUtils.remove(name, ".");
            }
            return name;
        }

        public String getSqlParameterNameWrapper() {
            return sqlParameterNameWrapper;
        }

        public String getSqlParameterValueWrapper() {
            return sqlParameterValueWrapper;
        }

        public MatchMode getMatchMode() {
            return matchMode;
        }

		public String getAssociationPath() {
			return associationPath;
		}

		public String getAliasName() {
			return aliasName;
		}

    }

    public static class SortAttribute {

        public enum SortMode {ASC, DESC}

        private String name;

        private SortMode sortMode = SortMode.ASC;

        private SortAttribute() {
        }

        public static SortAttribute newInstance(String name, SortMode sortMode) {
            SortAttribute result = new SortAttribute();

            result.name = name;
            result.sortMode = sortMode;

            return result;
        }

        public String getName() {
            return name;
        }

        public SortMode getSortMode() {
            return sortMode;
        }

    }

    public static class ComplexQueryAttribute {

        private List<QueryAttribute<?>> attributes = new ArrayList<QueryAttribute<?>>();

        private ComplexQueryMode mode;

        protected ComplexQueryAttribute(ComplexQueryMode mode) {
            this.mode = mode;
        }

        public static ComplexQueryAttribute newInstance(ComplexQueryMode mode) {
            ComplexQueryAttribute result = new ComplexQueryAttribute(mode);

            return result;
        }

        public ComplexQueryAttribute addQueryAttribute(QueryAttribute<?> attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public List<QueryAttribute<?>> getQueryAttributes() {
            return attributes;
        }

        public ComplexQueryMode getMode() {
            return mode;
        }
    }

    protected Criteria addQueryRestrictions(Criteria query, List<QueryAttribute<?>> attributes) {
        for (QueryAttribute<?> attribute : attributes) {
            Criterion restriction = resolveQueryAttributeRestriction(attribute);
            if(attribute.getAssociationPath() != null){
            	query.createAlias(attribute.getAssociationPath(), attribute.getAliasName());
            }
            
            query = query.add(restriction);
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    private Criterion resolveQueryAttributeRestriction(QueryAttribute<?> attribute) {
        switch (attribute.getMode()) {
            case EQ: {
                return Restrictions.eq(attribute.getName(), attribute.getValue());
            }
            case EQ_IGNORE_CASE: {
            	return Restrictions.eq(attribute.getName(), attribute.getValue()).ignoreCase();
            }
            case EQ_IGNORE_CASE_AND_SPACE: {
                return Restrictions.sqlRestriction("LOWER(REPLACE(" + attribute.getName()+ ", ' ' , '')) = '"+attribute.getValue().toString().toLowerCase()+"'");
            }
            case NOT_EQ: {
            	 return Restrictions.neOrIsNotNull(attribute.getName(), attribute.getValue());
            }
            case IN: {
                Collection<Object> values = (Collection<Object>) attribute.getValue();

                if (values.isEmpty()) {
                    throw new IllegalArgumentException("Empty collection was provided for query");
                }

                return Restrictions.in(attribute.getName(), values);
            }
            case NOT_IN: {
                Collection<Object> values = (Collection<Object>) attribute.getValue();

                return Restrictions.not(Restrictions.in(attribute.getName(), values));
            }
            case LE: {
                return Restrictions.le(attribute.getName(), attribute.getValue());
            }
            case LT: {
                return Restrictions.lt(attribute.getName(), attribute.getValue());
            }
            case GE: {
                return Restrictions.ge(attribute.getName(), attribute.getValue());
            }
            case GT: {
                return Restrictions.gt(attribute.getName(), attribute.getValue());
            }
            case LIKE: {
                MatchModeData matchModeData = MatchModeData.newInstance((String) attribute.getValue());

                MatchMode matchMode = attribute.getMatchMode() != null ? attribute.getMatchMode() : matchModeData.getMatchMode();

                return Restrictions.like(attribute.getName(), matchModeData.getData(), matchMode);
            }
            case ILIKE: {
                MatchModeData matchModeData = MatchModeData.newInstance((String) attribute.getValue());

                MatchMode matchMode = attribute.getMatchMode() != null ? attribute.getMatchMode() : matchModeData.getMatchMode();

                return Restrictions.ilike(attribute.getName(), matchModeData.getData(), matchMode);
            }
            case NULL: {
                return Restrictions.isNull(attribute.getName());
            }
            case NOT_NULL: {
                return Restrictions.isNotNull(attribute.getName());
            }
            default: {
                throw new IllegalStateException("Query attribute query mode is not supported. Mode: " + attribute.getMode());
            }
        }
    }

    protected Criteria addQueryOrders(Criteria query, List<SortAttribute> attributes) {
        for (SortAttribute attribute : attributes) {
        	if(attribute.getName().contains(".")){
        		switch (attribute.getSortMode()) {
                case ASC: {                	
                	query = query.createCriteria(StringUtils.substringBefore(attribute.getName(), "."))
                			.addOrder(Order.asc(StringUtils.substringAfter(attribute.getName(),".")));
                    break;
                }
                case DESC: {
                	query = query.createCriteria(StringUtils.substringBefore(attribute.getName(), "."))
                			.addOrder(Order.desc(StringUtils.substringAfter(attribute.getName(),".")));
                    break;
                }
                default: {
                    throw new IllegalStateException("Query attribute sort mode is not supported. Mode: " + attribute.getSortMode());
                }
            }
        	}else{
	            switch (attribute.getSortMode()) {
	                case ASC: {
	                    query = query.addOrder(Order.asc(attribute.getName()));
	                    break;
	                }
	                case DESC: {
	                    query = query.addOrder(Order.desc(attribute.getName()));
	                    break;
	                }
	                default: {
	                    throw new IllegalStateException("Query attribute sort mode is not supported. Mode: " + attribute.getSortMode());
	                }
	            }
        	}
        }

        return query;
    }

    protected Criteria addComplexQueryRestrictions(Criteria query, List<ComplexQueryAttribute> attributes) {
        for (ComplexQueryAttribute attribute : attributes) {
            switch (attribute.getMode()) {
                case OR: {
                    List<QueryAttribute<?>> entries = (List<QueryAttribute<?>>) attribute.getQueryAttributes();
                    if (entries.isEmpty()) {
                        break;
                    }
                    List<Criterion> restrictions = new ArrayList<Criterion>();

                    for (QueryAttribute<?> entry : entries) {
                        Criterion restriction = resolveQueryAttributeRestriction(entry);
                    
                        if(entry.getAssociationPath() != null){
                        	query.createAlias(entry.getAssociationPath(), entry.getAliasName());
                        }
                        restrictions.add(restriction);                        
                    }
                    query = query.add(Restrictions.or(restrictions.toArray(new Criterion[restrictions.size()])));
                    break;
                }
                case AND: {
                    List<QueryAttribute<?>> entries = (List<QueryAttribute<?>>) attribute.getQueryAttributes();
                    if (entries.isEmpty()) {
                        break;
                    }
                    List<Criterion> restrictions = new ArrayList<Criterion>();

                    for (QueryAttribute<?> entry : entries) {
                        Criterion restriction = resolveQueryAttributeRestriction(entry);
                    
                        if(entry.getAssociationPath() != null){
                        	query.createAlias(entry.getAssociationPath(), entry.getAliasName());
                        }
                        restrictions.add(restriction);                        
                    }
                    query = query.add(Restrictions.and(restrictions.toArray(new Criterion[restrictions.size()])));
                    break;
                }
                default: {
                    throw new IllegalStateException("Complex query attribute query mode is not supported. Mode: " + attribute.getMode());
                }
            }
        }

        return query;
    }

    private static final String DELETE_QUERY_TEMPLATE = "delete from {type} {where} {parameters}";

    protected void delete(Class<? extends BaseEntity> type, Long id) {
        delete(type, QueryAttribute.newIdInstance(id));
    }

    protected void delete(Class<? extends BaseEntity> type, QueryAttribute<?>... attributes) {
        delete(type, Arrays.asList(attributes));
    }

    protected void delete(Class<? extends BaseEntity> type, List<QueryAttribute<?>> attributes) {
        String query = DELETE_QUERY_TEMPLATE.replace("{type}", type.getSimpleName());

        List<String> parameters = new ArrayList<String>();
        for (QueryAttribute<?> attribute : attributes) {
            String attributeName = attribute.getName();
            String attributeParameterName = ":" + attribute.getParameterName();

            if (StringUtils.isNotEmpty(attribute.getSqlParameterNameWrapper())) {
                attributeName = attribute.getSqlParameterNameWrapper().replace("{parameter}", attributeName);
            }

            if (StringUtils.isNotEmpty(attribute.getSqlParameterValueWrapper())) {
                attributeParameterName = attribute.getSqlParameterValueWrapper().replace("{parameter}", attributeParameterName);
            }

            switch (attribute.getMode()) {
                case EQ: {
                    parameters.add(attributeName + " = " + attributeParameterName);
                    break;
                }
                case IN: {
                    parameters.add(attributeName + " in ( " + attributeParameterName + " )");
                    break;
                }
                case LE: {
                    parameters.add(attributeName + " <= " + attributeParameterName);
                    break;
                }
                case LT: {
                    parameters.add(attributeName + " < " + attributeParameterName);
                    break;
                }
                case GE: {
                    parameters.add(attributeName + " >= " + attributeParameterName);
                    break;
                }
                case GT: {
                    parameters.add(attributeName + " > " + attributeParameterName);
                    break;
                }
                default: {
                    throw new IllegalStateException("Query attribute query mode is not supported. Mode: " + attribute.getMode());
                }
            }
        }

        if (parameters.isEmpty()) {
            query = query.replace("{where}", "").replace("{parameters}", "");
        } else {
            query = query.replace("{where}", "where").replace("{parameters}", StringUtils.join(parameters, " and "));
        }

        Query hqlQuery = getCurrentSession().createQuery(query);
        for (QueryAttribute<?> attribute : attributes) {
            hqlQuery = applyNamedParameterToQuery(hqlQuery, attribute.getParameterName(), attribute.getValue());
        }

        hqlQuery.executeUpdate();
    }
    protected void updateSqlQueryWithParam(String query, HashMap params) {
        Query sqlQuery = getCurrentSession().createSQLQuery(query);
        if(params != null && !params.isEmpty()){
        Set<String> keys = params.keySet();
         for (String key : keys) {
             sqlQuery.setParameter(key, params.get(key));
         }
        }
        sqlQuery.executeUpdate();

    }
    
    public Object callSqlFunction(String function) {
    	  Query sqlQuery = getCurrentSession().createSQLQuery(function);
          return sqlQuery.uniqueResult();
     }

    public void setEvict(Object obj){
    	getCurrentSession().evict(obj);
    }
    
	public Object executeProcedure(Class clz, String sql, Object[] objects) {
		try {

			Query query = getCurrentSession().createSQLQuery(sql);

			if (clz != null) {
				query.setResultTransformer(Transformers.aliasToBean(clz));
			} else {
				query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			}

			for (int i = 0; i < objects.length; i++) {
				try {
					logger.info("Object id {}, ===obj {}" ,i , objects[i]);
					if (objects[i] == null) {
						query.setParameter(i, null);

					} else {
						query.setParameter(i, objects[i]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return query.list();

		} catch (Exception exception) {
			throw exception;
		}
	}
}
