package com.trinary.rmmv.util.types;

import java.util.List;
import java.util.Map;

import com.trinary.rpgmaker.ro.PluginRO;

public class ProjectRO {
	protected String name;
	protected String path;
	protected List<PluginRO> plugins;
	protected Map<String, PluginDescriptor> pluginDescriptors;
	
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
	 * @return the plugins
	 */
	public List<PluginRO> getPlugins() {
		return plugins;
	}
	
	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(List<PluginRO> plugins) {
		this.plugins = plugins;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * @return the pluginDescriptors
	 */
	public Map<String, PluginDescriptor> getPluginDescriptors() {
		return pluginDescriptors;
	}
	
	/**
	 * @param pluginDescriptors the pluginDescriptors to set
	 */
	public void setPluginDescriptors(Map<String, PluginDescriptor> pluginDescriptors) {
		this.pluginDescriptors = pluginDescriptors;
	}
	
	public PluginDescriptor getPluginDescriptor(PluginRO plugin) {
		return getPluginDescriptor(plugin.getFilename());
	}
	
	public PluginDescriptor getPluginDescriptor(String filename) {
		return this.pluginDescriptors.get(filename);
	}
}