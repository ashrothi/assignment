/**
 * This package contain the  class for Third Party Application to set Generic Responses for Calling  API
 */
package com.rating.response;

/**
 * To Import Classes to access their functionality
 */
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This class work to to set Generic Responses for Calling API
 * 
 * 
 * @author Pooja Singh
 *
 */
public class Message {
	/***
	 * Declaring Description ,object, list and valid bit
	 */
	private String description;
	private Object object;
	private List<Object> list = new ArrayList<>();
	private boolean valid;
	private int status;

	public Message() {
		// TODO Auto-generated constructor stub
	}

	public Message(String description, boolean valid) {
		this.description = description;
		this.valid = valid;
	}

	public Message(String description, boolean valid, Object object) {
		this(description, valid);
		this.object = object;
	}

	public Message(String description, boolean valid, List<Object> list) {
		this(description, valid);
		this.list = list;
	}
	
	public Message(String description, boolean valid, Object object, int status) {
		this(description, valid,object);
		this.status=status;
	}

	public Message(String description, Object object, List<Object> list, boolean valid, int status) {
		this.description = description;
		this.object = object;
		this.list = list;
		this.valid = valid;
		this.status = status;
	}

	/**
	 * To get the Message
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * To set the Message
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * to get Object
	 * 
	 * @return
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * To set Object
	 * 
	 * @param object
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * to get the List
	 * 
	 * @return
	 */
	public List<Object> getList() {
		return list;
	}

	/**
	 * To set the list
	 * 
	 * @param list
	 */
	public void setList(List<Object> list) {
		this.list = list;
	}

	/**
	 * To get If Message is Valid
	 * 
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * To set Result Valid
	 * 
	 * @param valid
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * To get If Message is Status
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * To set Result status
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

}
