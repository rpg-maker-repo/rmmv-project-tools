package com.trinary.rmmv.util.types;

import java.util.List;

import com.trinary.rpgmaker.ro.PluginRO;

public class AmbiguousPluginRO extends PluginRO {
	protected List<PluginRO> plugins;

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
}