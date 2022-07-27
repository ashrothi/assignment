package com.rating.utils.search;

import java.io.Serializable;
import java.util.List;

public class PaginationSearchResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4506235845650164551L;

    private List<?> results;
    private Long rowCount;

    public List<?> getResults() {
        return results;
    }

    public void setResults(List<?> results) {
        this.results = results;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }

    private PaginationSearchResult(Builder builder) {
        this.setResults(builder.results);
        this.setRowCount(builder.rowCount);
    }

    public static PaginationSearchResult newSearchResult(Long rowCount, List<?> results) {
        Builder builder = new Builder();
        builder.rowCount(rowCount);
        builder.results(results);
        return builder.build();
    }

    public static PaginationSearchResult newSearchResultEmpty() {
        Builder builder = new Builder();
        return builder.build();
    }

    public static class Builder {
        private List<?> results;
        private Long rowCount;

        public Builder results(List<?> results) {
            this.results = results;
            return this;
        }

        public Builder rowCount(Long rowCount) {
            this.rowCount = rowCount;
            return this;
        }

        public PaginationSearchResult build() {
            return new PaginationSearchResult(this);
        }

    }

}