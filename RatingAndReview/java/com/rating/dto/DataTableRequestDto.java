package com.rating.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rating.utils.datatable.OrderParameter;

public class DataTableRequestDto {

	public class SearchParameterDto {

		private String value;

		@JsonInclude(value = JsonInclude.Include.NON_NULL)
		private Boolean regex;

		public SearchParameterDto() {
		}

		public SearchParameterDto(String value, Boolean regex) {
			super();
			this.value = value;
			this.regex = regex;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Boolean getRegex() {
			return regex;
		}

		public void setRegex(Boolean regex) {
			this.regex = regex;
		}

	}

	public class ColumnParameterDto {

		@NotBlank
		private String data;

		public ColumnParameterDto() {
			this.data = "";
		}

		public ColumnParameterDto(String data) {
			super();
			this.data = data;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	private Integer start;

	private Integer length;

	@NotNull
	private SearchParameterDto search;

	private List<OrderParameter> order;

	private List<ColumnParameterDto> columns;

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public SearchParameterDto getSearch() {
		return search;
	}

	public void setSearch(SearchParameterDto search) {
		this.search = search;
	}

	public List<OrderParameter> getOrder() {
		return order;
	}

	public void setOrder(List<OrderParameter> order) {
		this.order = order;
	}

	public List<ColumnParameterDto> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnParameterDto> columns) {
		this.columns = columns;
	}

}