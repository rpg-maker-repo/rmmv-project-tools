package com.trinary.rmmv.util.types;

import com.trinary.rpgmaker.ro.PluginRO;

public class OutOfDatePluginRO extends PluginRO {
	protected PluginRO latestVersion;
	
	public OutOfDatePluginRO(PluginRO plugin) {
		this.setId(plugin.getId());
		this.setName(plugin.getName());
		this.setDescription(plugin.getDescription());
		this.setDateCreated(plugin.getDateCreated());
		this.setVersion(plugin.getVersion());
		this.setCompatibleRMVersion(plugin.getCompatibleRMVersion());
		this.setHash(plugin.getHash());
	}

	/**
	 * @return the latestVersion
	 */
	public PluginRO getLatestVersion() {
		return latestVersion;
	}

	/**
	 * @param latestVersion the latestVersion to set
	 */
	public void setLatestVersion(PluginRO latestVersion) {
		this.latestVersion = latestVersion;
	}
}