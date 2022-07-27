package com.rating.dl;

import java.util.List;

import com.rating.transform.TransformProcessor;
import com.rating.utils.search.PaginationSearchResult;

public abstract class QueryFilterTemplate<T> {

	private CrudRepository crudRepository;
	private T searchContainer;
	private TransformProcessor transformProcessor;

	public QueryFilterTemplate(CrudRepository crudRepository, T searchContainer,
			TransformProcessor transformProcessor) {
		this.crudRepository = crudRepository;
		this.searchContainer = searchContainer;
		this.transformProcessor = transformProcessor;
	}

	public QueryFilterTemplate(CrudRepository crudRepository, T searchContainer) {
		this.crudRepository = crudRepository;
		this.searchContainer = searchContainer;
	}

	public abstract CrudRepository.QueryFilter buildSearchFilter(T searchContainer);

	public PaginationSearchResult load(Class targetClass) {
		CrudRepository.QueryFilter searchParam = buildSearchFilter(this.searchContainer);
		PaginationSearchResult sr = executeSearch(searchParam, targetClass);
		if (sr == null) {
			return PaginationSearchResult.newSearchResultEmpty();
		}
		return sr;
	}

	public PaginationSearchResult load() {
		return load(null);
	}

	private PaginationSearchResult executeSearch(CrudRepository.QueryFilter filter, Class targetClass) {
		PaginationSearchResult paginationSearchResult = null;
		List<?> entries = this.crudRepository.getEntityEntries(filter);

		// if (transformProcessor != null && targetClass != null) {
		// entries = transformProcessor.transformListTo(entries, targetClass);
		// }
		/*
		 * transform only if source and target class type is different
		 *
		 */
		// Change as class name coming different because of lazy loading.
		// modified by Ankita
		if (transformProcessor != null && targetClass != null && !entries.isEmpty()
				&& !entries.get(0).getClass().getName().split("_")[0].equals(targetClass.getName())) {
			// if (transformProcessor != null && targetClass != null && !entries.isEmpty()
			// && entries.get(0).getClass() != targetClass) {
			entries = transformProcessor.transformListTo(entries, targetClass);
		}

		long counts = this.crudRepository.countEntityEntries(filter);
		paginationSearchResult = paginationSearchResult.newSearchResult(counts, entries);
		return paginationSearchResult;
	}

}
