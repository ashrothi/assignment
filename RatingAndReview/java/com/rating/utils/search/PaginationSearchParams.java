package com.rating.utils.search;

import com.rating.utils.datatable.DataTablesInput;

public class PaginationSearchParams {
    private Integer page;
    private Integer pageSize;
    private String searchValue;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getOffset() {
        return this.page * this.pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public static PaginationSearchParams buildSearchParams(DataTablesInput input) {
        PaginationSearchParams paginationSearchParams = new PaginationSearchParams();
        paginationSearchParams.setPage(input.getStart());
        paginationSearchParams.setPageSize(input.getLength());
        paginationSearchParams.setSearchValue(input.getSearch().getValue());
        return paginationSearchParams;
    }
}
