package com.trinary.rmmv.util.analysis.types;

public enum PluginMetaDataLocale {
	EN("EN"),
	JA("JA");
	
	protected String value;
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	PluginMetaDataLocale(String value) {
		this.value = value;
	}
}