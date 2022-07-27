package com.rating.transform;

import org.dozer.CustomFieldMapper;
import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

/**
 * @author Ankita Shrothi
 * 
 *         Helps to map hibernate entity fields with it's DTOs with
 *         customizations. i.e it does not opens lazy initialized Hibernate
 *         Proxy objects.
 *
 */
@Component
public class RatingAndReviewFieldMapper implements CustomFieldMapper {

	/**
	 * When performing all field mappings. If false is returned from the call to
	 * mapField(), then the field will be subsequently mapped by Dozer as normal.
	 */
	@Override
	public boolean mapField(Object source, Object destination, Object sourceFieldValue, ClassMap classMap,
							FieldMap fieldMapping) {

		// Check if field is a Hibernate collection proxy, otherwise Allow
		if (!(sourceFieldValue instanceof HibernateProxy || sourceFieldValue instanceof PersistentBag))
			// Allow dozer to map as normal
			return false;

			/*
			 * Entites will be class of HibernateProxy
			 *
			 * and List<?> will be of class PersistentBag
			 *
			 * so, isInitialized check logic is different for both, as it is needed to
			 * type-cast for the same.
			 */

			// Check if field is already initialized, Otherwise Restrict dozer to map this
		else if (sourceFieldValue != null && sourceFieldValue instanceof HibernateProxy
				&& !((HibernateProxy) sourceFieldValue).getHibernateLazyInitializer().isUninitialized())
			// Hibernate property is initialized, Allow dozer to map this
			return false;

			// Check if field is already initialized, Otherwise Restrict dozer to map this
		else if (sourceFieldValue != null && sourceFieldValue instanceof PersistentBag
				&& Hibernate.isInitialized((PersistentBag) sourceFieldValue))
			// Hibernate property is initialized, Allow dozer to map this
			return false;

		// Set destination to null, and tell dozer that the field is mapped
		/*
		 * This will return any non-initialized PersistentSet objects as null. Do this
		 * so that when they are passed to the client it can be differentiated between a
		 * NULL (non-loaded) collection and an empty collection.
		 */
		destination = null;
		return true;
	}
}
