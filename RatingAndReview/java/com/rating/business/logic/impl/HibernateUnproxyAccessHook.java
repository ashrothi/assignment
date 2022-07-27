package com.rating.business.logic.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer;
import org.javers.core.graph.ObjectAccessHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rating.bo.common.BaseEntity;

/**
 * @author Ankita Shrothi
 * 
 * This custom version of ObjectHook provides functionality to access and initialize Hiberate entiter which are lazy initialized.
 * specifically when using Javers.
 *
 */
@Component
public class HibernateUnproxyAccessHook implements ObjectAccessHook {
	private static Logger logger = LoggerFactory.getLogger(HibernateUnproxyAccessHook.class);

	@Override
	public <T> T access(T entity) {
		if (entity instanceof HibernateProxy) {
			if (Hibernate.isInitialized(entity)) {
				HibernateProxy proxy = (HibernateProxy) entity;
				T unproxed = (T) proxy.getHibernateLazyInitializer().getImplementation();
				return unproxed;
			} else {
				HibernateProxy proxy = (HibernateProxy) entity;
				Class<?> entityClass = proxy.getHibernateLazyInitializer().getPersistentClass();
				BaseEntity entityObj = null;
				try {
					Constructor<?> constr = entityClass.getConstructor();
					entityObj = (BaseEntity) constr.newInstance();
					entityObj.setId((Long) proxy.getHibernateLazyInitializer().getIdentifier());

				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				}

				return (T) entityObj;

			}
		}
//
		else if (entity instanceof JavassistLazyInitializer) {
			JavassistLazyInitializer proxy = (JavassistLazyInitializer) entity;
			T unproxed = (T) proxy.getImplementation();
			logger.info("Javers: unproxying instance of " + entity.getClass().getSimpleName() + " to "
					+ unproxed.getClass().getSimpleName());
			return unproxed;

		}
		return entity;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public <T> T access(T entity) {
//		if (entity instanceof HibernateProxy) {
//			Hibernate.initialize(entity);
//			HibernateProxy proxy = (HibernateProxy) entity;
//			T unproxed = (T) proxy.getHibernateLazyInitializer().getImplementation();
//			logger.info("Javers: unproxying instance of " + entity.getClass().getSimpleName() + " to "
//					+ unproxed.getClass().getSimpleName());
//			return unproxed;
//		}
//
//		else if (entity instanceof JavassistLazyInitializer) {
//			JavassistLazyInitializer proxy = (JavassistLazyInitializer) entity;
//			T unproxed = (T) proxy.getImplementation();
//			logger.info("Javers: unproxying instance of " + entity.getClass().getSimpleName() + " to "
//					+ unproxed.getClass().getSimpleName());
//			return unproxed;
//
//		}
//		return entity;
//	}
}
