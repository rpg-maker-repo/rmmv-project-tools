package com.trinary.rmmv.util.types;

import java.util.List;

import com.trinary.rpgmaker.ro.PluginRO;

public class ProjectRO {
	protected String name;
	protected String path;
	protected List<PluginRO> plugins;
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
	
	
}