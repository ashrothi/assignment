package com.rating.utils.order;

import java.util.HashMap;
import java.util.Map;

import com.rating.utils.datatable.DataTablesInput;
import com.rating.utils.datatable.OrderParameter;

public class OrderParams {
	private Map<String,String> orderParameters = new HashMap<String,String>();
	
	
    public Map<String, String> getOrderParameters() {
		return orderParameters;
	}


	public void setOrderParameters(Map<String, String> orderParameters) {
		this.orderParameters = orderParameters;
	}

	public static OrderParams buildOrderParams(DataTablesInput input) {
        OrderParams orderParams = new OrderParams();

		 if(input.getOrder().size() > 0){
			 for (OrderParameter op :input.getOrder()){
				 orderParams.orderParameters.put(input.getColumns().get(op.getColumn()).getData(), op.getDir());
			 }
		 }
		 return orderParams;
    }
	
	public static OrderParams getOrderParam(String column, String sortDir) {
		OrderParams orderParams = new OrderParams();
		orderParams.orderParameters.put(column, sortDir);
		return orderParams;
	}
}
