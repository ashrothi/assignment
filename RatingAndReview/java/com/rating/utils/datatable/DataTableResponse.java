package com.rating.utils.datatable;

import java.util.List;

public class DataTableResponse {
    private List<?> data;
    private Long recordsTotal;
    private Long recordsFiltered;
    private Long draw;

    public DataTableResponse(List<?> data, Long recordsTotal, Long recordsFiltered, Long draw) {
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.draw = draw;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public Long getDraw() {
        return draw;
    }

    public void setDraw(Long draw) {
        this.draw = draw;
    }
}
