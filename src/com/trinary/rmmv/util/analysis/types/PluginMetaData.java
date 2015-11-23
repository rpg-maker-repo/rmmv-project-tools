package com.trinary.rmmv.util.analysis.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PluginMetaData {
	protected String pluginFilename;
	protected String pluginName;
	protected String pluginDescription;
	protected String pluginVersion;
	protected String pluginRMVersion;
	protected String pluginAuthor;
	protected String help;
	protected Map<String, PluginParam> parameters;
	
	public PluginMetaData() {
		parameters = new LinkedHashMap<String, PluginParam>();
	}
	
	public PluginDescriptor createPluginDescriptor() {
		PluginDescriptor descriptor = new PluginDescriptor();
		
		if (pluginName != null) {
			descriptor.setName(pluginName);
		} else {
			descriptor.setName(pluginFilename.replaceAll("\\.js", ""));
		}
		descriptor.setDescription(pluginDescription);
		descriptor.setStatus(false);
		
		for (Entry<String, PluginParam> entry : parameters.entrySet()) {
			String defaultValue = "";
			if (entry.getValue().getDefaultValue() != null) {
				defaultValue = entry.getValue().getDefaultValue();
			}
			descriptor.getParameters().put(entry.getKey(), defaultValue);
		}
		
		return descriptor;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return pluginFilename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.pluginFilename = filename;
	}
	/**
	 * @return the pluginName
	 */
	public String getPluginName() {
		return pluginName;
	}
	/**
	 * @param pluginName the pluginName to set
	 */
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}
	/**
	 * @return the pluginVersion
	 */
	public String getPluginVersion() {
		return pluginVersion;
	}
	/**
	 * @param pluginVersion the pluginVersion to set
	 */
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	/**
	 * @return the pluginRMVersion
	 */
	public String getPluginRMVersion() {
		return pluginRMVersion;
	}
	/**
	 * @param pluginRMVersion the pluginRMVersion to set
	 */
	public void setPluginRMVersion(String pluginRMVersion) {
		this.pluginRMVersion = pluginRMVersion;
	}
	/**
	 * @return the pluginAuthor
	 */
	public String getPluginAuthor() {
		return pluginAuthor;
	}
	/**
	 * @param pluginAuthor the pluginAuthor to set
	 */
	public void setPluginAuthor(String pluginAuthor) {
		this.pluginAuthor = pluginAuthor;
	}
	/**
	 * @return the pluginDescription
	 */
	public String getPluginDescription() {
		return pluginDescription;
	}
	/**
	 * @param pluginDescription the pluginDescription to set
	 */
	public void setPluginDescription(String pluginDescription) {
		this.pluginDescription = pluginDescription;
	}
	/**
	 * @return the help
	 */
	public String getHelp() {
		return help;
	}
	/**
	 * @param help the help to set
	 */
	public void setHelp(String help) {
		this.help = help;
	}
	/**
	 * @return the parameters
	 */
	public Map<String, PluginParam> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, PluginParam> parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String parameterString = "";
		String sep = "";
		
		for (Entry<String, PluginParam> parameter : parameters.entrySet()) {
			parameterString += String.format("%s%s (%s) : %s", 
					sep, 
					parameter.getKey(), 
					parameter.getValue().getDescription(), 
					parameter.getValue().getDefaultValue());
			sep = ", ";
		}
		
		return "PluginMetaData [pluginName=" + pluginName
				+ ", pluginDescription=" + pluginDescription
				+ ", pluginVersion=" + pluginVersion + ", pluginRMVersion="
				+ pluginRMVersion + ", pluginAuthor=" + pluginAuthor
				+ ", help=" + help + ", parameters=" + parameterString + "]";
	}
}