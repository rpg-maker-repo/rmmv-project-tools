package com.trinary.rmmv.util.analysis.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PluginDescriptor {
	protected String name;
	protected Boolean status;
	protected String description;
	protected Map<String, String> parameters;
	
	public PluginDescriptor() {
		parameters = new LinkedHashMap<String, String>();
	}
	
	@SuppressWarnings("unchecked")
	public PluginDescriptor(Map<String, Object> jsObject) {
		this.name = (String)jsObject.get("name");
		this.status = (Boolean)jsObject.get("status");
		this.description = (String)jsObject.get("description");
		this.parameters = (Map<String, String>)jsObject.get("parameters");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String parameterString = "";
		String sep = "";
		
		for (Entry<String, String> parameter : parameters.entrySet()) {
			parameterString += String.format("%s%s : %s", sep, parameter.getKey(), parameter.getValue());
			sep = ", ";
		}
		
		return "PluginDescriptor [name=" + name + ", status=" + status
				+ ", description=" + description + ", parameters=" + parameterString
				+ "]";
	}
}