/**
 * 
 */
package com.inca.saas.wms.jms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author liubin
 *
 */
public class TransData implements Serializable {

	String id;

	String setId;

	Set<String> customerIdList = new HashSet<>();

	String desc;

	String intent;

	Map<String, String> properties = new HashMap<>();

	Object data;

	public TransData() {
		super();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the setId
	 */
	public String getSetId() {
		return setId;
	}

	/**
	 * @param setId
	 *            the setId to set
	 */
	public void setSetId(String setId) {
		this.setId = setId;
	}

	
	/**
	 * @return the customerIdList
	 */
	public Set<String> getCustomerIdList() {
		return customerIdList;
	}

	/**
	 * @param customerIdList the customerIdList to set
	 */
	public void setCustomerIdList(Set<String> customerIdList) {
		this.customerIdList = customerIdList;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the intent
	 */
	public String getIntent() {
		return intent;
	}

	/**
	 * @param intent
	 *            the intent to set
	 */
	public void setIntent(String intent) {
		this.intent = intent;
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}
	
	public String getProperty(String key) {
		return this.properties.get(key);
	}
	
	public String toJson() throws Exception {
		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(this);
		return json;
	}

	public void addCustomerId(String id) {
		this.customerIdList.add(id);
	}
	
}
